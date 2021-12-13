package pl.kkapalka.salesman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Group;

import java.io.IOException;

public class SalesmanSolverApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SalesmanSolverApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);
        stage.setTitle("Hello!");

        Group root = new Group();
        Scene subScene = new Scene(root, 200, 150, javafx.scene.paint.Color.WHITE);
        javafx.scene.control.TabPane tabPane = new javafx.scene.control.TabPane();
        javafx.scene.layout.BorderPane borderPane = new javafx.scene.layout.BorderPane();
        for (int i = 0; i < 5; i++) {
            javafx.scene.control.Tab tab = new javafx.scene.control.Tab();
            tab.setGraphic(new javafx.scene.shape.Circle(0, 0, 10));
            javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox();
            hbox.getChildren().add(new javafx.scene.control.Label("Tab" + i));
            hbox.setAlignment(javafx.geometry.Pos.CENTER);
            tab.setContent(hbox);
            tabPane.getTabs().add(tab);
        }
        // bind to take available space
        borderPane.prefHeightProperty().bind(subScene.heightProperty());
        borderPane.prefWidthProperty().bind(subScene.widthProperty());

        borderPane.setCenter(tabPane);
        root.getChildren().add(borderPane);

        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}