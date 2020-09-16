/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.client.game;

import java.io.File;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;


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
    String valorX;
    String valorY;
    
    
    @FXML TextField TF_NOME;
    @FXML TextField TF_X;
    @FXML TextField TF_Y;
    @FXML TextField TF_NAME_SERVER;
    @FXML TextField TF_IP_SERVER;
    @FXML TextField TF_PORT_SERVER;
    @FXML TextField TF_FOLDER;
    @FXML Button BUTTON_FOLDER;
    @FXML Button BUTTON_VERIFICAR;
    
    @FXML
    private Button buttonConnect;
    
    Registry registry = null;
    ServerInterface server = null;
    
    @FXML
    public void initialize() {
        buttonConnect.setBackground(startbg);
    }

    @FXML
    public void escolherPasta(MouseEvent event){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        if(selectedDirectory != null){
            TF_FOLDER.setText(selectedDirectory.getAbsolutePath());
        }
    }
    
    @FXML
    public void verifyInfo(MouseEvent event){
        boolean nomeOK = false;
        boolean xOK = false;
        boolean yOK = false;
        if(registry == null || server == null){
            try{
                registry = LocateRegistry.getRegistry(TF_IP_SERVER.getText(), Integer.parseInt(TF_PORT_SERVER.getText()));
                server = (ServerInterface) registry.lookup(TF_NAME_SERVER.getText());
                System.out.println("Server: " + server);
                System.out.println("Server Registry: " + registry);
                //Verifica Nome
                if(TF_NOME.getText()!=null && !TF_NOME.getText().equals("")){
                    if(server.getNames().contains(TF_NOME.getText())){
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Nome Indisponível! Por favor, escolha outro nome.", ButtonType.OK);
                        alert.setGraphic(null);
                        alert.showAndWait();
                        TF_NOME.clear();
                        TF_NOME.setPromptText("Digite seu Nome");
                    }
                    else{
                        nome = TF_NOME.getText();
                        nomeOK = true;
                    }
                    
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, digite um nome!", ButtonType.OK);
                    alert.setGraphic(null);
                    alert.showAndWait();
                }
                //Verifica Localização X
                if(TF_X.getText()!=null || TF_X.getText().equals("")){
                    if (isNumeric(TF_X.getText())) {
                        valorX = TF_X.getText();
                        xOK = true;
                    } else {
                        System.out.println("Valor de X inválido! Por favor, digite um número!");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Valor de X inválido! Por favor, digite um número.", ButtonType.OK);
                        alert.setGraphic(null);
                        alert.showAndWait();
                        TF_X.clear();
                        TF_X.setPromptText("Digite sua localização X");
                    }
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, digite um valor para a localização X.", ButtonType.OK);
                    alert.setGraphic(null);
                    alert.showAndWait();
                    TF_X.clear();
                    TF_X.setPromptText("Digite sua localização X");
                }
                
                //Verifica Localização Y
                if(TF_Y.getText()!=null || TF_Y.getText().equals("")){
                    if(isNumeric(TF_Y.getText())){
                        valorY = TF_Y.getText();
                        yOK = true;
                    }
                    else{
                        System.out.println("Valor de Y inválido! Por favor, digite um número!");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Valor de Y inválido! Por favor, digite um número.", ButtonType.OK);
                        alert.setGraphic(null);
                        alert.showAndWait();
                        TF_Y.clear();
                        TF_Y.setPromptText("Digite sua localização Y");
                    }
                    
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, digite um valor para a localização Y.", ButtonType.OK);
                    alert.setGraphic(null);
                    alert.showAndWait();
                    TF_Y.clear();
                    TF_Y.setPromptText("Digite sua localização Y");
                }
                if(nomeOK && xOK && yOK){
                    buttonConnect.setDisable(false);
                    buttonConnect.setVisible(true);
                }
            }catch(Exception e){e.printStackTrace();}
            
        }
        else{
            try{
                //Verifica Nome
                if(TF_NOME.getText()!=null && !TF_NOME.getText().equals("")){
                    if(server.getNames().contains(TF_NOME.getText())){
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Nome Indisponível! Por favor, escolha outro nome.", ButtonType.OK);
                        alert.setGraphic(null);
                        alert.showAndWait();
                        TF_NOME.clear();
                        TF_NOME.setPromptText("Digite seu Nome");
                    }
                    else{
                        nome = TF_NOME.getText();
                        nomeOK = true;
                    }
                    
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, digite um nome!", ButtonType.OK);
                    alert.setGraphic(null);
                    alert.showAndWait();
                }
                //Verifica Localização X
                if(TF_X.getText()!=null || TF_X.getText().equals("")){
                    if (isNumeric(TF_X.getText())) {
                        valorX = TF_X.getText();
                        xOK = true;
                    } else {
                        System.out.println("Valor de X inválido! Por favor, digite um número!");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Valor de X inválido! Por favor, digite um número.", ButtonType.OK);
                        alert.setGraphic(null);
                        alert.showAndWait();
                        TF_X.clear();
                        TF_X.setPromptText("Digite sua localização X");
                    }
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, digite um valor para a localização X.", ButtonType.OK);
                    alert.setGraphic(null);
                    alert.showAndWait();
                    TF_X.clear();
                    TF_X.setPromptText("Digite sua localização X");
                }
                
                //Verifica Localização Y
                if(TF_Y.getText()!=null || TF_Y.getText().equals("")){
                    if(isNumeric(TF_Y.getText())){
                        valorY = TF_Y.getText();
                        yOK = true;
                    }
                    else{
                        System.out.println("Valor de Y inválido! Por favor, digite um número!");
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Valor de Y inválido! Por favor, digite um número.", ButtonType.OK);
                        alert.setGraphic(null);
                        alert.showAndWait();
                        TF_Y.clear();
                        TF_Y.setPromptText("Digite sua localização Y");
                    }
                    
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Por favor, digite um valor para a localização Y.", ButtonType.OK);
                    alert.setGraphic(null);
                    alert.showAndWait();
                    TF_Y.clear();
                    TF_Y.setPromptText("Digite sua localização Y");
                }
                if(nomeOK && xOK && yOK){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Informações verificadas! Pode iniciar a aplicação", ButtonType.OK);
                    alert.setGraphic(null);
                    alert.showAndWait();
                    buttonConnect.setDisable(false);
                    buttonConnect.setVisible(true);
                }
            }catch(Exception e){e.printStackTrace();}
        }
        
    }
    
    @FXML
    public void connect(ActionEvent event){
        try{
            
            
            //valorX = Float.parseFloat(TF_X.getText());
            //valorY = Float.parseFloat(TF_Y.getText());
            //valorX = TF_X.getText();
            //valorY = TF_Y.getText();
            if (TF_FOLDER.getText() == null || TF_FOLDER.getText().equals("")) {

                File storageDir = new File("C:/" + TF_NOME.getText() + "Storage");
                if (!storageDir.isDirectory()) {
                    storageDir.mkdir();
                    System.out.println("Storage Folder: " + "C:/" + TF_NOME.getText() + "Storage/");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Os arquivos serão salvos na pasta C:/" + TF_NOME.getText() + "Storage/", ButtonType.OK);
                    alert.setGraphic(null);
                    alert.showAndWait();
                    TF_FOLDER.setText("C:/" + TF_NOME.getText() + "Storage/");
                } else {
                    int i = 0;
                    while (storageDir.isDirectory()) {
                        i++;
                        storageDir = new File("C:/" + TF_NOME.getText() + "Storage_" + i);
                    }
                    storageDir.mkdir();
                    System.out.println("Storage Folder: " + "C:/" + TF_NOME.getText() + "Storage_" + i + "/");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Os arquivos serão salvos na pasta C:/" + TF_NOME.getText() + "Storage_"+ i + "/", ButtonType.OK);
                    alert.setGraphic(null);
                    alert.showAndWait();
                    TF_FOLDER.setText("C:/" + TF_NOME.getText() + "Storage_" + i + "/");
                }

            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Os arquivos serão salvos na pasta " + TF_FOLDER.getText(), ButtonType.OK);
            alert.setGraphic(null);
            alert.showAndWait();
            client = new Client(server,TF_IP_SERVER.getText(),TF_NAME_SERVER.getText(),Integer.parseInt(TF_PORT_SERVER.getText()),
                    registry,TF_NOME.getText(), valorX, valorY,  TF_FOLDER.getText());
            client.setMenuController(this);
            
            server.registerClient(client,TF_NOME.getText(),valorX,valorY);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        float d = Float.parseFloat(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
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
