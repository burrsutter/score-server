package com.redhatkeynote.score.client;

import com.redhatkeynote.score.Achievement;
import com.redhatkeynote.score.AchievementList;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;

import com.redhatkeynote.score.Player;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.marshalling.json.JSONMarshaller;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

public class LoadTest {

    public static final ServerInfo KEYNOTE = new ServerInfo("http://score-production.apps-test.redhatkeynote.com/kie-server/services/rest/server",
                                                            "score_70e71112d291851c17a1214b5a650837",
                                                            "kieserver",
                                                            "ki3server!" );
    public static final ServerInfo LOCAL = new ServerInfo("http://localhost:8080/kie-server/services/rest/server",
                                                            "score",
                                                            "kieserver",
                                                            "kieserver1!" );


    @BeforeClass
    public static void beforeClass() {

    }

    @AfterClass
    public static void afterClass() {

    }

    @Test
    public void testGame()
            throws Exception {
        final ServerInfo SERVER = LOCAL;
        final int PLAYER_COUNT = 4;
        final int TEAM_COUNT = 4;
        final int REQUESTS_PER_THREAD = 10000;
        final int THREADS = 8;

        KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration( SERVER.URL, SERVER.USER, SERVER.PASSWORD );
        configuration.setMarshallingFormat( MarshallingFormat.JSON );
        HashSet<Class<?>> extraClasses = new HashSet<Class<?>>( Arrays.asList( Player.class, AchievementList.class, Achievement.class ) );
        configuration.addJaxbClasses( extraClasses );
        KieServicesClient kc = KieServicesFactory.newKieServicesClient( configuration );
        RuleServicesClient rules = kc.getServicesClient( RuleServicesClient.class );

        KieCommands cmd = KieServices.Factory.get().getCommands();
        Callable<Void>[] tasks = new Callable[THREADS];
        final AtomicInteger errors = new AtomicInteger( 0 );
        for( int t = 0; t < THREADS; t++ ) {
            final int task_index = t;
            tasks[t] = () -> {
                final JSONMarshaller json = new JSONMarshaller( extraClasses, LoadTest.class.getClassLoader() );
                Command[] base = new Command[3];
                base[1] = cmd.newInsert( new AchievementList(), "newAchievements" );
                base[2] = cmd.newFireAllRules();

                for ( int i = 0; i < REQUESTS_PER_THREAD; i++ ) {
                    int player = ((i*THREADS)+task_index)%PLAYER_COUNT;
//                    int player = task_index;
                    int team = player%TEAM_COUNT;
                    base[0] = cmd.newInsert( new Player( "foo" + player, "John Doe", team, i, i % 15, player % 2 == 0 ) );

                    Command batch = cmd.newBatchExecution( Arrays.asList( base ), "ScoreSession" );
                    ServiceResponse<ExecutionResults> results = rules.executeCommandsWithResults( SERVER.CONTAINER, batch );
                    if( results.getType().equals( ServiceResponse.ResponseType.FAILURE ) ) {
                        System.err.format("ERROR detected in task %d. Total of %d errors. ", task_index, errors.incrementAndGet());
                        System.err.println(json.marshall( results ) );
                    }
                    //System.out.println( json.marshall( results ) );
                }
                return null;
            };
        }

        ExecutorService executors = Executors.newFixedThreadPool( THREADS );
        Future[] results = new Future[tasks.length];
        Instant start = Instant.now();
        for( int i = 0; i < tasks.length; i++ ) {
            results[i] = executors.submit( tasks[i] );
        }
        for( int i = 0; i < results.length; i++ ) {
            // make sure each task finished
            results[i].get();
        }
        Instant end = Instant.now();
        executors.shutdown();
        long millis = Duration.between( start, end ).toMillis();
        double throughput = ((double)REQUESTS_PER_THREAD*THREADS) / ((double) millis / 1000.0);
        System.out.format( "Time spent = %d, throughput = %4.2f requests/second\n", millis, throughput );
    }

    public static class ServerInfo {
        public final String URL;
        public final String CONTAINER;
        public final String USER;
        public final String PASSWORD;

        public ServerInfo(String URL, String CONTAINER, String USER, String PASSWORD) {
            this.URL = URL;
            this.CONTAINER = CONTAINER;
            this.USER = USER;
            this.PASSWORD = PASSWORD;
        }
    }

}
