package com.redhatkeynote.score;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.runtime.KieContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScoreServer {

    private static final Logger      LOGGER          = LoggerFactory.getLogger( ScoreServer.class );
    private static final ScoreServer SERVER          = new ScoreServer();
    private AtomicReference<Set<Achievement>> achievements = new AtomicReference<Set<Achievement>>(null);

    public static final Logger logger() {
        return LOGGER;
    }

    public static final ScoreServer server() {
        return SERVER;
    }

    private EntityManager entityManager = null;

    public synchronized ScoreServer init(KieContext kcontext) {
        try {
            if ( entityManager == null ) {
                EntityManagerFactory emf;
                final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                try {
                    // https://issues.jboss.org/browse/DROOLS-1108
                    ClassLoader cl = ((InternalKnowledgeBase) kcontext.getKieRuntime().getKieBase()).getRootClassLoader();
                    Thread.currentThread().setContextClassLoader( cl );
                    emf = Persistence.createEntityManagerFactory("score");
                } finally {
                    Thread.currentThread().setContextClassLoader(tccl);
                }
                entityManager = emf.createEntityManager();
                new Transaction<Object>(entityManager) {
                    @Override
                    public Object call() throws Exception {
                        TypedQuery<Game> query = em().createQuery("from Game g", Game.class);
                        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                        try {
                            query.getSingleResult();
                        } catch (NoResultException e) {
                            Game game = new Game(Status.CLOSED);
                            em().persist(game);
                        }
                        return null;
                    }
                }.transact();
            }
        } catch( Throwable t ) {
            LOGGER.error( "Error initializing the server:", t );
        }
        return this;
    }

    public Set<Achievement> loadAchievements() {
        Set<Achievement> current = achievements.get();
        if (current != null) {
            return current;
        }
        final List<Achievement> listOfAchievements = new Transaction<List<Achievement>>(entityManager) {
            @Override
            public List<Achievement> call() throws Exception {
                TypedQuery<Achievement> query = em().createQuery("from Achievement a", Achievement.class);
                return query.getResultList();
            }
        }.transact();
        final HashSet<Achievement> newSet = (listOfAchievements == null ? new HashSet<>() : new HashSet<>(listOfAchievements));
        if (achievements.compareAndSet(null, newSet)) {
            return newSet;
        } else {
            return loadAchievements();
        }
    }

    public void createAchievement(final String description ) {
        if (description == null) {
            throw new IllegalArgumentException("Uninitialised achievement");
        }
        final String uuid = UUID.randomUUID().toString();
        Achievement a = new Achievement(uuid, description);

        new Transaction<Object>(entityManager) {
            @Override
            public Object call() throws Exception {
                TypedQuery<Achievement> query = em().createQuery("from Achievement a where a.description = :description", Achievement.class);
                query.setParameter("description", description);
                query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                try {
                    query.getSingleResult();
                } catch (NoResultException e) {
                    em().persist(a);
                }
                return null;
            }
        }.transact();

        achievements.set(null);
    }

    public Player loadPlayer( Player p ) {
        String uuid = p != null ? p.getUuid() : null;
        if (uuid == null) {
            throw new IllegalArgumentException("unidentified player");
        }
        return new Transaction<Player>(entityManager) {
            @Override
            public Player call() throws Exception {
                TypedQuery<Player> query = em().createQuery("from Player p where p.uuid = :uuid", Player.class);
                query.setParameter("uuid", uuid);
                Player player;
                try {
                    player = query.getSingleResult();
                    if (p.getUsername() != null) player.setUsername(p.getUsername());
                    if (p.getTeam() != null) player.setTeam(p.getTeam());
                    if (p.getScore() != null) player.setScore(p.getScore());
                    if (p.getConsecutivePops() != null) player.setConsecutivePops(p.getConsecutivePops());
                    if (p.getGoldenSnitch() != null) player.setGoldenSnitch(p.getGoldenSnitch());
                    player = em().merge(player);
                } catch (NoResultException e) {
                    player = p;
                    em().persist(player);
                }
                return player;
            }
        }.transact();
    }

//    // TODO: respect game status
    public Player savePlayer(Player p) {
        String uuid = p != null ? p.getUuid() : null;
        if (uuid == null) {
            throw new IllegalArgumentException("unidentified player");
        }
        return new Transaction<Player>(entityManager) {
            @Override
            public Player call() throws Exception {
                TypedQuery<Player> query = em().createQuery("from Player p where p.uuid = :uuid", Player.class);
                query.setParameter("uuid", uuid);
                Player player;
                try {
                    player = query.getSingleResult();
                    if (p.getAchievements() != null) {
                        player.addAchievements(p.getAchievements());
                    }
                    player = em().merge(player);
                } catch (NoResultException e) {
                    e.printStackTrace();
                    player=null;
                }
                return player;
            }
        }.transact();
    }

    public ScoreSummary getScoreSummary( final ScoreSummary ss ) {
        final int limit = ss.getTopPlayers() < 0 ? 0 : ss.getTopPlayers();
        return new Transaction<ScoreSummary>(entityManager) {
            @Override
            public ScoreSummary call() throws Exception {
                // TODO: collapse into one query
                for (int t=1; t < 5; t++) {
                    TypedQuery<TeamScore> query = em().createQuery("select new com.redhatkeynote.score.TeamScore(p.summary, sum(p.score)) from Player p where p.summary = :summary group by p.summary", TeamScore.class);
                    query.setParameter("team", Integer.valueOf(t));
                    try {
                        TeamScore teamScore = query.getSingleResult();
                        ss.addTeamScore(teamScore);
                    } catch (NoResultException e) {
                        LOGGER.warn(String.format("Team %s doesn't exist.", t));
                    }
                }
                TypedQuery<PlayerScore> query = em().createQuery("select new com.redhatkeynote.score.PlayerScore(p.username, p.score) from Player p order by p.score description, p.username asc", PlayerScore.class);
                query.setMaxResults(limit);
                List<PlayerScore> playerScores = query.getResultList();
                for (PlayerScore playerScore : playerScores) {
                    ss.addTopPlayerScore(playerScore);
                }
                return ss;
            }
        }.transact();
    }

    public void deletePlayers() {
        new Transaction<Void>(entityManager) {
            @Override
            public Void call() throws Exception {
                Query delete = em().createQuery("delete from Player p");
                delete.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                delete.executeUpdate();
                return null;
            }
        }.transact();
    }
}
