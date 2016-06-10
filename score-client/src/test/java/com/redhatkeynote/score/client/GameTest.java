package com.redhatkeynote.score.client;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;

import com.redhatkeynote.score.Player;

public class GameTest {

    // team #, player #, player score
    private static int[][] SCORES = new int[][] {
        new int[] {1, 1,   10},
        new int[] {2, 1,  100},
        new int[] {3, 1,   50},
        new int[] {4, 1,  500},
        new int[] {3, 1,  100},
        new int[] {3, 1,  800},
        new int[] {1, 1, 1000},
        new int[] {2, 2,  700},
        new int[] {4, 2, 1000},
    };

    private static KieSession session = null;

    @BeforeClass
    public static void beforeClass() {
        session = KieServices.Factory.get().getKieClasspathContainer().newKieSession();
        new Thread(new Runnable() {
            @Override
            public void run() {
                session.fireUntilHalt();
            }
        }).start();
    }

    @AfterClass
    public static void afterClass() {
        session.halt();
        session.dispose();
        session = null;
    }

    @Test
    public void testGame() throws Exception {
        for (int[] s : SCORES) {
            Integer team = s[0];
            Integer player = s[1];
            Integer score = s[2];
            String uuid = String.format("uuid-%s_%s", team, player);
            String username = String.format("Team%s_Player%s", team, player);
            session.insert(new Player(uuid, username, team, score));
            Thread.sleep(1000);
        }
        Thread.sleep(2000);
    }

}
