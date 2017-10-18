package ge.shitbot.bot.drivers.bookies;

import ge.shitbot.bot.OddType;
import ge.shitbot.bot.drivers.BookieDriver;
import ge.shitbot.bot.drivers.BookieDriverGeneral;
import ge.shitbot.bot.exceptions.UnknownOddTypeException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;

/**
 * Created by giga on 9/13/17.
 */
public class LiderBetDriver extends BookieDriverGeneral implements BookieDriver {

    String baseUrl = "https://www.lider-bet.com/web/ka/promotions/?page=sagzuri";
    String user = "i.gatenashvili";
    String password = "camaross69";

    public LiderBetDriver(WebDriver webDriver) {
        super(webDriver);
    }

    protected void goToMainPage() {

        webDriver.get(baseUrl);
    }

    protected void login() {

        goToMainPage();

        //Input user name
        webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/table/tbody/tr/td[2]/span/div/div[2]/input")).sendKeys(user);
        webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/table/tbody/tr/td[2]/span/div/div[3]/input")).sendKeys(password);
        webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/table/tbody/tr/td[2]/span/div//*[@onclick='auth_submiter(this)']")).click();

    }

    public boolean isLoggedIn() {
        goToMainPage();

        try{
            webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/table/tbody/tr/td[2]/div/div[5]/a[@class='my_room']")).isDisplayed();
        } catch (Throwable e) {
            return false;
        }

        return true;
    }

    public Long getBalance() {

        login();

        WebElement balanceElement = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[2]/div/table/tbody/tr/td[2]/div/div[4]/a[@class='user-amount-block']")));

        String rawBalance = balanceElement.getText().trim().replaceAll("GEL", "").replaceAll(",", ".").trim();

        return Math.round(Double.parseDouble(rawBalance) * 100);
    }

    public void createBet(String category, String subCategory, String teamOneName, String teamTwoName, String oddType, Double amount, Double oddConfirmation) throws UnknownOddTypeException {

        //FIXME: If team names are too short or empty it will match lot of odd rows, most probably first row will be selected
        //FIXME: Currently we choose odds using td index which may change in future.
        //TODO: Add event date confirmation
        //TODO: Add odd confirmation.*/

        int oddTypeIndex = getOddTypeIndex(oddType);

        if(!isLoggedIn()) {
            login();
        }

        ensureClick(By.xpath("/html/body/div[contains(@class, 'menu lb_nav')]/div/ul/li/a[contains(string(.), 'სპორტი')]"));

        presenceOfElementLocated(By.xpath("/html/body/table/tbody/tr/td[contains(@class, 'tote-content')]/ul[contains(@class, 'sport-name-list')]/li[contains(string(.), 'ფეხბურთი')]")).click();

        presenceOfElementLocated(By.xpath("/html/body/table/tbody/tr/td[contains(@class, 'tote-content')]/ul[contains(@class, 'country-name-list')]/li/a/span[contains(text(), '"+category+"')]")).click();

        presenceOfElementLocated(By.xpath("/html/body/table/tbody/tr/td[contains(@class, 'tote-content')]/ul[contains(@class, 'country-name-list')]/li/a/span[contains(text(), '"+category+"')]/parent::a/parent::li/parent::ul/li/div[contains(@class, 'nav')]/div[contains(@class, 'category-item')]/a[contains(string(.), '"+subCategory+"')]")).click();

        // We need to scroll down, otherwise game/event list is not inserted into page.
        windowScroll(0L, 1000L);

        //Prick particular odd by confirming category/subCategory/event
        presenceOfElementLocated(By.xpath("//div[contains(@class, 'game-category-list')]/div[contains(@class, 'category_wraper')]/div[contains(@class, 'category_wraper_line')]/div[contains(@class, 'category_wraper_title') and contains(string(.), '"+category+"') and contains(string(.), '"+subCategory+"')]/parent::div/following-sibling::div[contains(@class, 'game-list')]//div[contains(@class, 'match-list')]/div/div/div/div[contains(@class, 'game_ev') and contains(text(), '"+teamOneName+"') and contains(text(), '"+teamTwoName+"')]/parent::div/parent::div/following-sibling::div/table/tbody/tr/td["+oddTypeIndex+"]")).click();

        WebElement stakeInput = presenceOfElementLocated(By.xpath("/html/body/table/tbody/tr/td/div/div/div[contains(@class, 'ticket_bottom_panel')]/table/tbody/tr/td/div/div[contains(@class, 'fsoni1_wraper')]/following-sibling::input[contains(@class, 'fsoni') and contains(@onkeyup, 'calc_bet_1')]"));

        stakeInput.clear();

        stakeInput.sendKeys(presentDouble(amount));

    }

    //Non-zero based index
    protected int getOddTypeIndex(String oddType) throws UnknownOddTypeException {

        String[] arr = {
                OddType.Win,
                OddType.Draw,
                OddType.Loose,
                OddType.WinOrDraw,
                OddType.DrawOrLoose,
                OddType.WinOrLoose,
                "",
                "",
                OddType.Under25,
                "",
                OddType.Over25,
                OddType.Yes,
                OddType.No};

        int index = Arrays.asList(arr).indexOf(oddType);

        if( index == -1 ) {
            throw new UnknownOddTypeException("Odd type [" + oddType + "]");
        }

        return index + 1;
    }
}
