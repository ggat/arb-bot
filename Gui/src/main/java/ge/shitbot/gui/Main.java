package ge.shitbot.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by giga on 9/13/17.
 */
public class Main extends Application {

    Label response;

    public static void main(String[] args) {
        //MainForm form = new MainForm();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Shit Bot v0.1");

        FlowPane pane = new FlowPane();

        Button btnAlpha = new Button("Alpha");
        Button btnBeta = new Button("Beta");
        response = new Label("Click a Button.");

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

        pane.getChildren().addAll(btnAlpha, btnBeta, response);

        Scene scene = new Scene(pane, 800, 600);

        stage.setScene(scene);
        stage.show();
    }

}
