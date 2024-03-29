package ge.arb-bot.gui;

import ge.arb-bot.bot.exceptions.BookieDriverNotFoundException;
import ge.arb-bot.bot.exceptions.UnknownOddTypeException;
import ge.arb-bot.core.Calc;
import ge.arb-bot.core.datatypes.Arb;
import ge.arb-bot.gui.service.BotService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

/**
 * Created by giga on 10/3/17.
 */
public class ArbDetailsPanel extends GridPane {

    protected static Logger logger = LoggerFactory.getLogger(ArbDetailsPanel.class);

    public static class Event extends GridPane {
        final Label bookie = new Label();
        final Label oddType = new Label();
        final Label odd = new Label();
        final Label category = new Label();
        final Label subCategory = new Label();
        final Label teamName = new Label();
        final Label teamNameCompare = new Label();
        final Label stake = new Label();
        final Label win = new Label();
        final Label totalWin = new Label();

        protected void copyOnClick(MouseEvent event) {
            Object target = event.getTarget();
            if(target instanceof Text) {
                Text targetLabel = (Text) target;
                String name = targetLabel.getText();

                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(name != null ? name : "");
                clipboard.setContent(content);

                System.out.println("Text copied: " + name);
            }
        }

        Event(){

            bookie.setOnMouseClicked(this::copyOnClick);
            oddType.setOnMouseClicked(this::copyOnClick);
            odd.setOnMouseClicked(this::copyOnClick);
            category.setOnMouseClicked(this::copyOnClick);
            subCategory.setOnMouseClicked(this::copyOnClick);
            teamName.setOnMouseClicked(this::copyOnClick);
            stake.setOnMouseClicked(this::copyOnClick);
            win.setOnMouseClicked(this::copyOnClick);
            totalWin.setOnMouseClicked(this::copyOnClick);

            Font defaultFont = Font.getDefault();
            Font h1 = Font.font(defaultFont.getName(), FontWeight.BOLD, 24);
            Font h2 = Font.font(defaultFont.getName(), 20);
            Font h3 = Font.font(defaultFont.getName(), 16);

            bookie.setFont(h1);
            oddType.setFont(h3);
            teamName.setFont(Font.font(defaultFont.getName(), FontWeight.BOLD, 14));
            teamName.setTextFill(Color.BLUE);
            teamNameCompare.setFont(Font.font(defaultFont.getName(), FontWeight.BOLD, 14));
            teamNameCompare.setTextFill(Color.GREY);
            //odd.setFont(h2);
            //stake.setFont(h2);
            win.setFont(h2);
            win.setTextFill(Color.RED);

            /*
            bookie.setStyle("-fx-background-color: #CCCCCC;");
            oddType.setStyle("-fx-background-color: #CCCCCC;");*/

            this.add(bookie, 0, 0);
            this.add(oddType, 0, 1);
            this.add(teamName, 0, 2);
            this.add(teamNameCompare, 0, 3);
            this.add(odd, 0, 4);
            this.add(stake, 0, 5);
            this.add(win, 0, 6);
            this.add(totalWin, 0, 7);
            this.add(category, 0, 8);
            this.add(subCategory, 0, 9);
        }

        Event(HPos alignment){

            this();

            GridPane.setHalignment(bookie, alignment);
            GridPane.setHalignment(oddType, alignment);
            GridPane.setHalignment(odd, alignment);
            GridPane.setHalignment(category, alignment);
            GridPane.setHalignment(subCategory, alignment);
            GridPane.setHalignment(teamName, alignment);
            GridPane.setHalignment(teamNameCompare, alignment);
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

        public void setCategory(String category) {
            this.category.setText(category);
        }

        public void setSubCategory(String subCategory) {
            this.subCategory.setText(subCategory);
        }

        public void setTeamNameCompare(String subCategory) {
            this.teamNameCompare.setText(subCategory);
        }

    }

    Event firstCriteria = new Event(HPos.RIGHT);
    Event secondCriteria = new Event();
    NumberField stakeField = new NumberField();
    Button makeStakeButton = new Button("Create stakes");
    Arb arb;

    //User input feed from stakeField.
    Long totalStake = 100L;

