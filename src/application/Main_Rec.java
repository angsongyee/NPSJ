package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main_Rec extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("FaceRecognizer.fxml"));
			BorderPane root = (BorderPane) loader.load();
			Scene scene = new Scene(root);
			FaceController ctrl = loader.getController();
			ctrl.init();
			primaryStage.getIcons().add(new Image("file:logo.jpg"));
			primaryStage.setTitle("SecuNet");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
