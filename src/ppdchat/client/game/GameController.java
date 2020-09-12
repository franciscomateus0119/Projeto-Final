/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.client.game;

import ppdchat.PPDChat;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;


/**
 * FXML Controller class
 *
 * @author Matheus
 */
public class GameController{
    private MainGameController main; 
    Stage stage;
    
    String meuNome = "X";
    String ambienteAtual = "Nenhum";
    float meuX;
    float meuY;
    
    String lastSelected;
    Map<String, String> contatos = new HashMap<>();
    
    @FXML
    private Label LABEL_AMBIENTE;
    @FXML
    private Label LABEL_Y;
    @FXML
    private Label LABEL_X;
    @FXML
    private Label LABEL_NOME;
    @FXML
    private HBox HBOX_DISPOSITIVOS;

    
    public void init(MainGameController mainGameController){
        main = mainGameController;    
        //aceitarEnter();
        stage = PPDChat.getStage();   

    }

    /*
    public void aceitarEnter() {
        TF_GAME.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isShiftDown()) {
                    if (event.getCode() == KeyCode.ENTER) {
                        String texto = TF_GAME.getText();
                        TF_GAME.setText(texto + "\n");
                        TF_GAME.positionCaret(TF_GAME.getText().length());
                    }
                } else {
                    if (event.getCode() == KeyCode.ENTER) {
                        String texto = TF_GAME.getText();
                        event.consume(); 
                        /*
                        try {
                            enviarMensagem(texto);
                        } catch (JMSException ex) {
                            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (NamingException ex) {
                            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (RemoteException ex) {
                            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        System.out.println("Mensagem Enviada");
                        TF_GAME.clear();
                    }
                }
            }
        });     
    }
*/

    public String getMeuNome() {
        return meuNome;
    }

    public void setMeuNome(String meuNome) {
        this.meuNome = meuNome;
    }

    public String getAmbienteAtual() {
        return ambienteAtual;
    }

    public void setAmbienteAtual(String ambienteAtual) {
        this.ambienteAtual = ambienteAtual;
    }

    public float getMeuX() {
        return meuX;
    }

    public void setMeuX(float meuX) {
        this.meuX = meuX;
    }

    public float getMeuY() {
        return meuY;
    }

    public void setMeuY(float meuY) {
        this.meuY = meuY;
    }
}
