package ge.shitbot.datasources.notifications;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import ge.shitbot.datasources.datatypes.Arb;
import ge.shitbot.datasources.exceptions.DataSourceException;
import ge.shitbot.datasources.source.ArbDataSource;
import ge.shitbot.datasources.source.MainDataSource;
import javafx.collections.FXCollections;

import java.text.DecimalFormat;
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

        SendMessage request = new SendMessage("438642777", data)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true)
                .replyToMessageId(1)
                .replyMarkup(new ForceReply());

        // sync
        SendResponse sendResponse = bot.execute(request);
        boolean ok = sendResponse.isOk();
        Message message = sendResponse.message();
    }

    public static void main(String[] args) {

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

                arbs.forEach(v -> {
                    Double profit = v.getProfit();

                    if(profit > 1.2) {
                        buffer.append(" " + (new DecimalFormat("#0.00")).format(v.getProfit()));
                    }
                });

                System.out.println("Created buffer: " + buffer);

                TelegramNotification.sendMessage(buffer.toString());
            }
        }, 0, intervalSeconds * 1000);
    }
}
