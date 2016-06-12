package com.redhatkeynote.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.runtime.KieContext;
import org.kie.server.api.marshalling.Marshaller;
import org.kie.server.api.marshalling.MarshallerFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ScoreServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreServer.class);
    private static final ScoreServer SERVER = new ScoreServer();
    private static final Marshaller JSON_MARSHALLER = MarshallerFactory.getMarshaller(MarshallingFormat.JSON, ScoreServer.class.getClassLoader());

    public static final Logger logger() {
        return LOGGER;
    }

    public static final ScoreServer server() {
        return SERVER;
    }

    private EntityManager entityManager = null;

    public synchronized ScoreServer init(KieContext kcontext) {
        try {
            if (entityManager == null) {
                EntityManagerFactory emf;
                final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                try {
                    // https://issues.jboss.org/browse/DROOLS-1108
                    ClassLoader cl = ((InternalKnowledgeBase)kcontext.getKieRuntime().getKieBase()).getRootClassLoader();
                    Thread.currentThread().setContextClassLoader(cl);
                    emf = Persistence.createEntityManagerFactory("score");
                } finally {
                    Thread.currentThread().setContextClassLoader(tccl);
                }
                entityManager = emf.createEntityManager();
                new Transaction<Object>(entityManager) {
                    @Override
                    public Object call() throws Exception {
                        TypedQuery<Long> query = em().createQuery("select count(g) from Game g", Long.class);
                        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
                        Long result = query.getSingleResult();
                        if (result.intValue() == 0) {
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

    public List<Achievement> loadAchievements() {
        // loads and returns the list of available achievements from the database
        return Arrays.asList( Achievement.ACHIEVEMENTS );
    }

    // TODO: respect game status
    public Player savePlayer(Player p) {
//        String uuid = p != null ? p.getUuid() : null;
//        if (uuid == null) {
//            throw new IllegalArgumentException("unidentified player");
//        }
//        return new Transaction<Player>(entityManager) {
//            @Override
//            public Player call() throws Exception {
//                TypedQuery<Player> query = em().createQuery("from Player p where p.uuid = :uuid", Player.class);
//                query.setParameter("uuid", uuid);
//                Player player;
//                try {
//                    player = query.getSingleResult();
//                    if (p.getUsername() != null) player.setUsername(p.getUsername());
//                    if (p.getTeam() != null) player.setTeam(p.getTeam());
//                    if (p.getScore() != null) player.setScore(p.getScore());
//                    em().merge(player);
//                } catch (NoResultException e) {
//                    player = p;
//                    em().persist(player);
//                }
//                return player;
//            }
//        }.transact();
        return p;
    }

    public void reportAchievement(Player p, Achievement a) {
        LOGGER.info(String.format( "http://achievement/api/%s/%s", p.getUuid(), a.getType() ));
    }

    public ScoreSummary getScoreSummary(int numTopPlayerScores) {
        final int limit = numTopPlayerScores < 0 ? 0 : numTopPlayerScores;
        return new Transaction<ScoreSummary>(entityManager) {
            @Override
            public ScoreSummary call() throws Exception {
                ScoreSummary ss = new ScoreSummary();
                // TODO: collapse into one query
                for (int t=1; t < 5; t++) {
                    TypedQuery<TeamScore> query = em().createQuery("select new com.redhatkeynote.score.TeamScore(p.team, sum(p.score)) from Player p where p.team = :team group by p.team", TeamScore.class);
                    query.setParameter("team", Integer.valueOf(t));
                    try {
                        TeamScore teamScore = query.getSingleResult();
                        ss.addTeamScore(teamScore);
                    } catch (NoResultException e) {
                        LOGGER.warn(String.format("Team %s doesn't exist.", t));
                    }
                }
                TypedQuery<PlayerScore> query = em().createQuery("select new com.redhatkeynote.score.PlayerScore(p.username, p.score) from Player p order by p.score desc, p.username asc", PlayerScore.class);
                query.setMaxResults(limit);
                List<PlayerScore> playerScores = query.getResultList();
                for (PlayerScore playerScore : playerScores) {
                    ss.addTopPlayerScore(playerScore);
                }
                return ss;
            }
        }.transact();
    }

    public void broadcastScores(int numTopPlayerScores) {
        ScoreSummary ss = getScoreSummary(numTopPlayerScores);
        String json = JSON_MARSHALLER.marshall(ss);
        LOGGER.info(String.format("http://leaderboard/api?json=%s", json));
    }

}
