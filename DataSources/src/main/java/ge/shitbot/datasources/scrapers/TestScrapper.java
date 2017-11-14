package ge.shitbot.datasources.scrapers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by giga on 11/12/17.
 */
public class TestScrapper {

    private static Logger logger = LoggerFactory.getLogger(TestScrapper.class);

    String searchUrl = "https://www.crystalbet.com/Pages/Sports.aspx";
    String sessionId;
    String postData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl00%24HiddenFieldChampionatId=REPLACE_MEEEE&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&__EVENTARGUMENT=REPLACE_MEEEE&__VIEWSTATE=4Ccer4UsqpQeWnpjWv%2BA3Kda%2F%2FZFMr8lgg9K3KZJFHE%2BzOSAFL2eKqNPJT9v9V0ui4c%2FOGcbrAwcyPjo7NWIVzO2C%2B%2BEmPfFDuQurqtAt344Hz5ojlwVYWF2JIYUQmTR4TihVIN3Ui4%2FEWmCOZ%2BrkRFNr2rM5uAPpQUGt6qb8ULXc1gKdjg%2B2NW1j0tJm99bLPOt%2F3aIgk8suP8UtA5tOP8iL1lp1BYx6zjPBenkUeVOrFqOZcDSlpGkuTJ3BEgAxzsh4D0ID3M4NW5fZZra9qT2Klfy8KZMN1Cpzl5QGiM%2FzPPO%2F7O2vj%2FTqVYKTKQv%2Bee0DMEHJPUD87m%2BdLymPpn2FMYvSA67Pkl6PG%2BldRxQ7qEIaBo6Vguch3no15tN79qxmP3GeINf4UF7Hmtq5jgVwvHJgOeJg%2FLwxXlP3z%2BMnFLwil2sREdjdFbW7xlfHsFWHMRdwvlGoDwTVX%2FwiQGErfkUr9i9MP9j2eQ3RSkyvker3ybyC3lhm%2FHpElqWwez2IH7wgrzArBt4uAbeYwoFT5V4r29f%2F06rFrm8E%2F5q6gvgT4i4nGvr63aOGsbR4jKciXjPZAXUplDSVTCMsdgT01gXA%2FcXdhzvBfLSt0yICrmmjv3r4RjzO8ufNmJ8KBEFKZeB1GSjM2FFL4zoLf%2F4rJzpoBM8BRbhHAeJsbrx4mbgmJRSjR9Fzgj7m6As8ArF0Lvq3gFeCau4W%2FASFIfN%2BlB42fiOsANoHqGAauS45sNXLAc0VFNY4BvTVAn9LhC9mB8gYWAMvhAglMNWTcdztXRd2ZFjmMUsqh8I5MfqqnbfceKXFFrJL0xMYdeF%2F9sdXGNBVdh06iQ6bv6TzGxlM5Vw49uyE1AhCrbgohEE6my7mxmOWuLhlot37BGDg%2Bbw%2BskBmHy9SNnr5H676A3yOcq8ahYuHSMajZ2zquPxfFzOxmBizmmojdCzM%2B5vX7DJa%2B4YcxdJlnG4KJgkIbkDvm4n%2F9TcWxyrTtPYj8jJ4%2F4TsaGrnqy7hB%2F4zh6EpZl%2BuRQcbbUoDg3KPQhA8ZB4OQP%2FY8oXK9e6EJSYdv9L1NQ9C0HIsoajRUZtU3qwilvsICX1nWSl76hkcVmLxs5D3yTq91VPMB7CmOQ%2FvHJgomdZUmCpQTZLst2dq8RpMhUCRehnM6K%2BnncvEyXxWpxfpXdyW6M%2BkXZ5JwzhqNhGgjTLLAhJO2Jjj6YuZrWOV%2FuWOcB8LJ%2BN%2FnzZkNI8lzabL0dD0PL46d2Lrk3ypUltCi63GLx%2FOTenkHyFpI2gm23MTb0S%2FRWo6HHkLN52KULpTtZ%2B%2Fl6a%2BgEBmuYltsC3YHjPlZtXVwfzaez7h%2BDANUvFYzlF1rMuAVC4%2BxkTjesONvS3RbigUVvZDEARaiKzfwM92c%2BPlAWfTlHY9zvCK9LMlUeFDTkTHCyXsvw3qoug0OcVzgpiclxCGJ%2F10ievc1kXj5CN0lw01xTH%2Fty8H8gm4KMDcleSIBh%2BH4o5CYZoIL8qz6BCLJhQdPzXnvcC1LgkGparWzxLBF1FnFlfraoNhujc7v3JgP8%2F9%2F0EA4OsNPaLujSuEeB4bWELAwUnm4W7iT1OlVSg4mCzkij6uWCdCCUpmFI%2FpWJDo3wuDcNDrPQGvAwwr6fEhjDOzdXq%2FOJzXAL7iZBO6pgNJG%2B0zvN0tJ01nqgliNdRAKV2Te1kicUp%2FlVnsRUV6llm0EPycx%2BJFd9LqRBPXjyYZwXtJqTPGF%2FDJsyvfs%2BIHlJi45q8x7%2B6NFjwvvZ%2FVjdpke5%2BTMlQEumqaMVhNSjV18fdtuLuzS6yxgdqMByukgDkf5Whdp1h30UkPurDho8vYmTnulfS5sPAtM9QVp3%2ByI4ZsXBK9uiwczq4lnbpkoe2EgcylY1IM0GngILw3P1eUz3sp33df8J3uAaqmiBC5q8GxGJVZqKqUXyA9EST72IcE%2B0C1dG4QXckmcsXntKq206dMv1ep79dyfpaUmhl9ZmUMWVvEISUr8hIe0s5QSBsckHTivWjKTYEiXba0Iaoadcpyt75lpcy0BW15KIJnn1IKDSXundGSLkNTncX%2Bvkta9eeXGYfU3MSdNFc%2BX5eSLCrspz1m0P%2FtpVR0yf2zBO7iqzt9u8m8bjB9dzJjmgl2qCGXZjEkqnFI7UO2YCyAMcKqRtZPo%2FlfCdUSK7eRLg8ST147log6StZux%2FbNYtjmug99nffikUPWG6av6MFO4HZ%2B5iaEIw1RsnCUpIBF5O8dGOQWqKO%2BYCsegTB%2BdS1KeuS0LnpdtR%2Ba5sIWMRkatFqr0V6vhpM0HG%2FnmQ6WuLJYIqDQ25DBqe45pWhFSZr5WsgLQTs2tP3KJB4TSJx0tMWrkzJLeKb78mus4QsTlI1Fr10mt7V%2BqTdsn6DWmG%2BjAq73Pcy9GhgypNaaKncu%2FGIhbC8tZ3nKEZlla5ip57tN5DwSg30cS59UEaKFV6R8UFCKvkTc2erqnoJPzQUSK4hzCKSVO8f9sJrWCWm8OACsU0A54%2BQVlK%2BSreSdk0Je9W0KEgeQ56m1f4vyrqTmF4meI%2B6p2vRnHFX1GlTjwyM6MauRFxO9r%2FsqgGHSaePz%2F2EfANSPBRmg2BtiKXX84vw%2BkPPrREgJ4Hu2b39PtqoW%2FdSao8qc68D0sn0R%2BgiSESqUUMdus29HDYkh49F%2B7ziviZh6Sm0A5hsvzUeZdcMquJTE58QxI9h1uaI1Xj%2FfRB9ODlI%2FUvuEcAtNNPH2z4SlRJdg1gL951AB8UoQynkH%2BrsjSRA0lGjrmFYR48bfzOk8PyUDNWHtr%2B1z6EpSNwgCbWl9xuXj%2FM2J90Rl2QekIlZqhVg7VtgSzEkd2VOoDGgnjTJivi1jjzMSMGxZKiZVP%2FMHVPL%2FCHNSh6DbO%2FAvveKu195VlAjzkWV74ilq4J%2B0rjFkYEm%2F5f3n%2FxVAzkG25KQhO6bRQ6rc5Z%2FN1djGm74p6OzfELNEZUhci0A3qvS4D%2BQ7DpB8bd2g%2FIzDh4%3D&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";

