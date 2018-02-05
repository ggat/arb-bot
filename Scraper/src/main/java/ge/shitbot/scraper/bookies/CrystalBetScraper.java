package ge.shitbot.scraper.bookies;

import ge.shitbot.core.datatypes.OddType;
import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import javax.swing.text.html.Option;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by giga on 11/12/17.
 */
public class CrystalBetScraper implements BookieScraper {

    private static Logger logger = LoggerFactory.getLogger(CrystalBetScraper.class);

    String searchUrl = "https://www.crystalbet.com/Pages/Sports.aspx";
    String rootUrl = "https://www.crystalbet.com/Pages/StartPage.aspx";
    String sessionId;

    public CrystalBetScraper() {

    }

    public List<Category> getFreshData() throws ScraperException {
        logger.info("Start scrapping of CrystalBet");

        try {
            //String championatsListPostData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateSportTypes&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=zuraba1234&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=ggatenashvili&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl00%24HiddenFieldChampionatId=-1&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=ggatenashvili&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateSportTypes&__EVENTARGUMENT=16&__VIEWSTATE=2MPsEK4Mg5rZnJ0QFeyDC6hgsHENrdFonozQG5yckwd3i%2BEHf6oRInHKG1GNXwgYnquUV8x8EoS6YLjFcndmMe5fm2pvMcX8bYMZlhJb8tsPzhlldQl7McdXepqYpMGVWKU%2FqJdd2ugnnPAJAEf9UJNj90KV8fBChNpJTaM7hOycyHhYcv8gmMQWWpO6RXWJkOjTGIL4Pi1kqWMhPsTae65%2F1gj%2BSQbuxGMvASnIoFeSbQKJonNRwgVzVGG2Z5WMY7Km390QnpUmv4KYYBeCTv1cTIF2uy9IZvUUWgiZclgE7a7%2FnQNDpqpIcfniziIK3UnqarB7%2BKS9X3dCY79tXOAyfE%2B0CapCtiyugDDCfWG8KqMjUrjx5i%2BOSy2dvZjFmJUPur10CXFVmiVax30T0ijhHGLGr9ARVEtJahu98DC0PMYHRRIrqp95%2Fv%2BcCeOU6RNvxgIGeugzrfAmGkVPf%2F2e%2FNrdqrDDsTZpsx95qtEpWN84kQtXsEYoQ%2FTlI6UPlCB9cHxDO3Mbt5Hkcs0bnM75ARdhoqNwtbyUPgC7UIK0sStUalM6Ay3HekD17pTm3Ahjlkc1DCa7ivZhk8Q2Op4AyHhpNaLV5uKaaPsGO%2FwUbJNRMNaTEbTGmbCKBS7gTqin2QK6eNRs3%2FxVmMo0byRVG3yDj9T%2Feseb2d%2FSOGdEEzapB4Fh8YZ0jopUBOSwJ0eZCG3HOEHSFDgEChWKIn1Z389gmmAvV3jM6QKkACFVdCGIEx9vWSYX3fKhwp9lkdaa7yGYeZBHMsUGJ%2FcNWzxYcGT%2FEAGPXMqh5TzxCGnkdnn6lsXPrGFx3K5CBdUoeMJVWwVLmPSSi8%2B02YP4TWB45uWEWQl%2FNTOA1rOmcuzr74pCV2JREaW1G3FzaEA%2FD13Rir5cS7jBaacyka00VQ1ml0EtFggGPVNvbPZjZCaM3pFPoEPerDyHHHML2oM0ajQQpSIiozBIIhWP0Eqi5ImHI2w%2B8dmTYB5TGxmultRITvIjtch1rDa8LJYSUqsCuPP0duzPf%2BKzrRAu%2FHmRfMVpsDUdfqlzH2EJqnkZu1eqCOJ4u9NH%2B5jDpSDe92OnXo4aYrVuPqiG2UKSHqyQZxpMB2RNZuAnlkgmioawlgnWplX%2FxyFnVeqi7TypU1XuJXXYGZ%2BR1oxZFcLaTpnzXpIONa3K1G0f7nnUpBIlTIicEeIDPIoC1QLh413U4j58zkXEqo8LIAl60fDTkoxDw%2Fzw1UNBOCc7u4thmuOVQM%2BbAxAj0RzJVU%2B%2BR5xvjubkSlY%2B2QkDmX0A6ZKFM4IzwOjnB3CNXLUqze4gaBWUyGBCFYcuPEqcVu3wIbPMpAEKt9tOnWZ%2Bl%2FEfOpQwyORFviBV5Co7zTgbQp%2Bl4p%2BVoo757gDIwQMPmrFkOYXbNVnIhGNvPOrihxdSuijZ3%2FICr9gvlJd0SEiTtDI8%2B%2ByDLh821gAqE0Cu%2Fx0364eLq5CuWSLd%2FQ3dQTjdyI9C6hk369YgXnK%2FcB6ifvmF6o9hc963qdaePHrhH%2F2wxVeuWu%2FKi%2FCvd%2Fi1PgVu0fnvqVZJMUKGHbyjZiwQE5nHwJqABjDt2qqzKvkjgvZVGWFfqsPi4hrLDkyMgYn40cHsw9A1wc2dJPd0jZKkPaMwpPN717nXWU5IPBhgSlLOuwaVybul93XwiXewSDd186lgyVS2ybmrTajegCzy5CMcu5BA4wd%2BwzjUv0aHMNa84UyWLuE5%2BCG1RXlUMz6UjgGa1dQyuE%2B57dOCOiPYwcQm3xpWhP7RzwWUJPDSNdYmHFqd7DRJaeBH6e1J%2B6C6ik9gLVzAsf9%2Fvb9U5gbnWwdrZI8UrNRCVfPREvfKqzNI9wmgm42ltPZfgTTbssIh4qelYYNIa%2BrJLJg40AYlEJa40Z%2FXVPuMfljbbCKn8iwPNWs%2BZgKrCmiYt3rdv6XQT0dtfoM4EVBjS%2BE581vEmYbFLD1AQ%2BhHRAWIn23pHzkAfXO8AzMEfi4lOdCfZGPa6c0gY%2FpsGEfYWT5dgSbNxcTpWdXrd5yl7by6cGQtdAX704%2BTrPYJ%2F2S8L7rz8ax7xPem2P1iXLzsuzm%2BZUL8iWvufR432SBWbMrHcpfi4J5xQWmTuZd2bptwvIl3D9OPmapBf1WHS538ouhqHJ2QbK24RyiaaONxFU251vJfIOwMVy92Z5WTyykAhZbpc8B2k9cLPzi%2BGhohTnky8FNZUqKS63%2FNwsB6NRDmynJyICVZsTVeVzzXtY6G%2Fr%2FhsIXjGy2Cs2YyT7I1oTVO0It2kQLQENxQP2j5pLdHHBFBP58dwo2O8%2Fb5e2k%2BRL5W9MwuYOcUTz1mAJjvlKQwNccGRPQoqfgJUe%2FheJLCIl5wBG1nL0wv%2F9jro2yqPo2GhILjT1mU5RyGHKXbNV02eODIbLLrnhEUAggN2nnTq752yiLzWcw%2B2DLPS2SK2txStY24LGt6NU35NpuWcltqNgCgaWm9bzG%2BZyNT91H%2BN6kz27Fbc7w0YnSh6clFFaB28x3jUAtJOKr2VjXqZB0CsLcosQIwWug3atpvwq7p4hpTHTKOQGPjT2zkdFL18p6OweEULavLmBmFwmCuv2D7TXZRMZqdOSP%2FBA%2B2SJJgiLwODRRcmtQyxg%2B%2FwWdq02Yp3PmC91P2tyS5%2BbzTFdqstq%2BVcx21p0Ik6yBtoVK4Pk1RlNbkrE20fn8nHtLJrpmbTbp2JesfpgaFP4e%2FE%2FxNPbH5x20t6f7EmrWIbwkw0PNrNDwOKLuloc4%2BlXZX3K0bjunqT2V3wmcIgqd9U392Z%2BudcUAbxZ5Hjtju5EvdTxT6Qbk78hQRhZsHEDUrLzGtNPp857U5fU9L9Qy9OU0FBZIo5zDYskdgRCF4Iy0Oj%2F7G6Ba5DIEsPEbcaWciwXytLkOdd9mojEiJ7AUGL30fNQK60EJw5oFwzSFxxw8cJzyQ2qEUfkRYNbj60M%2BZrVZlgGVW5o5m8SxsLASQculQfVTNhgY2ofk%2FPDyIBit5LglvTN3u%2FII9JnM7Kb8nT85WKM4JwwVAgAsk6tuXlNbHXTCH6OSM9%2BFYi5qa%2BHoVCoz02JzvP3Nxh4Yted3Y75aUcsrsu7Qh74INinYqPQLkwojoP3yaKT%2FV2wznlKcCfCEWgWP5ZuRSVXvHCSN7YIpAXUrEEd6JQIz6JiU%2BT1P2niDd1hNXsxAZ7bLW4bgQeRDDiXo3R1mWgRM9zsdk%2BRA1ebKJCTpM7HsdGzMDKegrzGoiIIgmRn%2FAjdsJFoKgso%2FsyPsYOvDyqfEGo8ADmceD5VaJltP8RX2sTtWREhkeCmPDdRNGpLhKLtyizfSAX30cNs05lz5umF9wn6lcLu%2FfrLE%2FYNWtIZfiSL6UfbPTyQDoNW4HfgGSFyjfrm9qT%2Bc8wMduISf9%2BS6B1RlR6q0gNUGnEJ8gQVQXZc3Gjhb5LLXIECeoB7Bc%2FrFO06STHG8wvAMKQFkCqEL3qy4RVIW5hFOIJ9i%2FrTmcdQVDlWvvvAPJObO5sXXsQAHvniExI6TfYc0AO1YGKj5kXrcv6NJnF3E8bQr27I1ACRtdI2w6Ois0KCk9EVDsLx3HZxy0a2cStzA6gRa%2BYJ9G9eDUxWTCgB8K2DG2i5txIilXfjUHTPQyCCRSCryOxXzlTgFaECpDcHASOun6lU%2Bf1RIZPHk%2FkG%2BjFBXe1xo%2BOUCEUtjbOwRv67iGeLAMb3bKCkswhZBd218oxkoAiuwixZ0r1%2FtEvrcDQqCnkkzHXo%2F7V31Ad%2FdVpLG0DKeAhKjgR%2BTxY55wEKDwQlTzaZr8cbHZHFtMJDjzrL%2B%2FkTcSVpnNYH1jRZXLLHudUILA096b4TU9iqxG5nlYPP%2FtJxZV5%2BSUTSE9lbAjsuuDnrYFzLtHbT06%2BMewQFBrBdJV1NbyMUzwsVRldssjSNSci7kaE8kT6JZJY32Zp%2F6FBtJKMEInnzkjYRL2GRlpZ3GW1Id0SAv%2FHirsDrPWo7vTX22BDsBQcpMEaOWO4pGNKrT%2BZP%2B8qmDXDgDe13gShl7no3I8lQktWUWZ1NTf8W4amucgbDPo6hrptPWOTmpazAiceGqQKpakd46CmO2zWbdlPFoTM5GQPCrxdeln2SIFsT7ZbHrO5VDyFq9G81ca9rcQP%2BeqNgfbxz5N2Qc24vgT0QwYyYADk7JzWaVgCg9WvhZH&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";
            String championatsListPostData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateSportTypes&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=zuraba1234&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=zurabinio&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl00%24HiddenFieldChampionatId=-1&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl01%24HiddenFieldChampionatId=51869&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl02%24HiddenFieldChampionatId=13434&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl03%24HiddenFieldChampionatId=31549&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl04%24HiddenFieldChampionatId=13441&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl05%24HiddenFieldChampionatId=21680&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl06%24HiddenFieldChampionatId=13445&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl07%24HiddenFieldChampionatId=48987&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl08%24HiddenFieldChampionatId=13448&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl09%24HiddenFieldChampionatId=13452&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl10%24HiddenFieldChampionatId=23585&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl11%24HiddenFieldChampionatId=13455&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl12%24HiddenFieldChampionatId=14320&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=zurabinio&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateSportTypes&__EVENTARGUMENT=16&__VIEWSTATE=EKaSR7FN0QQXq%2BBHizJZ79av8KcwXmHIVw0UNm3rmnZoL2XaAemE%2F59ji8La8jixy8edsSt%2BkaQOokRBHEHxCNGNdlydr8XDi0qi%2FKSFWVDQWTNEVu6LgDaH%2B5MvAI6H5uuyxVY4YDGtRyTti%2BtdFJVQQggY%2FwMHefvzSX%2FyhMGenL4S1iHjXon0JpWJrgSDyThFZrfNtWdnH5%2F80cFXW4bkDEPAhAy1%2FQ7Di%2FXYrKJ08Evc8rH5Tw199o%2FD2yGmQGLX2n%2Bb%2BXHhVD59P6DLncjdY1iQ4G1ZOBIAAh7aX%2BftzUZNlc32y5sb4lcbkN5JjKM36pTIGEUDeX3%2FBTSWhipl%2Be5uQ3JK6Bn9jgY0Z6DMIocpgRH64pNu0QAxo3FP%2F%2FTKTRvh2%2BESQnEn5alPcffKPIyGuVTHcumUf7KF6KOsP9QoQd35db5QXek8JL7ahSXK0lKEZQ92PFkt%2FTK8PIAK1KiUZ6H38%2Fz4Rp6b9gvwskxtMWAUE5HTHTRN4L84VCOJptxXQiQN6HKAAMyjHS%2F8kZzRo5Oc8Q02t2boqpZy32uGPVYCUqIt26ok%2BY6KiCPlEM38jg6xCvtP%2FceBH9PR15D8zv4d2JvdSDTu0tHyNwtGd0lz8QB%2BXWwE2P0hawtUAkhevYEutQXgmiKIxLhVsy3leWr02b3mzgt2KtYx2wXwQO4EMgZZEDNgV7iZGLaKgNv8TrJG1eugYh%2Fn%2FMJec0fNRmSYWv2frSznCWO4gA2xOc6tTN%2BZ1T%2BXhY68iG22lYix7Hzf%2FoBNoY36OlPz0TV%2BzvPa7QdueUUA%2FjTdiziv4NF5RZ%2FGH2EFtxVC0Qj42AAbVjSjjkG67E%2FHgtg6qf79HZxIQ5gSRpimh%2BkRBaYtubnZEm%2BTq3Zxg5%2FjwgRYLbjHzXLRlRp2E%2BOeqPypA4%2FgQfN6lIFaeoQJyP1KAKJQLd7yiBexFeSsUdRI89J1v3qWzo1FFTdT0bmiL84%2BlBwWRLd%2F0HVzTlCyoN0hmq8L4ZFYgryGs%2BD08OH3CFoWUYG0pDwsy2UpEprzZkRRk7zFIEXn21xzY7gXg2pySDTHsFoV3qWvTAAawwZSlvDf0jIWyOGjPUFXeLFOFDDtMH4N%2FqiIwWrUAxE66NwZsETxTVqezN%2BnEJu5upkX0ssOi%2FRoj1fnXum2%2BFVFkHTTOgRnN7vc4c23M%2F%2BX0X8xYZOYnAkulrqtegyDdBeMWSvkS5MWGkFyAJ10ppWEQJD0SuFuuT31KOw4cDzEwjhpaTXsLX6ZGVK1wrdOcn3m%2B2g0uQX74G%2FlnOkU0zXSrEB45pUI%2FSSIrW%2B9PR0zjwbakjtGKNilv6K2aLhRQQdQJZ8KZ2rQeeO6n3aMvt3Vya1WnAo8664j%2Fu%2FmYtOoDGpDWFSUvpnvNEYWoeJIBdqAn2o8yfmh2xQIZEy8W4r5iymJZ1jzTCpVbPfqVFdZgMIuh4jx3kZ6PXk2QFlJd66RI%2BnLdZJsRfio2Vf8C60wLH%2FbYZUNdRdaBXQQX00O58N6PQbWjmkykeEHaafGEAfKMN%2BNvItf9NJ68ug1wp2xDug7T1Qx8B46pOT1kQclBs%2FKuvCXgkGkKlmQwaYJpYJm%2F%2Fcpgwq5cHQFpSqwHbXl7l9dA6NCm8o8wjInSAHgmQadTwWO0wgZc7dFWw3yOLy2t%2Ba5OKfTPwRPy8xwOYQCIo4pxog%2BbCPJvthdbTE5qYES6Byk9bqvwn9oyVLWqgyTGav6UWaezeuqOjWuQ0ZB2T2XhR%2BhFGGDawNnycesF8uIAZPjH3teL7CZvx2eml8Cp%2FHog8wogrgTJFGAbeYATOiONYnSjXA6NlTbxpESfeJA61EXS2wmnVV3NTORfVDNelIqpzLKQQcMm8Gf0pLJ4svb6zY5oHZnHfUskYlENY3sM6CwL6RR98ah6EYGhBn4pg0GAk8vl8TIEodbF6SvodPPtpMqnKSiawFos2Cy%2Fr%2ByI9wwXCCk3WHLn4Xb9AVWVQ%2B7J2prwf064OajsfIkBcqf8foPnrmtYAEpDMuUtnW2fHYA98TGGRYG8rQCMzd5Lw3wHfIFrRqkyTieB95z4jMiyLHAg9mMz70LZla16sAnuWT8Gh3a%2FEVRZsvgqWIsjJgvUVzHPtmH7dwmtHAVJrfxQc%2FdrWXqM1wEBWPimJF2KwLbWRtzG1INPzqyqyMVAgQE85MgT6E0iXrDaNOiDc75j8dJDlaZCIy2sf%2BkZslrYI6I885gXeokWj76v1Zdto%2FBP%2B05JHNNH2f%2F5xdsGo8TqBtEIjIJWs9kOkiE2IRdWh4Yu6OKw6cS51L%2B6iABLszTDpNl%2Bf9E0TvjqzmF0dvTGpUYJHdMNGIDqWE%2BqXhvPAaeBL9w%2FTVc64P9Z3GA%2BmujgPxegq6c3TXxw1Y%2B9P%2BUNc5rDxR%2F4OLT14n8csb77sWxzQtHkDffG4ziBL8hpi4y7r%2FjE9Nz4y2qkejIGfx9VvVpflhg9qVSOS9NHXhBFWLtnkeLFXJs7XyfQSThmLGiHXzPpdHgXT%2FM%2BoJngUl2YJ%2BNubQbwmfizJCMMDcSQE3XSj2rpp8aRerEC%2FPX42znLKUpK3SW0%2FsDmIkyZ3cTRy8zGOk2jZ018GBVWVBamxlE8hPzupwSzHecP3QBPxJBxLeeTJZvaR8lGgHG4i%2BFbhCk7RmRE0%2FHtwP%2FxxkUjs%2BwywLTBRU7ShNpn0AOwIOsIQDdO3Zp4q%2B%2BtTf14dBSvLKPoe56UXszlnmiDxEh7M%2BN3NWdzPnraKePv3SRdg8DPpgXSo9fUQVmaFOhpnAMBshmkMedq%2BeKo%2BaL%2BR47nNFcQUc4irqJYiIKoo0NP2xt31%2FK3%2FhnCGv3kZa1c%2B%2BvIB3kWervrheMTmfIfC7hDFCw6mnpGp3b2c8ZfSFRtwmGny4wkTKdoc4varjXAiEfD%2FUggbtU98GbbCk%2F2ESQBtCquxu7or97MumXSXKNMO%2FKyIY4QpPLM7hWlMpH9U99cIYpps2D6QFfATqsF0G7yP51Qzq84hNfPEkgyHCRtooSz8Seb0i0AFhPOi42L%2F%2BEl7FqcIO17r%2FU%2BxMFzrhehBbSDxY9s9%2FpoPD8sZUGvsfJtWuhRg0oEScF4a8RQhWWXCM2pdltjEoBxAIXkplc%2ForC8FJVk3ffEeaUceQwN0%2FKtn1a1wQRrm0oY1VsiOAYnznrhN%2BTxDdTpTVNCIf3jYad2oHDZy8%2Fbdkz9JAFWWT%2BjFxr1bv%2BQEBe7zPY9Wl13yS8t1KhQDSFt4wc3xxZ2jCwpuSr%2BkyRsg2aNE5iZlQJDwiknA9NJlAyN9DXRAPsSIcJarwZkr8SC4xzQcaq%2F8szqmF%2BPlVM%2B3rT7rcf2SGoPTojik5ObSYVFn9a%2FPUozx1kBNFPM0PkrsftuFnKqLhLJ36zLWxBKBA%2FqLzyODfV%2BUvhKvSbKzDB96snOa%2FzvBasHtePE5UcS5WcZILF%2BYzos8Ksoss68zP1xA6M4gCcIxwr%2BdDpngKS8M8DSCbeb93B6oJ9AiHEmwEE7DNQ6DEAoGbYhlWtab3jRlcm1Ltp0gyr2K4tlNlHSqlk1j4OQND16%2FERVEkZa%2B31CkoeaTrGgbVhHPHNg90szpB9lcKAzHaRYD2tie1KKapkh%2BL00yMURnqEItWtqQRRbfnoTfUxXhW0n5kfnTi9VfhT04VYNOizgzofBimOwWjn0cBuGgA9pBkmJdZbMhpZeptEiruEdtW38VS4fZ5hPdqTu5MJ1yveS%2F4d%2FKPatonHt0I%2Fl9vhjSDC%2F04%2BcxF7YVwCkWkbEmer%2BXF4L7UXoiN9tKDa6kp5zisIYxDsSCTV3xpyfYMa1bj%2BpyU3QdI3xIdca%2BQP86Vnnt%2FE3a76C45mgrsM8gXvjBRMPhZxDjGzJwE401KLGl9V3SWjUsM3Q%2FIEqGXISjJKrAuZcu5oN0t6A64tdMzUEtctozYI4NeVtMTbk5cGyjZDmt%2FRAjN%2B%2F6gIAOoBeCp6D3TSBTgBQ8wwkNKOs%2B8%2Fgng8NnsLdK1mL91AXx96ijQdiJpc%2B1GAPP0%2Fgv5s67jAGVBim7%2B0XnEQM4tM9BsSap4%2BVf%2FLtRYftD2OZzoysXbRVffZ%2B8MOA2zMo33a3SawZ3tHwuj3R6Fn%2B02xi%2FVfMnqh3P9ffMwNHSc0kqm3ZNmwp%2FsThSwJfY1R8nsmCBtHownlrcNs7Z%2FcleBE25eyXVGeaY98W1Eu9KhrJztaH2RMEthBLUSeVDMEo8lU%2FeUy7n3bcFCUfTZQ0RDSZW4hPCXRKeCIwMu1dilMb%2FQ1R9mnfn8Hr5v7q3Cxu9RCLLrDj1fa2wqmj8%2FUYMw0YsEEQ9p9z9ulgqqAStTYtAePWxSxq80C3sDPci7zsB%2BZDnskdXo0kKKOtsffluNdndI4xfSchDicPGDBTVOVpFBy73hJUrdoytbsMlqGxOzixTcgDKcGFJTUtV05SKM3BASz1PcEi7mlUjqUOGBnSkYwlStjklo2Zgfl99DQE%2FfVDNUYGeICdAY0YipXRY7zAMZKCAM6ULON%2BQBLpjbve0bjf1fduLSZqHI8rr%2BcQEFBABdgIJ5UOQ8YnnyRBoow4xCqEU84F4CRheNMFzQLAc0ejWO7gwlpg78JXFVoW%2FuUdTzVn1APwu4ANnEidpOHWnvgjxRPOFuiGgMTHIE6BrkP%2BF9uOkPZXFdCDdZVMJg4%2F5jWHbLTNW5o427yVC%2FqDMu%2BGQXZk%2FM6p6yjbePZZRxmseEJB8oC7ZwyrBH3OIH9azZj%2BWDBKlPlB90jPxvKN74EU76H8pN2LZDxngYwYfvj4NfQYY8mZ8RmNQZg0yb%2BvQ8v436NG4J4oe4FFvBeX8GPjExGPEVOPUPFHHY%2FYkNCcMhYw9zFuSUS6Q%2FULGvBVElNRoAn50oN%2B9nqU2J4EQKhzXeyAjH%2FMjkss%2BqTml15mncuNSfDyYvELtLnwuUCe%2FNqnPii5vbpD1krOuoMaSNZAZjzsLynC2pDMJ5%2Fq%2Bx0iWN5fcIcgzmqcbKdLU5YwXYHa%2FyJ3I7QKGEvltKJ4wA6gSw%2BccFZZcf6krPBFVGgOOylrLjiukxGfwxa4K7fluJ8%2FEzTVfErJ0VdcV3xCMwJZ4AbayWDuNvc932Z051N2K1u5I8TRD9ewUUOkdUybUGXONz6sC0ebBy8cnf3uBN5CGHAH7ggtAf21hNybu4NynIjrHvilnytCbFbsPh4vyYRl9EjIUre%2BPukiFvLxIfUMr2%2FZoCwVpPKsikyPRslXpxPzqmBr6oDBbo6cBYli3BmLsqrPo%2FRrZhxJvm6DG%2BLOhprg4Nv1tQfhAbKTd7CiS88nHZILUvqcfxHMW9%2FPgQDaNG3pFJPYkyP18LlU1YFO18nJNCdNYaYaIK3ql9DOWRINFWwCNwsw9xRwM6UWTT1j0p7fCYMZZPoWDvAYsdCZnf4WkmHQHO0XW%2Fs5P1GpmL1Yz%2BuAE1R3VDKRtVhY24JWlgpinAj97N7YxyafF4DAi7x8bHD44qnO1DaMmK0kxATB%2F2RIdPG1UQ7UR83u0HE9NjndIU4tVPK1GpbTNA8jjnIHV0nAyKZWmhBekNeK0G7JdmnsSyCLTuunDKXr%2BeFZQBdg6goSgD7sioQfEylD31NI3HWSdvaOujzMz4rSihVs4HcV4m4e54L46%2B9JZrDax0iIcUgrbq33hC3WK2a6i12uSqGQcwMODuE6agoxAjE7U4tylL8Z0c%3D&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";
            sessionId = Jsoup.connect(rootUrl).method(Connection.Method.GET).execute().cookie("ASP.NET_SessionId");
            changeLang(sessionId);

            Document categories = Jsoup.connect(searchUrl).cookie("ASP.NET_SessionId", sessionId).requestBody(championatsListPostData).post();

            List<Category> result = parseCategories(categories);
            logger.info("End scrapping of CrystalBet");

            return result;

        } catch (Exception | UncheckedIOException e) {

            logger.error("Scrapping of CrystalBet failed {}", e);
            e.printStackTrace();
            throw new ScraperException(e);
        }
    }

