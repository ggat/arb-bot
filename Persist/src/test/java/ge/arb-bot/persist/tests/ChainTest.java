package ge.arb-bot.persist.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.arb-bot.persist.CategoryInfoRepository;
import ge.arb-bot.persist.ChainRepository;
import ge.arb-bot.persist.models.CategoryInfo;
import ge.arb-bot.persist.models.Chain;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by giga on 12/7/17.
 */
public class ChainTest {

    ChainRepository repository;

    @Before
    public void setup() throws Exception {
        repository = new ChainRepository();
    }

    @Test
    public void testGetChains() throws IOException {
        long startTime = System.nanoTime();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<Long, Long>> adaptedChains = new ArrayList<>();

        List<Chain> chains = repository.all();

        for (Chain chain : chains) {
            String data = chain.getData();
            JsonNode chainNode = objectMapper.readTree(data);

            Map<Long, Long> resultChain = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = chainNode.fields();

            while(fields.hasNext()) {
                Map.Entry<String, JsonNode> nodeEntry = fields.next();

                String bookieIdString = nodeEntry.getKey();

                if(!bookieIdString.equals("subs") && !bookieIdString.equals("edit")) {
                    Long categoryId = nodeEntry.getValue().asLong();
                    Long bookieId = Long.valueOf(bookieIdString);

                    resultChain.put(bookieId, categoryId);
                }
            }

            adaptedChains.add(resultChain);
        }

        long endTime = System.nanoTime();

        System.out.println("duration: " + ((endTime - startTime) / 1000000));
        System.out.println("adaptedChains.size(): " + adaptedChains.size());
    }
}
