package ge.shitbot.analyzer;

import ge.shitbot.analyzer.datatypes.CategoryData;
import ge.shitbot.analyzer.datatypes.ComparableChain;
import ge.shitbot.analyzer.datatypes.EventData;
import ge.shitbot.core.Calc;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.core.datatypes.OddType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/20/17.
 */
public class Analyzer {

    public List<Arb> findArbs(List<ComparableChain> comparableChains) {

        List<Arb> resultArbs = new ArrayList<>();

        for(ComparableChain comparableChain : comparableChains) {

            for (int i = 0; i < comparableChain.size(); i++) {

                for (int k = i + 1; k < comparableChain.size(); k++) {

                    CategoryData categoryDataOne = comparableChain.get(i);
                    CategoryData categoryDataTwo = comparableChain.get(k);

                    if (categoryDataOne != categoryDataTwo) {

                        for(int iEvent = 0; iEvent < categoryDataOne.getEvents().size(); iEvent++ ) {
                            for(int kEvent = 0; kEvent < categoryDataTwo.getEvents().size(); kEvent++ ) {

                                EventData eventDataOne = categoryDataTwo.getEvents().get(iEvent);
                                EventData eventDataTwo = categoryDataTwo.getEvents().get(kEvent);

                                //Check if dates of categories match
                                if(eventDataOne.getDate() == eventDataTwo.getDate()) {

                                    //Check if any of team names match
                                    if(eventDataOne.getSideOne().equals(eventDataTwo.getSideOne()) ||
                                            eventDataOne.getSideTwo().equals(eventDataTwo.getSideTwo())
                                            ) {

                                        //This fucking should mean that events matched.
                                        //Start comparing odds of these two events
                                        for (OddType oddType : OddType.values()) {

                                            Double eventDataOneOdd = eventDataOne.getOdds().get(oddType);
                                            Double eventDataTwoOdd = eventDataTwo.getOdds().get(oddType.contrary());

                                            Double profit = Calc.profit(eventDataOneOdd, eventDataTwoOdd);

                                            if(profit > 0) {
                                                //We found arb!
                                                Arb arb = new Arb();
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
