package ge.shitbot.core.datatypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ge.shitbot.core.datatypes.deserialize.BookieDeserializer;
import ge.shitbot.core.datatypes.deserialize.ArbDateDeserializer;

import java.sql.Timestamp;

/**
 * Created by giga on 9/27/17.
 */
public class Arb {

    public static class Bookie {
        private String name;
        private String oddType;
        private Double odd;
        private String teamOneName;
        private String teamTwoName;
        private String category;
        private String subCategory;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("odd_type")
        public String getOddType() {
            return oddType;
        }

        public void setOddType(String oddType) {
            this.oddType = oddType;
        }

        public Double getOdd() {
            return odd;
        }

        public void setOdd(Double odd) {
            this.odd = odd;
        }

        @JsonProperty("team_1_name")
        public String getTeamOneName() {
            return teamOneName;
        }

        public void setTeamOneName(String teamOneName) {
            this.teamOneName = teamOneName;
        }

        @JsonProperty("team_2_name")
        public String getTeamTwoName() {
            return teamTwoName;
        }

        public void setTeamTwoName(String teamTwoName) {
            this.teamTwoName = teamTwoName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        @JsonProperty("sub_category")
        public String getSubCategory() {
            return subCategory;
        }

        public void setSubCategory(String subCategory) {
            this.subCategory = subCategory;
        }
    }

    private Double profit;

    @JsonDeserialize(using = ArbDateDeserializer.class)
    private Timestamp date;
    private Long hostID;
    private Long guestID;

    @JsonProperty("bookie_1")
    @JsonDeserialize(using = BookieDeserializer.class)
    Bookie bookieOne;

    @JsonProperty("bookie_2")
    @JsonDeserialize(using = BookieDeserializer.class)
    Bookie bookieTwo;

    /*
    {
    "profit": 0.50916496945009726,
    "date": "28 Sep 2017 23:05",
    "hostID": 11553,
    "guestID": 11554,
    "bookie_1": {
      "name": "BetLive",
      "odd_type": "1",
      "odd": 3.5,
      "team_1_name": "შერიფი",
      "team_2_name": "კოპენჰაგენი",
      "category": "UEFA",
      "sub_category": "ევროპის ლიგა"
    },
    "bookie_2": {
      "name": "CrocoBet",
      "odd_type": "X2",
      "odd": 1.41,
      "team_1_name": "",
      "team_2_name": "",
      "category": "UEFA",
      "sub_category": "ევროპის ლიგა"
    }
    */

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Long getHostID() {
        return hostID;
    }

    public void setHostID(Long hostID) {
        this.hostID = hostID;
    }

    public Long getGuestID() {
        return guestID;
    }

    public void setGuestID(Long guestID) {
        this.guestID = guestID;
    }

    @JsonProperty("bookie_1")
    public Bookie getBookieOne() {
        return bookieOne;
    }

    public void setBookieOne(Bookie bookieOne) {
        this.bookieOne = bookieOne;
    }

    @JsonProperty("bookie_2")
    public Bookie getBookieTwo() {
        return bookieTwo;
    }

    public void setBookieTwo(Bookie bookieTwo) {
        this.bookieTwo = bookieTwo;
    }


}
