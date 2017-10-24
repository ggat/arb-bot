package ge.shitbot.datasources.datatypes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by giga on 9/29/17.
 */
public class DateDeserializer extends JsonDeserializer<Timestamp> {

    @Override
    public Timestamp deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {

        String patternA =   "dd MMM yyyy HH:mm";

        SimpleDateFormat format = new SimpleDateFormat(
                patternA);

//        String pattern =    "28 Sep 2017 23:05";

        String str = jp.getText().trim();

        try {
            return new Timestamp(format.parse(str).getTime());
        } catch (ParseException e) {
            //e.printStackTrace();
        }

        return new Timestamp(ctx.parseDate(str).getTime());
    }
}
