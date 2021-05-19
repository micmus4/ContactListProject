package pl.musialowicz.contactlist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.musialowicz.contactlist.contact.DataManagement;


public class Main extends Application {

    private final DataManagement dataInstance = DataManagement.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        primaryStage.setTitle("Contact List");
        primaryStage.setScene(new Scene(root, 685, 350));
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    @Override
    public void stop() throws Exception {
        dataInstance.saveContactsToFile();
    }

    @Override
    public void init() throws Exception {
        dataInstance.loadContactsFromFile();
    }

    public static void main(String[] args) {
        DataManagement data = DataManagement.getInstance();
        data.createFile(); // if it does not exist, but it should...
        launch(args);
    }
}
