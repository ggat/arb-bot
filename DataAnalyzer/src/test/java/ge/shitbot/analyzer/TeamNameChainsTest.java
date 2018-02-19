package ge.shitbot.analyzer;

import ge.shitbot.analyzer.datatypes.CategoryData;
import ge.shitbot.analyzer.datatypes.EventData;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by giga on 12/24/17.
 */
public class TeamNameChainsTest {

    @Test
    public void testMergeChains() {

        TeamNameChains teamNameChains = new TeamNameChains();

        Map<String, String> chain = new HashMap<>();
        chain.put("ADJ", "TEAM");
        chain.put("EUR", "TEAM_EUR");

        Map<String, String> chain2 = new HashMap<>();
        chain2.put("ADJ", "TEAM2");
        chain2.put("EUR", "TEAM2_EUR");

        teamNameChains.add(chain2);
        teamNameChains.add(chain);

        assertEquals(chain, teamNameChains.findFirst("ADJ", "TEAM"));
        assertEquals(chain2, teamNameChains.findFirst("EUR", "TEAM2_EUR"));
        assertNull(teamNameChains.findFirst("EUR2", "TEAM2_EUR"));
    }
}
