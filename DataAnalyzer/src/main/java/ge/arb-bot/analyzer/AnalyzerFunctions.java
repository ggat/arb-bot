package ge.arb-bot.analyzer;

import ge.arb-bot.analyzer.datatypes.CategoryData;
import ge.arb-bot.analyzer.datatypes.EventData;
import ge.arb-bot.core.Calc;
import ge.arb-bot.core.datatypes.Arb;
import ge.arb-bot.core.datatypes.OddType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 2/21/18.
 */
public class AnalyzerFunctions {

    public static List<Arb> compareForArb(CategoryData categoryDataOne, EventData eventDataOne, CategoryData categoryDataTwo, EventData eventDataTwo) {
        return compareForArb(categoryDataOne, eventDataOne, categoryDataTwo, eventDataTwo, 0.0);
    }

    public static List<Arb> compareForArb(CategoryData categoryDataOne, EventData eventDataOne, CategoryData categoryDataTwo, EventData eventDataTwo, Double minimumLimit) {

        List<Arb> resultArbs = new ArrayList<>();

        //This fucking should mean that events matched.
        //Start comparing odds of these two events
        for (OddType oddType : OddType.values()) {

            Double eventDataOneOdd = eventDataOne.getOdds().get(oddType);
            Double eventDataTwoOdd = eventDataTwo.getOdds().get(oddType.contrary());

            // If event on one or both sides are missing oddType we cannot compare
            // them so we skip it.
            if(eventDataOneOdd == null || eventDataTwoOdd == null) continue;

            Double profit = Calc.profit(eventDataOneOdd, eventDataTwoOdd);

            //TODO: Temporary searching for negative arbs too
            if(profit > minimumLimit) {
                //We found arb!
                Arb arb = new Arb();
                arb.setProfit(profit);

                //Here it does not really matter from which side we take date/time
                arb.setDate(new Timestamp(eventDataOne.getDate().getTime()));

                Arb.Bookie bookie1 = new Arb.Bookie();
                Arb.Bookie bookie2 = new Arb.Bookie();
                arb.setBookieOne(bookie1);
                arb.setBookieTwo(bookie2);

                //Set bookie names
                arb.getBookieOne().setName(categoryDataOne.getBookieName());
                arb.getBookieTwo().setName(categoryDataTwo.getBookieName());

                //Set team names
                arb.getBookieOne().setTeamOneName(eventDataOne.getSideOne());
                arb.getBookieOne().setTeamTwoName(eventDataOne.getSideTwo());
                arb.getBookieTwo().setTeamOneName(eventDataTwo.getSideOne());
                arb.getBookieTwo().setTeamTwoName(eventDataTwo.getSideTwo());

                //Set category/subCategory
                arb.getBookieOne().setCategory(categoryDataOne.getCategory());
                arb.getBookieOne().setSubCategory(categoryDataOne.getSubCategory());
                arb.getBookieTwo().setCategory(categoryDataTwo.getCategory());
                arb.getBookieTwo().setSubCategory(categoryDataTwo.getSubCategory());

                //Set odds
                arb.getBookieOne().setOddType(oddType.stringValue());
                arb.getBookieOne().setOdd(eventDataOneOdd);
                arb.getBookieTwo().setOddType(oddType.contrary().stringValue());
                arb.getBookieTwo().setOdd(eventDataTwoOdd);

                resultArbs.add(arb);
            }
        }

        return resultArbs;
    }
}
