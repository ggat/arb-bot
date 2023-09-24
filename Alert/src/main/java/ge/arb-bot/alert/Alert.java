package ge.arb-bot.alert;

import ge.arb-bot.alert.notifications.TelegramNotification;

/**
 * Created by giga on 2/24/18.
 */
public class Alert {

    public static void broadcast(String message) {
        TelegramNotification.sendMessage(message);
    }
}