    protected List<Category> parseCategories(Document champ) throws IOException {

        List<Category> categories = new ArrayList<>();
        int subCategoryCount = 0;
        int parsedEventCount = 0;
        Elements eventList = champ.select(".new_sport_country");

        for (Element row : eventList) {

            String name = row.selectFirst(".new_sport_country1").textNodes().get(0).text();
            String id = row.selectFirst(".new_sport_country3").attr("onclick").split(":")[1].split("\"")[0];

            Category category = new Category(name, Long.parseLong(id));
            Elements subcategoryDiv = row.parent().select(".new_sport_div > .new_sport");

            logger.trace("Start parsing of subCategories for category: {} id={}", category.getName(), category.getId());

            for (Element subCategoryElement : subcategoryDiv) {

                //Create subcategory POJO from this html element
                String subCategoryName = subCategoryElement.selectFirst(".new_sport1").textNodes().get(0).text();
                Long subCategoryId = Long.parseLong(subcategoryDiv.attr("championat-data"));
                Category subCategory = new Category(subCategoryName, subCategoryId);

                //Add this subCategory to category
                category.addSubCategory(subCategory);
                Document events = null;

                //String postData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl00%24HiddenFieldChampionatId=REPLACE_MEEEE&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&__EVENTARGUMENT=REPLACE_MEEEE&__VIEWSTATE=4Ccer4UsqpQeWnpjWv%2BA3Kda%2F%2FZFMr8lgg9K3KZJFHE%2BzOSAFL2eKqNPJT9v9V0ui4c%2FOGcbrAwcyPjo7NWIVzO2C%2B%2BEmPfFDuQurqtAt344Hz5ojlwVYWF2JIYUQmTR4TihVIN3Ui4%2FEWmCOZ%2BrkRFNr2rM5uAPpQUGt6qb8ULXc1gKdjg%2B2NW1j0tJm99bLPOt%2F3aIgk8suP8UtA5tOP8iL1lp1BYx6zjPBenkUeVOrFqOZcDSlpGkuTJ3BEgAxzsh4D0ID3M4NW5fZZra9qT2Klfy8KZMN1Cpzl5QGiM%2FzPPO%2F7O2vj%2FTqVYKTKQv%2Bee0DMEHJPUD87m%2BdLymPpn2FMYvSA67Pkl6PG%2BldRxQ7qEIaBo6Vguch3no15tN79qxmP3GeINf4UF7Hmtq5jgVwvHJgOeJg%2FLwxXlP3z%2BMnFLwil2sREdjdFbW7xlfHsFWHMRdwvlGoDwTVX%2FwiQGErfkUr9i9MP9j2eQ3RSkyvker3ybyC3lhm%2FHpElqWwez2IH7wgrzArBt4uAbeYwoFT5V4r29f%2F06rFrm8E%2F5q6gvgT4i4nGvr63aOGsbR4jKciXjPZAXUplDSVTCMsdgT01gXA%2FcXdhzvBfLSt0yICrmmjv3r4RjzO8ufNmJ8KBEFKZeB1GSjM2FFL4zoLf%2F4rJzpoBM8BRbhHAeJsbrx4mbgmJRSjR9Fzgj7m6As8ArF0Lvq3gFeCau4W%2FASFIfN%2BlB42fiOsANoHqGAauS45sNXLAc0VFNY4BvTVAn9LhC9mB8gYWAMvhAglMNWTcdztXRd2ZFjmMUsqh8I5MfqqnbfceKXFFrJL0xMYdeF%2F9sdXGNBVdh06iQ6bv6TzGxlM5Vw49uyE1AhCrbgohEE6my7mxmOWuLhlot37BGDg%2Bbw%2BskBmHy9SNnr5H676A3yOcq8ahYuHSMajZ2zquPxfFzOxmBizmmojdCzM%2B5vX7DJa%2B4YcxdJlnG4KJgkIbkDvm4n%2F9TcWxyrTtPYj8jJ4%2F4TsaGrnqy7hB%2F4zh6EpZl%2BuRQcbbUoDg3KPQhA8ZB4OQP%2FY8oXK9e6EJSYdv9L1NQ9C0HIsoajRUZtU3qwilvsICX1nWSl76hkcVmLxs5D3yTq91VPMB7CmOQ%2FvHJgomdZUmCpQTZLst2dq8RpMhUCRehnM6K%2BnncvEyXxWpxfpXdyW6M%2BkXZ5JwzhqNhGgjTLLAhJO2Jjj6YuZrWOV%2FuWOcB8LJ%2BN%2FnzZkNI8lzabL0dD0PL46d2Lrk3ypUltCi63GLx%2FOTenkHyFpI2gm23MTb0S%2FRWo6HHkLN52KULpTtZ%2B%2Fl6a%2BgEBmuYltsC3YHjPlZtXVwfzaez7h%2BDANUvFYzlF1rMuAVC4%2BxkTjesONvS3RbigUVvZDEARaiKzfwM92c%2BPlAWfTlHY9zvCK9LMlUeFDTkTHCyXsvw3qoug0OcVzgpiclxCGJ%2F10ievc1kXj5CN0lw01xTH%2Fty8H8gm4KMDcleSIBh%2BH4o5CYZoIL8qz6BCLJhQdPzXnvcC1LgkGparWzxLBF1FnFlfraoNhujc7v3JgP8%2F9%2F0EA4OsNPaLujSuEeB4bWELAwUnm4W7iT1OlVSg4mCzkij6uWCdCCUpmFI%2FpWJDo3wuDcNDrPQGvAwwr6fEhjDOzdXq%2FOJzXAL7iZBO6pgNJG%2B0zvN0tJ01nqgliNdRAKV2Te1kicUp%2FlVnsRUV6llm0EPycx%2BJFd9LqRBPXjyYZwXtJqTPGF%2FDJsyvfs%2BIHlJi45q8x7%2B6NFjwvvZ%2FVjdpke5%2BTMlQEumqaMVhNSjV18fdtuLuzS6yxgdqMByukgDkf5Whdp1h30UkPurDho8vYmTnulfS5sPAtM9QVp3%2ByI4ZsXBK9uiwczq4lnbpkoe2EgcylY1IM0GngILw3P1eUz3sp33df8J3uAaqmiBC5q8GxGJVZqKqUXyA9EST72IcE%2B0C1dG4QXckmcsXntKq206dMv1ep79dyfpaUmhl9ZmUMWVvEISUr8hIe0s5QSBsckHTivWjKTYEiXba0Iaoadcpyt75lpcy0BW15KIJnn1IKDSXundGSLkNTncX%2Bvkta9eeXGYfU3MSdNFc%2BX5eSLCrspz1m0P%2FtpVR0yf2zBO7iqzt9u8m8bjB9dzJjmgl2qCGXZjEkqnFI7UO2YCyAMcKqRtZPo%2FlfCdUSK7eRLg8ST147log6StZux%2FbNYtjmug99nffikUPWG6av6MFO4HZ%2B5iaEIw1RsnCUpIBF5O8dGOQWqKO%2BYCsegTB%2BdS1KeuS0LnpdtR%2Ba5sIWMRkatFqr0V6vhpM0HG%2FnmQ6WuLJYIqDQ25DBqe45pWhFSZr5WsgLQTs2tP3KJB4TSJx0tMWrkzJLeKb78mus4QsTlI1Fr10mt7V%2BqTdsn6DWmG%2BjAq73Pcy9GhgypNaaKncu%2FGIhbC8tZ3nKEZlla5ip57tN5DwSg30cS59UEaKFV6R8UFCKvkTc2erqnoJPzQUSK4hzCKSVO8f9sJrWCWm8OACsU0A54%2BQVlK%2BSreSdk0Je9W0KEgeQ56m1f4vyrqTmF4meI%2B6p2vRnHFX1GlTjwyM6MauRFxO9r%2FsqgGHSaePz%2F2EfANSPBRmg2BtiKXX84vw%2BkPPrREgJ4Hu2b39PtqoW%2FdSao8qc68D0sn0R%2BgiSESqUUMdus29HDYkh49F%2B7ziviZh6Sm0A5hsvzUeZdcMquJTE58QxI9h1uaI1Xj%2FfRB9ODlI%2FUvuEcAtNNPH2z4SlRJdg1gL951AB8UoQynkH%2BrsjSRA0lGjrmFYR48bfzOk8PyUDNWHtr%2B1z6EpSNwgCbWl9xuXj%2FM2J90Rl2QekIlZqhVg7VtgSzEkd2VOoDGgnjTJivi1jjzMSMGxZKiZVP%2FMHVPL%2FCHNSh6DbO%2FAvveKu195VlAjzkWV74ilq4J%2B0rjFkYEm%2F5f3n%2FxVAzkG25KQhO6bRQ6rc5Z%2FN1djGm74p6OzfELNEZUhci0A3qvS4D%2BQ7DpB8bd2g%2FIzDh4%3D&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";
                String postData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=zuraba1234&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=zurabinio&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=zurabinio&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=zurabinio&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&__EVENTARGUMENT=REPLACE_MEEEE&__VIEWSTATE=ufHtRLuxTTjmnD8lX%2F8A7EZT2IvuBaHtuGTAQtnL%2F1ay08G%2BfRVArfw%2Fcv1Es329HWMPFKjsLCSApTlHNFqs33q19GB55OWaYRcqbUDs3ubT5bDlP9dyV6BU3xeBULMOIWiJmm%2BqxHtpsMHBNdV8NJ7uaoLD56jtZhcFjYvatfx7ucBFl4tqS76tY6kC4I8YjdNsWdgsfUN8EQsS6CTJpP%2B9cAEhJlN2vwR2dqz%2BXlxkDJA6cZ9l23H4S%2FvFUgm%2BaUS%2Bcb%2Bn8QtNKsUER%2BZART91VP4DA88c%2FDj2IYigiQMciECD6YfVGFEnulF3RDDqPWhCjVOjVOduPGn3yW%2BsihHr%2F4pbas%2FvG7VjpjmiMCntzcOALRmwsLyhQL5nil6B34iuI2DAYu3TTvMQ1pErE1416wP3O%2BPsY4%2F%2F9uzSvI0EsO303l2PMFHlzCxtqnCI7yZs%2FzXOIUUDZA2cFrYgw4dWTlfU73xjvfcjcHS%2FW6kNwrHSYupdV1upEhnBCjBcCSpRbLRch2opgL%2BQFagLZTGSubALod709fhPRLEo%2FFtkWXqUKAE1AnbIJPF2VRefBL1QZzc7PcNDDqWsZdTIi%2B6Uzp7si%2F6wNcwD0etRzN7j87XAP8D7RCbV0jm7CNZN7Yf%2FScg6pwFwMeuO2%2B2iz9Jyy6bgPvgr5mMzrftvBRoEC3sq0i%2FdpgOFf9zxNwujL%2FB6V6OUV2nIYOMIb05lpdI4XUPqLYFKUAXF2IyOu%2FYaL59oWkp5eBifqteZFBtPGp2RBSj8oPeNfsuv9HWDoXS5fX2BgK7yoPfUsqMfscd5KTgtDaGWroLM%2BmEpynRAaB0YuLXm%2F6pPn1HYDLJcISnuPlBhojmipUpAbkAJziRUeM%2BwdRLt6FW%2BSbhSr%2BWqtAGy8btxxg8K%2F9MzRvt0AuSECrw%2FgQk4RtyyxOLlaczqKYReHKFTadmozNBJlQcu2r0Zvfcs4QxwcP3N1bkZP%2FD5sxplC2ON5jrFK5SPVUAo86ke4k6IID1pnsf8tMU7fHWqijsYkqu9KpipccNwoT4kKV1Ld0eMBMRldOwP7rWqrM44B5vDRdWCRbIvPKi74jSc3RP%2BYzO40Hfv3SBphit6EHyAesGk917on4O1QP%2BwNRocR0bNUFb3iwmwFX51DyYuULSUACj%2BM5x5TJcNr%2B1SD0EdPgFon%2Fg8y32DOhfO2z9U4iCqlhuZwGxOwFaDy%2FXvluy1q7qS1aViKSejfSwFPITRuCIr4fW7j500hddi0Lzig18td5rabF4mpAnbOUyJKthS77Rg79n0LWNZQXWF8zQrNpWpeofB%2B3XowsEDBGZ99f9TPFCHXc%2Fo4IzxWWlW4Sw7F%2BeYf25solKP0t5JDMZHzqN3ZjA5wHGE6Kb%2BZpwjdAJgEtd3Gausgn9RcF7WgkTr4qUKjAWxZx2zmQYdlFpxlzwIS2Nn2panz7RXF%2BVnw6KKpvDzvIWU6qzEUuJ0dV7Zu2jB090SBwAzAABgHpnIyxhnBTUQvqQF29hY2%2BIk7EKkbCNp39nblq26V4Y2%2FmvCgiLb8UeEHR51muxEa0rI7r%2BVi6neWi%2BhzSejxXzfqqqlnh6DpcTeDEfUp3kGeCQVaLQdlg9uEyhqoErdcXy2gJEjbglq7zwo0XdsFJSbO%2Fw%2FsM%2Fiupv6xnLBp68U5%2FxdRxvQYeKZ7cBzM0Ca8ki2yLvQ83DN%2Bip1Ww18Uv3%2FZ9CD41zN58ISTdvQIAj5xskeNxfDoPlUborHG3IVvXIvLAVD2HcSb9xO9swwZJ8lcHbIUwTytHukvSXrqhW04bqL%2B9Ooz0o5BfQpVo3DQSKU0jZaODlh1MZqKyav%2BPnAzee%2BQ7u40QfIm2fKiCBIz%2FMiGZfefr%2FXuAANOVhfMaIjCTOnB6rPCipz2gsywVvcBGcs2UUos6XEItxOAbCHDotEXsGIhJMC%2B841s3rOaNkRFgmG7GpR9t2v7sMz8rm83tGUUIp0JbVI%2FY8OLxzpqjJNgq%2FMWyOLktveFopAKoeMAARolGq6uNOmsc5yylbN7kN73XVZwUn2%2FHTB2A7GvpoRd6VeFwwYanGBQpb764V%2BxmgFZW3KUgKznHF2WjGSAN4ZWqt%2B%2B%2BqRTq3jaRQ5frPxRsR6JW1UEtO40vOP2Gth9xdZoYLJekzAc5ZjvxhCRJB0HP9n6ZOmoe9vY0HfoyZR4T34cjNzCgG55krVVhkXASZNeojX2GT8skZhRsIXpdcVL1NW69X6p6p6ISw8s2%2FuCERRrrzU79SrGRitv6iCtAgAVlDZw6VOrG5rjixmgL38WdGQFMOs0Go8VgXVD0OUyfgzsKPQcF%2Fd%2F9GZueTbBwT6JrCOzXmCR8XVdbPuU2lUiVxq859ljjWTgF3y8bnVmPsnElimdFckRgtggdSzHbLkwlbF6vPf56djPH5yXAaUJzbEk5TuBIX44Cm0zVhFM0WC9iPdXDc1p2ijTnLDpxAqmXuJF0kLmkxDVIuAEXEfXCZvHftYVb1wBMqOOmzyb7xxuJh0Y0bPOz%2FFgY2sNvheGtu%2F5hfWlG3gHeeSOnZh2u3P26DLj81rhqTPG02e6qgPbLalECpsw2NHovw28G4bxv82TRWbJTK8NuOaJUuodLVJp34E5tiGb0qdNH3cDcibWXkTNPNKv55QEhfRO4YMhFEFEsC58REDlllTRC7%2BwxmYGZ5YYKlOwAVGXpUuyccza1scGx7gP0Q%2BT1JVTpkBA3SffKx7H9Mn3NwOcY1sw0U3FMVO3xhwHL1QDz%2FFOPOpw83RDqHA%2B%2FmbZC0%2FP2SzsYf2xnrxH%2F4CxXdb6tkai9eOoPNeVvzH9kHUqygULa2FgtOqrkfmip9nldDOOWzr8TbGfEyN1pw0p0HSUkP7JBi6%2Fo7XNbhp%2FT83e%2BZ7RRZ5Q2oHNhcqUMkrvBFX4jOJYfDyHGBprRl3UFYvHsLjIX4%2FTxbbYc4zWZawnH9yxUUKRkQIbx%2B9dZqlDMDAMSebkRPBn3nsnJkH82T21Gn2NKYk82LvZinq38aV%2FR%2FCQ0Wc6zo%2FHMJNY%2BRDgH89KrPcNQ8lNzLB9%2FZoZ9Hq3o%2FUq3GyeoFFODtesOBYhx%2FaNUb%2FyKGwK22s8vgCu1Vomui7%2F5KmPGMs%2BywxhA36oOfgod6Iladb1xgv8%2FofxY0g81CVGMxgPgbv8QkqgRNVf8OIl18hU4szVprbmt%2BNyBmIqrng3mz3mE%2BUR4BKrV3M0%2BNITiMHxERSXp2UXt5Qco0WlpnhBj1%2B9BRb%2FARCwYqUDylrsPZ%2B7gM9pVw3urg%2F9HzEanGDN7towSOTpicXM%2BVqcmOCyrXB7ePqmcVYJ9SMOU8oRLGbYPG2uQZhbeW%2BbRnWHk4retOT80T69lF%2B9MErJ2Mm65bi%2BGk3nN%2BC3gW92ehyeKnFZgUxEGbpgXcN%2BZMa0VMxLFZpF1vhBwlCGgUbh5FUjaUiIkGolBxCT60uE1sgFVLbsrkiweMaW6LzQvVR%2B8AmCA0Vsb96LUmv8tF8sNq5gwqM%2BjPsbZ4LymPC9lZmf4wuyVDUgiKjfW4h1o0AL3QYO4B1omVTSmgw0yRJ7kkm85y6NTPcXg4mBvxmVxMMKeUmc3awYnhCwP2K2YCubxB4bGg4kOVb6BqU5HZ0JtbYGOkAKXvN0jRSO4CW5b%2Fe8xu3pFjrlH6J4PrxrSrLG1ASlxz6Bh4SZc9DvFOgW4xhNj7NqRhiaveCd1rP0tpqrwdubtsiqe6JOSSe%2FLNTBrdWtexrGQj0l6EAzTkH4qli7N6r6tUJwJKa0hXHD3LxUAJl%2BJXAgWEAwqRHS%2B5OqPDlD0u7ZnWZa%2FQYsXpf0qxiMlPPcZJDzU1r4YYOezkDvY9QvvLzylWrly%2Bt%2FfPB0X3f1Jr7nI%2BoEqTAyDNapIL8hWk1bNyrNYrsiZBUxABeHsutoztXICW1%2BN98chKVdQNusLoUY9yOeWHrYDBrAwfW6qV0WsRuwati%2BxDZy3Pael02FbAav8X3Lq3pZn53U7xcvbCPUgBMTizTDsq167DEHXgTu7hkBXYf1YM4AGMIgjbMw4kmYmMC9ItI4qnEs1WoAkLEUG%2FmYqnSAopLRVz%2FBP%2BMUgZmLCyliqxEGovWpgHInOWsM3xo5Z5uEJpgyym8jgc6zMoKf9%2Fr5ucA%2BVnt%2BOHVijja0rXjbOdC4CfE1h7GHMRgCIKORtgjyVYR82np9%2FsaeVZW9m5NO3RAQtR0hGaxq6hJSDd9jPPVisZJYqxn6V%2BJODnAEwO70efg3p3wNLnCbdwaX7lAcvYi2ByJLKO4fNnCh6wRzet1xUHVArY%2Fg4Tp50ozR4yXByTplBnuPKz2pjkRzcXghnOLxyo2ZcQZuUieEoK8AGrdBSwGcEAau%2Fktx7i1lytynK9heI0ZtdyU9TSvgMv5388E6gZWKpJvU8DCqGEJX%2FDo%2BgFeI0Q1gQ75Vxiwhg25m4djieS7xZMGuh2q4cXBjvVqiWSn553dCLauUlwya1%2BOXHCXu6ITPL6Snh8NMZK0Wo2WhA4ylxSCZA2LBTNAW0v%2B7Zo6tZM7PnoYD8kyaA4hzyt572Q5ARcwKjWrMu7pLvcNb7rAaswS%2FzBmfswcgOxd3uS99yvwC2lv08cM%2BfWGdVVoDOzGHT%2Bv4xMlSRsuH8kk15FtDv9ajAQm49baDpkOsLSQdLtFGmI13QOZMjgkOrsoyciHV%2FJx3maSGgtOhjvqNP4FEe0L%2BRDt%2FnQk5ZV8tDkXBBbG4ZokyYV4ZyaytZQR8tCm8u62xVvwnWIp6HhP9J8hKatJvw%3D%3D&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";

                try {
                    //Get events html data using subCategory id.
                    events = Jsoup.connect(searchUrl).cookie("ASP.NET_SessionId", sessionId)
                            .requestBody(postData.replace("REPLACE_MEEEE", subCategory.getId().toString())).post();
                } catch (IOException | UncheckedIOException e) {

                    logger.error("Request for events html data of subCategory={} id={} failed.", subCategory.getName(),
                            subCategory.getId());

                    continue;
                }

                logger.trace("Start parsing of events for subcategory: {} id={}", subCategory.getName(), subCategory.getId());

                //Parse events html data using add it to subCategory.
                parseEvents(events, subCategory);

                parsedEventCount += subCategory.getEvents().size();
            }

            subCategoryCount += category.getSubCategories().size();
            categories.add(category);
        }

        logger.info("Categories parsed: {}", categories.size());
        logger.info("Subcategories parsed: {}", subCategoryCount);
        logger.info("Events parsed: {}", parsedEventCount);

        return categories;
    }

