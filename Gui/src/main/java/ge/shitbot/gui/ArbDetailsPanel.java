package ge.shitbot.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Created by giga on 10/3/17.
 */
public class ArbDetailsPanel extends GridPane {

    public static class Event extends GridPane {
        final Label bookie = new Label();
        final Label oddType = new Label();
        final Label odd = new Label();
        final Label teamName = new Label();
        final Label stake = new Label();
        final Label win = new Label();
        final Label totalWin = new Label();

        Event(){
            this.add(bookie, 0, 0);
            this.add(oddType, 0, 0);
        }

        public String getBookie() {
            return bookie.getText();
        }

        public void setBookie(String bookie) {
            this.bookie.setText(bookie);
        }

        public String getOddType() {
            return oddType.getText();
        }

        public void setOddType(String oddType) {
            this.oddType.setText(oddType);
        }

        public String getOdd() {
            return odd.getText();
        }

        public void setOdd(String odd) {
            this.odd.setText(odd);
        }

        public String getTeamName() {
            return teamName.getText();
        }

        public void setTeamName(String teamName) {
            this.teamName.setText(teamName);
        }

        public String getStake() {
            return stake.getText();
        }

        public void setStake(String stake) {
            this.stake.setText(stake);
        }

        public String getWin() {
            return win.getText();
        }

        public void setWin(String win) {
            this.win.setText(win);
        }

        public String getTotalWin() {
            return totalWin.getText();
        }

        public void setTotalWin(String totalWin) {
            this.totalWin.setText(totalWin);
        }
    }

    Event eventOne = new Event();
    Event eventTwo = new Event();

    ArbDetailsPanel(){
        this.add(eventOne, 0, 0);
        this.add(eventTwo, 0, 1);
    }

    public Event getEventOne() {
        return eventOne;
    }

    public void setEventOne(Event eventOne) {
        this.eventOne = eventOne;
    }

    public Event getEventTwo() {
        return eventTwo;
    }

    public void setEventTwo(Event eventTwo) {
        this.eventTwo = eventTwo;
    }
}
