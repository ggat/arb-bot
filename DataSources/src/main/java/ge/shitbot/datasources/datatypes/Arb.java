package ge.shitbot.datasources.datatypes;

import java.sql.Date;

/**
 * Created by giga on 9/27/17.
 */
public class Arb {

    public class Bookie {
        private String name;
        private String oddType;
        private String odd;
        private String teamOneName;
        private String teamTwoName;
        private String category;
        private String subCategory;
    }

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

    private Double profit;
    private Date date;
    private Long hostID;
    private Long guestID;

    Bookie bookieOne;
    Bookie bookieTwo;

}
