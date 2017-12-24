package ge.shitbot.daemon;

import ge.shitbot.daemon.analyze.utils.ChainUtils;
import ge.shitbot.persist.models.Chain;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by giga on 12/7/17.
 */
public class ChainUtilsTest {

    @Test
    public void testAdaptChains() {
        String[] sampleChains = new String[]{
                "{\"25\": \"2786\", \"27\": \"3274\", \"28\": \"2989\", \"29\": \"2916\", \"30\": \"3136\", \"edit\": true, \"subs\": [{\"25\": \"2787\", \"27\": \"3275\", \"28\": \"2990\", \"29\": \"2923\", \"30\": \"3137\", \"edit\": true}, {\"25\": \"2787\", \"27\": \"3275\", \"28\": \"2990\", \"29\": \"2923\", \"30\": \"3137\", \"edit\": false}]}",
                "{\"25\": \"2786\", \"27\": \"3274\", \"28\": \"2989\", \"29\": \"2916\", \"30\": \"3136\", \"edit\": true, \"subs\": [{\"25\": \"2787\", \"27\": \"3275\", \"28\": \"2990\", \"29\": \"2923\", \"30\": \"3137\", \"edit\": true}]}",
        };

        List<Chain> dbChains = new ArrayList<>();

        for(String sampleChain : sampleChains) {
            Chain chain = new Chain();
            chain.setData(sampleChain);
            chain.setId(111L);

            dbChains.add(chain);
        }

        List<ge.shitbot.daemon.analyze.models.Chain> adaptedChains = ChainUtils.adaptChains(dbChains);

        assertEquals(5, adaptedChains.size());

        for(ge.shitbot.daemon.analyze.models.Chain chain : adaptedChains) {
            assertTrue("One of the Chain is empty.", chain.size() > 0);
        }
    }
}
