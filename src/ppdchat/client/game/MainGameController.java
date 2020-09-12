/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.client.game;

import ppdchat.PPDChat;
import ppdchat.client.game.ChatToolbarController;
import ppdchat.client.Client;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

/** 
 * FXML Controller class
 *
 * @author Matheus
 */
public class MainGameController {

    static MainGameController g;
    @FXML
    private GameController gameController;
    
    @FXML
    private ChatToolbarController chatToolbarController;
    private Client client;
    private Stage mainStage;
    
    @FXML
    public void initialize() {
        PPDChat.addOnChangeScreenListener(new PPDChat.OnChangeScreen() {
            @Override
            public void onScreenChanged(String newScreen, Object data, Stage stage) {
                if(newScreen.equals("game")){
                    mainStage = stage;
                    HashMap dataMap = ((HashMap<String, Object>) data);
                    client = (Client) dataMap.get("client");
                    try {
                        initControllers();
                    } catch (RemoteException ex) {
                        Logger.getLogger(MainGameController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if(newScreen.equalsIgnoreCase("stop")){
                    if(client != null){
                        //client.sendQuitMessage();
                        //client = null;
                    }
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }    
    
    public void initControllers() throws RemoteException{
        chatToolbarController.init(this);
        gameController.init(this);
        client.setGameController(this);
    }

    public GameController getGameController() {
        return gameController;
    }

    public ChatToolbarController getChatToolbarController() {
        return chatToolbarController;
    }

    public Client getClient() {
        return client;
    }

    public static MainGameController getG() {
        return g;
    }
    
    
    
}
