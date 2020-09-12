/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Matheus
 */
public interface ClientInterface extends Remote{
    void enviarClientName() throws RemoteException;
    void enviarPedidoAtualizarLista() throws RemoteException;
    void receberListaAtualizada(ArrayList<String> listaDispositivos) throws RemoteException;
    void receberArquivo(byte[] mydata, String filename, int length) throws RemoteException;
    
    /*
    void enviarStart() throws RemoteException;
    void receberStart() throws RemoteException;
    void enviarTexto(String texto) throws RemoteException;
    void receberTexto (String texto) throws RemoteException;
    void enviarStatus (String id,String status) throws RemoteException;
    void receberStatus (String id, String status) throws RemoteException;
    void receberChat (String chatMsg) throws RemoteException;
    void enviarNick(String nick) throws RemoteException;
    void receberNick(String nick) throws RemoteException;
    void receberConfig(String tipo) throws RemoteException;
    */
}
