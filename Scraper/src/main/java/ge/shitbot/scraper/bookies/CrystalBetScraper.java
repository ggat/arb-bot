package ge.shitbot.scraper.bookies;

import ge.shitbot.core.datatypes.OddType;
import ge.shitbot.core.datatypes.util.Resources;
import ge.shitbot.core.datatypes.util.StringUtils;
import ge.shitbot.core.datatypes.util.http.Http;
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
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    protected Connection createCategoriesConnection() {

        //String championatsListPostData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=zuraba1234&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=zurabinio&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=zurabinio&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&__EVENTARGUMENT=SelectAllChampionats%3A16&__VIEWSTATE=IOTx851Af01sTXEegQn7deNqlnG7MTmgjxdKD3OCZGYfFV42f25u8YeZySIWqenWS2eyIZ77UuvHndL2yEEIxQmGQpX1elpxB7uC6uiGpwJ5osTImiB7IPf6rm3dhrMXqA8U7s%2FwZuVskTrNJIMwcdsralT4jOAMA087kjeoaEfWcOu9SGkXD9O2P74XScYaY33IymWEBSgNnzDxwtfMT7KLV7wGMibnmruvb%2F%2FbsfbWuHe%2FL1VcVkaM2t12kypyd18MaWbjOIR%2FusAAs9QN%2FovygAg2Uhq5x6HEP0p7nq3ieAimyzb7xceyjGiQALtJw9S1tp4ZhOp0cm4cL6JtglYeXFj0XLDbPXf87jAy5Ikzhn%2BpNOjXLuRde957fMcwWTt5ju5%2FGps02JBaDmkes75d7LBN0Iqm2aYoP9bKkNgoL2U%2BLlzGeUO9AqhHNt5Qdb2lB66RGvVgh1P0fONaugAMgcpvdzjxcVquLAz%2FErbWGdGz1KpMNi5ENaeee57ZeyaTsez0nBY1D9K1k4yPU7zHdKJoORWGSg4BTm0IWAgFbOHmU1MMc7yeB6B6yFaoadPI4BRcscaUDbQbd49mTGGgUztXzK0lByeqaSISJKEy6ygFeVdGV2fZf3%2F9pagReABro8qeNV5IdXlquuDm7n0SUxYjIIP13CNJUx9m%2FKEypLv6ZQ2fZigsWJClJLFmJqnLvU0nkgUNt5iZmntkR1jvPZikf6gFLdrxuskLPryfIomPA9FjnNntkOWi6iS7e8n%2FDRu%2Bx%2FwwmBMDtuYAyb7%2BYAFF%2BdHIRULgya3em5gy44sOiscu2vpxuMBaP4C%2F438wSua9FtzFFk6yiKpi6JsiGZ%2BpjoVZAYG6OlsmN9NOO7xpSm%2FngjhToia82hvTjxACsKBIjkYKpeo2nnGytOVZfwW505ZzVZnblX8yvxJBTnggGkWriH4y2fe1PW0dB%2BHuTSSXHwDfqAwIXjxR2yz4OxDAByImQimF8hl0Fcxlb8gevIfIYKBaaYyBpPFGNPmnWyF2b76JorEn0717cFX%2F6QeXv%2BEK6TgeEjgs1ZmGaHGLw9a6k5EPFKOrQFK%2B9U1zzBpSALNUXjnQpoGL6L%2BSmbhm4YkC19E9aGe%2B4WPD1Pti5DinBBtnexVeX9xZDe0Aknjm0fLKWuLCAc6drCMfPAf0FTlQsFVtf1HLe61s%2BfrUBfVYm0scZ31nw2fBbKnElB0vfZBJ8rDglGQV4irGV6HYImI9DMnMbAQ9SUUjbBfmF2QG9cPu09hiUURtv3pEndcq8gWAArpN6yU1xxqnMseYz02hBIzTH6UleiPS%2BvWvrSz1xp8vqh4awnOV12XOxqVcPQbHlU%2FpmsQ6C%2BEilV2lNPVijBsYsBQbDPcZluE%2FTAA3OgSSsr%2FHuDVHH9gufJsr7HPKJtxKWNrJA3h8w6O%2BxJSD8BX56Xg%2F%2F4pRGqQG4o4vLDoNFbWOEOiJ%2F4bFzzJjjFBtgZ2vOIoFXQlKwHWir6qyvuXqs%2F5RmS2WqBXVz6ZwwDBBx8mpS8hJpnqsW%2Fkr6wu2RrJGoxm3Dhd1dPPqLRizlthLI7w%2BhCvZwfs5bhcBrbTrH9VRQl%2B%2F1uCvDPCQ%2FFTojc2Io9neV08%2BBr47%2FETgelNNQd7bT0oG0i1yGkfeo9AMIRm5PSAvsd7Tmfl%2B9mrUX7%2Fd0lJTmWHPaXoU5Qma9FTfc2es3jIZE%2FsWFw9AabCivYUNm5%2F3KHwAsEwGRdr70egiLftyuF3606jjg%2FtfIzUaKeuR%2F2hnOGoyyaJwSdGC%2F8Cj2kUcR3JpRQah4Ta%2Br7eCu8It95O%2BatDdYJ0yoiySOjwEDe2cvuOybnRoXkm339Xs3dsq66Jvyug43pb5s0xER6wwc5lMZgT5zcEny7aMUTXR%2BI%2BKRA90qddE1OqjEQ6rs4MF%2B7X3sj7AS6XVo7srBj0PIWXDt3iBuOHa5CmJBLrGLpZFfFDq%2FnG4xCBAb76Ga47gPZJ03PihohqvMLjZUA4Y4ey6JZv%2BTgntwx8vlrojwo2%2BUVtpBgMvnd2b2G0Ki6ZiiCJ8jcDgWHA2ZDdeW%2BX9bLLG4mm7i6VVmf79qh4VCBC3BpoZtSJAmSnHGerO5yftQaPxe1SGDx4GPQDqjOQJ%2BuE%2F92IDr0cUc39V8y7OKbCUQmObFvJiFAvTFmErVAx4T8wdBIg9yqZUGAMHGxZ0wLXFc7nHLPOMe%2FK9cNKZgA5fbeBp07iR94xJnE%2BTyJxNHL3ydyO3FXxf%2BW4WqcdHDtYn4AImDurL5EsdOkY7%2Fpf5kLnKiRIoo2%2FLtobVW%2F6OCb4vps9g9jkK4XLjv8%2BNXrM1bQvKTQ1TBlfSKSpY8ASqPC4c16R1ruw5L3EhittyUwqFvhXSt2qUGT5q5vDgHmcPWX7%2FXH2G868Qlv7%2FXVJwv5b4hiPHghOsqax0kDT%2BHJ%2BDOTt9MRHRuxFl5R%2FJUeCM1lPzDqliEQ7FlPbjH2MrW85RAWyEgRthL4A6IrTe6RLbRstMR3PSvw2SI1dN8mRLduGIDbUyE00p4gmxTbVfnIBxdhOnr%2FlRI9IXSV0YfMgqVBbOM7Dwyw%2FRNz5dMsjIjxlbsJrh52XVbVFtJpMjZnSFFzVkuiWtlYe%2FVbKqG8132sYye4SeYkouBLzkQD2gNC4MwuRTtQk%2B%2FuNGe2W2SaYQQY8Oe7%2BjKcyR9i%2BfqA8L6K%2Fzu5saF%2Fhwc1iSXfGzFfxSViJsoILP2bPhE6qLq73uyjp0UOMuTzbi%2BnieuNi3xyIakeR7Xm2ZKSTJ4stkJKaXvHyFq3ebYIl1FhNzyIaD0PIc2P8xWN3TQ8HxLSvDYbBcgUp7o4lJKrVXpfFYtkh83CMTT6OFWTAFjTQrjvva9dH5mK8sqvTMYkTeSzDGUT%2FO9mGlBISozS%2BmdvwzgFfPSmw7oy35ohpFVcvmwreBgUFrOZ3W2Oa6Sa%2FnPCeBWqXlAmqfmk047YdkVKWXnrb8TemuD3qPjKATdVh4vq%2FtW%2FF96ZWxIbDQA0aVdSVN4AJTcQjcJ%2FQP8tsNxoBw%2Bbx5PSmu%2F65jENzpqrtKwAWwtcvFkDIFgsGZHZmDJZ8EmCdWseK%2BaK10eOJCbN0VoJVjE4B6sAmpDP5MAUP7y5LQtILm5m5BfEYIipj6avWFi8HCZEmqI9DUVHsWXjMeW26jXgXVdaMv7iSSm8Up%2BBQR5eUq8iulx0kDGtbDZHRSJT5PiK8%2FnLXc8AWw24%2B1KRu0pe%2FjD8djTLUI%2Bi3If9%2B2TEFT1K3e0GhkjBc4bAT%2BH%2Ftykfb0ddsOdKHqOmN163A9DjLku2PZafkmywuwyQ6%2FU6%2Fvin7xa4dms0qWBvWGopV6EqULdYVDcPpA1J8fsPqeMJOeaqc4b%2FBEPaLOpYZ3AwKFdSkYkSJshID0Rg%2F8MCiYXzZso6C2rh%2FWPJO2rkEe1QP%2Fg%2FAGrhMTsVHKj3g5N3RVAqcdiCi6vVKYuukdHgC6V5YEGSOGQ5f%2FxISnbcD1qc%2ByNyprQzcnYprJqCEqGAhGFYGDcjKOjJ0H3IR1EFemHUv7U9dPNkHkqjwsfENS5qZAIhIhtKiuHqCv7B9JlpHsq25KNa9gQ1EfHqCKgIGGytqQQsjqAPDQepcLT47GTX4xJdAk%2BRMAlP3kU6dYxpx2o%2BGMtEJsdnzoGocpvtR3YYy4MMVnaiQe3L4xwT%2BLAYS6F6bgvvUN2kR8KR2eAP3cfBfhtyFxfFPQqQhMtRSgHDkgV4VZ8MBZ9VsTyBB4QC5QcXGQbINO0BOtVPocX7lB%2BpWleDKwk4vxQIla0%2FcqRUT05GJmjNNtMknZy2TeGu5aR1cwjfJqFy4JtaX3Ag5YN4mZaY5XidKIm%2FQfMX%2BzzyPXJjF%2B24N7uyAScWooW%2B%2FArQ7h4YamywKbAefHLDAjXj%2FdlTmRQ66%2B%2FSh4R%2Fisl0N6lNlE4B2JR6AYb69TrVwIjQPEi7qY5ehp79N7bRtdhkwrmuhJLmc9t2BkSWNe35zVTRzG342UNHipPAerUOScITQReOB7B4K6y9NZA%2FJNFUD7ju7HInSANEB4y8e2%2FWHqIKHq5oVjZBlLUR%2BeerDKdCR6XjV5lnQO0mJiik6oMEFBrkx%2BgJN1JscM%2BIjIORcoVWzHQ6ZPAELjc%2B82zAghoZkxj%2FpBd0Xjz1htezysYLJs6wVYgXiSzcLfEerMsles0cxRN4OwoEMRrOZNwpwhxfIEyl72vRUWRNzB%2BHZpN4iNhYLcKmRA9IsH6utvzSaR%2BM2HWWOBfpC4Y2wZwUAi%2Fmkj7l77Ssvvy5qCnGx6r9uRRq5SDMmtbbcZWmC96FPF9ZUibYyD%2Bquy2dMTLof%2BwzapgiFmrI%2FpsLLDgPsT%2Bp2v3nYl1J%2B2aJyTfxxpy4mJ1GgnEO%2BzHg8SoUUr4Bl5QRB2iQiDj6QoZunDEcxjR2sVSVdIFtSBQO73E2a4xZkCFxDbnioy%2BcuHu%2BiuuQgfcYLu05NJzQuMnyaIfDWvv1ANPOmfHsVQl8c5mg0M%2BhpWLF4bG3p9gJ7G1ASyKyxM%2FrZ5enPYWo0%3D&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";
        String championatsListPostData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=zuraba1234&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=zurabinio&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=zurabinio&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&__EVENTARGUMENT=SelectAllChampionats%3A16&__VIEWSTATE=QynkspLYHM9ORQ9gX0%2BrVxMs4XUyBQslNi81PyomldnUSMIxoX1aiSnuJitDhtnoO5MVu74OUMlOoqqEuMAarxPHkX8WZ46kQIQrL3ThJe854TLFhmoWyMpcSh8ZYi5zYeYBB4UHsfA10ZmaREX9brLFcMOGFwhMSfI9L8LGSTkQ%2FeaQI%2FBaQrDLIQaCRcDH8Ry%2FIvCZ5LsfUnB45VdbdM5TJUhu2pqzMjv%2BTSeJ0785DYvMBlIFy6AFffXnQi6fQCfDMNdjLnX5RwqHVJ5GnYMGaBlJfnXH1Ocq4BAAaFJe31vN%2FuVVhccqwqpvtewx1zYXdqyX0%2F3FQs9Hiu9iVzz8bXJtj89EFvh2C1ksBxK9Ezllyd3XxOXh7PC4kOFTESi6L7iL2E%2BC%2Bi7TvyOgJMAcuG%2Bjy4%2BqYUPlbeKa3vynlxqox7v43OZiQMFZ6BCyBNOuMQLPs6FxZzGr2kwAaiogU%2BR%2FmBRbPUCt5fLU3%2B8MEJPk17Zc1VA%2FbFSNjf50GVeIvOHTDHVzfzi1%2BR80MU6ZZ51ygmy17%2B25pqXFPT%2BAbTnYIFQ49T%2BoU3VD2dqokei06ohVLpno2kd2YE%2Bykbkp%2BGjMdxetyNE8h%2FHZDG28SK3EolWOd7VgJcsQUvISaVpmN6DEFIYd5Mukx5hnGx3xYOtTR9Wubts4NBODRhiE4IllfsPaDGowceIBCUc7konyaPKujftXYUVFImBa0OjqWPUAny0UgIhIBCTilAsXTXT7raDl26S4Fwyzd5Lkz4POLdplHyyN%2B47nj8eY1KK0Dlynd8NBnW4ZaK%2BT%2FOntyk%2BPVHyKA7yDLD8nVQnwfBFEiZJ1gRwOO8NW7fbCt9j%2FBqt%2FaXOZewb8i2rFOWVMtA8cGA0cYksWNH3G6XImpZqOfLQaGxtXx8e%2F9mfrjB6o8TKR%2FVtgbGGAlGox5NE4RnrHZL3lbk9GleYxfu30M5qy6ryd3SUmyVFbqoNXN5ilkEYHEZ2HInTgNZ9XCaEPXF8YcI7N8LuKnFa4gumIx8cOVbEAzHoQoGgLFNupqbTzcPbfb8Lg%2FHzfosGMVilgjRnE2mCdinCzmxXswFBXVxVGwf1PVYwv7Dscpez5Qnd1EixZzUe1gca8LYJxuXEsiTpUiwhervcWSILDWYIB9IAiP%2FcsciqLw%2FyYOe1y%2FJc%2Fncz9cp73Nm%2FMXib1ruEzEcx6RDrJpQXvo4uqz18%2BF0Lo4pVzyrbAlYEUc2AlCv0fBbrEFNqtCwt73RMtOKe2OYr5syjZ5fJyaAnXQNVnPXO%2BVGUV6KhXF78RdIA%2FuNU7k%2F%2Fx2ZqRicKE62LeFxoLoQGJwu2t30gHklx%2F42mqgBHQTd42LqFEPQPTjnAgTwAap%2BeZq03VpZ4wmLxzkjuJbjz8MjhVPlBwNYqOY5d%2FWtalYTS8cvfyzerbmYjJAnPxkAjeJ%2FnWd01fQgZQ0W2SiY6cou8pXYitkDIeIiRlshKBLkikn7686kL0x%2B%2Fhuh5PGyx2IJ3MiGBGszZYcFT4ay0uwiqYWn0FBYzogUOQNtoVACaZV%2FstvGEQVAAlfy%2BnM4aeqNXnwW%2FroYTlhOVhsTyoOHX9D4TcajToCbSz0wRiU1%2BfyY5CD073W94NKwDfoJ%2F5Lyqj5z7t2KUYDcCk%2FHOWPT74NDpOtVKeDzRdW%2FEfPPfciQz4TmNZ5eXLiCZdV1qOo0UWVQ3DZKwwzMilWDm1dGlnjABCDiDU4FT1mpC6FygkImbTNTCC61Q7%2FbGHq4j3TzBjzjP3sKU2ndTg99xir84TL8Kc0MjK1n%2BLAYItXBiY2M8zSuuBgJZffTTZh1KFN%2FDCcyhbKQyMHphKWIhR8VvKCfHCvx51qGehfuQEB1RIykoYeIB1ZmRAFi58xBqXuvlapnhaDEO9TF4QDs4esKuRhZKGugwThsvmV8TmcVopHMFkb0YuEJutAvEmBvRMdNagk6FobB6bi1SCrw%2B9rj21qbDF4hlk8fbJugx9P%2FZ4usSdDxH3K2nZosdUZ4r2oTU932b8aA5Jcwwa2HbotR62Jd68M3LSLvcj3KFUN9qO30ucFj5oFLrBC8D6GpoQ3ftuBrkBSPcae4MNZM%2BQYDFU%2BPKKm0%2FxcIPu%2BY9YRIYf0jkqbySzo%2FVUFVA%2F1tpi%2Br4EO2HFEH%2BC0IQ8WLsM93GYHem%2FEf%2BTtY%2B8h7GsAxv9ox1wpr%2BMHfOw9n8HBnEq8wFkjQY9b8doNU8Em%2B0in0DnCGoK5sJ2bhXE7%2BPh5O2YpzmeehmQ1suevXJDcZcoA1hVVVWg0mPMFid40ufZO73ul9YlL%2B%2B1CFkkF9kZuSqCUyHaSdCIqmaWnvMsQfVMFfW63x%2BukFFYpvuOhr9XMITX0v5Kf%2BJRvWz3s12CyTyrHsNLOvzf92I6XmbnY9O4hqHoIJZZ%2Be98Vh0%2Bof4caKXnaVXbdrvLRAbGabvTeKtesprkS1S0onEU273296BCsyIIdDDhghue2soWr2dVV%2BdXbo1Z%2B72mrSE6e5hfTAPLeEO7SH%2BxceRmZGL70NoCwawwb8y2Q9Reni9CnAzrrFOe9VbDEQdebziEJ6FoViKCC3c6tvYAZ3Y48ylw9MpiniOmHG%2FITWglO9y6WYmBxi753Y5SWUqFvb74KP2x3RDspbr7rLaG8tficoK4JyH6KZoUxuiUJKCjnKwp5dDTFf9yz%2BgN8NJTHDWBzvZ1Ak7hriHsy0%2B0lIR5A7WDo8CqRlo8jdarbREsWydpjkcDehAP36ncHmIwq7CdXFgzCV9SfTfMcCXPhV8cAbb3UVy2ZdTSciDASLQ2L9j67gbXUHBrBtu%2FJbcl%2Bjy4qYZ%2FAR5d2t9RshjLwNEwdzlqlqAmwY1IrhtzudFV86sa97GFB4R%2BnWPrLaMD20SsWH1gCotCSAVrBRe4kn3iiTk0vPla9GpvyOqyhhYVllpBqywBOK4z0MMMzmxNxuGIkgC28lARITBbOUK6en81RLFPOojQZVwTfZ0UYyvw9XlCoQCdJ10aQJbn6LNThvRowzpJSgiDr3cNZoCdP6vkySaNGt2dZEOvvdNhTXauhzs4vjHGokdZLdCCJkixPfB4nfRYUxEYLgd7Qy7%2B7yEp3VuNWKNi%2BjempaA%2BwZJi8yKSkJDP4meodUJTgfI%2FeTNNTDevO7vnoC7Du0H9FTB2qnV%2Fgg5jm50zh1DoVjor3aFkVCUnv9Gl%2BFl36K8QOpIszhto7ijb1pqdjNJKOt%2FQ5M6%2FtjHvy3Z1RHYPsxGXfuBVPIDK6Sv7OJbjcY7DJwj2s0vLr6Hb6qlEOqSQhhYVUEL9eTUhykpXyvN3%2BYNY4COLJtT%2BZO7rSgSRYFSfUKV83Epm1cZ0%2BTwdnSgIoYneQdgK8MVpj0XiLE9KRXbIVaDbApauHbMYhR4Zz5bzfEioS8Y9gY9JhdpAcj4MDiAmSbmr46dFeAMNczeKX2M35hEowp3cV6XkII3NLLo8IrVIp0Faj7wUbs27xFH4IBeLmgir8TGQ4YuMdVtAboeOGh7rcn3oN4b7nxhV0X8yU3ATQxHjpT6VAWZwSlLsHn2Fg%2B7eZQc5WOclMz6LIAJU08zzqGYTcCVpWcb6b3QA8XjSgFca1g4ntDnz99wCe%2BgyzOIKSorm6ievjN8YlEohyLoRY5lDKs8%2FPI%2BR%2F7LtaB70B4IhQdlaKmWFCMgX9Zktzkl%2FNRVl8NAevX2HkwmVAzpdnM2IV44%2FuqSttgZl988Ivy48HsJqYiwl39EfkpDI4XOSuzJcqYjBXXr48U030DixF8Dx%2B5O2GaT9wQoEsGo%2BYUoQ3sMK5ENRw%2BouIrWkKZlG7gvezSysFPhe6De7KfNJU%2BsL2TfQWyq%2F%2FiGit2%2FzNJQP9btG297EswqccXaaXOd6luz6gONKHS8c8rHkJSuwtDWSE7OUGpbeAIs%2BkiBOimRz3C850AeCUY%2BQdfjOUHtbpDzneYyBwXhM9m863a2KfMqmc5mW2WQ4j5xC6oegG5%2BLqC%2FOzUpbU0E0Xu1IuyqT7QEF6beAf307lG4Itjvcy3pAgm0Rmj0L9s5F7t0DLkxh%2FgohNvqp45b2Hx21KqxCOJOVodEdcFa0UGYLIiDLEya8O7J7DAdFH1fhMJRYlturhLXSRfpIKTvqNFzdK3AM1GBKOA93yzk5xqBX3YeQRILGjcsg4sqS0CxpCg9KOL48gBeuDMIR%2ByD1SlAvLhNE%2FfdNMGwQRn7C5RgM49R5Dy%2FRMxAFvvIsSY4%2FFxzDXY0sZMiWvdW%2Fq4RNMGeJb%2BriHeu2gnNC7Dk8nTT5OqS%2FzZlAFRe2y3p6YbSSk33TRuXCRA3pUvoFfsxhRuGrqKxflS13kH0MMF4QPYb9G4psgg7FtKGXYRzCdtUWX4q9KXhqsJC9ZprNA5GadoR2%2Buxm3japhvFLArnYgPBbm90IwRckhQknsd%2BNq1kejSJQqoKAp%2BHJw04ht%2FpIb%2BYg4WdJyQ28t8BvBX%2FyhPzIKPaCrFc8d5eCkE95CyPrZ1RLUVpJ%2FRn0EDkg%2Ff5Od%2F0PQFl4L%2BrWJCNXroCzwBMe34NEoptJGzZp%2BrA9BAG5ZwH9W16CWaA1E6zB29K9gXC20nZoWQTKEdTYeNudJpA5Sp1damlYjeExUEEiKRxZK7BODm4rboUl7Z4ZOhSMf4EPV8Z%2BCCoGTdhr%2BCWvXF5vhKKui32R1ynUSANfdZZbIgRBfPGYG0NpaSoV3qeJSf8GMRpkNN8Uxyld9nZKaoiq80xKM5NqVAhgQKte1TZE2DSXK%2FYSXy8J3Hv1KP8NObvY1saiMnQ0y7tdbU6mZouxjinZrbhHFXtz%2BGOKbf%2FVH87rTY%2FRQzsOfhgWCuvnAcgHq7Ho3lMfoamaU074S5RJsPBS1BgI9YgsnaHJaJvk8Jyml3GGCAxaXHgxYpISTy5o2nc1p5X6fCbRXaccU6Qoeg36QBa2%2BESC2ysFWCAS5%2FE1&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";

        return Jsoup.connect("http://www.crystalbet.com/Pages/Sports.aspx")
                .header("Host", "www.crystalbet.com")
                .header("Connection", "keep-alive")
                .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/64.0.3282.140 Chrome/64.0.3282.140 Safari/537.36")
                .header("Cache-Control", "no-cache")
                .header("Origin", "chrome-extension://fhbjgbiflinjbdggehcddcbncdddomop")
                .header("Postman-Token", "0bc116ca-cbf4-b83c-8067-44d1ab3df3ce")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Cookie", "ASP.NET_SessionId=" + sessionId)
                .requestBody(championatsListPostData);
    }

    public List<Category> getFreshData() throws ScraperException {
        logger.info("Start scrapping of CrystalBet");

        try {

            sessionId = Jsoup.connect(rootUrl).method(Connection.Method.GET).execute().cookie("ASP.NET_SessionId");
            changeLang(sessionId);

            //TODO: For some reason we need make categories request twice to get expected result.
            createCategoriesConnection().post();
            Document categories = createCategoriesConnection().maxBodySize(10000000).post();

            List<Category> result = parseCategories(categories);
            logger.info("End scrapping of CrystalBet");

            return result;

        } catch (Exception | UncheckedIOException e) {

            logger.error("Scrapping of CrystalBet failed {}", e);
            e.printStackTrace();
            throw new ScraperException(e);
        }
    }

    public static void main(String[] args) throws Exception {

        CrystalBetScraper scraper = new CrystalBetScraper();
        scraper.getFreshData();
    }

    protected List<Category> parseCategories(Document champ) throws IOException {

        //champ = Jsoup.parse(Resources.fromRootAsString(this, "crystal_categories.html"));

        String localSeparator = "|^";
        Map<String, List<Event>> eventsByCategory = new HashMap<>();
        Elements categoryElements = champ.select(".x_games_loop");

        for (Element categoryElemet : categoryElements) {

            //Football - UEFA, Champions League
            String champTitle = categoryElemet.select(".x_loop_title_bg").get(1).text().trim();
            String[] sportCategory = champTitle.split(" - ");
            if (sportCategory.length < 2) {
                logger.warn("Failed to get Category and subCategory names for champ skipping.. champTitle={}", champTitle);
                continue;
            }

            String[] categorySubCategory = sportCategory[1].split(", ");
            if (categorySubCategory.length < 2) {
                logger.warn("Failed to get Category and subCategory names for champ skipping.. champTitle={}", champTitle);
                continue;
            }

            Elements games = categoryElemet.select(".game-row.x_loop_game_title_block");

            for (Element game : games) {
                try {
                    // Parse date
                    Element dateElement = game.selectFirst(".game-date.x_game_date>span>font");
                    Element timeElement = game.selectFirst(".game-date.x_game_date>span>span.time");
                    String eventDate = dateElement.text();
                    String eventTime = timeElement.text();
                    Date eventDateTime = getDate(eventDate + " " + eventTime);

                    // Parse Name
                    String eventName = game.selectFirst(".game-title.x_game_title>span").ownText();
                    String[] sideNames = eventName.split(" - ");
                    if (sideNames.length < 2) {
                        logger.info("Name parsing failed for={}", champTitle);
                        continue;
                    }

                    Elements oddCells = game.select(".x_loop_res.Snatch");
                    Map<OddType, Double> odds = parseOdds(oddCells);

                    //Construct event from parsed data (Without category subcategory they are attached later.)
                    Event event = new Event();
                    event.setSideOne(sideNames[0]);
                    event.setSideTwo(sideNames[1]);
                    event.setDate(eventDateTime);
                    event.setOdds(odds);

                    String existringEventsIndex = categorySubCategory[0] + localSeparator + categorySubCategory[1];
                    List<Event> existingEventsForThisCategory = eventsByCategory.get(existringEventsIndex);
                    List<Event> events;
                    if (existingEventsForThisCategory != null) {
                        events = existingEventsForThisCategory;
                    } else {
                        events = new ArrayList<>();
                        eventsByCategory.put(existringEventsIndex, events);
                    }
                    events.add(event);

                } catch (ParseException e) {

                    logger.warn("Parsing for date time failed for subcategory: {} id={} e={}", categorySubCategory[1], e);
                    continue;
                } catch (Exception e) {

                    logger.warn("Event parsing failed for: {} id={} e={}", categorySubCategory[1], e);
                    continue;
                }
            }
        }

        // We saved events as in a Map (eventsByCategory) that has key like England|^Premier League and value is list of Event objects
        // Now we create actual category and subcategory objects and attach corresponding events.
        List<Category> categories = new ArrayList<>();
        for (Map.Entry<String, List<Event>> categoryEventList : eventsByCategory.entrySet()) {

            List<Event> events = categoryEventList.getValue();

            String[] cats = categoryEventList.getKey().split(Pattern.quote(localSeparator));
            String categoryName = cats[0];
            String subCategoryName = cats[1];

            Category category = new Category(categoryName, 1L);
            Category subCategory = new Category(subCategoryName, 1L);
            category.addSubCategory(subCategory);

            for (Event event : events) {
                subCategory.addEvent(event);
            }

            categories.add(category);
        }

        return categories;
    }

    protected Map<OddType, Double> parseOdds(Elements oddCells) {

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

            if (colClass.isPresent()) {
                try {
                    Double odd = Double.parseDouble(oddCell.text());

                    String className = colClass.get();
                    OddType oddType = oddTypeOrder.get(className);

                    if (oddType == null) continue;

                    resultOdds.put(oddType, odd);
                } catch (NumberFormatException e) {
                    // Just for code to be readable.
                    continue;
                }
            }
        }

        return resultOdds;
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
}