    public ArbDetailsPanel(){
        this.add(firstCriteria, 0, 0);
        this.add(secondCriteria, 1, 0);

        //this.add(stakeField, 0, 1, 2, 1);
        stakeField.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, 24));
        stakeField.setAlignment(Pos.CENTER);
        stakeField.setPromptText("Total stake");

        stakeField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if(newValue.equals(null) || newValue.equals("")){
                    return;
                }

                try{
                    totalStake = Long.parseLong(newValue);
                } catch (NumberFormatException e) {
                    return;
                }

                recalucalte();
            }
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(stakeField, makeStakeButton);
        vBox.setSpacing(10);

        makeStakeButton.setMaxWidth(Double.MAX_VALUE);
        makeStakeButton.setPrefHeight(42);
        makeStakeButton.setMinHeight(36);
        this.add(vBox, 0, 1, 2, 1);

        makeStakeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                BotService botService = new BotService();
                Arb.Bookie bookie1 = arb.getBookieOne();
                Arb.Bookie bookie2 = arb.getBookieTwo();

                Calc.Pair<Double> stakes = Calc.stakes(new Double(totalStake), bookie1.getOdd(), bookie2.getOdd());

                try {
                    botService.createBet(bookie1, stakes.getA());
                } catch (Exception e) {
                    logger.error("Stake creation failed for {} error was {}", bookie1, e);
                }

                try {
                    botService.createBet(bookie2, stakes.getB());
                } catch (Exception e) {
                    logger.error("Stake creation failed for {} error was {}", bookie1, e);
                }
            }
        });

        this.setHgap(10);
        this.setVgap(10);
    }

    public Arb getArb() {
        return arb;
    }

    public void setArb(Arb arb) {

        this.getFirstCriteria().setBookie(arb.getBookieOne().getName());
        this.getFirstCriteria().setOddType(arb.getBookieOne().getOddType());
        this.getFirstCriteria().setOdd(arb.getBookieOne().getOdd().toString());
        this.getFirstCriteria().setTeamName(arb.getBookieOne().getTeamOneName());
        this.getFirstCriteria().setTeamNameCompare(arb.getBookieTwo().getTeamOneName());
        this.getFirstCriteria().setCategory(arb.getBookieOne().getCategory());
        this.getFirstCriteria().setSubCategory(arb.getBookieOne().getSubCategory());

        this.getSecondCriteria().setBookie(arb.getBookieTwo().getName());
        this.getSecondCriteria().setOddType(arb.getBookieTwo().getOddType());
        this.getSecondCriteria().setOdd(arb.getBookieTwo().getOdd().toString());
        this.getSecondCriteria().setTeamName(arb.getBookieTwo().getTeamTwoName());
        this.getSecondCriteria().setTeamNameCompare(arb.getBookieOne().getTeamTwoName());
        this.getSecondCriteria().setCategory(arb.getBookieTwo().getCategory());
        this.getSecondCriteria().setSubCategory(arb.getBookieTwo().getSubCategory());

        this.arb = arb;

        recalucalte();
    }

    protected void recalucalte() {

        if(arb == null) {
            return;
        }

        Calc.Pair<Double> stakes = Calc.stakes(new Double(totalStake), arb.getBookieOne().getOdd(), arb.getBookieTwo().getOdd());
        Calc.Pair<Double> wins = Calc.wins(new Double(totalStake), arb.getBookieOne().getOdd(), arb.getBookieTwo().getOdd());

        this.getFirstCriteria().setStake(presentDouble(stakes.getA()));
        this.getSecondCriteria().setStake(presentDouble(stakes.getB()));

        this.getFirstCriteria().setWin(presentDouble(wins.getA()));
        this.getSecondCriteria().setWin(presentDouble(wins.getB()));

        this.getFirstCriteria().setTotalWin(presentDouble(wins.getA() + totalStake));
        this.getSecondCriteria().setTotalWin(presentDouble(wins.getB() + totalStake));
    }

    private String presentDouble(Double value){
        return (new DecimalFormat("#0.00")).format(value);
    }

    public Event getFirstCriteria() {
        return firstCriteria;
    }

    public void setFirstCriteria(Event firstCriteria) {
        this.firstCriteria = firstCriteria;
    }

    public Event getSecondCriteria() {
        return secondCriteria;
    }

    public void setSecondCriteria(Event secondCriteria) {
        this.secondCriteria = secondCriteria;
    }
}
