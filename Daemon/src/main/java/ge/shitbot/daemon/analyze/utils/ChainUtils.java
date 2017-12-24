package ge.shitbot.daemon.analyze.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ge.shitbot.daemon.analyze.models.Chain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by giga on 12/22/17.
 */
public class ChainUtils {

    protected static Logger logger = LoggerFactory.getLogger(ChainUtils.class);

    public static List<Chain> adaptChains(List<ge.shitbot.persist.models.Chain> chains) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<Chain> adaptedChains = new ArrayList<>();

        for (ge.shitbot.persist.models.Chain chain : chains) {
            String data = chain.getData();

            JsonNode chainNode = null;
            try {
                chainNode = objectMapper.readTree(data);
            } catch (IOException e) {
                logger.warn("Could not parse chain with id {}", chain.getId());
                continue;
            }

            recurseOnChains(chainNode, adaptedChains);
        }

        return adaptedChains;
    }

    /**
     * Produces puts
     *
     */
    protected static void recurseOnChains( JsonNode chainNode, List<Chain> adaptedChains) {

        Chain resultChain = new Chain();
        Iterator<Map.Entry<String, JsonNode>> fields = chainNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> nodeEntry = fields.next();

            String bookieIdString = nodeEntry.getKey();

            if(bookieIdString.equals("subs")) {
                //Chain may have multiple subCains "subs" is array therefore
                ArrayNode subChainNodes = (ArrayNode) nodeEntry.getValue();
                for(JsonNode subChainNode : subChainNodes){
                    recurseOnChains(subChainNode, adaptedChains);
                }
                continue;
            }

            if (!bookieIdString.equals("edit")) {
                Long categoryId = nodeEntry.getValue().asLong();
                Long bookieId = Long.valueOf(bookieIdString);

                resultChain.put(bookieId, categoryId);
            }
        }

        adaptedChains.add(resultChain);
    }
}
