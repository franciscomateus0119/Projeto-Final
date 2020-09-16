/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.client;
import javax.jms.*;
import javax.jms.JMSException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import ppdchat.client.game.MainGameController;
import ppdchat.client.game.MenuController;
import ppdchat.server.ServerInterface;
import ppdchat.client.ClientFile;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;

/**
 *
 * @author Matheus
 */
public class Client extends UnicastRemoteObject implements ClientInterface, Serializable{
    private ServerInterface server;
    private MainGameController mainController;
    private MenuController menuController;
    private String serverIP;
    private String serverName;
    private int serverPort;
    private String nome;
    private String clientX;
    private String clientY;
    private String ambienteAtual = "";
    Map<Integer, String> names = new HashMap<>();
    private int nameCounter = 0;
    
    Registry serverRegistry;
    //String filename = "1.png";
    //String filepath = "C:/Users/Matheus/Desktop/Joguinhos/4chat/teste/" +filename;
    String clientstoragepath;
    
    public Client(ServerInterface server, String ip, String servername, int port, Registry registro, String nome, String x, String y,  String storagepath) throws RemoteException{
        super();
        this.server = server;
        this.nome = nome;
        this.serverIP = ip;
        this.serverName = servername;
        this.serverPort = port;
        this.serverRegistry = registro;
        this.clientX = x;
        this.clientY = y;
        this.clientstoragepath = storagepath;
    }

    public void setMenuController(MenuController menucontroller){
        menuController = menucontroller;
        Platform.runLater(() -> this.menuController.gameStartReady());
        
    }

    public void setGameController(MainGameController mainController) throws RemoteException {
        this.mainController = mainController;
        setInfo();
        
    }

    public String getNome() {
        return nome;
    }
    
