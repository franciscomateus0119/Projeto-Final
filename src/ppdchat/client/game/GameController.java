/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.client.game;

import java.io.File;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
import javafx.stage.FileChooser;
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
    
    ArrayList<String> nomesDosDispositivos = new ArrayList<>();
    
    Map<String, String> contatos = new HashMap<>();
    
    ListView<String> listviewDispositivos;
    
    ObservableList<String> dispositivos;
    
    @FXML
    private Label LABEL_AMBIENTE;
    @FXML
    private Label LABEL_Y; 
    @FXML
    private Label LABEL_X;
    @FXML
    private Label LABEL_NOME;
    
    @FXML Label LABEL_DISPOSITIVOS_ENCONTRADOS;
    @FXML Button BUTTON_SELECIONAR_ARQUIVO;
    @FXML Button BUTTON_MOSTRAR_DISPOSITIVOS;
    @FXML Button BUTTON_ENVIAR_ARQUIVO;
    
    @FXML TextField TF_SELECIONAR_ARQUIVO;
    
    @FXML
    private HBox HBOX_DISPOSITIVOS;
    

    
    public void init(MainGameController mainGameController){
        main = mainGameController;
        
        listviewDispositivos = new ListView<>();
        listviewDispositivos.setPrefWidth(200);
        listviewDispositivos.setPrefHeight(200);
        listviewDispositivos.setLayoutX(376);
        listviewDispositivos.setLayoutY(192);
        listviewDispositivos.setVisible(true);
        listviewDispositivos.toFront();
        HBOX_DISPOSITIVOS.getChildren().addAll(listviewDispositivos);
        dispositivos = FXCollections.observableArrayList();
        
        //aceitarEnter();
        stage = PPDChat.getStage();   

    }
    
    @FXML
    
    public void pedirAtualizarLista(MouseEvent event){
        try {
            main.getClient().enviarPedidoAtualizarLista();
        } catch (Exception e) {e.printStackTrace();}
    }
    
    public void atualizarListaDispositivos(ArrayList<String> listaDeDispositivos) {
        //Limpa a ListaView de Dispositivos
        listviewDispositivos.getItems().clear();
        //Limpa a Lista dos Nomes dos Dispositivos no Ambiente (ArralistList)
        nomesDosDispositivos.clear();
        int tamanho = listaDeDispositivos.size();
        for(int f = 0;f<tamanho;f++){
            //Se a Lista de Nomes não contém o nome do index atual da listaDeDispositivos recebida do servidor
            if(!nomesDosDispositivos.contains(listaDeDispositivos.get(f))){
                //Adicione o nome do dispositivo à lista de dispositivos no ambiente
                nomesDosDispositivos.add(listaDeDispositivos.get(f));
                //Adicione o nome do dispositivo na lista observável de dispositivos
                dispositivos.add(listaDeDispositivos.get(f));
                //Coloque a lista observável no ListViews
                listviewDispositivos.setItems(dispositivos);
                System.out.println("Novo usuario disponível: " + listaDeDispositivos.get(f));
            }
  
        } 
    }
    
    @FXML
    public void selecionarArquivo(MouseEvent event){
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        if(selectedFile!=null){
            TF_SELECIONAR_ARQUIVO.setText(selectedFile.getAbsolutePath());
        }
    }
    @FXML
    public void enviarArquivo(MouseEvent event){
        if(TF_SELECIONAR_ARQUIVO.getText()!=null && !TF_SELECIONAR_ARQUIVO.getText().equals("")){
            File fileDir = new File(TF_SELECIONAR_ARQUIVO.getText());
            if(fileDir.isFile()){
                try{
                    main.getClient().enviarArquivo(TF_SELECIONAR_ARQUIVO.getText(), fileDir.getName());
                }catch(Exception e){e.printStackTrace();}
                TF_SELECIONAR_ARQUIVO.clear();
                TF_SELECIONAR_ARQUIVO.setPromptText("Diretório do Arquivo");
            }
            
        }
    }
    
    public void updateInfo(){
        Platform.runLater(() -> {
            LABEL_NOME.setText(meuNome);
            LABEL_AMBIENTE.setText(ambienteAtual);
            LABEL_X.setText(Float.toString(meuX));
            LABEL_Y.setText(Float.toString(meuY));
        });
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

    public Label getLABEL_DISPOSITIVOS_ENCONTRADOS() {
        return LABEL_DISPOSITIVOS_ENCONTRADOS;
    }

    public void setLABEL_DISPOSITIVOS_ENCONTRADOS(Label LABEL_DISPOSITIVOS_ENCONTRADOS) {
        this.LABEL_DISPOSITIVOS_ENCONTRADOS = LABEL_DISPOSITIVOS_ENCONTRADOS;
    }
    
    
}
