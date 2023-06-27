import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

public class CannonDriver extends Application {

    @Override // loads fxml and all the other fun stuff
    public void start(Stage stage) throws Exception {
        Parent root;
        root = FXMLLoader.load( getClass().getResource("CannonGameInterface.fxml") );
        Scene scene = new Scene(root);
        stage.setTitle("Cannonball!");
        stage.setScene(scene);

        stage.show(); // and 

    }

    public static void main(String args) {
        launch(args);
    }

}
