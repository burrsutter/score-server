package com.redhatkeynote.score.client;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;

import com.redhatkeynote.score.Player;
import org.kie.api.runtime.StatelessKieSession;

public class GameTest {

    // team #, player #, player score, pops
    private static int[][] SCORES = new int[][]{
            new int[]{1, 1, 10, 3},
            new int[]{2, 1, 100, 3},
            new int[]{3, 1, 50, 3},
            new int[]{4, 1, 500, 3},
            new int[]{3, 1, 100, 3},
            new int[]{3, 1, 800, 3},
            new int[]{1, 1, 1000, 3},
            new int[]{2, 2, 700, 3},
            new int[]{4, 2, 1000, 3},
    };

    private static StatelessKieSession session = null;

    @BeforeClass
    public static void beforeClass() {
        session = KieServices.Factory.get().getKieClasspathContainer().newStatelessKieSession();
    }

    @AfterClass
    public static void afterClass() {
        session = null;
    }

    @Test
    public void testGame() throws Exception {
        for (int[] s : SCORES) {
            Integer team = s[0];
            Integer player = s[1];
            Integer score = s[2];
            Integer pops = s[3];
            String uuid = String.format("uuid-%s_%s", team, player);
            String username = String.format("Team%s_Player%s", team, player);
            session.execute(new Player(uuid, username, team, score, pops ));
        }
    }

}
