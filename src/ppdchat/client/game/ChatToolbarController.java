/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.client.game;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.Random;

import java.util.HashMap;
import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.scene.shape.Circle;


import java.rmi.RemoteException;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * FXML Controller class
 *
 * @author Matheus
 */
public class ChatToolbarController{
    private MainGameController main;

    /*
    int nContato = 0;
    Random random = new Random();
    int randomNum = random.nextInt((100 - 1) + 1);
    String nick = "Anônimo" + randomNum +": ";
    */

    public void init(MainGameController mainGameController){
        main = mainGameController; 
        
    }   
   
}
