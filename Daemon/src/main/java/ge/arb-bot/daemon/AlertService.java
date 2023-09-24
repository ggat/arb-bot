package ge.arb-bot.daemon;

import ge.arb-bot.alert.Alert;
import ge.arb-bot.core.datatypes.Arb;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by giga on 2/24/18.
 */
public class AlertService {

    private static Logger logger = LoggerFactory.getLogger(AlertService.class);

    private static List<Arb> lastArbs;
    private static Date messageLastSend = new Date();
    private static long pingTime = 30;

    protected static void checkIfLongTimeAgo(List<Arb> arbs) {
        //if(notSending) {
            long diff = new Date().getTime() - messageLastSend.getTime();

            if(TimeUnit.MILLISECONDS.toMinutes(diff) > pingTime) {

                messageLastSend = new Date();

                Alert.broadcast("No interesting arbs in last "+ pingTime +" min. \n" +
                        "Arbs=" + arbs.size());
            }
        //}
    }

    public static void propagateArbs(List<Arb> arbs) {

        List<Arb> goodArbs = arbs.stream().filter(v -> v.getProfit() > 1.2).collect(Collectors.toList());

        if(lastArbs != null && CollectionUtils.isEqualCollection(lastArbs, goodArbs)) {
            logger.info("Arbs did not change, no updates are propagated.");
            checkIfLongTimeAgo(arbs);
            return;
        }

        if(arbs.size() == 0) {
            logger.info("No arbs to send.");
            checkIfLongTimeAgo(arbs);
            return;
        }

        arbs.sort(new Comparator<Arb>() {
            @Override
            public int compare(Arb o1, Arb o2) {

                Double profit1 = o1.getProfit();
                Double profit2 = o2.getProfit();

                if(profit1.equals(profit2))
                    return 0;
                return profit1 < profit2 ? 1 : -1;
            }
        });

        if(arbs.get(0).getProfit() <= 1.7) {
            logger.info("Very low arbs not sending message at all.");
            checkIfLongTimeAgo(arbs);
            return;
        }

        lastArbs = goodArbs;

        StringBuffer buffer = new StringBuffer();
        arbs.forEach(v -> {

            SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm MMM d");
            Timestamp date = v.getDate();

            Double profitD = v.getProfit();

            String dateTime = simpleDate.format(date);
            String profit = (new DecimalFormat("#0.00")).format(profitD);

            String bookieOneName = getShotName(v.getBookieOne().getName());
            String bookieTwoName = getShotName(v.getBookieTwo().getName());

            //"13.63 CrystalBet - AdjaraBet 12:00 Jul 14";

            if(profitD > 1.2) {

                String line = String.format("%-5s %3s - %-3s %-13s\n", profit, bookieOneName, bookieTwoName, dateTime) + "";

                //buffer.append("" + profit  + " " + bookieOneName + " - " + bookieTwoName + " " + dateTime + "\n");
                buffer.append(line);
            }
        });

        long lowProfitCount = arbs.size() - goodArbs.size();
        buffer.append(lowProfitCount + " more..");

        messageLastSend = new Date();
        Alert.broadcast(buffer.toString());
    }

    private static String getShotName(String name) {

        String shotName = name;

        switch (name) {
            case "AdjaraBet":
                shotName = "ADJ";
                break;

            case "BetLive":
                shotName = "BTL";
                break;

            case "CrystalBet":
                shotName = "CRY";
                break;

            case "EuropeBet":
                shotName = "EUR";
                break;

            case "LiderBet":
                shotName = "LDR";
                break;

            case "CrocoBet":
                shotName = "CRO";
                break;

            default:
                shotName = name;
                break;
        }

        return shotName;
    }
}
