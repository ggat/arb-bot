package ge.arb-bot.core.datatypes.deserialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by giga on 9/29/17.
 */
public abstract class AbstractDateDeserializer extends JsonDeserializer<Date> {

    protected abstract String getPattern();

    protected Date convert(String str, DeserializationContext ctx) {

        String patternA = getPattern();
        SimpleDateFormat format = new SimpleDateFormat(patternA);

//      String pattern =    "28 Sep 2017 23:05";
        try {
            return new Date(format.parse(str).getTime());
        } catch (ParseException e) {
            //e.printStackTrace();
        }

        return new Date(ctx.parseDate(str).getTime());
    }

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {

        //String pattern = "28 Sep 2017 23:05";
        String str = jp.getText().trim();

        return convert(str, ctx);
    }
}
