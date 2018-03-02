package ge.shitbot.daemon;

import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.core.datatypes.OddType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by giga on 2/26/18.
 */
public class AlertServiceTest {

    @Test
    public void testArbComparison() {

        ArrayList<Arb> arbs = new ArrayList<>();

        arbs.add(createArbOne());
        arbs.add(createAebTwo());

        ArrayList<Arb> arbs2 = SerializationUtils.clone(arbs);

        //assertEquals(bookie, bookie2);
        assertTrue(CollectionUtils.isEqualCollection(arbs, arbs2));
    }

    protected Arb createArbOne() {
        Arb.Bookie bookie = new Arb.Bookie();

        bookie.setName("ADJ");
        bookie.setOddType("X");
        bookie.setOdd(1.8);
        bookie.setCategory("England");
        bookie.setSubCategory("Championship");
        bookie.setTeamOneName("Liverpool");
        bookie.setTeamTwoName("Manchester");

        Arb.Bookie bookie2 = new Arb.Bookie();

        bookie2.setName("EUR");
        bookie2.setOddType("X");
        bookie2.setOdd(2.4);
        bookie2.setCategory("England");
        bookie2.setSubCategory("Championship");
        bookie2.setTeamOneName("Liverpool AC");
        bookie2.setTeamTwoName("Manchester");

        Arb arb = new Arb();
        arb.setBookieOne(bookie);
        arb.setBookieTwo(bookie2);
        arb.setDate(new Timestamp(new Date().getTime()));
        arb.setProfit(1.1);

        return arb;
    }

    protected Arb createAebTwo() {
        Arb.Bookie bookie = new Arb.Bookie();

        bookie.setName("ADJ");
        bookie.setOddType("X");
        bookie.setOdd(1.8);
        bookie.setCategory("Spain");
        bookie.setSubCategory("Championship");
        bookie.setTeamOneName("Liverpool");
        bookie.setTeamTwoName("Manchester");

        Arb.Bookie bookie2 = new Arb.Bookie();

        bookie2.setName("EUR");
        bookie2.setOddType("X");
        bookie2.setOdd(2.4);
        bookie2.setCategory("Spain");
        bookie2.setSubCategory("Championship");
        bookie2.setTeamOneName("Liverpool AC");
        bookie2.setTeamTwoName("Manchester");

        Arb arb = new Arb();
        arb.setBookieOne(bookie);
        arb.setBookieTwo(bookie2);
        arb.setDate(new Timestamp(new Date().getTime()));
        arb.setProfit(1.1);

        return arb;
    }
}
