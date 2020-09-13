/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import ppdchat.server.ServerInterface;
import ppdchat.server.Server;
import ppdchat.server.Server;
import ppdchat.server.ServerInterface;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.File;
/**
 *
 * @author Matheus
 */
public class InitServer extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {}
    public static void main(String args[]){
        System.out.println("Starting...");
        File storageDir = new File("C:/ServerStorage");
        if(!storageDir.isDirectory()){
            storageDir.mkdir();
        }
        try{
            ServerInterface server = new Server();
            ServerInterface serverinterface = (ServerInterface) UnicastRemoteObject.exportObject((ServerInterface) server, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("RMIServer", serverinterface);
            System.out.println("RMI Server iniciado com sucesso!!");
            System.out.println("Procurando Espaço de Tuplas...");
            try{
                server.encontrarEspaco();
            }catch(Exception e){e.printStackTrace();}
            
            
            
        }
        catch(RemoteException e){
            System.out.println("Failed Server Initialization!");
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
