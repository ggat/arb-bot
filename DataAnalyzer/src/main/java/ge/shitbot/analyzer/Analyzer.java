package ge.shitbot.analyzer;

import ge.shitbot.analyzer.datatypes.CategoryData;
import ge.shitbot.analyzer.datatypes.ComparableChain;
import ge.shitbot.analyzer.datatypes.EventData;
import ge.shitbot.core.Calc;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.core.datatypes.OddType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/20/17.
 */
public class Analyzer {

    Logger logger = LoggerFactory.getLogger(Analyzer.class);

    public List<Arb> findArbs(List<ComparableChain> comparableChains) {

        List<Arb> resultArbs = new ArrayList<>();

        for(ComparableChain comparableChain : comparableChains) {

            logger.debug("Working on chain({}): {}", comparableChain.size(), comparableChain.toString());

            for (int i = 0; i < comparableChain.size(); i++) {

                for (int k = i + 1; k < comparableChain.size(); k++) {

                    CategoryData categoryDataOne = comparableChain.get(i);
                    CategoryData categoryDataTwo = comparableChain.get(k);

                    logger.debug("Comparing CategoryDatas {} {}", categoryDataOne, categoryDataTwo);

                    if (categoryDataOne != categoryDataTwo) {

                        for(int iEvent = 0; iEvent < categoryDataOne.getEvents().size(); iEvent++ ) {
                            for(int kEvent = 0; kEvent < categoryDataTwo.getEvents().size(); kEvent++ ) {

                                EventData eventDataOne = categoryDataOne.getEvents().get(iEvent);
                                EventData eventDataTwo = categoryDataTwo.getEvents().get(kEvent);

                                logger.debug("Comparing Events {} {}", eventDataOne, eventDataTwo);

                                Long timeDiff = eventDataOne.getDate().getTime() - eventDataTwo.getDate().getTime();

                                //Check if dates of categories match
                                if(timeDiff == 0) {

                                    //Check if any of team names match
                                    if(eventDataOne.getSideOne().equals(eventDataTwo.getSideOne()) ||
                                            eventDataOne.getSideTwo().equals(eventDataTwo.getSideTwo())
                                            ) {

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
                                            if(profit > -7 || true) {
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
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return resultArbs;
    }
}
