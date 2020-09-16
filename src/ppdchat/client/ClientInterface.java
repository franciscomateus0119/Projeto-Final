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
    void receberArquivos(ArrayList<byte []> mydata, ArrayList<String> filenames) throws RemoteException;
    void receberAmbiente(String nomeAmbiente) throws RemoteException;
    void enviarNovaLocalizacao(String x, String y,String nome, String ambiente) throws RemoteException;
    void atualizarLocalizacao(String x, String y) throws RemoteException;
    String enviarAmbienteAtual() throws RemoteException;
    String getClientX() throws RemoteException;
    String getClientY() throws RemoteException;
    String getAmbienteAtual() throws RemoteException;
    
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
