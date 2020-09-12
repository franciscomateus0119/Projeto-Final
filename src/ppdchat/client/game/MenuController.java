/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.client.game;

import ppdchat.server.ServerInterface;
import ppdchat.client.Client;
import ppdchat.PPDChat;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.Stage;



/**
 * FXML Controller class
 *
 * @author Matheus
 */
public class MenuController {
    private Client client;
    private boolean jogoIniciado;
    BackgroundImage startimg = new BackgroundImage( new Image( getClass().getResource("conteudo/start.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
    Background startbg = new Background(startimg);
    String nome;
    float valorX;
    float valorY;
    @FXML TextField TF_NOME;
    @FXML TextField TF_X;
    @FXML TextField TF_Y;
    @FXML TextField TF_NAME_SERVER;
    @FXML TextField TF_IP_SERVER;
    @FXML TextField TF_PORT_SERVER;
    
    @FXML
    private Button buttonConnect;
    
    
    
    @FXML
    public void initialize() {
        buttonConnect.setBackground(startbg);
    }

    @FXML
    public void connect(ActionEvent event){
        try{
            
            Registry registry = LocateRegistry.getRegistry(TF_IP_SERVER.getText(), Integer.parseInt(TF_PORT_SERVER.getText()));
            ServerInterface server = (ServerInterface) registry.lookup(TF_NAME_SERVER.getText());
            System.out.println("Server: " + server);
            System.out.println("Server Registry: "+registry);
            valorX = Float.parseFloat(TF_X.getText());
            valorY = Float.parseFloat(TF_Y.getText());
            client = new Client(server,TF_IP_SERVER.getText(),TF_NAME_SERVER.getText(),Integer.parseInt(TF_PORT_SERVER.getText()),
                    registry,TF_NOME.getText(), valorX, valorY);
            client.setMenuController(this);
            server.registerClient(client);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
     
    public void gameStartReady(){
        Map<String, Object> data = new HashMap<>();
        data.put("client", client);
        jogoIniciado = true;
        PPDChat.changeScreen("game", data);
        
    }
    
    
    public void getText(){
        Platform.runLater(() -> setNome(TF_NOME.getText()));
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    
}
