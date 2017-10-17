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
                .replaceAll("hasClass\\((['\"].*['\"])\\)","contains(concat(' ', normalize-space(@class), ' '), $1 )");
    }

    public static void main(String[] args) {
        org.openqa.selenium.By result = By.xpath("hasClass('kutu')");

        System.out.println("AAA, " + result);
    }
}
