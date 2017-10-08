package ge.shitbot.gui;

import ge.shitbot.datasources.datatypes.Arb;
import ge.shitbot.datasources.exceptions.DataSourceException;
import ge.shitbot.datasources.source.ArbDataSource;
import ge.shitbot.datasources.source.MainDataSource;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.Date;

/**
 * Created by giga on 9/13/17.
 */
public class Main extends Application {

    Label response;
    Label currentWidth;
    Label currentHeight;
    private TableView table;

    public static void main(String[] args) {
        //MainForm form = new MainForm();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Shit Bot v0.1");

        GridPane pane = new GridPane();

        Button btnAlpha = new Button("Alpha");
        Button btnBeta = new Button("Beta");
        response = new Label("Click a Button.");
        currentWidth = new Label("W");
        currentHeight = new Label("H");

        btnAlpha.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                response.setText("Alpha clicked.");
            }
        });

        btnBeta.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                response.setText("Beta clicked.");
            }
        });

        //pane.getChildren().addAll(btnAlpha, btnBeta, response, currentWidth, currentHeight);

        //MainPane
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setPrefSize(300, 200);

        //Scene scene = new Scene(pane, 800, 600);
        Scene scene = new Scene(splitPane, 800, 600);

        // Add resize listeners
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldW, Number newW) {
                currentWidth.setText(newW.toString());
            }
        });

        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldH, Number newH) {
                currentHeight.setText(newH.toString());
            }
        });

        TableView tableView = createTableView();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Arb>() {
            @Override
            public void changed(ObservableValue<? extends Arb> observable, Arb oldValue, Arb newValue) {
                if(newValue != null) {
                    arbDetailsPanel.getFirstCriteria().setBookie(newValue.getBookieOne().getName());
                    arbDetailsPanel.getFirstCriteria().setOddType(newValue.getBookieOne().getOddType());
                    arbDetailsPanel.getFirstCriteria().setOdd(newValue.getBookieOne().getOdd().toString());
                    arbDetailsPanel.getFirstCriteria().setTeamName(newValue.getBookieOne().getTeamOneName());
                } else {
                    arbDetailsPanel.getSecondCriteria().setBookie(newValue.getBookieTwo().getName());
                    arbDetailsPanel.getSecondCriteria().setOddType(newValue.getBookieTwo().getOddType());
                    arbDetailsPanel.getSecondCriteria().setOdd(newValue.getBookieTwo().getOdd().toString());
                    arbDetailsPanel.getSecondCriteria().setTeamName(newValue.getBookieTwo().getTeamTwoName());
                }
            }
        } /*(obs, oldSelection, newSelection) -> {

            if(newSelection != null) {
                Arb selectedArb = (Arb) newSelection;

                System.out.println("newSelection class: " + newSelection.getClass().getName());
                arbDetailsPanel.getFirstCriteria().setBookie(selectedArb.getBookieOne().getName());
            } else {
                arbDetailsPanel.getFirstCriteria().setBookie("Unknown");
            }
        }*/);

        ArbDetailsPanel detailsPanel = createDetailsPanel();
        splitPane.getItems().addAll(wrapWithVBox(tableView), wrapWithVBox(detailsPanel));

        stage.setScene(scene);
        stage.show();
    }

    /*protected void arbSelectedListener(ObservableValue obs, Arb oldSelection, Arb newSelection) {
        if(newSelection != null) {
            Arb selectedArb = (Arb) newSelection;

            System.out.println("newSelection class: " + newSelection.getClass().getName());
            arbDetailsPanel.getFirstCriteria().setBookie(selectedArb.getBookieOne().getName());
        } else {
            arbDetailsPanel.getFirstCriteria().setBookie("Unknown");
        }
    }*/

    protected TableView createTableView() {

        table = new TableView();

        TableColumn action = new TableColumn("Action");

        Callback<TableColumn<Arb, String>, TableCell<Arb, String>> buttonCellFactory
                = //
                new Callback<TableColumn<Arb, String>, TableCell<Arb, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Arb, String> param) {
                        final TableCell<Arb, String> cell = new TableCell<Arb, String>() {

                            final Button btn = new Button("Bet");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        Arb arb = getTableView().getItems().get(getIndex());
                                        System.out.println(arb.getProfit());
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        action.setCellFactory(buttonCellFactory);

        TableColumn stake = new TableColumn("Stake");

        Callback<TableColumn<Arb, String>, TableCell<Arb, String>> textFieldCellFactory
                = //
                new Callback<TableColumn<Arb, String>, TableCell<Arb, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Arb, String> param) {
                        final TableCell<Arb, String> cell = new TableCell<Arb, String>() {

                            TextField field = new TextField("100");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                field.setPrefColumnCount(2);

                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    field.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent event) {

                                        }
                                    });

                                    /*btn.setOnAction(event -> {
                                        Arb arb = getTableView().getItems().get(getIndex());
                                        System.out.println(arb.getProfit());
                                    });*/
                                    setGraphic(field);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        stake.setCellFactory(textFieldCellFactory);

        TableColumn myProfit = new TableColumn("My Profit");

        myProfit.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Arb, Double>, StringExpression>() {
            @Override
            public StringExpression call(TableColumn.CellDataFeatures<Arb, Double> cellDataFeatures) {

                Double profit = Calc.profit(cellDataFeatures.getValue().getBookieOne().getOdd(),
                        cellDataFeatures.getValue().getBookieTwo().getOdd());

                return Bindings.format("%.2f", profit);
            }
        });

        TableColumn profit = new TableColumn("Profit");
        TableColumn date = new TableColumn("Date");

        TableColumn hostID = new TableColumn("HostID");
        hostID.setVisible(false);

        TableColumn guestID = new TableColumn("GuestID");
        guestID.setVisible(false);

        TableColumn bookeOne = new TableColumn("Bookie One");
        finalizeBookieColumn(bookeOne, "bookie_1");

        TableColumn bookieTwo = new TableColumn("Bookie Two");
        finalizeBookieColumn(bookieTwo, "bookie_2");

        profit.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Arb, Double>, StringExpression>() {
            @Override
            public StringExpression call(TableColumn.CellDataFeatures<Arb, Double> cellDataFeatures) {
                return Bindings.format("%.2f", cellDataFeatures.getValue().getProfit());
            }
        });

        date.setCellValueFactory(new PropertyValueFactory<Arb, Date>("date"));
        hostID.setCellValueFactory(new PropertyValueFactory<Arb, Long>("hostID"));
        guestID.setCellValueFactory(new PropertyValueFactory<Arb, Long>("guestID"));

        table.getColumns().addAll(action, myProfit, stake, profit,date, hostID, guestID, bookeOne, bookieTwo);

        try {
            table.setItems(getArbs());
        } catch (DataSourceException e) {
            e.printStackTrace();
        }

        /*final Label label = new Label("Address Book");

        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 20, 10, 20));
        vBox.getChildren().addAll(label, table);

        GridPane.setHgrow(vBox, Priority.ALWAYS);
        GridPane.setVgrow(vBox, Priority.ALWAYS);*/

        //pane.getChildren().add(vBox);
        return table;
    }

    protected ObservableList<Arb> getArbs() throws DataSourceException {
        ArbDataSource<Arb> source = new MainDataSource<>();
        return FXCollections.observableArrayList(source.getArbs());
    }

    //Helper class to avoid checking which bookie to use every time we add nested property factory for arb.
    private abstract class MyCallback<FT> implements Callback<TableColumn.CellDataFeatures<Arb, FT>, ObservableValue> {

        String bookieId;
        Arb.Bookie bookie;

        public MyCallback(String bookie) {
            this.bookieId = bookie;
        }

        abstract protected ObservableValue<FT> getPropertyValue();

        @Override
        public ObservableValue<FT> call(TableColumn.CellDataFeatures<Arb, FT> cellDataFeatures) {

            if(bookieId.equals("bookie_1")) {
                bookie = cellDataFeatures.getValue().getBookieOne();
            } else if(bookieId.equals("bookie_2")){
                bookie = cellDataFeatures.getValue().getBookieTwo();
            }

            return getPropertyValue();
        }
    }

    private void finalizeBookieColumn(TableColumn col, String bookie) {

        TableColumn name = new TableColumn("Name");
        TableColumn oddType = new TableColumn("Odd\ntype");
        TableColumn odd = new TableColumn("Odd");
        TableColumn teamOneName = new TableColumn("Team one");
        TableColumn teamTwoName = new TableColumn("Team two");
        TableColumn category = new TableColumn("Category");
        TableColumn subCategory = new TableColumn("Sub category");

        col.getColumns().addAll(name, oddType, odd, teamOneName, teamTwoName, category, subCategory);

        /*name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Arb, Long>, ObservableValue>() {
            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<Arb, Long> cellDataFeatures) {

                //if(bookie.equals("bookie1"))
                return new SimpleLongProperty(cellDataFeatures.getValue().getBookieOne().getOdd());
            }
        });*/

        name.setCellValueFactory(new MyCallback<String>(bookie){

            @Override
            protected SimpleStringProperty getPropertyValue(){
                return new SimpleStringProperty(bookie.getName());
            }
        });

        oddType.setCellValueFactory(new MyCallback<String>(bookie){

            @Override
            protected SimpleStringProperty getPropertyValue(){
                return new SimpleStringProperty(bookie.getOddType());
            }
        });

        odd.setCellValueFactory(new MyCallback<Number>(bookie){

            @Override
            protected ObservableValue<Number> getPropertyValue(){
                return new SimpleDoubleProperty(bookie.getOdd());
            }
        });

        teamOneName.setCellValueFactory(new MyCallback<String>(bookie){

            @Override
            protected ObservableValue<String> getPropertyValue(){
                return new SimpleStringProperty(bookie.getTeamOneName());
            }
        });

        teamTwoName.setCellValueFactory(new MyCallback<String>(bookie){

            @Override
            protected ObservableValue<String> getPropertyValue(){
                return new SimpleStringProperty(bookie.getTeamTwoName());
            }
        });

        category.setCellValueFactory(new MyCallback<String>(bookie){

            @Override
            protected ObservableValue<String> getPropertyValue(){
                return new SimpleStringProperty(bookie.getCategory());
            }
        });

        subCategory.setCellValueFactory(new MyCallback<String>(bookie){

            @Override
            protected ObservableValue<String> getPropertyValue(){
                return new SimpleStringProperty(bookie.getSubCategory());
            }
        });
    }

    ArbDetailsPanel arbDetailsPanel;

    protected ArbDetailsPanel createDetailsPanel() {
        arbDetailsPanel = new ArbDetailsPanel();
        arbDetailsPanel.getFirstCriteria().setBookie("CrystalBet");
        arbDetailsPanel.getFirstCriteria().setOdd("1.45");
        arbDetailsPanel.getFirstCriteria().setOddType("1");
        arbDetailsPanel.getFirstCriteria().setTeamName("Real Madrid");
        arbDetailsPanel.getFirstCriteria().setStake("79");
        arbDetailsPanel.getFirstCriteria().setStake("114.55");

        arbDetailsPanel.getSecondCriteria().setBookie("BetLive");
        arbDetailsPanel.getSecondCriteria().setOdd("5.5");
        arbDetailsPanel.getSecondCriteria().setOddType("X2");
        arbDetailsPanel.getSecondCriteria().setTeamName("Barca");
        arbDetailsPanel.getSecondCriteria().setStake("21");
        arbDetailsPanel.getSecondCriteria().setStake("115.5");

        return arbDetailsPanel;
    }

    protected VBox wrapWithVBox(Node node) {
        final VBox vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setPadding(new Insets(10, 20, 10, 20));
        //vBox.getChildren().addAll(label, table);

        GridPane.setHgrow(vBox, Priority.ALWAYS);
        GridPane.setVgrow(vBox, Priority.ALWAYS);

        vBox.getChildren().add(node);

        return vBox;
    }
}
