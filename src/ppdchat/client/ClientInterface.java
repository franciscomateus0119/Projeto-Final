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
    void receberArquivos(ArrayList<byte []> mydata, ArrayList<String> filenames, String nomeOrigem) throws RemoteException;
    void receberAmbiente(String nomeAmbiente) throws RemoteException;
    void enviarNovaLocalizacao(String x, String y,String nome, String ambiente) throws RemoteException;
    void atualizarLocalizacao(String x, String y) throws RemoteException;
    void mostrarAlertaAmbienteIncompativel() throws RemoteException;
    void mostrarAlertaEnvioSucesso() throws RemoteException;
    String enviarAmbienteAtual() throws RemoteException;
    String getClientX() throws RemoteException;
    String getClientY() throws RemoteException;
    String getAmbienteAtual() throws RemoteException;
}
