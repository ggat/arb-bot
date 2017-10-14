package ge.shitbot.bot.drivers;

import org.openqa.selenium.WebDriver;

import java.io.Closeable;

/**
 * Created by giga on 9/13/17.
 */
public class AbstractBookieDriver implements Closeable {

    protected WebDriver webDriver;

    public AbstractBookieDriver(WebDriver webDriver){
        this.webDriver = webDriver;
    }

    public void close() {
        webDriver.close();
    }
}
