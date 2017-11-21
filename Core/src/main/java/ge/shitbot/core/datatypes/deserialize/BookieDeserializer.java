package ge.shitbot.core.datatypes.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ge.shitbot.core.datatypes.Arb;

import java.io.IOException;

/**
 * Created by giga on 9/29/17.
 */
public class BookieDeserializer extends JsonDeserializer<Arb.Bookie> {

    @Override
    public Arb.Bookie deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        return jp.readValueAs(Arb.Bookie.class);
    }
}
