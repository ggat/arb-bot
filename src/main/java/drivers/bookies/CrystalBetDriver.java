package drivers.bookies;

import drivers.AbstractBookieDriver;
import drivers.BookieDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by giga on 9/13/17.
 */
public class CrystalBetDriver extends AbstractBookieDriver implements BookieDriver {

    String baseUrl = "https://www.crystalbet.com/Pages/StartPage.aspx";
    String user = "zurabinio";
    String password = "zuraba1234";

    public CrystalBetDriver(WebDriver webDriver) {
        super(webDriver);
    }

    protected void goToMainPage() {

        webDriver.get(baseUrl);
    }

    protected void goToAccountPage() {

        if(!isLoggedIn()) {
            login();
        }

        WebElement accountLink = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"ctl00_UpdatePanelLogin\"]/a[contains(@class, 'myaccaunt')]")));

        accountLink.click();
    }

    protected void login() {

        goToMainPage();

        //Input user name
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_UserName")).sendKeys(user);
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_Password")).sendKeys(password);
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_LoginButton")).click();

    }

    public boolean isLoggedIn(){

        try{
            webDriver.findElement(By.xpath("//*[@id=\"ctl00_MainLoginView_MainLoginStatus\"]")).isDisplayed();
        } catch (Throwable e) {
            return false;
        }

        return true;

    }

    public Long getBalance() {

        goToAccountPage();

        WebElement balanceElement = presenceOfElementLocated(By.xpath("//*[@id=\"LabelSportsBalance\"]"));

        String rawBalance = balanceElement.getText().trim().replaceAll("GEL", "").replaceAll(",", ".").trim();

        return Math.round(Double.parseDouble(rawBalance) * 100);
    }

    public void createBet(String category, String subCategory, String teamOneName, String teamTwoName, Double oddConfirmation) {

        if(!isLoggedIn()) {
            login();
        }

        // nav სპორტი
        ensureClick(By.xpath("//a[@id=\"ctl00_hlSports\"]"));
        // cat ფეხბურთი
        presenceOfElementLocated(By.xpath("//*[@id=\"x_menu_items_block\"]/a[contains(@onclick, 'DoSportTypePostBack(16)')]")).click();
        // category
        presenceOfElementLocated(By.xpath("//*[@id=\"ctl00_ctl00_ContentPlaceHolder1_ContentPlaceHolderSportHeader_UpdatePanelChampionats\"]//div[contains(@class, 'country-items-holder')]//div[contains(@class, 'new_sport_country1') and contains(text(), '"+ category +"')]")).click();
        // subCategory
        presenceOfElementLocated(By.xpath("//*[@id=\"ctl00_ctl00_ContentPlaceHolder1_ContentPlaceHolderSportHeader_UpdatePanelChampionats\"]//div[contains(@class, 'country-items-holder')]//div[contains(@class, 'new_sport_country1') and contains(text(), '"+ category +"')]/parent::div/following-sibling::div[contains(@class, 'new_sport_div')]//div[contains(@class, 'new_sport1') and contains(text(), '"+ subCategory +"')]")).click();

        //FIXME: If team names are too short or empty it will match lot of odd rows, most probably first row will be selected
        //FIXME: Currently we choose odds using td index which may change in future.
        //TODO: Add event date confirmation
        // rowWithOdds
        presenceOfElementLocated(By.xpath("//div[contains(@class, 'x_loop_title_bg') and contains(text(), '"+category+"') and contains(text(), '"+ subCategory +"')]/parent::div/following-sibling::div[contains(@class, 'x_loop_list')]/table/tbody/tr[contains(@class, 'x_loop_game_title_block')]/td[contains(@class, 'x_game_title')]//span[contains(text(), '"+teamOneName+"') and contains(text(), '"+teamTwoName+"')]/parent::td/parent::tr/td[6]")).click();

        //TODO: Add odd confirmation.

    }

    protected WebElement presenceOfElementLocated(By by) {
        return (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected void ensureClick(By by) {
        ensureClick(by, 1500L);
    }

    protected void ensureClick(By by, Long timeout) {
        try {
            // nav სპორტი
            WebElement element = presenceOfElementLocated(by);

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            element.click();

            // It seems we find element before page refreshes after login and then it not in current DOM anymore
            // so we try to find it again.
        } catch (StaleElementReferenceException e) {

            presenceOfElementLocated(by).click();
        }
    }
}
