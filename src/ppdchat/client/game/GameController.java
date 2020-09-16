/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.client.game;

import java.io.File;
import java.io.IOException;
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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    
    String meuNome;
    String ambienteAtual;
    String meuX;
    String meuY;
    
    String lastSelected;
    
    ArrayList<String> nomesDosDispositivos = new ArrayList<>();
    
    Map<String, String> contatos = new HashMap<>();
    
    ListView<String> listviewDispositivos;
    ListView<String> listviewArquivos;
    ObservableList<String> dispositivos;
    ObservableList<String> arquivos;
    List<File> arquivosSelecionados;
    @FXML
    private Label LABEL_AMBIENTE;
    @FXML
    private Label LABEL_Y; 
    @FXML
    private Label LABEL_X;
    @FXML
    private Label LABEL_NOME;
    @FXML
    private Label LABEL_STORAGE;
    
    @FXML Button BUTTON_SELECIONAR_ARQUIVO;
    @FXML Button BUTTON_MOSTRAR_DISPOSITIVOS;
    @FXML Button BUTTON_LOCALIZACAO;
    @FXML Button BUTTON_SELECIONAR_MULTIPLOS;
    @FXML Button BUTTON_ENVIAR_MULTIPLOS;

    
    @FXML TextField TF_NOME_DISPOSITIVO;
    @FXML TextField TF_X;
    @FXML TextField TF_Y;
    
    @FXML
    private HBox HBOX_DISPOSITIVOS;
    @FXML
    private HBox HBOX_ARQUIVOS_SELECIONADOS;

    
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
        
        listviewArquivos = new ListView<>();
        listviewArquivos.setPrefWidth(200);
        listviewArquivos.setPrefHeight(200);
        listviewArquivos.setLayoutX(376);
        listviewArquivos.setLayoutY(192);
        listviewArquivos.setVisible(true);
        listviewArquivos.toFront();
        HBOX_ARQUIVOS_SELECIONADOS.getChildren().addAll(listviewArquivos);
        arquivos = FXCollections.observableArrayList();
        
        listViewDispositivosListener();
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
        dispositivos.clear();
        listviewDispositivos.setItems(dispositivos);
        int tamanho = listaDeDispositivos.size();
        for (int f = 0; f < tamanho; f++) {
            //Se a Lista de Nomes não contém o nome do index atual da listaDeDispositivos recebida do servidor
            if (!nomesDosDispositivos.contains(listaDeDispositivos.get(f))) {
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
    
    public void resetListaDispositivos(){
        dispositivos.clear();
        listviewDispositivos.setItems(dispositivos);
    }
    

    
    @FXML
    public void selecionarArquivos(MouseEvent event){
        FileChooser fc = new FileChooser();
        arquivosSelecionados = fc.showOpenMultipleDialog(null);
        if(arquivosSelecionados!=null){
            listviewArquivos.getItems().clear();
            for(int i = 0; i < arquivosSelecionados.size();i++){
                //listviewArquivos.getItems().add(arquivosSelecionados.get(i).getAbsolutePath());
                listviewArquivos.getItems().add(arquivosSelecionados.get(i).getName());
            }
            
        }
        
    }
    @FXML
    public void enviarArquivos(MouseEvent event) throws IOException{
        if(TF_NOME_DISPOSITIVO.getText()!=null && !TF_NOME_DISPOSITIVO.getText().equals("")){
            if(arquivosSelecionados!=null){
                ArrayList<String> paths = new ArrayList<>();
                ArrayList<String> filenames = new ArrayList<>();
                for(int i = 0;i<arquivosSelecionados.size();i++){
                    paths.add(arquivosSelecionados.get(i).getAbsolutePath());
                    filenames.add(arquivosSelecionados.get(i).getName());
                    listviewArquivos.getItems().clear();
                    main.getClient().enviarArquivos(paths, filenames, TF_NOME_DISPOSITIVO.getText());
                }
                
            }
        }
        //Se um dispositivo não tiver sido selecionado
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione um dispositivo antes de enviar seus arquivos!", ButtonType.OK);
            alert.setGraphic(null);
            alert.setHeaderText("Dispositivo Não Selecionado");
            alert.show();
            TF_NOME_DISPOSITIVO.clear();
            TF_NOME_DISPOSITIVO.setPromptText("SELECIONE UM DISPOSITIVO");
        }
    }
    
    
    
    @FXML
    public void mudarLocalizacao(MouseEvent event){
        String x;
        String y;
        if(TF_X.getText()!= null && !TF_X.getText().equals("")){
            if(TF_Y.getText()!= null && !TF_Y.getText().equals("")){
                x = TF_X.getText();
                y = TF_Y.getText();
                TF_X.clear();
                TF_X.setPromptText("Nova Localização X");
                TF_Y.clear();
                TF_Y.setPromptText("Nova Localização Y");
                try{
                    main.getClient().enviarNovaLocalizacao(x, y, meuNome, ambienteAtual);
                }catch(Exception e){e.printStackTrace();}
                
            }
            else{
                Alert alert = new Alert(Alert.AlertType.WARNING, "Preencha o campo com um valor válido!", ButtonType.OK);
                alert.setGraphic(null);
                alert.setHeaderText("Valor Y inválido");
                alert.show();
                TF_Y.setPromptText("DIGITE UM Y");
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING, "Preencha o campo com um valor válido!", ButtonType.OK);
            alert.setGraphic(null);
            alert.setHeaderText("Valor X inválido");
            alert.show();
            TF_X.setPromptText("DIGITE UM X");
        }
    }
    

    
    public void updateInfo(){
        System.out.println("Meu Nome: " + meuNome + " - Ambiente Atual: " + ambienteAtual +" - X: " + meuX + " - Y: " + meuY);
        Platform.runLater(() -> {
            LABEL_NOME.setText("Nome: " + meuNome);
            LABEL_AMBIENTE.setText("Ambiente: " + ambienteAtual);
            LABEL_X.setText("X: " + (meuX));
            LABEL_Y.setText("Y: " + (meuY));
        });
        
        
    }
    
    public void alertNovaLocalizacao(){
        Alert alert = new Alert(Alert.AlertType.WARNING, "A mudança para uma nova localização foi um sucesso!", ButtonType.OK);
        alert.setGraphic(null);
        alert.setHeaderText("Mudança de Localização");
        alert.show();
    }
    
    public void alertAmbienteSetado(){
        Alert alert = new Alert(Alert.AlertType.WARNING, "Dispositivo presente em: " + ambienteAtual, ButtonType.OK);
        alert.setGraphic(null);
        alert.setHeaderText("Ambiente Encontrado");
        alert.show();
    }
    
    public void alertAmbienteIncompatível(){
        Alert alert = new Alert(Alert.AlertType.WARNING, "O dispositivo selecionado está em um ambiente diferente do seu.\nPor favor, atualize a lista de dispositivos", ButtonType.OK);
        alert.setGraphic(null);
        alert.setHeaderText("Ambientes Diferentes");
        alert.show();
    }
    
    public void alertEnvioSucesso(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Seus arquivos foram enviados com sucesso!", ButtonType.OK);
                alert.setGraphic(null);
                alert.setHeaderText("Arquivos Enviados com Sucesso!");
                alert.show();
    }
    public void alertEnvioFalha(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Não foi possível enviar seus arquivos!", ButtonType.OK);
                alert.setGraphic(null);
                alert.setHeaderText("Falha no envio de Arquivos!");
                alert.show();
    }

    public void listViewDispositivosListener() {
        listviewDispositivos.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
            String selectedItem = listviewDispositivos.getSelectionModel().getSelectedItem();
            int index = listviewDispositivos.getSelectionModel().getSelectedIndex();    
            TF_NOME_DISPOSITIVO.setText(selectedItem);
            System.out.println("Dispositivo selecionado: " + selectedItem);
        });
    }

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

    public String getMeuX() {
        return meuX;
    }

    public void setMeuX(String meuX) {
        this.meuX = meuX;
    }

    public String getMeuY() {
        return meuY;
    }

    public void setMeuY(String meuY) {
        this.meuY = meuY;
    }

    public Label getLABEL_STORAGE() {
        return LABEL_STORAGE;
    }

    public void setLABEL_STORAGE(Label LABEL_STORAGE) {
        this.LABEL_STORAGE = LABEL_STORAGE;
    }
    
}
