package main;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainLauncher extends Application {
	
	public static URL mainWindow = MainLauncher.class.getResource("/mainWindow.fxml");
	public static URL mainCSS = MainLauncher.class.getResource("/main.css");
	
	@Override
	public void start(Stage primaryStage) {
		
		try {
			Parent root = FXMLLoader.load(mainWindow);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(mainCSS.toExternalForm());
			
		    primaryStage.setScene(scene);
		    
		    primaryStage.setMinWidth(1280);
		    primaryStage.setMinHeight(720);
		    
		    primaryStage.setResizable(true);
		    primaryStage.setTitle("JavaFX Animator v0.1");
		    primaryStage.show();
		    
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	public static void onLaunch(String[] args) {
		MainLauncher.launch(args);
	}
}
