package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Robot NCO");
        primaryStage.setScene(new Scene(root, 800, 1100));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


}
