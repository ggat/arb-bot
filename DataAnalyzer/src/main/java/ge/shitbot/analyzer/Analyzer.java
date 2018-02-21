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
        return findArbs(comparableChains, 0d);
    }

    public List<Arb> findArbs(List<ComparableChain> comparableChains, Double minimumLimit) {

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

                                Long timeDiff = eventDataOne.getDate().getTime() - eventDataTwo.getDate().getTime();

                                //Check if dates of categories match
                                if(timeDiff == 0) {

                                    //Check if any of team names match
                                    if(eventDataOne.getSideOne().equals(eventDataTwo.getSideOne()) ||
                                            eventDataOne.getSideTwo().equals(eventDataTwo.getSideTwo())
                                            ) {

                                        resultArbs.addAll(AnalyzerFunctions.compareForArb(categoryDataOne, eventDataOne, categoryDataTwo, eventDataTwo));
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