    public void setInfo(){
        Platform.runLater(() -> {
            mainController.getGameController().setMeuNome(nome);
            mainController.getGameController().setMeuX(clientX);
            mainController.getGameController().setMeuY(clientY);
            mainController.getGameController().setAmbienteAtual(ambienteAtual);
            mainController.getGameController().getLABEL_STORAGE().setText(clientstoragepath);
            mainController.getGameController().updateInfo();
        });
        try{
            enviarClientName();
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void enviarClientName() throws RemoteException{
        server.registerClientName(this, nome);
    }
    @Override
    public String enviarAmbienteAtual() throws RemoteException{
        return ambienteAtual;
    }
    @Override
    public void enviarPedidoAtualizarLista() throws RemoteException{
        System.out.println("Enviando pedido de AtualizarLista!");
        server.receberAtualizarListaDispositivos(this, nome, ambienteAtual);
    }
    
    @Override
    public void enviarNovaLocalizacao(String x, String y, String nome, String ambiente) throws RemoteException{
        server.receberPedidoNovaLocalizacao(this, x, y, nome, ambiente);
    }
    
    @Override
    public void atualizarLocalizacao(String x, String y) throws RemoteException{
        System.out.println("Atualizando localização de ("+clientX+","+clientY+") para(" + x +"," + y+").");
        Platform.runLater(() -> {
            this.clientX = x;
            this.clientY = y;
            mainController.getGameController().setMeuX(x);
            mainController.getGameController().setMeuY(y);
            mainController.getGameController().updateInfo();
        });
    }
    
    @Override
    public void receberListaAtualizada(ArrayList<String> listaDispositivos) throws RemoteException{
        listaDispositivos.remove(nome);
        System.out.println("Lista de Dispositivos Recebidas! Tamanho: " + listaDispositivos.size());
        if(listaDispositivos.size() > 0){
            Platform.runLater(() -> {
                mainController.getGameController().atualizarListaDispositivos(listaDispositivos);
                //mainController.getGameController().getLABEL_DISPOSITIVOS_ENCONTRADOS().setText("Dispositivo(s) Encontrado(s)!");
            });   
        }
        else{
            Platform.runLater(() -> {
                mainController.getGameController().resetListaDispositivos();
                //mainController.getGameController().getLABEL_DISPOSITIVOS_ENCONTRADOS().setText("Dispositivo(s) Encontrado(s)!");
            }); 
        }
        /*
        else{
            Platform.runLater(() -> {
                mainController.getGameController().getLABEL_DISPOSITIVOS_ENCONTRADOS().setText("Nenhum Dispositivo Encontrado!");
            });
            
        }
        */
            
        
    }
    

    
    public void enviarArquivos(ArrayList<String> paths, ArrayList<String> filenames, String dispositivoAlvo) throws FileNotFoundException, IOException{
        ArrayList<File> clientpathfiles = new ArrayList<>();
        ArrayList<byte []> mydata = new ArrayList<>();
        ArrayList<FileInputStream> in = new ArrayList();
        for(int i=0;i<paths.size();i++){
            
            clientpathfiles.add(new File(paths.get(i)));
            
        }
        for(int i=0;i<clientpathfiles.size();i++){
            //byte [] data=new byte[(int) clientpathfiles.get(i).length()];
            mydata.add(new byte[(int) clientpathfiles.get(i).length()]);
            
        }
        
        for(int i=0;i<clientpathfiles.size();i++){
            in.add(new FileInputStream(clientpathfiles.get(i)));
        }
        for(int i=0;i<in.size();i++){
            in.get(i).read(mydata.get(i),0,mydata.get(i).length);
        }
        server.receberArquivos(mydata, filenames, dispositivoAlvo);
        
        
    }
    
    @Override
    public void receberArquivos(ArrayList<byte []> mydata, ArrayList<String> filenames) throws RemoteException{
        try{
            for(int i = 0;i<filenames.size();i++){
                File pathfile = new File(clientstoragepath+filenames.get(i));
                //System.out.println("Enviando arquivo para: "+pathfile);
                FileOutputStream out = new FileOutputStream(pathfile);
                byte[] data = mydata.get(i);
                out.write(data);
                out.flush();
                out.close();
            }
        }catch(Exception e){e.printStackTrace();}
    }

    
    @Override
    public void receberAmbiente(String nomeAmbiente) throws RemoteException{
        System.out.println("Novo Ambiente de nome " + nomeAmbiente);
        this.ambienteAtual = nomeAmbiente;
        Platform.runLater(() -> {
            mainController.getGameController().setAmbienteAtual(nomeAmbiente);
            mainController.getGameController().updateInfo();
            mainController.getGameController().resetListaDispositivos();
        });
    }
    
    
    
    //<editor-fold defaultstate="collapsed" desc="OldProject">
    /*
    @Override
    public void enviarStart() throws RemoteException{
        server.receberStart();
    }
    
    @Override
    public void receberStart(){
        System.out.println("Iniciando Partida!");
        Platform.runLater(() -> this.menuController.gameStartReady());
    }
    
    @Override
    public void enviarTexto(String texto) throws RemoteException{
        System.out.println("Você Digitou: "+ texto);
        server.broadcastTexto(this, texto);
        //Platform.runLater(() ->(mainController.getChatToolbarController().mostrarTexto(texto)));
    }
    
    @Override
    public void receberTexto(String texto){
        System.out.println("Mensagem recebida do servidor: " + texto);
        //Platform.runLater(() -> (mainController.getChatToolbarController().mostrarTexto(texto)));
    }
    
    
    
    @Override
    public void enviarStatus(String id, String status) throws RemoteException{
        server.broadcastStatus(this, id, status);
    }
    
    @Override
    public void receberStatus(String id, String status){
        //Platform.runLater(() -> mainController.getGameController().statusPessoa(id, status));
    }
    
    
    @Override
    public void receberChat(String chatmsg){
        //Platform.runLater(() -> mainController.getChatToolbarController().selectChatBox(chatmsg));
    }
    
    @Override
    public void enviarNick(String nick) throws RemoteException{
        server.broadcastNick(this, nick);
    }
    
    @Override
    public void receberNick(String nick) {
        //Setando o nick do cliente. Sempre será o primeiro nick recebido
        System.out.println(names.size());
        if (names.size() == 0) {
            this.names.put(names.size(), nick);
            //mainController.getGameController().setMeuNome(nick);
            Platform.runLater(() -> {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
        }
        //Setando os próximos 3 contatos
        else if(names.size()!= 4){
            this.names.put(names.size(), nick);
            Platform.runLater(() -> {
                try {
                    
                    //mainController.getChatToolbarController().setContato(nick);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
        }
        
    }
    
    @Override
    public void receberConfig(String tipo) throws RemoteException{
        if (tipo.matches("A")) {
            System.out.println("(A)Meu nome é: " + this.nome);
            Platform.runLater(() -> {
                //mainController.getGameController().setPessoa("A");
            });
            System.out.println("(A)Meu nome é: " + this.nome);
            enviarNick(this.nome);
        } else if (tipo.matches("B")) {
            System.out.println("(B)Meu nome é: " + this.nome);
            Platform.runLater(() -> {
                //mainController.getGameController().setPessoa("B");
            });
            
            System.out.println("(B)Meu nome é: " + this.nome);
            enviarNick(this.nome);
        } else if (tipo.matches("C")) {
            System.out.println("(C)Meu nome é: " + this.nome);
            Platform.runLater(() -> {
                //mainController.getGameController().setPessoa("C");
            });
            
            enviarNick(this.nome);
        } else if (tipo.matches("D")) {
            System.out.println("(D)Meu nome é: " + this.nome);
            Platform.runLater(() -> {
                //mainController.getGameController().setPessoa("D");
            });
            
            enviarNick(this.nome);
        }
        
    }
    */
//</editor-fold>

    @Override
    public String getClientX() throws RemoteException{
        return clientX;
    }

    @Override
    public String getClientY() throws RemoteException{
        return clientY;
    }
}
