/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.server;
import ppdchat.client.ClientInterface;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Matheus
 */
public class Server implements ServerInterface{
    private int clientesConectados = 0;
    private int reiniciarpartida = 0;
    protected ArrayList<ClientInterface> clients;
    public Map<Integer, String> names = new HashMap<>();
    
    public Server() throws RemoteException{
        super();
        clients = new ArrayList<>();
    }
    
    @Override
    public void registerClient(ClientInterface client) throws RemoteException{
        System.out.println(clients.size());
        if(clients.size()<=4){
            System.out.println("Novo Cliente!");
            clients.add(client);
            System.out.println("Nº de clientes: " + clients.size());
            System.out.println("Jogador " + clients.size() + " se conectou! Preparando sua Janela de Chat!");
            iniciarJogo();
        }
    }
    @Override
    public void broadcastTexto(ClientInterface client, String texto){
        if(clients.size() <= 4){
            int i = clients.indexOf(client);
            int clientsSize = clients.size();
            int j = 0;
            
            if (clients.size() == 2) {
                try {
                    if (i == 0) {
                        clients.get(1).receberTexto(texto);
                    }
                    if (i == 1) {
                        clients.get(0).receberTexto(texto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (clients.size() == 3) {
                try {
                    if (i == 0) {
                        clients.get(1).receberTexto(texto);
                        clients.get(2).receberTexto(texto);
                    }
                    if (i == 1) {
                        clients.get(0).receberTexto(texto);
                        clients.get(2).receberTexto(texto);
                    }
                    if (i == 2) {
                        clients.get(0).receberTexto(texto);
                        clients.get(1).receberTexto(texto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (clients.size() == 4) {
                try {
                    if (i == 0) {
                        clients.get(1).receberTexto(texto);
                        clients.get(2).receberTexto(texto);
                        clients.get(3).receberTexto(texto);
                    }
                    if (i == 1) {
                        clients.get(0).receberTexto(texto);
                        clients.get(2).receberTexto(texto);
                        clients.get(3).receberTexto(texto);
                    }
                    if (i == 2) {
                        clients.get(0).receberTexto(texto);
                        clients.get(1).receberTexto(texto);
                        clients.get(3).receberTexto(texto);
                    }
                    if (i == 3) {
                        clients.get(0).receberTexto(texto);
                        clients.get(1).receberTexto(texto);
                        clients.get(2).receberTexto(texto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            
        }
    }
    
    @Override
    public void broadcastStart(ClientInterface client) throws RemoteException{
        client.receberStart();
    }
    
    @Override
    public void receberStart() throws RemoteException{
        clientesConectados();
    }
    
    @Override
    public void broadcastStatus(ClientInterface client, String id, String status){
        if(clients.size() <= 4){
            int i = clients.indexOf(client);
            if (clients.size() == 2) {
                try {
                    if (i == 0) {
                        clients.get(1).receberStatus(id, status);
                    }
                    if (i == 1) {
                        clients.get(0).receberStatus(id, status);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (clients.size() == 3) {
                try {
                    if (i == 0) {
                        clients.get(1).receberStatus(id, status);
                        clients.get(2).receberStatus(id, status);
                    }
                    if (i == 1) {
                        clients.get(0).receberStatus(id, status);
                        clients.get(2).receberStatus(id, status);
                    }
                    if (i == 2) {
                        clients.get(0).receberStatus(id, status);
                        clients.get(1).receberStatus(id, status);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (clients.size() == 4) {
                try {
                    if (i == 0) {
                        clients.get(1).receberStatus(id, status);
                        clients.get(2).receberStatus(id, status);
                        clients.get(3).receberStatus(id, status);
                    }
                    if (i == 1) {
                        clients.get(0).receberStatus(id, status);
                        clients.get(2).receberStatus(id, status);
                        clients.get(3).receberStatus(id, status);
                    }
                    if (i == 2) {
                        clients.get(0).receberStatus(id, status);
                        clients.get(1).receberStatus(id, status);
                        clients.get(3).receberStatus(id, status);
                    }
                    if (i == 3) {
                        clients.get(0).receberStatus(id, status);
                        clients.get(1).receberStatus(id, status);
                        clients.get(2).receberStatus(id, status);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            
        }
        
    }
    

    @Override
    public void broadcastNick(ClientInterface client, String nick) throws RemoteException{
        int i = clients.indexOf(client);
        if(names.size()!=4){
            names.put(names.size(), nick);    
            if(names.size()==1){
                clients.get(0).receberNick(nick);
            }
            else if(names.size()==2){
                clients.get(0).receberNick(nick);
                clients.get(1).receberNick(nick);
                clients.get(1).receberNick(names.get(0));
                
            }
            else if(names.size()==3){
                clients.get(0).receberNick(nick);
                clients.get(1).receberNick(nick);
                clients.get(2).receberNick(nick);
                clients.get(2).receberNick(names.get(0));
                clients.get(2).receberNick(names.get(1));
                
            }
            else if(names.size()==4){
                clients.get(0).receberNick(nick);
                clients.get(1).receberNick(nick);
                clients.get(2).receberNick(nick);
                clients.get(3).receberNick(nick);
                clients.get(3).receberNick(names.get(0));
                clients.get(3).receberNick(names.get(1));
                clients.get(3).receberNick(names.get(2));
            }

        }
    }
    
    @Override
    public void enviarConfig(ClientInterface client, String tipo) throws RemoteException{
        client.receberConfig(tipo);
    }

    private void clientesConectados() throws RemoteException{
        this.clientesConectados+=1;
        if(this.clientesConectados <= 4){
            configurarCliente();
            
        }
    }
    
    private void iniciarJogo(){
        System.out.println("Preparações terminadas - CHAT START!");
        reiniciarpartida = 0;
        try{
            broadcastStart(clients.get(clients.size()-1));

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void configurarCliente() throws RemoteException{
        if(clients.size()==1){
            enviarConfig(this.clients.get(0),"A");
        }
        else if(clients.size()==2){
            enviarConfig(this.clients.get(1),"B");
        }
        else if(clients.size()==3){
            enviarConfig(this.clients.get(2),"C");
        }
        else if(clients.size()==4){
            enviarConfig(this.clients.get(3),"D");
        }
    }
    

}
