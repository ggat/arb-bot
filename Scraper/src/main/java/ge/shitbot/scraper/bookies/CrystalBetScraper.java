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

        String championatsListPostData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=zuraba1234&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=zurabinio&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=zurabinio&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&__EVENTARGUMENT=SelectAllChampionats%3A16&__VIEWSTATE=IOTx851Af01sTXEegQn7deNqlnG7MTmgjxdKD3OCZGYfFV42f25u8YeZySIWqenWS2eyIZ77UuvHndL2yEEIxQmGQpX1elpxB7uC6uiGpwJ5osTImiB7IPf6rm3dhrMXqA8U7s%2FwZuVskTrNJIMwcdsralT4jOAMA087kjeoaEfWcOu9SGkXD9O2P74XScYaY33IymWEBSgNnzDxwtfMT7KLV7wGMibnmruvb%2F%2FbsfbWuHe%2FL1VcVkaM2t12kypyd18MaWbjOIR%2FusAAs9QN%2FovygAg2Uhq5x6HEP0p7nq3ieAimyzb7xceyjGiQALtJw9S1tp4ZhOp0cm4cL6JtglYeXFj0XLDbPXf87jAy5Ikzhn%2BpNOjXLuRde957fMcwWTt5ju5%2FGps02JBaDmkes75d7LBN0Iqm2aYoP9bKkNgoL2U%2BLlzGeUO9AqhHNt5Qdb2lB66RGvVgh1P0fONaugAMgcpvdzjxcVquLAz%2FErbWGdGz1KpMNi5ENaeee57ZeyaTsez0nBY1D9K1k4yPU7zHdKJoORWGSg4BTm0IWAgFbOHmU1MMc7yeB6B6yFaoadPI4BRcscaUDbQbd49mTGGgUztXzK0lByeqaSISJKEy6ygFeVdGV2fZf3%2F9pagReABro8qeNV5IdXlquuDm7n0SUxYjIIP13CNJUx9m%2FKEypLv6ZQ2fZigsWJClJLFmJqnLvU0nkgUNt5iZmntkR1jvPZikf6gFLdrxuskLPryfIomPA9FjnNntkOWi6iS7e8n%2FDRu%2Bx%2FwwmBMDtuYAyb7%2BYAFF%2BdHIRULgya3em5gy44sOiscu2vpxuMBaP4C%2F438wSua9FtzFFk6yiKpi6JsiGZ%2BpjoVZAYG6OlsmN9NOO7xpSm%2FngjhToia82hvTjxACsKBIjkYKpeo2nnGytOVZfwW505ZzVZnblX8yvxJBTnggGkWriH4y2fe1PW0dB%2BHuTSSXHwDfqAwIXjxR2yz4OxDAByImQimF8hl0Fcxlb8gevIfIYKBaaYyBpPFGNPmnWyF2b76JorEn0717cFX%2F6QeXv%2BEK6TgeEjgs1ZmGaHGLw9a6k5EPFKOrQFK%2B9U1zzBpSALNUXjnQpoGL6L%2BSmbhm4YkC19E9aGe%2B4WPD1Pti5DinBBtnexVeX9xZDe0Aknjm0fLKWuLCAc6drCMfPAf0FTlQsFVtf1HLe61s%2BfrUBfVYm0scZ31nw2fBbKnElB0vfZBJ8rDglGQV4irGV6HYImI9DMnMbAQ9SUUjbBfmF2QG9cPu09hiUURtv3pEndcq8gWAArpN6yU1xxqnMseYz02hBIzTH6UleiPS%2BvWvrSz1xp8vqh4awnOV12XOxqVcPQbHlU%2FpmsQ6C%2BEilV2lNPVijBsYsBQbDPcZluE%2FTAA3OgSSsr%2FHuDVHH9gufJsr7HPKJtxKWNrJA3h8w6O%2BxJSD8BX56Xg%2F%2F4pRGqQG4o4vLDoNFbWOEOiJ%2F4bFzzJjjFBtgZ2vOIoFXQlKwHWir6qyvuXqs%2F5RmS2WqBXVz6ZwwDBBx8mpS8hJpnqsW%2Fkr6wu2RrJGoxm3Dhd1dPPqLRizlthLI7w%2BhCvZwfs5bhcBrbTrH9VRQl%2B%2F1uCvDPCQ%2FFTojc2Io9neV08%2BBr47%2FETgelNNQd7bT0oG0i1yGkfeo9AMIRm5PSAvsd7Tmfl%2B9mrUX7%2Fd0lJTmWHPaXoU5Qma9FTfc2es3jIZE%2FsWFw9AabCivYUNm5%2F3KHwAsEwGRdr70egiLftyuF3606jjg%2FtfIzUaKeuR%2F2hnOGoyyaJwSdGC%2F8Cj2kUcR3JpRQah4Ta%2Br7eCu8It95O%2BatDdYJ0yoiySOjwEDe2cvuOybnRoXkm339Xs3dsq66Jvyug43pb5s0xER6wwc5lMZgT5zcEny7aMUTXR%2BI%2BKRA90qddE1OqjEQ6rs4MF%2B7X3sj7AS6XVo7srBj0PIWXDt3iBuOHa5CmJBLrGLpZFfFDq%2FnG4xCBAb76Ga47gPZJ03PihohqvMLjZUA4Y4ey6JZv%2BTgntwx8vlrojwo2%2BUVtpBgMvnd2b2G0Ki6ZiiCJ8jcDgWHA2ZDdeW%2BX9bLLG4mm7i6VVmf79qh4VCBC3BpoZtSJAmSnHGerO5yftQaPxe1SGDx4GPQDqjOQJ%2BuE%2F92IDr0cUc39V8y7OKbCUQmObFvJiFAvTFmErVAx4T8wdBIg9yqZUGAMHGxZ0wLXFc7nHLPOMe%2FK9cNKZgA5fbeBp07iR94xJnE%2BTyJxNHL3ydyO3FXxf%2BW4WqcdHDtYn4AImDurL5EsdOkY7%2Fpf5kLnKiRIoo2%2FLtobVW%2F6OCb4vps9g9jkK4XLjv8%2BNXrM1bQvKTQ1TBlfSKSpY8ASqPC4c16R1ruw5L3EhittyUwqFvhXSt2qUGT5q5vDgHmcPWX7%2FXH2G868Qlv7%2FXVJwv5b4hiPHghOsqax0kDT%2BHJ%2BDOTt9MRHRuxFl5R%2FJUeCM1lPzDqliEQ7FlPbjH2MrW85RAWyEgRthL4A6IrTe6RLbRstMR3PSvw2SI1dN8mRLduGIDbUyE00p4gmxTbVfnIBxdhOnr%2FlRI9IXSV0YfMgqVBbOM7Dwyw%2FRNz5dMsjIjxlbsJrh52XVbVFtJpMjZnSFFzVkuiWtlYe%2FVbKqG8132sYye4SeYkouBLzkQD2gNC4MwuRTtQk%2B%2FuNGe2W2SaYQQY8Oe7%2BjKcyR9i%2BfqA8L6K%2Fzu5saF%2Fhwc1iSXfGzFfxSViJsoILP2bPhE6qLq73uyjp0UOMuTzbi%2BnieuNi3xyIakeR7Xm2ZKSTJ4stkJKaXvHyFq3ebYIl1FhNzyIaD0PIc2P8xWN3TQ8HxLSvDYbBcgUp7o4lJKrVXpfFYtkh83CMTT6OFWTAFjTQrjvva9dH5mK8sqvTMYkTeSzDGUT%2FO9mGlBISozS%2BmdvwzgFfPSmw7oy35ohpFVcvmwreBgUFrOZ3W2Oa6Sa%2FnPCeBWqXlAmqfmk047YdkVKWXnrb8TemuD3qPjKATdVh4vq%2FtW%2FF96ZWxIbDQA0aVdSVN4AJTcQjcJ%2FQP8tsNxoBw%2Bbx5PSmu%2F65jENzpqrtKwAWwtcvFkDIFgsGZHZmDJZ8EmCdWseK%2BaK10eOJCbN0VoJVjE4B6sAmpDP5MAUP7y5LQtILm5m5BfEYIipj6avWFi8HCZEmqI9DUVHsWXjMeW26jXgXVdaMv7iSSm8Up%2BBQR5eUq8iulx0kDGtbDZHRSJT5PiK8%2FnLXc8AWw24%2B1KRu0pe%2FjD8djTLUI%2Bi3If9%2B2TEFT1K3e0GhkjBc4bAT%2BH%2Ftykfb0ddsOdKHqOmN163A9DjLku2PZafkmywuwyQ6%2FU6%2Fvin7xa4dms0qWBvWGopV6EqULdYVDcPpA1J8fsPqeMJOeaqc4b%2FBEPaLOpYZ3AwKFdSkYkSJshID0Rg%2F8MCiYXzZso6C2rh%2FWPJO2rkEe1QP%2Fg%2FAGrhMTsVHKj3g5N3RVAqcdiCi6vVKYuukdHgC6V5YEGSOGQ5f%2FxISnbcD1qc%2ByNyprQzcnYprJqCEqGAhGFYGDcjKOjJ0H3IR1EFemHUv7U9dPNkHkqjwsfENS5qZAIhIhtKiuHqCv7B9JlpHsq25KNa9gQ1EfHqCKgIGGytqQQsjqAPDQepcLT47GTX4xJdAk%2BRMAlP3kU6dYxpx2o%2BGMtEJsdnzoGocpvtR3YYy4MMVnaiQe3L4xwT%2BLAYS6F6bgvvUN2kR8KR2eAP3cfBfhtyFxfFPQqQhMtRSgHDkgV4VZ8MBZ9VsTyBB4QC5QcXGQbINO0BOtVPocX7lB%2BpWleDKwk4vxQIla0%2FcqRUT05GJmjNNtMknZy2TeGu5aR1cwjfJqFy4JtaX3Ag5YN4mZaY5XidKIm%2FQfMX%2BzzyPXJjF%2B24N7uyAScWooW%2B%2FArQ7h4YamywKbAefHLDAjXj%2FdlTmRQ66%2B%2FSh4R%2Fisl0N6lNlE4B2JR6AYb69TrVwIjQPEi7qY5ehp79N7bRtdhkwrmuhJLmc9t2BkSWNe35zVTRzG342UNHipPAerUOScITQReOB7B4K6y9NZA%2FJNFUD7ju7HInSANEB4y8e2%2FWHqIKHq5oVjZBlLUR%2BeerDKdCR6XjV5lnQO0mJiik6oMEFBrkx%2BgJN1JscM%2BIjIORcoVWzHQ6ZPAELjc%2B82zAghoZkxj%2FpBd0Xjz1htezysYLJs6wVYgXiSzcLfEerMsles0cxRN4OwoEMRrOZNwpwhxfIEyl72vRUWRNzB%2BHZpN4iNhYLcKmRA9IsH6utvzSaR%2BM2HWWOBfpC4Y2wZwUAi%2Fmkj7l77Ssvvy5qCnGx6r9uRRq5SDMmtbbcZWmC96FPF9ZUibYyD%2Bquy2dMTLof%2BwzapgiFmrI%2FpsLLDgPsT%2Bp2v3nYl1J%2B2aJyTfxxpy4mJ1GgnEO%2BzHg8SoUUr4Bl5QRB2iQiDj6QoZunDEcxjR2sVSVdIFtSBQO73E2a4xZkCFxDbnioy%2BcuHu%2BiuuQgfcYLu05NJzQuMnyaIfDWvv1ANPOmfHsVQl8c5mg0M%2BhpWLF4bG3p9gJ7G1ASyKyxM%2FrZ5enPYWo0%3D&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";

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
