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

import com.redhatkeynote.score.*;
import jdk.nashorn.internal.ir.annotations.*;
import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.junit.*;
import org.junit.Ignore;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.StatelessKieSession;

import java.util.Arrays;
import java.util.Set;

public class AchievementsTest {

    private static StatelessKieSession session = null;

    public static Achievement[] ACHIEVEMENTS = new Achievement[]{
            new Achievement( "score1", "Apprentice Scorer" ), // > 10
            new Achievement( "score3", "Expert Scorer" ), // > 100
            new Achievement( "score2", "Master Scorer" ), // > 300
            new Achievement( "pops3", "Apprentice Popper" ), // > 3
            new Achievement( "pops1", "Expert Popper" ), // > 10
            new Achievement( "pops2", "Master Popper" ), // > 15
            new Achievement( "golden", "Golden Snitch" )
    };

    @BeforeClass
    public static void beforeClass() {
        session = KieServices.Factory.get().getKieClasspathContainer().newStatelessKieSession();
        session.addEventListener( new DebugAgendaEventListener() );
    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void before() {
        session.execute( new DeletePlayers() );
    }

    @Test
    public void testScoreNoAchievement() {
        Player p = new Player( "p1", "Player 1", 1, 5, 3, false );
        AchievementList na = execute( session, p );
        Assert.assertTrue( na.getAchievements().isEmpty() );
    }

    @Test
    public void testScoreApprenticeAchievement() {
        Player p = new Player( "p1", "Player 1", 1, 60, 3, false );
        AchievementList na = execute( session, p );
        Assert.assertEquals( 1, na.getAchievements().size() );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[0] ) );
    }

    @Test
    public void testScoreExpertAchievement() {
        Player p = new Player( "p1", "Player 1", 1, 160, 3, false );
        AchievementList na = execute( session, p );
        Assert.assertEquals( 1, na.getAchievements().size() );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[0] ) );
        //Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[1] ) );
    }

    @Test
    public void testScoreMasterAchievement() {
        Player p = new Player( "p1", "Player 1", 1, 360, 3, false );
        AchievementList na = execute( session, p );
        Assert.assertEquals( 2, na.getAchievements().size() );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[0] ) );
        //Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[1] ) );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[2] ) );
    }

    @Test @Ignore("Apprentice popper is not pre-loaded")
    public void testScoreApprenticePopper() {
        Player p = new Player( "p1", "Player 1", 1, 10, 5, false );
        AchievementList na = execute( session, p );
        Assert.assertEquals( 1, na.getAchievements().size() );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[3] ) );
    }

    @Test
    public void testScoreExpertPopper() {
        Player p = new Player( "p1", "Player 1", 1, 5, 12, false );
        AchievementList na = execute( session, p );
        Assert.assertEquals( 1, na.getAchievements().size() );
        //Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[3] ) );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[4] ) );
    }

    @Test
    public void testScoreMasterPopper() {
        Player p = new Player( "p1", "Player 1", 1, 5, 15, false );
        AchievementList na = execute( session, p );
        Assert.assertEquals( 2, na.getAchievements().size() );
        //Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[3] ) );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[4] ) );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[5] ) );
    }

    @Test
    public void testScoreFlagNewAchievements() {
        // Set up achievement 3 and 4
        Player p = new Player( "p1", "Player 1", 1, 5, 12, false );
        AchievementList na = execute( session, p );
        Assert.assertEquals( 1, na.getAchievements().size() );
        //Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[3] ) );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[4] ) );
        // Now trigger 5
        p = new Player( "p1", "Player 1", 1, 5, 15, false );
        na = execute( session, p );
        Assert.assertEquals( 1, na.getAchievements().size() );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[5] ) );
    }

    @Test
    public void testScoreGoldenSnitch() {
        Player p = new Player( "p1", "Player 1", 1, 1, 1, true );
        AchievementList na = execute( session, p );
        Assert.assertEquals( 1, na.getAchievements().size() );
        Assert.assertTrue( na.hasAchievement( ACHIEVEMENTS[6] ) );
    }

    @Test
    public void testCreateABrandNewAchievement() {
        PlayerAchievement pa = new PlayerAchievement( "p1", "ach_type", "A brand new achievement" );
        Set<Achievement> achievements = ScoreServer.server().loadAchievements();
        int size = achievements.size();
        session.execute( pa );
        achievements = ScoreServer.server().loadAchievements();
        Assert.assertEquals( size + 1, achievements.size() );
        boolean found = false;
        for ( Achievement a : achievements ) {
            if ( a.getDescription().equals( pa.getAchievement() ) ) {
                found = true;
            }
        }
        Assert.assertTrue( found );
    }

    private AchievementList execute(final StatelessKieSession session, final Player p) {
            AchievementList na = (AchievementList) ((StatelessKnowledgeSessionImpl) session).executeWithResults(
                    Arrays.asList( p, new AchievementList() ),
                    new ClassObjectFilter( AchievementList.class ) ).get( 0 );
            return na;
        }
    }
