package ge.arb-bot.scraper;

import ge.arb-bot.scraper.bookies.*;
import ge.arb-bot.scraper.datatypes.Category;
import ge.arb-bot.scraper.exceptions.ScraperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.List;

/**
 * Created by giga on 11/21/17.
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    final String outFileName = "freshData.tmp";

    public static void main(String[] args) throws Exception {
        Main self = new Main();

        self.getDataAndWriteToFile();
        //self.readSerializedData();
    }

    private void readSerializedData() throws Exception {
        FileInputStream fis = new FileInputStream(outFileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        HashMap<Long, List<? extends Category>> readObjects = (HashMap<Long, List<? extends Category>>) ois.readObject();
        ois.close();
    }

    private void getDataAndWriteToFile() throws Exception {
        HashMap<Long, List<? extends Category>> liveData = new HashMap<>();
        liveData.put(25L, new AdjaraBetScraper().getFreshData());
        liveData.put(28L, new EuropeBetScraper().getFreshData());
        liveData.put(30L, new CrocoBetScraper().getFreshData());
        liveData.put(29L, new LiderBetScraper().getFreshData());
        liveData.put(27L, new CrystalBetScraper().getFreshData());

        File yourFile = new File(outFileName);
        yourFile.createNewFile();

        FileOutputStream fos = new FileOutputStream(outFileName);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(liveData);
        oos.close();
    }
}
