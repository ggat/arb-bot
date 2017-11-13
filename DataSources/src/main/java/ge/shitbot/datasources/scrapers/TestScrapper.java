package ge.shitbot.datasources.scrapers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by giga on 11/12/17.
 */
public class TestScrapper {

    public TestScrapper() {

        try {
            String searchUrl = "https://www.crystalbet.com/Pages/Sports.aspx";
            String rootUrl = "https://www.crystalbet.com/Pages/StartPage.aspx";

            String postData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl00%24HiddenFieldChampionatId=13434&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&__EVENTARGUMENT=13435&__VIEWSTATE=4Ccer4UsqpQeWnpjWv%2BA3Kda%2F%2FZFMr8lgg9K3KZJFHE%2BzOSAFL2eKqNPJT9v9V0ui4c%2FOGcbrAwcyPjo7NWIVzO2C%2B%2BEmPfFDuQurqtAt344Hz5ojlwVYWF2JIYUQmTR4TihVIN3Ui4%2FEWmCOZ%2BrkRFNr2rM5uAPpQUGt6qb8ULXc1gKdjg%2B2NW1j0tJm99bLPOt%2F3aIgk8suP8UtA5tOP8iL1lp1BYx6zjPBenkUeVOrFqOZcDSlpGkuTJ3BEgAxzsh4D0ID3M4NW5fZZra9qT2Klfy8KZMN1Cpzl5QGiM%2FzPPO%2F7O2vj%2FTqVYKTKQv%2Bee0DMEHJPUD87m%2BdLymPpn2FMYvSA67Pkl6PG%2BldRxQ7qEIaBo6Vguch3no15tN79qxmP3GeINf4UF7Hmtq5jgVwvHJgOeJg%2FLwxXlP3z%2BMnFLwil2sREdjdFbW7xlfHsFWHMRdwvlGoDwTVX%2FwiQGErfkUr9i9MP9j2eQ3RSkyvker3ybyC3lhm%2FHpElqWwez2IH7wgrzArBt4uAbeYwoFT5V4r29f%2F06rFrm8E%2F5q6gvgT4i4nGvr63aOGsbR4jKciXjPZAXUplDSVTCMsdgT01gXA%2FcXdhzvBfLSt0yICrmmjv3r4RjzO8ufNmJ8KBEFKZeB1GSjM2FFL4zoLf%2F4rJzpoBM8BRbhHAeJsbrx4mbgmJRSjR9Fzgj7m6As8ArF0Lvq3gFeCau4W%2FASFIfN%2BlB42fiOsANoHqGAauS45sNXLAc0VFNY4BvTVAn9LhC9mB8gYWAMvhAglMNWTcdztXRd2ZFjmMUsqh8I5MfqqnbfceKXFFrJL0xMYdeF%2F9sdXGNBVdh06iQ6bv6TzGxlM5Vw49uyE1AhCrbgohEE6my7mxmOWuLhlot37BGDg%2Bbw%2BskBmHy9SNnr5H676A3yOcq8ahYuHSMajZ2zquPxfFzOxmBizmmojdCzM%2B5vX7DJa%2B4YcxdJlnG4KJgkIbkDvm4n%2F9TcWxyrTtPYj8jJ4%2F4TsaGrnqy7hB%2F4zh6EpZl%2BuRQcbbUoDg3KPQhA8ZB4OQP%2FY8oXK9e6EJSYdv9L1NQ9C0HIsoajRUZtU3qwilvsICX1nWSl76hkcVmLxs5D3yTq91VPMB7CmOQ%2FvHJgomdZUmCpQTZLst2dq8RpMhUCRehnM6K%2BnncvEyXxWpxfpXdyW6M%2BkXZ5JwzhqNhGgjTLLAhJO2Jjj6YuZrWOV%2FuWOcB8LJ%2BN%2FnzZkNI8lzabL0dD0PL46d2Lrk3ypUltCi63GLx%2FOTenkHyFpI2gm23MTb0S%2FRWo6HHkLN52KULpTtZ%2B%2Fl6a%2BgEBmuYltsC3YHjPlZtXVwfzaez7h%2BDANUvFYzlF1rMuAVC4%2BxkTjesONvS3RbigUVvZDEARaiKzfwM92c%2BPlAWfTlHY9zvCK9LMlUeFDTkTHCyXsvw3qoug0OcVzgpiclxCGJ%2F10ievc1kXj5CN0lw01xTH%2Fty8H8gm4KMDcleSIBh%2BH4o5CYZoIL8qz6BCLJhQdPzXnvcC1LgkGparWzxLBF1FnFlfraoNhujc7v3JgP8%2F9%2F0EA4OsNPaLujSuEeB4bWELAwUnm4W7iT1OlVSg4mCzkij6uWCdCCUpmFI%2FpWJDo3wuDcNDrPQGvAwwr6fEhjDOzdXq%2FOJzXAL7iZBO6pgNJG%2B0zvN0tJ01nqgliNdRAKV2Te1kicUp%2FlVnsRUV6llm0EPycx%2BJFd9LqRBPXjyYZwXtJqTPGF%2FDJsyvfs%2BIHlJi45q8x7%2B6NFjwvvZ%2FVjdpke5%2BTMlQEumqaMVhNSjV18fdtuLuzS6yxgdqMByukgDkf5Whdp1h30UkPurDho8vYmTnulfS5sPAtM9QVp3%2ByI4ZsXBK9uiwczq4lnbpkoe2EgcylY1IM0GngILw3P1eUz3sp33df8J3uAaqmiBC5q8GxGJVZqKqUXyA9EST72IcE%2B0C1dG4QXckmcsXntKq206dMv1ep79dyfpaUmhl9ZmUMWVvEISUr8hIe0s5QSBsckHTivWjKTYEiXba0Iaoadcpyt75lpcy0BW15KIJnn1IKDSXundGSLkNTncX%2Bvkta9eeXGYfU3MSdNFc%2BX5eSLCrspz1m0P%2FtpVR0yf2zBO7iqzt9u8m8bjB9dzJjmgl2qCGXZjEkqnFI7UO2YCyAMcKqRtZPo%2FlfCdUSK7eRLg8ST147log6StZux%2FbNYtjmug99nffikUPWG6av6MFO4HZ%2B5iaEIw1RsnCUpIBF5O8dGOQWqKO%2BYCsegTB%2BdS1KeuS0LnpdtR%2Ba5sIWMRkatFqr0V6vhpM0HG%2FnmQ6WuLJYIqDQ25DBqe45pWhFSZr5WsgLQTs2tP3KJB4TSJx0tMWrkzJLeKb78mus4QsTlI1Fr10mt7V%2BqTdsn6DWmG%2BjAq73Pcy9GhgypNaaKncu%2FGIhbC8tZ3nKEZlla5ip57tN5DwSg30cS59UEaKFV6R8UFCKvkTc2erqnoJPzQUSK4hzCKSVO8f9sJrWCWm8OACsU0A54%2BQVlK%2BSreSdk0Je9W0KEgeQ56m1f4vyrqTmF4meI%2B6p2vRnHFX1GlTjwyM6MauRFxO9r%2FsqgGHSaePz%2F2EfANSPBRmg2BtiKXX84vw%2BkPPrREgJ4Hu2b39PtqoW%2FdSao8qc68D0sn0R%2BgiSESqUUMdus29HDYkh49F%2B7ziviZh6Sm0A5hsvzUeZdcMquJTE58QxI9h1uaI1Xj%2FfRB9ODlI%2FUvuEcAtNNPH2z4SlRJdg1gL951AB8UoQynkH%2BrsjSRA0lGjrmFYR48bfzOk8PyUDNWHtr%2B1z6EpSNwgCbWl9xuXj%2FM2J90Rl2QekIlZqhVg7VtgSzEkd2VOoDGgnjTJivi1jjzMSMGxZKiZVP%2FMHVPL%2FCHNSh6DbO%2FAvveKu195VlAjzkWV74ilq4J%2B0rjFkYEm%2F5f3n%2FxVAzkG25KQhO6bRQ6rc5Z%2FN1djGm74p6OzfELNEZUhci0A3qvS4D%2BQ7DpB8bd2g%2FIzDh4%3D&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";

            String sessionId = Jsoup.connect(rootUrl).method(Connection.Method.GET).execute().cookie("ASP.NET_SessionId");
            Document doc = Jsoup.connect(searchUrl).cookie("ASP.NET_SessionId", sessionId).requestBody(postData).post();

            Elements eventList = doc.select(".x_loop_game_title_block");

            for(Element row : eventList) {

                Element dateCell = row.selectFirst(".x_game_date");

                String eventDate = dateCell.selectFirst("font").text();
                String eventTime = dateCell.selectFirst("span.time").text();
                String eventTitle = row.selectFirst(".x_game_title > span").text();
                Elements oddCells = row.select(".x_loop_res");

                System.out.print(eventDate +" " + eventTime + " | " + eventTitle );

                for(Element oddCell : oddCells) {
                    System.out.print(" " + oddCell.text());
                }

                System.out.println("");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected String tidy(String content) {
        Tidy tidy = new Tidy();
        tidy.setCharEncoding(Configuration.UTF8);
        //tidy.setXHTML(true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        tidy.parse(new ByteArrayInputStream(content.getBytes()), outputStream);

        return (new String(outputStream.toByteArray())).trim();
    }

    protected String removeLines(String initial) {
        String[] lines = initial.split("\n");

        List<String> lineList = new ArrayList<>(Arrays.asList(lines));

        lineList.remove(0);
        lineList.remove(lineList.size()-1);
        lineList.remove(lineList.size()-1);
        lineList.remove(lineList.size()-1);

        String[] newLines = lineList.toArray(new String[lineList.size()]);

        return String.join("\n", newLines);
    }

    public static void main(String[] args) {
        new TestScrapper();
    }
}
