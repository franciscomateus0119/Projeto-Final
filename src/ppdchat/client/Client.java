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
    
    Registry serverRegistry;
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
            mainController.getGameController().getLABEL_STORAGE().setText("Pasta: "+ clientstoragepath);
            mainController.getGameController().updateInfo();
            mainController.getGameController().alertAmbienteSetado();
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
            mainController.getGameController().alertNovaLocalizacao();
        });
    }
    
    @Override
    public void receberListaAtualizada(ArrayList<String> listaDispositivos) throws RemoteException{
        listaDispositivos.remove(nome);
        System.out.println("Lista de Dispositivos Recebidas! Tamanho: " + listaDispositivos.size());
        if(listaDispositivos.size() > 0){
            Platform.runLater(() -> {
                mainController.getGameController().atualizarListaDispositivos(listaDispositivos);
            });   
        }
        else{
            Platform.runLater(() -> {
                mainController.getGameController().resetListaDispositivos();
            }); 
        }   
    }
    

    
    public void enviarArquivos(ArrayList<String> paths, ArrayList<String> filenames, String dispositivoAlvo) throws FileNotFoundException, IOException{
        ArrayList<File> clientpathfiles = new ArrayList<>();
        ArrayList<byte []> mydata = new ArrayList<>();
        ArrayList<FileInputStream> in = new ArrayList();
        for(int i=0;i<paths.size();i++){
            
            clientpathfiles.add(new File(paths.get(i)));
            
        }
        for(int i=0;i<clientpathfiles.size();i++){
            mydata.add(new byte[(int) clientpathfiles.get(i).length()]);
            
        }
        
        for(int i=0;i<clientpathfiles.size();i++){
            in.add(new FileInputStream(clientpathfiles.get(i)));
        }
        for(int i=0;i<in.size();i++){
            in.get(i).read(mydata.get(i),0,mydata.get(i).length);
        }
        server.receberArquivos(mydata, filenames, dispositivoAlvo, nome);
        
        
    }
    
    @Override
    public void receberArquivos(ArrayList<byte []> mydata, ArrayList<String> filenames, String nomeOrigem) throws RemoteException{
        try{
            for(int i = 0;i<filenames.size();i++){
                File pathfile = new File(clientstoragepath+filenames.get(i));
                FileOutputStream out = new FileOutputStream(pathfile);
                byte[] data = mydata.get(i);
                out.write(data);
                out.flush();
                out.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    @Override
    public void mostrarAlertaAmbienteIncompativel() throws RemoteException{
        Platform.runLater(()->{
            mainController.getGameController().alertAmbienteIncompatível();
            mainController.getGameController().alertEnvioFalha();
        });
    }
    
    @Override
    public void mostrarAlertaEnvioSucesso() throws RemoteException{
        Platform.runLater(()->{
            mainController.getGameController().alertEnvioSucesso();
        });
    }

    @Override
    public String getClientX() throws RemoteException{
        return clientX;
    }

    @Override
    public String getClientY() throws RemoteException{
        return clientY;
    }

    @Override
    public String getAmbienteAtual() throws RemoteException {
        return ambienteAtual;
    }
    
    
}