    protected void parseEvents(Document events, Category surroundingCategory) {

        Elements eventList = events.select(".x_loop_game_title_block");

        for (Element row : eventList) {

            try {
                Element dateCell = row.selectFirst(".x_game_date");

                Element dateElement = dateCell.selectFirst("font");
                Element timeElement = dateCell.selectFirst("span.time");

                if(dateElement == null || timeElement == null) {
                    logger.info("This event has no date or time, so we skip it.");
                    continue;
                }

                String eventDate = dateElement.text();
                String eventTime = timeElement.text();

                Date eventDateTime = getDate(eventDate + " " + eventTime);

                Element sideNames = row.selectFirst(".x_game_title > span");

                if(sideNames == null || sideNames.children().size() < 1) {
                    logger.info("This event has no name element, so we skip it.");
                    continue;
                }

                sideNames = sideNames.child(0);

                if (sideNames == null) {
                    logger.debug("Cannot get side names element for subCategory={} id={}", surroundingCategory.getName(),
                            surroundingCategory.getId());
                    continue;
                    //throw new ScraperException("Cannot get side names element");
                }

                String eventTitle = sideNames.text().trim();
                Elements oddCells = row.select(".x_loop_res");

                String[] names = eventTitle.split(" - ");

                if (names.length != 2) {

                    logger.debug("Could not parse side names for text={}", eventTitle);
                    throw new ScraperException("Could not parse side names for text=" + eventTitle);
                }

                Event event = new Event(surroundingCategory, eventDateTime, names[0], names[1]);

                logger.trace("Adding event {} to subCategory {}", event.toString(), surroundingCategory.getName());
                surroundingCategory.addEvent(event);

                Map<String, OddType> oddTypeOrder = new HashMap<>();
                oddTypeOrder.put("col0", OddType._1);
                oddTypeOrder.put("col1", OddType._X);
                oddTypeOrder.put("col2", OddType._2);
                oddTypeOrder.put("col3", OddType._1X);
                oddTypeOrder.put("col4", OddType._X2);
                oddTypeOrder.put("col5", OddType._12);
                oddTypeOrder.put("col8", OddType._UNDER_25);
                oddTypeOrder.put("col9", OddType._OVER_25);
                oddTypeOrder.put("col10", OddType._YES);
                oddTypeOrder.put("col11", OddType._NO);

                Map<OddType, Double> resultOdds = new HashMap<>();
                //resultOdds.put(OddType._1, )

                for (Element oddCell : oddCells) {

                    Set<String> cssClasses = oddCell.classNames();
                    Optional<String> colClass = cssClasses.stream().filter(elem -> elem.contains("col")).findFirst();

                    if(colClass.isPresent()) {
                        try {
                            Double odd = Double.parseDouble(oddCell.text());

                            String className = colClass.get();
                            OddType oddType = oddTypeOrder.get(className);

                            if(oddType == null) continue;

                            resultOdds.put(oddType, odd);
                        } catch (NumberFormatException e) {
                            // Just for code to be readable.
                            continue;
                        }
                    }
                }

                event.setOdds(resultOdds);

            } catch (ParseException e) {

                logger.warn("Parsing for date time failed for subcategory: {} id={} e={}", surroundingCategory.getName(), e);

                //e.printStackTrace();
            } catch (Exception e) {

                logger.warn("Parsing failed for subCategory: {} e={}", surroundingCategory.getName(), e);

                //e.printStackTrace();
            }

        }
    }

