/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppdchat.server;
import ppdchat.client.ClientInterface;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 *
 * @author Matheus
 */
public interface ServerInterface extends Remote{
    void registerClient(ClientInterface client, String nome, String endereco, String x, String y) throws RemoteException;
    void registerClientName(ClientInterface client, String nomeClient) throws RemoteException;
    void receberAtualizarListaDispositivos(ClientInterface client, String nomeClient) throws RemoteException;
    void receberArquivo(byte[] mydata, String filename,int length, String dispositivoAlvo) throws RemoteException;
    void enviarArquivo(byte[] mydata, String filename,int length, String dispositivoAlvo) throws RemoteException;
    void encontrarEspaco() throws RemoteException;
    void procurarAmbiente(ClientInterface client, String nome, String endereco, String x, String y) throws RemoteException;
    void enviarAmbiente(ClientInterface client, String nomeAmbiente) throws RemoteException;
    /*
    void broadcastTexto(ClientInterface client, String texto) throws RemoteException;
    void broadcastStatus(ClientInterface client, String id, String status) throws RemoteException;
    void broadcastNick(ClientInterface client, String nick) throws RemoteException;
    void enviarConfig(ClientInterface client, String tipo) throws RemoteException;
    void broadcastStart(ClientInterface client) throws RemoteException;
    void receberStart() throws RemoteException;
    */
}
