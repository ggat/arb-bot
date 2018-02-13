package ge.shitbot.bot.drivers.bookies;

import ge.shitbot.bot.OddType;
import ge.shitbot.bot.drivers.BookieDriver;
import ge.shitbot.bot.drivers.BookieDriverGeneral;
import ge.shitbot.bot.exceptions.UnknownOddTypeException;
import ge.shitbot.bot.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;

/**
 * Created by giga on 9/13/17.
 */
public class CrystalBetDriver extends BookieDriverGeneral implements BookieDriver {

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

        //goToMainPage();

        //Input user name
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_UserName")).sendKeys(user);
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_Password")).sendKeys(password);
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_LoginButton")).click();

    }

    protected void changeLang() {

        WebElement langDropDown = presenceOfElementLocated(ge.shitbot.bot.selenium.By.xpath("//*[@id='ctl00_UpdatePanelLanguages']/div[havingClass('head1_1_new')]"));

        hoverAndClick(langDropDown);

        WebElement engButton = langDropDown.findElement(ge.shitbot.bot.selenium.By.xpath("//div[havingClass('head1_1_new_sub')]/div[havingClass('head1_1_new_sub1') and havingClass('en')]/label/a"));
        hoverAndClick(engButton);
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

    protected void createBetImpl(String category, String subCategory, String teamOneName, String teamTwoName, String oddType, Double amount, Double oddConfirmation) throws UnknownOddTypeException {

        // +2 cause first is TD is 'date' and second is 'team names'
        int oddTypeIndex = getOddTypeIndex(oddType);

        goToMainPage();

        // In case of Crystal bet we first login and only than change/choose language cause otherwise
        // It is reset to default lang after login
        if(!isLoggedIn()) {
            login();
        }

        // After login refreshes the page change lang elements are loaded again by JS probably and that's
        // why we need to retry if they get staled.
        try {
            changeLang();
        }
        catch(org.openqa.selenium.StaleElementReferenceException ex)
        {
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            changeLang();
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
        //presenceOfElementLocated(By.xpath("//div[contains(@class, 'x_loop_title_bg') and contains(text(), '"+category+"') and contains(text(), '"+ subCategory +"')]/parent::div/following-sibling::div[contains(@class, 'x_loop_list')]/table/tbody/tr[contains(@class, 'x_loop_game_title_block')]/td[contains(@class, 'x_game_title')]//span[contains(text(), '"+teamOneName+"') and contains(text(), '"+teamTwoName+"')]/parent::td/parent::tr/td["+oddTypeIndex+"]")).click();
        presenceOfElementLocated(By.xpath("//div[contains(@class, 'x_loop_title_bg') and contains(text(), '"+category+"') and contains(text(), '"+ subCategory +"')]/parent::div/following-sibling::div[contains(@class, 'x_loop_list')]/div[havingClass('game-table')]/div[contains(@class, 'x_loop_game_title_block')]/div[contains(@class, 'x_game_title')]//span[contains(text(), '"+teamOneName+"') and contains(text(), '"+teamTwoName+"')]/parent::div/parent::div/div[havingClass('Snatch')]["+oddTypeIndex+"]")).click();

        WebElement stakeInput = presenceOfElementLocated(By.xpath("//*[@id=\"TextBoxAmount\"]"));

        stakeInput.clear();

        stakeInput.sendKeys(presentDouble(amount));

        //TODO: Add odd confirmation.

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
