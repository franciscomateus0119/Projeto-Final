/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.server;
import ppdchat.client.ClientInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 *
 * @author Matheus
 */
public interface ServerInterface extends Remote{
    
    void registerClient(ClientInterface client) throws RemoteException;
    void broadcastTexto(ClientInterface client, String texto) throws RemoteException;
    void broadcastStatus(ClientInterface client, String id, String status) throws RemoteException;
    void broadcastNick(ClientInterface client, String nick) throws RemoteException;
    void enviarConfig(ClientInterface client, String tipo) throws RemoteException;
    void broadcastStart(ClientInterface client) throws RemoteException;
    void receberStart() throws RemoteException;
}
