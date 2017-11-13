package ge.shitbot.datasources.scrapers;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by giga on 11/12/17.
 */
public class TestScrapper {

    public TestScrapper() {
        WebClient client = new WebClient();

        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);

        try {
            String searchUrl = "https://www.crystalbet.com/Pages/Sports.aspx";

            WebRequest webRequest = new WebRequest(new URL(searchUrl), HttpMethod.POST);

            webRequest.setRequestBody("ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&__EVENTARGUMENT=13452&__VIEWSTATE=9SKyvzm%2FndYPfOpoLR2zXqJwR0wdGKVsyVlXvuFQ7i%2BnteB%2FKhKqdK2v7fdDA8TTWtGLxY8qJT76CF1q66%2Fq3nrpYr0j7LPGeNNJMLeq47CIz3SAEIBYOuqj%2BFUqXGzJZe9c4SB5vOss%2FCEB03jiMyGW6haE8jyNbflG6F%2FMr%2Bh%2BUiquGwjxLSClvByyCDSCiz6WuLCahvFFf7KH66qB8ZJiw48%2FP4W3dd3jJ6ZfLgW0Ctlpy0rE%2FjbRzPkUOvUrO0O%2BvlT2n7jNA4cDyasj2g6L74BcxictI3RgeBtP%2F9EZHMqjPkgKLtgB9wyA48IoGQQtaW58RwYmssPVs45QsHlThsLNIAAvHRp%2FkkxSX44Z81Edvm6bqq9o5RHSOFRM35%2BsozIEuSAZySiazV6chobb2Tiq7PmAM%2BP%2FOncry%2BT%2BrAgoyEXkREAYHTCPx1gIBCAW6ghHn1OjuPfwV4YejR0Mt20BiKa5GcQgu1AGgKMZH5yqFTIiIj4wzpbjLAhuRyAtGfAScSBBbdKJW0eJe9YiMCoC%2BsJsMtxL4N%2BYWJcCB2VSyQ5qfpE6ZCZasxuYlloMmFjahifLb6vrI%2BibJ1l0jyiNtCVSDbDYMKxDys5h9ZgfA3yNzx9JK6THTYVoNzgaKeOKBTUN%2Fhxgb3Aq90TWeqQIZykBjdMzFFkzxUYsQXIkYd0Z9%2BqNLJ4c9K27hD9rAna5qPRL%2BWgpPOXx6JYoyRrtsrqvtHkqUVkq0ojs38ILMqaXuvfngJIvO0GUD1uTjoOzub06a4y9H1Ba%2Fu39zQbihg2dtz5QOp8qIUd2r%2FE7fJFBaYKuRXWfp%2FMdmjwXqtvh5B3AEMQWr8F7zf%2BBtwHBN5xN8HOC32o%2BCtVLCV6O2NfqIRBOWBnkhCXStMyfPIYnfK60AXX8vsDYphz3o7dKRrE%2BxhFpZ518hYYwZM1p8p18cEgt5W%2FSCWXufS8SraUd%2Botm0jyg%2BFYJ3Iqtx7fTPOAJq2Tm38%2Fw7b%2BaE82URDDKq8S7ELrMG0xXEFErYpTQxS4xTKilFu7nmgSR4TylAnrmY2uSawNwZw8kot%2BMc%2F4%2B7KsrlFP7%2FYFd6EAqj%2B62ff5NwuNmbEOaLnCo7%2B7PJm7p2xrxFLDWxZUxC897paISbv%2BODc4qt%2F3N0YqSPfICuA54ZX%2BuOefWjXRsDezR7w8bAYTemzoBPzL2X%2BtjuPkFt9jmSvHhXHXMlCdicWncEVCz4kem0tiTpFwneZs%2FaMnTdW07Wi7SIafyGjo7G2YM2l7ZPDwjGNrHeb8BawB0N3IF1Hbb4LdwpHs%2Bm9Dz5gqvo%2FZ4CQ3rhgx0Eu4Yvsy%2Bj8W6llrVO612GEitrJCsmyFYliAj7i0GH1wPeMM5DBpTaSwx6xezmP%2BSWrz6hR%2FN5yHociOtgSYq9VKUyVtRxG7a2fPkAOa8penY9JUI0l9tsBOVvVLyW4VjnLmeFmaXYBbtDZu1H3dd9Fs%2BwpbOyNZ%2FxwbkRbmPXa%2FcmULbFdEKu66aeRwvIjt4HHqP%2B7K%2FEdlXCjJEZ%2Bw2nqNwcRALAb7sP6ZUIuwQ2Zl%2F8keTxUCllAcoE94u3D2O%2FnPwNR1EUHFoUnrGb5KX%2B4XfwNqCB%2FzOgkzuYJwD5PG157%2FgB1iqi0x23d2e6WgttTt5DQoXXrudEhxH8yUtbqncGB3S18AdEQgcEOr%2BrevzV5V7lCopyUWnu6hcijR0e5CpfYQRIbQu8zSZYpmKpMfimDFf35ZN1i4reDTCm93FQW3Nw3XPkcuuhq7t9h3ab%2FZElKtSZ2uIaA41P9VVOD67C38t6%2FhN1WOei40kQtn4%2BGs1dS1JrNdQt%2BiUW51Y35eDfc8dHvfpxm9apQlD2QO2aWaUUbYj4jQVSd1MJf4QDSpHj2qGeDQFhVuKhzrx7QKmxQNLKxSkKr5lSU87ijI7SNG6xThyU4fwHsHXY0TegbEwmlRpBzRp1O8AsiYBdt%2FZBoRGHH4vja5BhXILKXr3KlNTaSBudFS0sEGn38rxh2L9MDDWKLW9NEeAprr9W1C1hRvIpQyJgwZ18zv4%2BlvXj1zIx3FjUO4ZaOmPdIBVVUadKZqnQ1O3opoAb6cxl%2B3dDlyEFlY2KcW9e%2Bpvp%2Fa2LMl8Guui4cvbfGqh2Eq62tNsJOM5%2BXl%2FRQoQvF4x7U%2BXF1w2iWFFtrkPlHkFLKx8ZgvvIf0sdM33ZY%2F%2Bg9z%2FzXi5sI4D0aQTw6W4MiaosApJSu1NRhKt%2B1ezo7JE%2BYwP%2BkDJ0ceqGREaChirKeUek5ls2U%2F%2FrbiW%2BVMVqLDsKFKQS9z7rrNPnzFBv%2ByF%2FLx%2BPuFafe917QvPcQ7QiB1J71NU40FoNsKAY7i%2FQdS5O335Gka1DfjNdK9xehcltCaM1Hf0ovclEFtJsVMHfmYb7vDsSSNMPivlK0cIQfEPajZy8N%2BkD0mNLxJgree%2B9Ty1JUt%2FdRdYqkGMvdkE5CL%2BvjXJ4IWbnGdSCpCE6Ztx940CrKypi1bB54OivUXjwbH4DPt85z2R7ocTW91d93LRidwi8zRmwxuFp0EPY2SMAgnKrF3J3AooP9uFhr8l5p1aH7MVSZ9SsnO1wDRtWvv%2BWCx%2FwWGz03oREsS0HwCtziLFTf1YUtFv%2BZG7Ca2FdB018NKsv0sjb41Uh5cm62h6wO7Ds2lCLtMYrl1Z6hUexhN%2BOsoB6O9WrmQbT3fyke3idS80q8CCgBBABK9l7zZbp1MjVM09Et64NSUkmFE2d5vMxSPDTgrJWAa62KWvgMfkUMdsvxv8P1HMBS5f1KbkLupXFRwt7eg72tein6XeWq6lyfNZ2jzDnhYMLy5Nhmv%2FaXCuBRPkudHBU9FLv%2BS2iQvsQrn0LBcWa132okCGgAolFpK4UE%2Fi4QaGw8FGnji%2B5GQRRU59k%2FIqGmNyrReERwV%2BZjafAlMG%2FOK8ezV2R027WQ3yYjkPQnJfLYKGaCOeUyRlCFlTxQeU9zG3OhDKXZvvwkpdq2jjbAfc2EdKIWozMoTDVBLFH4M4m0FT9u1bsDRy9NqoVIUUDkUDbbzLUjQLFvlG3uOT1GG9YOyp%2BiBtB83Xx%2F0jH3LtbnzAsVvkAtcr3EfBYkDrRXF4D6TBJP4egv%2BKJ%2FYFTo8bPqSqM9fkioMLoW7vqVumDEfgJNebuBZlMuVyHGocK6S9QInoYSeu2UfQ5cx3ehG03iaL0bU4OWwINqZgBlexGYpMev%2BFxoqNjTVXMqIVXsi5N5DYV%2FW%2BElvh6Oh4Y8dhgeW07couv0Zpfk7saykrRwvb8C%2B5pvqRQEXwKvmfNvosTUiQsSdsw0bdOAbAqhbR%2BoJxkin8VEVBQEfOOP8jShf7kCsNnXxyZ7WS1WA5Ffu5mezm3K3bv%2B%2ByLRwyRefAgfaP4LzaNOgnqRdM5xA7ACnVCKb%2F1kdaeLb2xpddtRLNjuk847NZqhGhS%2FNnZDYgQ3QN7nAeRTREzjlhvTXwP3%2FRaoHGXg12qisx7%2Fc1SoDstVibXIpOdfmPmwjw4A9d%2FRZOSqXp7RtrE%2B5a8z3pxFVBG08RseDXbpLaBGTKjpQ02WdNCRMYomYCpDo2ON%2BOsz1UxI8xaKHpk%2BNFhqHVEfZfPQHb%2BLv8bQ%2BuU8%2BFTX9z7lD5JbWbvvdOznx7rQFapK5pGPsYx3ERGiIohDL3KOrvVq%2FWoTs3JGXVZk6Bg8z1I9CnOPjmtsN70l0uI4L%2B5bxEjOUaEDXWI6PmL2Ci7VjOQshFMMM0GCkexQU%2BydzoiiTXZEvoeyIqRiclK%2BIgVtEydaHZ3GIduwom4hKn%2FtGYNz5V8ah2XHNVULTb2RgBVTsl9yNeDd6Wtva1AU0dw6gn6d2%2FxheS3jqQ3wU27d9kMwnXCe8GZDRqNKVf84h%2FgtqrRBwg1NBrxrlIkzl2ojm1IxnFSpMUEuvqcgFccAD3v1VGuMms9Kl5euN7Z3bOHZZU%2BoNEXLY8RSQic1KVdZAVb4Joic9upj5O5g6BLTtvN8M5twvl5xXb%2BbgS1TrHa8mjvh58g%2FPqepasebzMFBwnAeO9x%2B3nzKB8Bi19vGsU64Zr%2FOxie3wg9%2FtgmCdN9idPa7OGKQmNyla2qSAjwioAjMf4I55ErSAWAYeB36QXenCZZt0HpWiYH88YW4%2FaIGByBaPa0K3TBFA89%2Bpv1Wk%3D&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&");
            webRequest.setRequestBody("ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&__EVENTARGUMENT=13434&__VIEWSTATE=yXPaEhfyiiwtdWp0Hdswdre35Os29yCrA1D%2B%2Bk7wW9MQPBuJ4fA9bmogu3JpRCofRllAnPA3Uyk2ALsTYAo4OuhvM5%2FuK3AM6o%2B%2FD3ezBbz%2BplCVFgadfkL16y93BOesPVTg5OxKtEsb%2FjP8ICXbht2etx1sK8TBczMCuZErmWkg6AKq3VR4NEpZsKOsTsPEI3XkoYtaERHLSK2fUmzxTENsbBN%2F%2FZtiSSSKV4RYoqnezfUtFmMs92o26vhS%2FcJGl%2FI1NhcIf29Y%2Bc5GtDjYNZg%2BlOZOCNbFQ2Q4fnEql92ClhUDc7mPG2upjFjf5iBAIXRp6LVhjHdNv7%2FUAh5Ez%2BDsCF6OOLK7PdJVLSoizbwuXuS9JWMtp%2BpIASlkVmpOOgfiKnnP03hR6QeTzfZPo80hm0iiiQMfgxU2mtRRIHrnWznSFOMk1HG4iitCrvOg1aYcSmdxHTkQfcNnj3VlSc9jsrsL7sQw0gdYaJA%2BEvpf8LrYwPYvXA4WiAXwxwJNwfnOiYm3FNZlq3fO%2Fm3G7bxmTZ6kVwnlUglYjoXUYqu%2FJuPrRv59T3D8Z9bXx6JpXYSAra6In5kfYuZMlTYRkpmgkmP%2BnwHYPb2MLDqbyGaPHkqLVLj4xta7JC%2FZZMrITV3peerIfh83Dp7F1fbbxJtThu4YAFFJAuDIM8J9%2FOQ5oSynbHm2Sd0fyaQpOpWX46D8jp2rzfNlk2fkWl4ACEIfEa1YLDBRLjy9zXx4Jgff%2BglfH8NzLpLpZzDNGQ1TIOVreo7NcQPSUmAA4mTETuAIbIrK0oMP1KT3IVjd3RPnD9VJginIASvIk4dJNMSclHwPiJBIKW0DP6oTVZaRhgjNR7sI3P4do%2BwTLMyciqy1TBTowZxv4lxMc7qVJs5xM%2FKEhQsM2LPeBbnpLeHdqQYWoFF1mYjwBXV33Y9jbOxx%2BmX4l2kOSvzI7Pgo1wt6OAjGfEzkdZwAbkeWnztnV7uv4kybYsZp6BXCsDFpBFwpvh9kf0IR3%2F5B6yc3MozzpEBGQ8TYntS5FKLGxxuQ2yHuwW1%2FjKVfrXkBeiiz%2FXObgzHsqcLKWzVekaOtDhxtplCha8tf8au1MZpKbftGuiGoL4Ra%2BvwUvov8xwD2fcmpJQbTsJlLVJYREYxl%2BDlOIcylO0oTg3LItKoGgGKrZDI%2B0w8riVqa4Wkz6x9Yn3aHS52OMUrJCpgNGTUoNqAP8UJdPSC8jLxQoTS2Fg%2F08I0eSpgeAY1CC3GJ2xpaU75ugNtr53QJ%2B097IC0RL2g1AwA6QFhwok233i3qkvUqLggrQQVYYxw9hQEAGZfqcTLZGnqZn%2FvrSePKGh3VBhfsZeAfAV%2B1RfUSi%2FRvEg%2FPKBiHWfrhStRC4pIFwByurEtRm%2FExWKU1a5y9W9bllEHKya05afs5FLM1jefZjUfnxNErlnRcsIckmCxTes0TTRu2IGnpf0T936R9tJ%2FsLJJsgqde5CHbkgV2tDBecd5eX53%2B83wYIJrOe5935CIfrw%2FxHmFJc8klqMTcf8Gxbj0ILSJQ3BYzg0EEFRKQjFj%2FH%2BrBM%2FHSvRc3tGFCTvCSeaM%2F6pz%2BfsC%2FG7Sdg0m3LMHs%2FFOdKqB8xJKuSl1abKrr4L0Z48qjzK4JnOZlo0vweGSlNtmPBHvRWlMh4HoGMw1Hk9gjYW1tZ3kNGZYrWOgTxXnrJboOjMmnfhYRvvJFawNwFVcj16KJD0wCBG5B7HbzM%2F0bwekNcNJ6pynNoigy%2FIAtZtCilWQ9Fo9MUKpi%2FIXnnqW%2BK%2FRcrXPpR1NDi7eJKPz5nluEGH8wLrsGCpkJeXFn4nJoU4T1bUMM%2F92tYYfpDVtxYyQBnmLdFdbLYtmPdBxMXd00keRh%2FymNru%2FIpwvlm%2F61I302giGuCkkMsHXMFeKVdsnA6GjeRGn6TAvKhOswOam8cvx9IKqASX2aPLOL9rk2KVB%2FT0eGe1S9yKho0TOADEWEg15FyChIj4sU9LdoYKGoPEfmuvkoTyCRtIgSeImC2UjKfUy92vg3rR5YSSTuOb6EVZaazh9dLATAi1UP2a6PPQMMVPoJlDmr0fbS8mJS0VO2SLSqIe8GDyacW7kFYB%2FU2n3UnDb3c34xCcBoHopGd9rXav%2FzA5MxGNGG3FPOLVfPr1FiFUfWTmDU9DI1Jzr7z2BKmSXZVE5CSbGP22%2BMoskMqDoIulNRejQuVpqqCGBu3YzHby9LirLBvk4E8tMoSzv673spjNgCuIyr7WzDWkfl9wIYaSbZhvsuhdGL2K8lOwbFGBYRfJ9YxRL3YuOjoDgYA1qfaSb22H6%2B7HgJOsPmnsZpt2wQrl99%2BL05HL9myBQgEApnVtv84jpma6wl%2BdER8J5Sg9GFNoCxQhWJMkuzaxrVq22jvZamWm0jtEm9kaKJrw7kDsPKybib9iACTnr6MGvodapVDyGBDXSrswPpe3TpitfGz8mX7XbN02MOvIgdvkbIla8JL8FrbrScLB9iHc8IOKMuIaIKw5%2FYdG2mfyYxcBVPnKx8dX1IU%2FaLtJLSXCbTKoMBipS9rhP%2BYe9Y1LC27aBHtIpjXmoBqYpF8HudQlguJ64HjzLFiMd10DZvVZq7k90jykJxBqvldUhm76KNMokanVQYqvWCukvhgCJeXVRlkh7P%2FUjlVTexh%2FO%2FXurXrjkdqRtwRGLB1D%2BGcONv2o%2BwsAlQidWQej%2BjlsWHRjBVsL9vHj2LZeKWLN94TkxLILPtHfW4G%2BW52jeFKY0nLDSa6b4Wk2%2BhPacoHoRMdWQ8HFFMEbJlw%2B7bCQUGz17QBxQ6SiLiN%2FCdRMC5pFDZauAJIR4EzUWXKYDj3BPOMbtQgaE4RRJ%2FdgQslvaUCZywEJl5hCknc7IPRWAIfAzJH9PLYIoQPa7muZsniR28rKBBw6AAvROhHFfipOlO518866BsOVYtls26%2F58B4JNC4mqZ0F44dX0OEvl7LLe%2F5NFVa70nXCJnB7gVFE72mycwcGYgH%2Bwwa5dY0hyEde5ZGygt1Zdi2bW6iCAPe8Qjk%2B0zwinjYzbmZdq5Z%2FrNut9NcC8v5pUOqzRS5UTU7AM91RXrSxbHEP07EKM8NG2ptv%2Fny29VnOqPgIJ75Pk82vVin5JKV09TO%2Bwr4WbY85pEoNaAOPb6aEduqzaBr1YgqApjWNblNkLkeok0ijhL%2Fn37MZ3DoQB%2FyydXAaekhGKqfTOjt2e0VZFj33OHwv%2ByWvIKJ6hrYU%2FPMXoKE8HF9Z5i9T5mp%2BbiGnjP%2Fh1q231UhzyaWg7WHL5iVNI%2Bz8%2FCdamNaAJe1uyWli36ZEJi4g%2FmU2RTzPoLTDyjUss8Mqar7kJ%2BC%2BVh2QRWTol718JSh1XY1qAYVGLy1d398E52oqelEiQKrX7eTf%2BtxKBTNw57wnS0SvNVxjjWUGgEUwFFVS%2F2UhEgDRQ1DsBoFEmk0m3tMAH5ARTWDcozBVDfdX4yXM%2F2r6Lypf8aJIAR0kYk1KSgrzIkAlLyhejtoZwEKkVYNbnG1rv%2B8hpoElahhrL6hP0wtv3oZPLHsSnADN%2BvhsyYB5k6Fv4SfsQetqJh34WmgyBGweNaedrItas%2FLcRGtZwov9YpjdlBO10rGfc8z14I8ZB5JTwue3lLTuEGBKt51siMBnIUJNePHGkEFmodITq15L0jCuFQgvROS0GOK3IanLWmYzwLTBnenI7ZbSXvnBvaAhSw1ur011dNfxkid%2B6PkejNI%2FXUM0psWa5Kd4EY6wr9tvzVT6rcgmZd0Nit2EbFipVQOaZRiqup2CLYogXFfWeg9KMFu6tXts5bVAaxrZ4SWOdktErWh1A3r2yCg%2B6wb7uXhk7cuE%2B7Aj5kT7IxeLrUs6tOkXjc53ZBv4vHvd%2FGQgBO5lPJiNgbyGp7PFDuqSm3czbF8fRHLgOzEFUIMTtPgDaOjIYYTBq%2B0bGheZbt6JNCzgTaClwv6CbGiBgrQ5YjtldG3cwToC9T5500lXg5wD%2BLHOTlxA8HdtfspK%2F8THH8sd1HJ3wJz4rNj1%2F0A7vzVI5%2BSFq%2FEwZjAJcvOggBj0xlZmfN1U6uCER9BbQisP6pOj8JcwLY2Lmj1sPk%2BdoVIZW3%2Fb%2FdUovmSrUQdn90D4NjovRx%2Bfk0T4sIoMUUjKCzcY%2BPuPgUoDRUTHQ%3D&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&");

            TextPage page = client.getPage(webRequest);

            WebResponse response = page.getWebResponse();

            String responseString = removeLines(response.getContentAsString());
            //String responseString = response.getContentAsString();

            org.jsoup.nodes.Document doc = Jsoup.parse(response.getContentAsString());

            Elements eventList = doc.select(".x_loop_game_title_block");

            //System.out.println(doc.select(".x_loop_game_title_block"));

            for(Element row : eventList) {

                Element dateCell = row.selectFirst(".x_game_date");

                String eventDate = dateCell.selectFirst("font").text();
                String eventTime = dateCell.selectFirst("span.time").text();
                String eventTitle = row.selectFirst(".x_game_title>span").text();
                Elements oddCells = row.select(".x_loop_res");

                System.out.print(eventDate +" " + eventTime + " | " + eventTitle );

                for(Element oddCell : oddCells) {
                    System.out.print(" " + oddCell.text());
                }

                System.out.println("");
            }

            //adds
            Long loadTime = response.getLoadTime();
            WebResponseData webResponseData = new WebResponseData(responseString.getBytes(), response.getStatusCode(), response.getStatusMessage(), new ArrayList<>());
            WebResponse newWebResponse = new WebResponse(webResponseData, webRequest, loadTime);

            WebWindow webWindow = page.getEnclosingWindow();

            HtmlPage newPage = new HtmlPage(newWebResponse, webWindow);

            System.out.print(page.toString());
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
