package ge.shitbot.analyzer;

import ge.shitbot.analyzer.datatypes.CategoryData;
import ge.shitbot.analyzer.datatypes.EventData;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.core.datatypes.util.FileSerializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by giga on 2/16/18.
 */
public class SimpleAnalyzer {

    private static SimpleAnalyzer instance = null;

    protected SimpleAnalyzer() throws IOException, ClassNotFoundException {
        obtainTeamNameChains();
    }

    protected void obtainTeamNameChains() throws IOException, ClassNotFoundException {
        try {
            teamNameChains = (TeamNameChains) FileSerializer.fromFile(fileName);
        } catch (FileNotFoundException e) {
            teamNameChains = new TeamNameChains();
        }
    }

    public static SimpleAnalyzer getInstance() throws IOException, ClassNotFoundException {
        if(instance == null) {
            instance = new SimpleAnalyzer();
        }
        return instance;
    }

    protected TeamNameChains teamNameChains;

    public TeamNameChains getTeamNameChains() {
        return teamNameChains;
    }

    private static final String fileName = "chains.ser";

    public void reset() throws IOException, ClassNotFoundException {
        FileSerializer.toFile(fileName, new TeamNameChains());
        obtainTeamNameChains();
    }

    protected boolean updated;

    public List<Arb> findArbs(List<? extends CategoryData> categoryDatas) throws IOException {

        do {

            updated = false;

            for (CategoryData data1: categoryDatas) {
                for (CategoryData data2: categoryDatas) {
                    for (EventData eventData : data1.getEvents()) {
                        for (EventData eventData2 : data2.getEvents()) {
                            if(eventData.equals(eventData2) || data1.getBookieName().equals(data2.getBookieName())) continue;
                            boolean eventsMatch = eventsMatch(eventData, data1.getBookieName(), eventData2, data2.getBookieName());
                            //System.out.println("Aliosha: " );
                        }
                    }
                }
            }
        } while (updated);


        FileSerializer.toFile(fileName, teamNameChains);

        return new ArrayList<>();
    }

    protected boolean eventsMatch(EventData eventData1, String bookie1, EventData eventData2, String bookie2) {

        Long timeDiff = eventData1.getDate().getTime() - eventData2.getDate().getTime();
        boolean eventsSameTime = timeDiff == 0;

        if(eventsSameTime) {

            boolean side1Equals = namesEqual(bookie1, eventData1.getSideOne(), bookie2, eventData2.getSideOne());
            boolean side2Equals = namesEqual(bookie1, eventData1.getSideTwo(), bookie2, eventData2.getSideTwo());

            if(!side1Equals && !side2Equals) {
                return false;
            }

            if(!side1Equals) {
                updateTeamNames(bookie1, eventData1.getSideOne(), bookie2, eventData2.getSideOne());
            } else {
                updateTeamNames(bookie1, eventData1.getSideTwo(), bookie2, eventData2.getSideTwo());
            }

            return true;
        }

        return false;
    }

    protected boolean namesEqual(String bookie1, String name1, String bookie2, String name2) /*throws NameChainFragmentationException*/ {


        // TODO: Should we directly names as strings first?
        // If so how do we update persisted data
        if(name1.equals(name2)) {
            return true;
        }

        Map<String, String> nameListByBookie1 = teamNameChains.findFirst(bookie1, name1);
        Map<String, String> nameListByBookie2 = teamNameChains.findFirst(bookie2, name2);

        if(nameListByBookie1 == null && nameListByBookie2 == null) {
            return false;
        }

        String nameForBookie1;
        String nameForBookie2;

        if(nameListByBookie1 != null && nameListByBookie2 == null) {
            nameForBookie1 = nameListByBookie1.get(bookie1);
            nameForBookie2 = nameListByBookie1.get(bookie2);
        } else if(nameListByBookie1 == null/* && nameListByBookie2 != null*/) {
            nameForBookie1 = nameListByBookie2.get(bookie1);
            nameForBookie2 = nameListByBookie2.get(bookie2);

            //  Here if we know that other team name matches then these different chains must be merged, otherwise
            //  Two chains found are not for same team
            //  This means name chains where found for both bookies but in different chains
        } else if(nameListByBookie1 != nameListByBookie2) {
            return false;
        } else { // They are in the same chain
            return true;
        }

        return nameForBookie1 != null && nameForBookie1.equals(name1) && nameForBookie2 != null && nameForBookie2.equals(name2);
    }

    //Update names are considered to be called only when other side matched.
    protected void updateTeamNames(String bookie1, String name1, String bookie2, String name2) {
        /*Map<String, String> nameListByBookie1 = teamNameChains.findFirst(bookie1, name1);
        Map<String, String> nameListByBookie2 = teamNameChains.findFirst(bookie2, name2);*/

        //Without specifing exact bookie we have more chances to find chain for this particular team.
        Map<String, String> nameListByBookie1 = teamNameChains.findFirst(bookie1, name1);
        Map<String, String> nameListByBookie2 = teamNameChains.findFirst(bookie2, name2);

        if(nameListByBookie1 != null && nameListByBookie2 != null && nameListByBookie1 != nameListByBookie2) {
            //This is the case where team names were found for both bookies
            //But they are not in the same chain
            //
            // Because update team names is always called when other side did match
            // We can consider that they were added to different chains because, of lack of information at at point was available.
            // For example consider condition when, there is no chain that contains any of names from (name1, name2)
            // for corrsponding bookies and also there is no chain that incodes String equal for these names for any bookie.
            //
            // So we can merge these chains here.

            //Remove old chains
            teamNameChains.remove(nameListByBookie1);
            teamNameChains.remove(nameListByBookie2);

            //Add new merged chain
            teamNameChains.add(mergeChains(nameListByBookie1, nameListByBookie2));

            updated = true;
        } else if(nameListByBookie1 != null && nameListByBookie2 == null) {
            nameListByBookie1.put(bookie2, name2);
            updated = true;
        } else if(nameListByBookie1 == null && nameListByBookie2 != null) {
            nameListByBookie2.put(bookie1, name1);
            updated = true;
        } else if(nameListByBookie1 == null && nameListByBookie2 == null) {

            // This is place where we may create new chain for team that already exists for other bookies.
            HashMap<String, String> newChain = new HashMap<>();
            newChain.put(bookie1, name1);
            newChain.put(bookie2, name2);

            teamNameChains.add(newChain);
            updated = true;
        }
    }

    public Map<String, String> mergeChains(Map<String, String> chain1, Map<String, String> chain2) {

        Map<String, String> resultChain = new HashMap<>();

        for (Map.Entry<String, String> entry1 : chain1.entrySet()) {
            resultChain.put(entry1.getKey(), entry1.getValue());
        }

        for (Map.Entry<String, String> entry2 : chain2.entrySet()) {

            resultChain.put(entry2.getKey(), entry2.getValue());
        }

        return resultChain;
    }
}
