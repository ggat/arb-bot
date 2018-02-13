package ge.shitbot.bot.selenium;

/**
 * Created by giga on 10/14/17.
 */
public abstract class By extends org.openqa.selenium.By {

    //Hiding xpath
    public static org.openqa.selenium.By xpath(String xpathExpression) {

        xpathExpression = translateXpath(xpathExpression);

        System.out.println("xpath: " + xpathExpression);

        return org.openqa.selenium.By.xpath(xpathExpression);
    }

    protected static String translateXpath(String xpathExpression) {
        return xpathExpression
                .replaceAll("havingClass\\((['\"][^\\)]*['\"])\\)","contains(concat(' ', normalize-space(@class), ' '), $1 )");
    }

    public static void main(String[] args) {

        //String rawXpath = "havingClass('kutu')";
        //String rawXpath = "/html/body/div/div[contains(@class, 'main-content')]//div[contains(@class, 'sport-categories-box')]/div/div/ul[havingClass('sport-list') and havingClass('subcategory')]//span[havingClass('categoryName') and contains(string(.), 'ესპანეთი')]/ancestor::li[1]";
        //String rawXpath = "//*[@id=\"sport-content\"]//div[contains(@class, 'country-level')]//div[contains(@class, 'panel-body')]//div[contains(@class, 'league-level')]//span[contains(string(.), 'ესპანეთი') and contains(string(.), 'ლა ლიგა') ]/ancestor::div[contains(@class, 'league-level')]/following-sibling::div//div[contains(@class, 'panel-body')]/ul/li[contains(@class, 'single-event')]//li[contains(@class, 'period-item')]/div[havingClass('event') and havingClass('name') and contains(string(.), 'ლევანტე') and contains(string(.), 'ხეტაფე')]";
        String rawXpath = "//div[havingClass('head1_1_new_sub')]/div[havingClass('head1_1_new_sub1') and havingClass('en')]";

        org.openqa.selenium.By result = By.xpath(rawXpath);

        System.out.println("AAA, " + result);
    }
}