    private Date getDate(String dateTime) throws ParseException {

        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        dateTime = currentYear + "/" + dateTime.trim();

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/dd/MM hh:mm");

        return format1.parse(dateTime);
    }

    private void changeLang(String sessionId) {

        try {
            final String payload = "------WebKitFormBoundaryeJBitNTLRtons1Vx\r\n" +
                    "Content-Disposition: form-data; name=\"__EVENTTARGET\"\r\n" +
                    "\r\n" +
                    "ctl00$ImageButtonEn\r\n" +
                    "------WebKitFormBoundaryeJBitNTLRtons1Vx--\r\n";

            HttpClient client = HttpClients.createDefault();

            URI target = new URI(rootUrl);

            HttpPost post = new HttpPost(target);
            post.setHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryeJBitNTLRtons1Vx");
            post.setHeader("Cookie", "last.login=zurabinio; ASP.NET_SessionId=" + sessionId + "; _ga=GA1.2.1803994248.1502804140; _gid=GA1.2.527046321.1511168820");
            post.setEntity(new ByteArrayEntity(payload.getBytes(), ContentType.DEFAULT_BINARY));

            HttpResponse response = client.execute(post);

            System.out.println(response.getEntity());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        lineList.remove(lineList.size() - 1);
        lineList.remove(lineList.size() - 1);
        lineList.remove(lineList.size() - 1);

        String[] newLines = lineList.toArray(new String[lineList.size()]);

        return String.join("\n", newLines);
    }

    public static void main(String[] args) {
        new CrystalBetScraper();
    }
}
