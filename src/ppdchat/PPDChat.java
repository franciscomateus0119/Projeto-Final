/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Matheus
 */
public class PPDChat extends Application {
    private static Stage stage;
    private static Scene menuScene;
    private static Scene mainGameScene;
    private static ArrayList<OnChangeScreen> listeners = new ArrayList<>();
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        
        Parent root = FXMLLoader.load(getClass().getResource("client/game/Menu.fxml"));
        menuScene = new Scene(root);
        Parent game = FXMLLoader.load(getClass().getResource("client/game/MainGame.fxml"));
        mainGameScene = new Scene(game);
        
        stage.setScene(menuScene);
        
        stage.show();
        
    } 

    
    public static void main(String[] args) {
        launch(args);
    }
    
    public static void changeScreen(String scr, Object data) {
        switch (scr) {
            case "game":
                stage.setScene(mainGameScene);
                stage.setResizable(false);
                notifyAllListeners("game", data, stage);
                break;
        }
    }
    
    public static interface OnChangeScreen {
        void onScreenChanged(String newScreen, Object data, Stage stage);
    }
     public static void changeScreen(String scr) {
        changeScreen(scr, null);
    }

    public static void addOnChangeScreenListener(OnChangeScreen newListener) {
        listeners.add(newListener);
    }

    private static void notifyAllListeners(String newScreen, Object data, Stage stage) {
        for (OnChangeScreen l : listeners)
            l.onScreenChanged(newScreen, data, stage);
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        PPDChat.stage = stage;
    }
    
    
}