    private class Category {
        private String name;
        private Integer id;
        private List<Category> subCategories = new ArrayList<>();

        public Category(String name, Integer id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    private class Subcategory extends Category {
        public Subcategory(String name, Integer id) {
            super(name, id);
        }
    }

    public TestScrapper() {

        logger.info("AAAAAAAAAAAAAAAA");

        /*try {

            String rootUrl = "https://www.crystalbet.com/Pages/StartPage.aspx";
            String championatsListPostData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateSportTypes&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=zuraba1234&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=ggatenashvili&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl00%24HiddenFieldChampionatId=-1&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=ggatenashvili&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateSportTypes&__EVENTARGUMENT=16&__VIEWSTATE=2MPsEK4Mg5rZnJ0QFeyDC6hgsHENrdFonozQG5yckwd3i%2BEHf6oRInHKG1GNXwgYnquUV8x8EoS6YLjFcndmMe5fm2pvMcX8bYMZlhJb8tsPzhlldQl7McdXepqYpMGVWKU%2FqJdd2ugnnPAJAEf9UJNj90KV8fBChNpJTaM7hOycyHhYcv8gmMQWWpO6RXWJkOjTGIL4Pi1kqWMhPsTae65%2F1gj%2BSQbuxGMvASnIoFeSbQKJonNRwgVzVGG2Z5WMY7Km390QnpUmv4KYYBeCTv1cTIF2uy9IZvUUWgiZclgE7a7%2FnQNDpqpIcfniziIK3UnqarB7%2BKS9X3dCY79tXOAyfE%2B0CapCtiyugDDCfWG8KqMjUrjx5i%2BOSy2dvZjFmJUPur10CXFVmiVax30T0ijhHGLGr9ARVEtJahu98DC0PMYHRRIrqp95%2Fv%2BcCeOU6RNvxgIGeugzrfAmGkVPf%2F2e%2FNrdqrDDsTZpsx95qtEpWN84kQtXsEYoQ%2FTlI6UPlCB9cHxDO3Mbt5Hkcs0bnM75ARdhoqNwtbyUPgC7UIK0sStUalM6Ay3HekD17pTm3Ahjlkc1DCa7ivZhk8Q2Op4AyHhpNaLV5uKaaPsGO%2FwUbJNRMNaTEbTGmbCKBS7gTqin2QK6eNRs3%2FxVmMo0byRVG3yDj9T%2Feseb2d%2FSOGdEEzapB4Fh8YZ0jopUBOSwJ0eZCG3HOEHSFDgEChWKIn1Z389gmmAvV3jM6QKkACFVdCGIEx9vWSYX3fKhwp9lkdaa7yGYeZBHMsUGJ%2FcNWzxYcGT%2FEAGPXMqh5TzxCGnkdnn6lsXPrGFx3K5CBdUoeMJVWwVLmPSSi8%2B02YP4TWB45uWEWQl%2FNTOA1rOmcuzr74pCV2JREaW1G3FzaEA%2FD13Rir5cS7jBaacyka00VQ1ml0EtFggGPVNvbPZjZCaM3pFPoEPerDyHHHML2oM0ajQQpSIiozBIIhWP0Eqi5ImHI2w%2B8dmTYB5TGxmultRITvIjtch1rDa8LJYSUqsCuPP0duzPf%2BKzrRAu%2FHmRfMVpsDUdfqlzH2EJqnkZu1eqCOJ4u9NH%2B5jDpSDe92OnXo4aYrVuPqiG2UKSHqyQZxpMB2RNZuAnlkgmioawlgnWplX%2FxyFnVeqi7TypU1XuJXXYGZ%2BR1oxZFcLaTpnzXpIONa3K1G0f7nnUpBIlTIicEeIDPIoC1QLh413U4j58zkXEqo8LIAl60fDTkoxDw%2Fzw1UNBOCc7u4thmuOVQM%2BbAxAj0RzJVU%2B%2BR5xvjubkSlY%2B2QkDmX0A6ZKFM4IzwOjnB3CNXLUqze4gaBWUyGBCFYcuPEqcVu3wIbPMpAEKt9tOnWZ%2Bl%2FEfOpQwyORFviBV5Co7zTgbQp%2Bl4p%2BVoo757gDIwQMPmrFkOYXbNVnIhGNvPOrihxdSuijZ3%2FICr9gvlJd0SEiTtDI8%2B%2ByDLh821gAqE0Cu%2Fx0364eLq5CuWSLd%2FQ3dQTjdyI9C6hk369YgXnK%2FcB6ifvmF6o9hc963qdaePHrhH%2F2wxVeuWu%2FKi%2FCvd%2Fi1PgVu0fnvqVZJMUKGHbyjZiwQE5nHwJqABjDt2qqzKvkjgvZVGWFfqsPi4hrLDkyMgYn40cHsw9A1wc2dJPd0jZKkPaMwpPN717nXWU5IPBhgSlLOuwaVybul93XwiXewSDd186lgyVS2ybmrTajegCzy5CMcu5BA4wd%2BwzjUv0aHMNa84UyWLuE5%2BCG1RXlUMz6UjgGa1dQyuE%2B57dOCOiPYwcQm3xpWhP7RzwWUJPDSNdYmHFqd7DRJaeBH6e1J%2B6C6ik9gLVzAsf9%2Fvb9U5gbnWwdrZI8UrNRCVfPREvfKqzNI9wmgm42ltPZfgTTbssIh4qelYYNIa%2BrJLJg40AYlEJa40Z%2FXVPuMfljbbCKn8iwPNWs%2BZgKrCmiYt3rdv6XQT0dtfoM4EVBjS%2BE581vEmYbFLD1AQ%2BhHRAWIn23pHzkAfXO8AzMEfi4lOdCfZGPa6c0gY%2FpsGEfYWT5dgSbNxcTpWdXrd5yl7by6cGQtdAX704%2BTrPYJ%2F2S8L7rz8ax7xPem2P1iXLzsuzm%2BZUL8iWvufR432SBWbMrHcpfi4J5xQWmTuZd2bptwvIl3D9OPmapBf1WHS538ouhqHJ2QbK24RyiaaONxFU251vJfIOwMVy92Z5WTyykAhZbpc8B2k9cLPzi%2BGhohTnky8FNZUqKS63%2FNwsB6NRDmynJyICVZsTVeVzzXtY6G%2Fr%2FhsIXjGy2Cs2YyT7I1oTVO0It2kQLQENxQP2j5pLdHHBFBP58dwo2O8%2Fb5e2k%2BRL5W9MwuYOcUTz1mAJjvlKQwNccGRPQoqfgJUe%2FheJLCIl5wBG1nL0wv%2F9jro2yqPo2GhILjT1mU5RyGHKXbNV02eODIbLLrnhEUAggN2nnTq752yiLzWcw%2B2DLPS2SK2txStY24LGt6NU35NpuWcltqNgCgaWm9bzG%2BZyNT91H%2BN6kz27Fbc7w0YnSh6clFFaB28x3jUAtJOKr2VjXqZB0CsLcosQIwWug3atpvwq7p4hpTHTKOQGPjT2zkdFL18p6OweEULavLmBmFwmCuv2D7TXZRMZqdOSP%2FBA%2B2SJJgiLwODRRcmtQyxg%2B%2FwWdq02Yp3PmC91P2tyS5%2BbzTFdqstq%2BVcx21p0Ik6yBtoVK4Pk1RlNbkrE20fn8nHtLJrpmbTbp2JesfpgaFP4e%2FE%2FxNPbH5x20t6f7EmrWIbwkw0PNrNDwOKLuloc4%2BlXZX3K0bjunqT2V3wmcIgqd9U392Z%2BudcUAbxZ5Hjtju5EvdTxT6Qbk78hQRhZsHEDUrLzGtNPp857U5fU9L9Qy9OU0FBZIo5zDYskdgRCF4Iy0Oj%2F7G6Ba5DIEsPEbcaWciwXytLkOdd9mojEiJ7AUGL30fNQK60EJw5oFwzSFxxw8cJzyQ2qEUfkRYNbj60M%2BZrVZlgGVW5o5m8SxsLASQculQfVTNhgY2ofk%2FPDyIBit5LglvTN3u%2FII9JnM7Kb8nT85WKM4JwwVAgAsk6tuXlNbHXTCH6OSM9%2BFYi5qa%2BHoVCoz02JzvP3Nxh4Yted3Y75aUcsrsu7Qh74INinYqPQLkwojoP3yaKT%2FV2wznlKcCfCEWgWP5ZuRSVXvHCSN7YIpAXUrEEd6JQIz6JiU%2BT1P2niDd1hNXsxAZ7bLW4bgQeRDDiXo3R1mWgRM9zsdk%2BRA1ebKJCTpM7HsdGzMDKegrzGoiIIgmRn%2FAjdsJFoKgso%2FsyPsYOvDyqfEGo8ADmceD5VaJltP8RX2sTtWREhkeCmPDdRNGpLhKLtyizfSAX30cNs05lz5umF9wn6lcLu%2FfrLE%2FYNWtIZfiSL6UfbPTyQDoNW4HfgGSFyjfrm9qT%2Bc8wMduISf9%2BS6B1RlR6q0gNUGnEJ8gQVQXZc3Gjhb5LLXIECeoB7Bc%2FrFO06STHG8wvAMKQFkCqEL3qy4RVIW5hFOIJ9i%2FrTmcdQVDlWvvvAPJObO5sXXsQAHvniExI6TfYc0AO1YGKj5kXrcv6NJnF3E8bQr27I1ACRtdI2w6Ois0KCk9EVDsLx3HZxy0a2cStzA6gRa%2BYJ9G9eDUxWTCgB8K2DG2i5txIilXfjUHTPQyCCRSCryOxXzlTgFaECpDcHASOun6lU%2Bf1RIZPHk%2FkG%2BjFBXe1xo%2BOUCEUtjbOwRv67iGeLAMb3bKCkswhZBd218oxkoAiuwixZ0r1%2FtEvrcDQqCnkkzHXo%2F7V31Ad%2FdVpLG0DKeAhKjgR%2BTxY55wEKDwQlTzaZr8cbHZHFtMJDjzrL%2B%2FkTcSVpnNYH1jRZXLLHudUILA096b4TU9iqxG5nlYPP%2FtJxZV5%2BSUTSE9lbAjsuuDnrYFzLtHbT06%2BMewQFBrBdJV1NbyMUzwsVRldssjSNSci7kaE8kT6JZJY32Zp%2F6FBtJKMEInnzkjYRL2GRlpZ3GW1Id0SAv%2FHirsDrPWo7vTX22BDsBQcpMEaOWO4pGNKrT%2BZP%2B8qmDXDgDe13gShl7no3I8lQktWUWZ1NTf8W4amucgbDPo6hrptPWOTmpazAiceGqQKpakd46CmO2zWbdlPFoTM5GQPCrxdeln2SIFsT7ZbHrO5VDyFq9G81ca9rcQP%2BeqNgfbxz5N2Qc24vgT0QwYyYADk7JzWaVgCg9WvhZH&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";


            sessionId = Jsoup.connect(rootUrl).method(Connection.Method.GET).execute().cookie("ASP.NET_SessionId");

            Document championats = Jsoup.connect(searchUrl).cookie("ASP.NET_SessionId", sessionId).requestBody(championatsListPostData).post();

            parseChampionats(championats);


        }catch(Exception e){
            e.printStackTrace();
        }*/
    }

    private List<Category> categories = new ArrayList<>();

    protected void parseChampionats(Document champ) throws IOException{

        Elements eventList = champ.select(".new_sport_country");

        for(Element row : eventList) {

            String name = row.selectFirst(".new_sport_country1").textNodes().get(0).text();
            String id = row.selectFirst(".new_sport_country3").attr("onclick").split(":")[1].split("\"")[0];

            Category category = new Category(name, Integer.parseInt(id));

            Elements subcategoryDiv = row.parent().select(".new_sport_div > .new_sport");

            for (Element subCategoryElement : subcategoryDiv) {
                Category subCategory = new Category(subCategoryElement.selectFirst(".new_sport1").textNodes().get(0).text(), Integer.parseInt(subcategoryDiv.attr("championat-data")));

                category.subCategories.add(subCategory);

                System.out.println(category.getName() + ": " + category.getId());
                System.out.println("SUB: " + subCategory.getName() + ": " + subCategory.getId());

                Document events = Jsoup.connect(searchUrl).cookie("ASP.NET_SessionId", sessionId)
                        .requestBody(postData.replace("REPLACE_MEEEE", subCategory.getId().toString())).post();

                parseEvents(events);
            }

            categories.add(category);


        }

        System.out.println(categories.size());
    }

    protected void parseEvents(Document events) {

        Elements eventList = events.select(".x_loop_game_title_block");

        for(Element row : eventList) {

            try {
                Element dateCell = row.selectFirst(".x_game_date");

                String eventDate = dateCell.selectFirst("font").text();
                String eventTime = dateCell.selectFirst("span.time").text();
                String eventTitle = row.selectFirst(".x_game_title > span").text();
                Elements oddCells = row.select(".x_loop_res");

                System.out.print(eventDate +" " + eventTime + " | " + eventTitle );

                for(Element oddCell : oddCells) {
                    System.out.print(" " + oddCell.text());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("");
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
