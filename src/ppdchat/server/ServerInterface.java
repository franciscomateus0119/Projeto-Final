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
import java.util.ArrayList;
/**
 *
 * @author Matheus
 */
public interface ServerInterface extends Remote{
    void registerClient(ClientInterface client, String nome,  String x, String y) throws RemoteException;
    void registerClientName(ClientInterface client, String nomeClient) throws RemoteException;
    void receberAtualizarListaDispositivos(ClientInterface client, String nomeClient, String nomeAmbiente) throws RemoteException;
    void receberArquivos(ArrayList<byte []> mydata, ArrayList<String> filenames, String dispositivoAlvo, String dispositivoOrigem) throws RemoteException;
    void enviarArquivos(ArrayList<byte []> mydata, ArrayList<String> filenames, ClientInterface client, String nomeOrigem) throws RemoteException;
    void encontrarEspaco() throws RemoteException;
    void procurarAmbiente(ClientInterface client, String nome, String x, String y) throws RemoteException;
    void enviarAmbiente(ClientInterface client, String nomeAmbiente) throws RemoteException;
    void receberPedidoNovaLocalizacao(ClientInterface client, String x, String y, String nome, String ambiente) throws RemoteException;
    ArrayList<String> getNames() throws RemoteException;

}
