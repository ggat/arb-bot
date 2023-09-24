package ge.arb-bot.alert.notifications;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
/*import ge.arb-bot.datasources.datatypes.Arb;
import ge.arb-bot.datasources.exceptions.DataSourceException;
import ge.arb-bot.datasources.source.ArbDataSource;
import ge.arb-bot.datasources.source.MainDataSource;
import javafx.collections.FXCollections;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;*/
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by giga on 11/14/17.
 */
public class TelegramNotification {

    public static void sendMessage(String data) {

        TelegramBot bot = new TelegramBot("495154722:AAEUUmDOHCp6mLpcCKPA-XxA055DKnMncjw");
        GetUpdates getUpdates = new GetUpdates().limit(100).offset(0).timeout(0);

        // sync
        GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
        List<Update> updates = updatesResponse.updates();

        //System.out.println("Size: " + updates.size());

        /*if(updates.size() > 0){
            System.out.println(updates.get(0));
        }*/

        //SendMessage request = new SendMessage("438642777", data)
        SendMessage request = new SendMessage("-262116666", data)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true);
                //.replyToMessageId(1)
                //.replyMarkup(new ForceReply());

        // sync
        SendResponse sendResponse = bot.execute(request);
        boolean ok = sendResponse.isOk();
        int errorCode = sendResponse.errorCode();
        Message message = sendResponse.message();
        String description = sendResponse.description();

        /*System.out.println("Response status: " + ok);
        System.out.println("Response errorCode: " + errorCode);
        System.out.println("Response message: " + message);
        System.out.println("Response description: " + description);*/
    }

    /*public static void main(String[] args) {

        Integer intervalSeconds = 900;

        Timer time = new Timer();

        time.schedule(new TimerTask() {
            @Override
            public void run() {
                List<Arb> arbs = null;
                try {
                    ArbDataSource<Arb> source = new MainDataSource<>();
                    arbs = FXCollections.observableArrayList(source.getArbs());
                } catch (DataSourceException e) {
                    e.printStackTrace();
                    return;
                }

                StringBuffer buffer = new StringBuffer();


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

                System.out.println("Created buffer: " + buffer);

                TelegramNotification.sendMessage(buffer.toString());
            }
        }, 0, intervalSeconds * 1000);
    }*/
}
