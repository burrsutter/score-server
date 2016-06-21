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

    private AtomicReference<EntityManager> entityManagerReference = new AtomicReference<>();

    public synchronized ScoreServer init(KieContext kcontext) {
        if (entityManagerReference.get() == null) {
            synchronized(this) {
                if (entityManagerReference.get() == null) {
                    try {
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
                        entityManagerReference.compareAndSet(null, emf.createEntityManager());
                    } catch( Throwable t ) {
                        LOGGER.error( "Error initializing the server:", t );
                    }
                }
            }
        }
        return this;
    }

    public Set<Achievement> loadAchievements() {
        Set<Achievement> current = achievements.get();
        if (current != null) {
            return current;
        }
        final List<Achievement> listOfAchievements = new Transaction<List<Achievement>>(entityManagerReference.get()) {
            @Override
            public List<Achievement> call() throws Exception {
                TypedQuery<Achievement> query = em().createNamedQuery("getAchievements", Achievement.class);
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

    public void createAchievement(final String type, final String description ) {
        if (description == null || type == null ) {
            throw new IllegalArgumentException("Uninitialised achievement");
        }
        Achievement a = new Achievement(type, description);

        new Transaction<Object>(entityManagerReference.get()) {
            @Override
            public Object call() throws Exception {
                TypedQuery<Achievement> query = em().createNamedQuery("findAchievementByTypeTeamScores", Achievement.class);
                query.setParameter("type", type);
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
        return new Transaction<Player>(entityManagerReference.get()) {
            @Override
            public Player call() throws Exception {
                TypedQuery<Player> query = em().createNamedQuery("findPlayerByUuid", Player.class);
                query.setParameter("uuid", uuid);
                Player player;
                try {
                    player = query.getSingleResult();
                    if (p.getUsername() != null) player.setUsername(p.getUsername());
                    if (p.getTeam() != null) player.setTeam(p.getTeam());
                    if (p.getScore() != null) player.setScore(p.getScore());
                    if (p.getConsecutivePops() != null) player.setConsecutivePops(p.getConsecutivePops());
                    if (p.getGoldenSnitch() != null) player.setGoldenSnitch(p.getGoldenSnitch());
                } catch (NoResultException e) {
                    player = p;
                    em().persist( player );
                }
                return player;
            }
        }.transact();
    }

//    // TODO: respect game status
    public Player savePlayer(Player p) {
        return new Transaction<Player>(entityManagerReference.get()) {
            @Override
            public Player call() throws Exception {
                return em().merge(p);
            }
        }.transact();
    }

    public ScoreSummary getScoreSummary( final ScoreSummary ss ) {
        final int limit = ss.getTopPlayers() < 0 ? 0 : ss.getTopPlayers();
        return new Transaction<ScoreSummary>(entityManagerReference.get()) {
            @Override
            public ScoreSummary call() throws Exception {
                TypedQuery<TeamScore> teamScoreQuery = em().createNamedQuery("getTeamScores", TeamScore.class);
                ss.addTeamScores(teamScoreQuery.getResultList());

                TypedQuery<PlayerScore> playerScoreQuery = em().createNamedQuery("getPlayerScores", PlayerScore.class);
                playerScoreQuery.setMaxResults(limit);
                ss.addTopPlayerScores(playerScoreQuery.getResultList());
                return ss;
            }
        }.transact();
    }

    public void deletePlayers() {
        new Transaction<Void>(entityManagerReference.get()) {
            @Override
            public Void call() throws Exception {
                Query delete = em().createNamedQuery("deletePlayers");
                delete.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                delete.executeUpdate();
                return null;
            }
        }.transact();
    }
}
