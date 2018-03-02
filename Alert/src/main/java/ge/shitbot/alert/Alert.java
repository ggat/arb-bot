package ge.shitbot.alert;

import ge.shitbot.alert.notifications.TelegramNotification;

/**
 * Created by giga on 2/24/18.
 */
public class Alert {

    public static void broadcast(String message) {
        TelegramNotification.sendMessage(message);
    }
}
