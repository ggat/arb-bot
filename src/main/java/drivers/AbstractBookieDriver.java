package drivers;

import org.openqa.selenium.WebDriver;

/**
 * Created by giga on 9/13/17.
 */
public class AbstractBookieDriver {

    protected WebDriver webDriver;

    public AbstractBookieDriver(WebDriver webDriver){
        this.webDriver = webDriver;
    }
}
