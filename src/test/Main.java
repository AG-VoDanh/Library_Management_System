package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new FXMLLoader(getClass().getResource(
                "/view/user/LoginView.fxml")).load());

        stage.setScene(scene);
        stage.setTitle("Quản Lý Thư Viện");
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.show();
    }

    static void main(String[] args) {
        launch(args);
    }


}
