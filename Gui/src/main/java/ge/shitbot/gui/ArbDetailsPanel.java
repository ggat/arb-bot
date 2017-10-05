package ge.shitbot.gui;

import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

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

            Font defaultFont = Font.getDefault();
            Font h1 = Font.font(defaultFont.getName(), FontWeight.BOLD, 24);
            Font h2 = Font.font(defaultFont.getName(), 20);
            Font h3 = Font.font(defaultFont.getName(), 16);

            bookie.setFont(h1);
            oddType.setFont(h3);
            teamName.setFont(Font.font(defaultFont.getName(), FontWeight.BOLD, 14));
            //odd.setFont(h2);
            //stake.setFont(h2);
            //win.setFont(h2);

            /*
            bookie.setStyle("-fx-background-color: #CCCCCC;");
            oddType.setStyle("-fx-background-color: #CCCCCC;");*/

            this.add(bookie, 0, 0);
            this.add(oddType, 0, 1);
            this.add(teamName, 0, 2);
            this.add(odd, 0, 3);
            this.add(stake, 0, 4);
            this.add(win, 0, 5);
            this.add(totalWin, 0, 5);
        }

        Event(HPos alignment){

            this();

            GridPane.setHalignment(bookie, alignment);
            GridPane.setHalignment(oddType, alignment);
            GridPane.setHalignment(odd, alignment);
            GridPane.setHalignment(teamName, alignment);
            GridPane.setHalignment(stake, alignment);
            GridPane.setHalignment(win, alignment);
            GridPane.setHalignment(totalWin, alignment);
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

    Event eventOne = new Event(HPos.RIGHT);
    Event eventTwo = new Event();
    NumberField stakeField = new NumberField();
    Button makeStakeButton = new Button("Create stakes");

    ArbDetailsPanel(){
        this.add(eventOne, 0, 0);
        this.add(eventTwo, 1, 0);

        this.add(stakeField, 2, 0);
        stakeField.setPromptText("Total stake");

        this.add(makeStakeButton, 2, 1);

        this.setHgap(10);
        this.setVgap(10);
    }

    public Event getFirstCriteria() {
        return eventOne;
    }

    public void setEventOne(Event eventOne) {
        this.eventOne = eventOne;
    }

    public Event getSecondCriteria() {
        return eventTwo;
    }

    public void setEventTwo(Event eventTwo) {
        this.eventTwo = eventTwo;
    }
}
