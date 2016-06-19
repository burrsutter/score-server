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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.StatelessKieSession;

import com.redhatkeynote.score.AchievementList;
import com.redhatkeynote.score.DeletePlayers;
import com.redhatkeynote.score.Player;
import com.redhatkeynote.score.PlayerScore;
import com.redhatkeynote.score.ScoreSummary;
import com.redhatkeynote.score.TeamScore;

public class SummaryRulesTest {

    private static StatelessKieSession scoreSession;
    private static StatelessKieSession summarySession;
    
    @BeforeClass
    public static void beforeClass() {
        scoreSession = KieServices.Factory.get().getKieClasspathContainer().newStatelessKieSession("ScoreSession");
        summarySession = KieServices.Factory.get().getKieClasspathContainer().newStatelessKieSession("SummarySession");
    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void before() {
        scoreSession.execute( new DeletePlayers() );
    }

    @Test
    public void testTeamScoresAndPlayerScores() {
        final int numPlayers = 1000;
        final int topPlayers = 10;
        final int numTeams = 4;
        final int[] teamScores = new int[numTeams];
        
        final TreeSet<PlayerScore> playerScores = new TreeSet<>();
        final Map<String, Player> players = new HashMap<>();
        
        final Random random = new Random();

        for(int playerCount = 0 ; playerCount < numPlayers ; playerCount++) {
            final int team = random.nextInt(numTeams);
            final int score = random.nextInt(1000);
            final String uuid = "uuid " + playerCount;
            final String username = "Player " + playerCount;
            
            teamScores[team] += score;
            players.put(uuid, new Player(uuid, username, team, score, 100, true));
            playerScores.add(new PlayerScore(uuid, username, score));
        }
        
        for (Player player: players.values()) {
            executeScore(player);
        }
        
        final ScoreSummary scoreSummary = executeSummary(topPlayers);
        
        final SortedSet<TeamScore> retrievedTeamScores = scoreSummary.getTeamScores();

        Assert.assertNotNull("Should have teams", retrievedTeamScores);
        Assert.assertEquals("Should have all teams", numTeams, retrievedTeamScores.size());

        int lastTeamScore = Integer.MAX_VALUE;
        final boolean found[] = new boolean[numTeams];
        
        for(TeamScore teamScore: retrievedTeamScores) {
            int team = teamScore.getTeam();
            Assert.assertEquals("Team score should be as expected", teamScores[team], teamScore.getScore().intValue());
            Assert.assertTrue("Team score should be less than or equal to previous", lastTeamScore >= teamScore.getScore());
            lastTeamScore = teamScore.getScore();
            found[team] = true;
        }
        
        for(int teamCount = 0 ; teamCount < numTeams ; teamCount++) {
            Assert.assertTrue("Team " + teamCount + " was found", found[teamCount]);
        }
        
        SortedSet<PlayerScore> topPlayerScores = scoreSummary.getTopPlayerScores();
        Assert.assertNotNull("Should have top players", topPlayerScores);
        Assert.assertEquals("Should have expected number of top players", topPlayers, topPlayerScores.size());
        
        final int lowestTopScore = topPlayerScores.last().getScore();
        int lastPlayerScore = Integer.MAX_VALUE;
        for(PlayerScore playerScore: topPlayerScores) {
            final String uuid = playerScore.getUuid();
            final Player player = players.remove(uuid);
            Assert.assertNotNull("Player should not have been tested so far", player);
            Assert.assertEquals("Player score should be as expected", player.getScore(), playerScore.getScore());
            Assert.assertTrue("Player score should be less than or equal to previous", lastPlayerScore >= playerScore.getScore());
            Assert.assertTrue("Player score should be greater than or equal to lowest top score", lowestTopScore <= playerScore.getScore());
            lastPlayerScore = playerScore.getScore();
        }

        for(Player player: players.values()) {
            Assert.assertTrue("No unreturned player should have a score greater than lowest top player", lowestTopScore >= player.getScore());
        }
    }

    private AchievementList executeScore(final Player p) {
        AchievementList na = (AchievementList) ((StatelessKnowledgeSessionImpl) scoreSession).executeWithResults(
                Arrays.asList( p, new AchievementList() ),
                new ClassObjectFilter( AchievementList.class ) ).get( 0 );
        return na;
    }

    private ScoreSummary executeSummary(final int topPlayers) {
        return (ScoreSummary) ((StatelessKnowledgeSessionImpl) summarySession).executeWithResults(
                Arrays.asList(new ScoreSummary(topPlayers) ),
                new ClassObjectFilter( ScoreSummary.class ) ).get( 0 );
    }
}
