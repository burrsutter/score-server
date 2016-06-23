package com.redhatkeynote.score.client;

import com.redhatkeynote.score.Achievement;
import com.redhatkeynote.score.AchievementList;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.*;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;

import com.redhatkeynote.score.Player;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.marshalling.json.JSONMarshaller;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

public class LoadTest {

//    public static final ServerInfo KEYNOTE = new ServerInfo("http://score-production.apps-test.redhatkeynote.com/kie-server/services/rest/server",
//                                                            "score_70e71112d291851c17a1214b5a650837",
//                                                            "kieserver",
//                                                            "ki3server!" );
//    public static final ServerInfo KEYNOTE_QA = new ServerInfo("http://score-qa.apps-test.redhatkeynote.com/kie-server/services/rest/server",
//                                                            "score_70e71112d291851c17a1214b5a650837",
//                                                            "kieserver",
//                                                            "ki3server!" );
//    public static final ServerInfo LOCAL = new ServerInfo("http://localhost:8080/kie-server/services/rest/server",
//                                                            "score",
//                                                            "kieserver",
//                                                            "kieserver1!" );


    public static void main( String[] args )
            throws Exception {
        Options options = new Options();
        options.addOption( Option.builder("server").hasArg().desc( "score server host URL (required)" ).build() );
        options.addOption( Option.builder( "user" ).hasArg().desc( "score server username (default: kieserver)" ).build() );
        options.addOption( Option.builder( "passwd" ).hasArg().desc( "score server password (default: ki3server!)" ).build() );
        options.addOption( Option.builder("kc").hasArg().desc( "score server kie-container (required)" ).build() );
        options.addOption( Option.builder( "players" ).hasArg().desc( "number of players (default: 400)" ).build() );
        options.addOption( Option.builder("threads").hasArg().desc( "number of threads (default: 48)" ).build() );
        options.addOption( Option.builder("reqs").hasArg().desc( "number of requests per thread (default: 1000)" ).build() );
        options.addOption( Option.builder("prefix").hasArg().desc( "player uuid prefix (default: random)" ).build() );
        options.addOption( Option.builder( "help" ).desc( "prints this help" ).build() );

        CommandLineParser parser = new DefaultParser();
        CommandLine cli = parser.parse( options, args );

        if( cli.hasOption( "help" ) || !cli.hasOption( "server" ) || !cli.hasOption( "kc" )) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp( "java -jar score-load-test.jar", options );
            System.exit( 0 );
        }
        String serverUrl = cli.getOptionValue( "server" ) + "/kie-server/services/rest/server";
        String username  = cli.hasOption( "user" ) ? cli.getOptionValue( "user" ) : "kieserver";
        String password  = cli.hasOption( "passwd" ) ? cli.getOptionValue( "passwd" ) : "ki3server!";
        String kiecont   = cli.getOptionValue( "kc" );
        int players = cli.hasOption( "players" ) ? Integer.parseInt( cli.getOptionValue( "players" ) ) : 400;
        int threads = cli.hasOption( "threads" ) ? Integer.parseInt( cli.getOptionValue( "threads" ) ) : 48;
        int reqs = cli.hasOption( "reqs" ) ? Integer.parseInt( cli.getOptionValue( "reqs" ) ) : 1000;
        String prefix  = cli.hasOption( "prefix" ) ? cli.getOptionValue( "prefix" ) : UUID.randomUUID().toString().substring( 0, 5 )+"_";

        System.out.println("--------------------------------------------------------------");
        System.out.format( "  Executing load test with the following parameters:\n" );
        System.out.format( "    server   = %s\n", serverUrl );
        System.out.format( "    username = %s\n", username );
        System.out.format( "    password = %s\n", password );
        System.out.format( "    kiecont  = %s\n", kiecont );
        System.out.format( "    players  = %d\n", players );
        System.out.format( "    threads  = %d\n", threads );
        System.out.format( "    reqs     = %d\n", reqs );
        System.out.format( "    prefix   = %s\n", prefix );
        System.out.println( "--------------------------------------------------------------\n" );

        executeLoadTest(
                serverUrl,
                username,
                password,
                kiecont,
                players,
                threads,
                reqs,
                prefix );
    }

    private static void executeLoadTest(String serverUrl, String username, String password, String kiecont, int players, int threads, int reqs, String prefix)
            throws InterruptedException, java.util.concurrent.ExecutionException {
        final int TEAM_COUNT = 4;

        KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration( serverUrl, username, password );
        configuration.setMarshallingFormat( MarshallingFormat.JSON );
        HashSet<Class<?>> extraClasses = new HashSet<Class<?>>( Arrays.asList( Player.class, AchievementList.class, Achievement.class ) );
        configuration.addJaxbClasses( extraClasses );
        KieServicesClient kc = KieServicesFactory.newKieServicesClient( configuration );
        RuleServicesClient rules = kc.getServicesClient( RuleServicesClient.class );

        KieCommands cmd = KieServices.Factory.get().getCommands();
        Callable<Void>[] tasks = new Callable[threads];
        final AtomicInteger errors = new AtomicInteger( 0 );
        for( int t = 0; t < threads; t++ ) {
            final int task_index = t;
            tasks[t] = () -> {
                System.out.println(">> Starting task thread: "+task_index);
                final JSONMarshaller json = new JSONMarshaller( extraClasses, LoadTest.class.getClassLoader() );
                Command[] base = new Command[3];
                base[1] = cmd.newInsert( new AchievementList(), "newAchievements" );
                base[2] = cmd.newFireAllRules();

                for ( int i = 0; i < reqs; i++ ) {

                    try {
                        int player = ((i*threads)+task_index)%players;
                        //                    int player = task_index;
                        int team = player%TEAM_COUNT;
                        base[0] = cmd.newInsert( new Player( prefix + player, "John Doe "+player, team, i, i % 15, player % 2 == 0 ) );

                        Command batch = cmd.newBatchExecution( Arrays.asList( base ), "ScoreSession" );
                        ServiceResponse<ExecutionResults> results = rules.executeCommandsWithResults( kiecont, batch );
                        if( results.getType().equals( ServiceResponse.ResponseType.FAILURE ) ) {
                            System.err.format( "ERROR detected in task %d. Total of %d errors.\n", task_index, errors.incrementAndGet() );
                            System.err.println(json.marshall( results ) );
                        }
                        //System.out.println( json.marshall( results ) );
                    } catch ( Exception e ) {
                        System.out.format( "EXCEPTION detected on task(%d): %s\n", task_index, e.getMessage() );
                        e.printStackTrace();
                    }
                }
                System.out.println("        << Terminating task thread: "+task_index);
                return null;
            };
        }

        ExecutorService executors = Executors.newFixedThreadPool( threads );
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
        long totalRequests = reqs*threads;
        double throughput = ((double)totalRequests) / ((double) millis / 1000.0);
        System.out.format( "\n\nTime spent = %d, total requests = %d, throughput = %4.2f requests/second\n", millis, totalRequests, throughput );
    }

}
