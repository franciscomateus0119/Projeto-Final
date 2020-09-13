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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;

import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;
import ppdchat.server.Lookup;
import ppdchat.utils.*;


/**
 *
 * @author Matheus
 */
public class Server implements ServerInterface, Serializable{
    private int clientesConectados = 0;
    protected ArrayList<ClientInterface> clients;
    public Map<String, ClientInterface> clientsByName = new HashMap<>();
    public Map<ClientInterface, String> namesByClient = new HashMap<>();
    public ArrayList<String> todosOsNomes = new ArrayList<>();
    public Map<Integer, String> names = new HashMap<>();
    
    Lookup finder;
    JavaSpace space;
    
    String storagepath = "C:/ServerStorage/";
    
    public Server() throws RemoteException{
        super();
        clients = new ArrayList<>();
    }
    
    @Override
    public void registerClient(ClientInterface client, String nome, String endereco, float x, float y) throws RemoteException{
        //System.out.println(clients.size());
        System.out.println("Novo Dispositivo!");
        clients.add(client);
        System.out.println("Nº de dispositivos: " + clients.size());
        System.out.println("Encontrando um ambiente para o dispositivo... ");
        //iniciarJogo();
        
    }
    
    @Override
    public void registerClientName(ClientInterface client, String nome){
        clientsByName.put(nome, client);
        namesByClient.put(client, nome);
        todosOsNomes.add(nome);
        System.out.println("Cliente " + client + " de nome "+nome+" adicionado ao HashMap clientsByName");
        System.out.println("ClientsByName size: " + clientsByName.size());
        System.out.println("TodosOsNomes size: " + todosOsNomes.size());
    }
    
    @Override
    public void receberAtualizarListaDispositivos(ClientInterface client, String nome){
        System.out.println("Pedido de atualização de lista de dispostivos recebido por " + nome);
        try {
            client.receberListaAtualizada(todosOsNomes);
        } catch (Exception e) {e.printStackTrace();}

   
    }
    
    @Override
    public void receberArquivo(byte[] mydata, String filename, int length, String dispositivoAlvo) throws RemoteException{

        enviarArquivo(mydata, filename, length, dispositivoAlvo);
    }
    
    @Override
    public void enviarArquivo(byte[] mydata, String filename,int length,String dispositivoAlvo) throws RemoteException{
        ClientInterface client = clientsByName.get(dispositivoAlvo);
        client.receberArquivo(mydata, filename, length);
    }
    
    //Funções do Espaço de Tuplas
    
    @Override
    public void procurarAmbiente(String nome, String endereco, float x, float y) throws RemoteException{
        ListaDeAmbientes template = new ListaDeAmbientes();
        if (template == null) {
            System.out.println("Template nulo!");
        }
        try{
            ListaDeAmbientes listadeambientes = (ListaDeAmbientes) space.take(template, null, 5 * 1000);
            //Se não existir lista de ambientes, crie um novo ambiente e uma nova lista de ambientes
            if (listadeambientes == null) {
                //Cria um novo Ambiente, com sua localização sendo a do dispositivo (pois ele é o primeiro a entrar)
                //Envia Ambiente para o Servidor de Ambientes (Espaço de Tuplas)
                //Coloca o novo Ambiente na lista de Ambientes
                //Envia a Lista de Ambientes para o Servidor de Ambientes
            }
            //Se já existir uma lista de ambientes
            else{
                //procura a lista de ambientes
                //para cada ambiente na lista de ambientes, verifica se já existe um dispositivo com o mesmo nome
                
                //-> Caso Normal <-
                //Se o nome do dispositivo for único, compara a distância do ambiente com o dispositivo
                //Se a distância for permitida, coloca o dispositivo na lista de dispositivos do ambiente
                //Com o ambiente atualizado, envie o ambiente para o Servidor de Ambientes
                
                //-> Caso nome não único <-
                //Se o nome do dispositivo não for único e não existir ambiente possível par inseri-lo, crie um novo ambiente, verifique a distancia e o envie para o servidor

                //-> Caso nome único<-
                //Se o nome do dispositivo for único, verifique a distância, adicione ao ambiente e envie o ambiente para o servidor
                
                //-> Distância não bate<-
                //Crie um novo ambiente para o dispositivo e envie o ambiente para o servidor
                    
                
                
            }
        }catch(Exception e){e.printStackTrace();}
        
    }
    
    @Override
    public void encontrarEspaco() throws RemoteException{
        this.finder = new Lookup(JavaSpace.class);
        this.space = (JavaSpace) finder.getService();
        if (space == null) {
            System.out.println("Não foi possível encontrar o JavaSpace!");
        } else {
            System.out.println("JavaSpace encontrado: " + space);
        }
    }
    
    //<editor-fold defaultstate="collapsed" desc="OldProject">
    /*
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
    
    */
//</editor-fold>
}
