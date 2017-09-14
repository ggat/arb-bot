package ge.shitbot.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by giga on 9/13/17.
 */
public class Main extends Application {

    public static void main(String[] args) {
        //MainForm form = new MainForm();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Shit Bot v0.1");

        Parent parent = FXMLLoader.load(getClass().getResource("simple.fxml"));

        StackPane pane = new StackPane();
        pane.getChildren().add(new Button("Some"));

        Scene scene = new Scene(parent, 800, 600);

        stage.setScene(scene);
        stage.show();
    }

}
