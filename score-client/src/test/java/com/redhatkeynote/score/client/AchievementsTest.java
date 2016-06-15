/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhatkeynote.score.client;

import com.redhatkeynote.score.Achievement;
import com.redhatkeynote.score.Player;
import com.redhatkeynote.score.PlayerAchievement;
import com.redhatkeynote.score.ScoreServer;
import org.drools.core.event.DebugAgendaEventListener;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.StatelessKieSession;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class AchievementsTest {

    private static StatelessKieSession session = null;

    public static Achievement[] ACHIEVEMENTS = new Achievement[]{
            new Achievement( "SCR_APP", "Apprentice Scorer" ),
            new Achievement( "SCR_EXP", "Expert Scorer" ),
            new Achievement( "SCR_MAS", "Master Scorer" ),
            new Achievement( "POP_APP", "Apprentice Popper" ),
            new Achievement( "POP_EXP", "Expert Popper" ),
            new Achievement( "POP_MAS", "Master Popper" ),
            new Achievement( "GLD_SNT", "Golden Snitch" )
    };

    @BeforeClass
    public static void beforeClass() {
        session = KieServices.Factory.get().getKieClasspathContainer().newStatelessKieSession();
        session.addEventListener( new DebugAgendaEventListener() );
        // preloading achievements just to facilitate tests
        ScoreServer.server().loadAchievements().addAll( Arrays.asList( ACHIEVEMENTS ) );
    }

    @AfterClass
    public static void afterClass() {
    }

    @Test
    public void testScoreNoAchievement() {
        Player p = new Player( "p1", "Player 1", 1, 10, 3, false );
        session.execute( p );
        Assert.assertTrue( p.getAchievements().isEmpty() );
    }

    @Test
    public void testScoreApprenticeAchievement() {
        Player p = new Player( "p1", "Player 1", 1, 60, 3, false );
        session.execute( p );
        Assert.assertEquals( 1, p.getAchievements().size() );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[0] ) );
    }

    @Test
    public void testScoreExpertAchievement() {
        Player p = new Player( "p1", "Player 1", 1, 160, 3, false );
        session.execute( p );
        Assert.assertEquals( 2, p.getAchievements().size() );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[0] ) );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[1] ) );
    }

    @Test
    public void testScoreMasterAchievement() {
        Player p = new Player( "p1", "Player 1", 1, 360, 3, false );
        session.execute( p );
        Assert.assertEquals( 3, p.getAchievements().size() );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[0] ) );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[1] ) );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[2] ) );
    }

    @Test
    public void testScoreApprenticePopper() {
        Player p = new Player( "p1", "Player 1", 1, 10, 5, false );
        session.execute( p );
        Assert.assertEquals( 1, p.getAchievements().size() );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[3] ) );
    }

    @Test
    public void testScoreExpertPopper() {
        Player p = new Player( "p1", "Player 1", 1, 10, 12, false );
        session.execute( p );
        Assert.assertEquals( 2, p.getAchievements().size() );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[3] ) );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[4] ) );
    }

    @Test
    public void testScoreMasterPopper() {
        Player p = new Player( "p1", "Player 1", 1, 10, 15, false );
        session.execute( p );
        Assert.assertEquals( 3, p.getAchievements().size() );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[3] ) );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[4] ) );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[5] ) );
    }

    @Test
    public void testScoreFlagNewAchievements() {
        Player p = new Player( "p1", "Player 1", 1, 10, 15, false );
        p.addAchievement( ACHIEVEMENTS[3] );
        p.addAchievement( ACHIEVEMENTS[4] );
        session.execute( p );
        Assert.assertEquals( 3, p.getAchievements().size() );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[3] ) );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[4] ) );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[5] ) );
        for( Achievement a : p.getAchievements() ) {
            if( a.equals( ACHIEVEMENTS[3] ) ) {
                Assert.assertFalse( a.isNewAchievement() );
            } else if( a.equals( ACHIEVEMENTS[4] ) ) {
                Assert.assertFalse( a.isNewAchievement() );
            } else if( a.equals( ACHIEVEMENTS[5] ) ) {
                Assert.assertTrue( a.isNewAchievement() );
            }
        }
    }

    @Test
    public void testScoreGoldenSnitch() {
        Player p = new Player( "p1", "Player 1", 1, 1, 1, true );
        session.execute( p );
        Assert.assertEquals( 1, p.getAchievements().size() );
        Assert.assertTrue( p.hasAchievement( ACHIEVEMENTS[6] ) );
    }

    @Test
    public void testCreateABrandNewAchievement() {
        PlayerAchievement pa = new PlayerAchievement( "p1", "A brand new achievement" );
        Set<Achievement> achievements = ScoreServer.server().loadAchievements();
        int size = achievements.size();
        session.execute( pa );
        Assert.assertEquals( size + 1, achievements.size() );
        boolean found = false;
        for( Achievement a : achievements ) {
            if( a.getDesc().equals( pa.getAchievement() ) ) {
                found = true;
            }
        }
        Assert.assertTrue( found );
    }


}
