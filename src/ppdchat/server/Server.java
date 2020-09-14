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
    protected ArrayList<String> names = new ArrayList<>();
    //public Map<String, ClientInterface> clientsByName = new HashMap<>();
    public Map<ClientInterface, String> namesByClient = new HashMap<>();
    public ArrayList<String> todosOsNomes = new ArrayList<>();
    //public Map<Integer, String> names = new HashMap<>();
    
    Lookup finder;
    JavaSpace space;
    
    String storagepath = "C:/ServerStorage/";
    
    public Server() throws RemoteException{
        super();
        clients = new ArrayList<>();
    }
    
    @Override
    public void registerClient(ClientInterface client, String nome, String endereco, String x, String y) throws RemoteException{
        //System.out.println(clients.size());
        System.out.println("Novo Dispositivo!");
        clients.add(client);
        System.out.println("Nº de dispositivos: " + clients.size());
        System.out.println("Encontrando um ambiente para o dispositivo... ");
        procurarAmbiente(client, nome, endereco, x, y);
        //iniciarJogo();
        
    }
    
    @Override
    public void registerClientName(ClientInterface client, String nome){
        names.add(nome);
        namesByClient.put(client, nome);
        todosOsNomes.add(nome);
        System.out.println("Cliente " + client + " de nome "+nome+" adicionado ao HashMap clientsByName");
        System.out.println("Names size: " + names.size());
        System.out.println("TodosOsNomes size: " + todosOsNomes.size());
    }
    
    @Override
    public void receberAtualizarListaDispositivos(ClientInterface client, String nome, String nomeAmbiente){
        System.out.println("Pedido de atualização de lista de dispostivos recebido por " + nome);
        //Procurar ambiente no Servidor de ambientes
        Ambiente templateAmbiente = new Ambiente();
        templateAmbiente.nomeAmbiente = nomeAmbiente;
        try {
            Ambiente ambiente = (Ambiente) space.read(templateAmbiente, null, 5 * 1000);
            //Se o ambiente for encontrado!
            if(ambiente!=null){
                ArrayList<String> listaDeDispositivos = new ArrayList<>();
                listaDeDispositivos = ambiente.dispositivosNoAmbiente;
                client.receberListaAtualizada(listaDeDispositivos);
            }
            
        } catch (Exception e) {e.printStackTrace();}

   
    }
    
    
    @Override
    public void receberArquivos(ArrayList<byte []> mydata, ArrayList<String> filenames, String dispositivoAlvo, String ambiente) throws RemoteException{
        int tamanhoNames = names.size();
        //Para cada nome na lista de nomes
        for (int i = 0; i< tamanhoNames; i++) {
            for (Map.Entry<ClientInterface, String> entry : namesByClient.entrySet()) {
                ClientInterface dispositivo = entry.getKey();
                String name = entry.getValue();  
                //Se o nome desta iteração for igual ao nome do dispositivo alvo
                if(names.get(i).equals(name) && names.get(i).equals(dispositivoAlvo)){
                    //Verifica se o ambiente deste dispositivo é o mesmo de quem está enviando o arquivo
                    String ambienteGet = dispositivo.enviarAmbienteAtual();
                    //Se forem os mesmos ambientes
                    if(ambienteGet.equals(ambiente)){
                        //Enviar arquivo
                        enviarArquivos(mydata, filenames,  dispositivo);
                    }
                }
            }
        }
    }
    

    
    @Override
    public void enviarArquivos(ArrayList<byte []> mydata, ArrayList<String> filenames, ClientInterface client) throws RemoteException{
        client.receberArquivos(mydata, filenames);
    }
    
    //Funções do Espaço de Tuplas
    
    @Override
    public void procurarAmbiente(ClientInterface client, String nome, String endereco, String x, String y) throws RemoteException{
        ListaDeAmbientes template = new ListaDeAmbientes();
        boolean dispositivoAdicionado = false;
        if (template == null) {
            System.out.println("Template nulo!");
        }
        try{//procura a lista de ambientes
            ListaDeAmbientes listadeambientes = (ListaDeAmbientes) space.take(template, null, 10 * 1000);
            //Se não existir lista de ambientes, crie um novo ambiente e uma nova lista de ambientes
            if (listadeambientes == null) {
                System.out.println("Lista de Ambientes NÃO FOI encontrada! Criando Ambiente...");
                ArrayList<String> listaDeDispositivos = new ArrayList<>();
                ArrayList<String> arrayListaDeAmbientes = new ArrayList<>();
                
                //Cria um novo Ambiente, com sua localização sendo a do dispositivo (pois ele é o primeiro a entrar)
                Ambiente novoAmbiente = new Ambiente();
                novoAmbiente.nomeAmbiente = "Ambiente_1";
                novoAmbiente.xAmbiente = x;
                novoAmbiente.yAmbiente = y;
                listaDeDispositivos.add(nome);
                novoAmbiente.dispositivosNoAmbiente = listaDeDispositivos;
                
                //Coloca o novo Ambiente em uma nova lista de Ambientes
                System.out.println("Adicionando " + novoAmbiente.nomeAmbiente + "na lista de ambientes...");
                arrayListaDeAmbientes.add("Ambiente_1");
                ListaDeAmbientes novaListaDeAmbientes = new ListaDeAmbientes();
                novaListaDeAmbientes.listaDeAmbientes = arrayListaDeAmbientes;
                
                //Envia o Ambiente e a Lista de Ambientes para o Servidor de Ambientes (Espaço de Tuplas)
                space.write(novoAmbiente, null, Lease.FOREVER);
                space.write(novaListaDeAmbientes, null, Lease.FOREVER);
                
                System.out.println("Ambiente e Lista de Ambientes inseridos no Servidor de Ambientes!");
                //Informa o dispositivo em qual ambiente ele está inserido
                enviarAmbiente(client, "Ambiente_1");
            }
            
            //Se já existir uma lista de ambientes
            else{
                System.out.println("Lista de Ambientes FOI encontrada!");
                int tamanho = listadeambientes.listaDeAmbientes.size();
                //para cada ambiente na lista de ambientes
                 for(int i=0;i<tamanho;i++){
                     Ambiente templateAmbiente = new Ambiente();
                     templateAmbiente.nomeAmbiente = listadeambientes.listaDeAmbientes.get(i);
                     Ambiente ambiente = (Ambiente) space.take(templateAmbiente, null, 5 * 1000);
                     
                     //Se o ambiente encontrado não for nulo
                     if(ambiente!=null){
                         //Verifica se na lista de dispositivos do ambiente encontrado existe um dispositivo com o nome do novo dispositivo
                         //Se não existe um dispositivo com o nome do novo dispositivo no ambiente (o Nome é único)
                         if(!ambiente.dispositivosNoAmbiente.contains(nome)){
                             System.out.println("O nome do dispositivo é único no ambiente " + ambiente.nomeAmbiente);
                             //compara a distância do dispositivo com a distância do ambiente
                             //Se a distância entre o ambiente e o dispositivo for menor que 10
                             Float ambienteX = Float.parseFloat(ambiente.xAmbiente);
                             Float ambienteY = Float.parseFloat(ambiente.yAmbiente);
                             if(verificarLocalizacao(Float.parseFloat(x),Float.parseFloat(y),ambienteX,ambienteY)<10){
                                 ArrayList<String> listaDeDispositivos = new ArrayList<>();
                                 listaDeDispositivos = ambiente.dispositivosNoAmbiente;
                                 String nomeDoAmbiente = ambiente.nomeAmbiente;
                                 System.out.println("A distância é menor que 10 metros!");
                                 //Adiciona o Dispositivo ao ambiente
                                 listaDeDispositivos.add(nome);
                                 ambiente.dispositivosNoAmbiente = listaDeDispositivos;
                                 dispositivoAdicionado = true;
                                 //Devolve o Ambiente e a Lista de Ambientes ao Servidor de Ambientes (Espaço de Tuplas)
                                 space.write(ambiente, null, Lease.FOREVER);
                                 space.write(listadeambientes, null, Lease.FOREVER);
                                 i = tamanho;
                                 System.out.println("Dispositivo" + nome + " Adicionado em" + ambiente.nomeAmbiente+ " !");
                                 //Informa o dispositivo em qual ambiente ele está inserido
                                 enviarAmbiente(client, nomeDoAmbiente);
                                 
                             }
                             //Se a distância for maior que 10 metros, não adicione o dispositivo e devolva o ambiente
                             else{
                                System.out.println("Distância é maior que 10 metros no ambiente " + ambiente.nomeAmbiente);
                                space.write(ambiente, null, Lease.FOREVER);
                             }
                         }
                         //Se o nome não for único no ambiente, não adicione o dispositivo e devolva o ambiente
                         else{
                             System.out.println("O nome não é único no ambiente " + ambiente.nomeAmbiente);
                             space.write(ambiente, null, Lease.FOREVER);
                         }
                     }
                }
                //Se o dispositivo não foi adicionado a nenhum ambiente, devido a Distância/Nome incompatíveis
                if(!dispositivoAdicionado){
                    System.out.println("Nenhum ambiente favorável foi encontrado! Criando novo ambiente...");
                    ArrayList<String> listaDeDispositivos = new ArrayList<>();
                    ArrayList<String> arrayListaDeAmbientes = new ArrayList<>();
                    arrayListaDeAmbientes = listadeambientes.listaDeAmbientes;
                    
                    //Crie um ambiente para este dispositivo
                    Ambiente newAmbiente = new Ambiente();
                    newAmbiente.nomeAmbiente = "Ambiente_" + (tamanho+1);
                    newAmbiente.xAmbiente = x;
                    newAmbiente.yAmbiente = y;
                    listaDeDispositivos.add(nome);
                    newAmbiente.dispositivosNoAmbiente = listaDeDispositivos;
                    String nomedoAmbiente = newAmbiente.nomeAmbiente;
                    System.out.println("Dispositivo " + nome + " foi alocado no ambiente " + newAmbiente.nomeAmbiente +" !");
                    //Adicione o ambiente na lista de ambientes
                    
                    arrayListaDeAmbientes.add(newAmbiente.nomeAmbiente);
                    listadeambientes.listaDeAmbientes = arrayListaDeAmbientes;
                    
                    //Insira o novo Ambiente e devolva a Lista de Ambientes para o Servidor de Ambientes
                    space.write(newAmbiente, null, Lease.FOREVER);
                    space.write(listadeambientes, null, Lease.FOREVER);
                    
                    //Informa o dispositivo em qual ambiente ele está inserido
                    enviarAmbiente(client, nomedoAmbiente);
                    
                    
                } 
                 
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
    public void receberPedidoNovaLocalizacao(ClientInterface client, String x, String y,String nome, String ambienteDoDispositivo) throws RemoteException{
        //Recebe pedido do cliente para mudar localização
        //Procura pela lista de ambientes
        ListaDeAmbientes template = new ListaDeAmbientes();
        boolean dispositivoAdicionado = false;
        if (template == null) {
            System.out.println("Template nulo!");
        }
        try{
            ListaDeAmbientes listadeambientes = (ListaDeAmbientes) space.take(template, null, 10 * 1000);
            //Se a lista de ambientes for encontrada
            if(listadeambientes!=null){
                System.out.println("Lista de Ambientes FOI encontrada!");
                int tamanho = listadeambientes.listaDeAmbientes.size();
                //Procura o ambiente Verifica se a nova distância condiz com o Ambiente Atual
                Ambiente templateAmbienteAtual = new Ambiente();
                templateAmbienteAtual.nomeAmbiente = ambienteDoDispositivo;
                Ambiente ambienteAtual = (Ambiente) space.take(templateAmbienteAtual, null, 5 * 1000);
                //Se o ambiente atual do dispositivo for encontrado!
                if(ambienteAtual!=null){
                    //Verifica se a nova distância condiz com o Ambiente Atual
                    float xDispositivo = Float.parseFloat(x);
                    float yDispositivo = Float.parseFloat(y);
                    float xAmbiente = Float.parseFloat(ambienteAtual.xAmbiente);
                    float yAmbiente = Float.parseFloat(ambienteAtual.yAmbiente);
                    //Se a distância com a nova localização ainda for MENOR que 10 metros
                    if (verificarLocalizacao(xDispositivo, yDispositivo, xAmbiente, yAmbiente) < 10) {
                        //Atualiza a localização do Dispositivo
                        client.atualizarLocalizacao(x, y);
                        //Devolve o Ambiente e a Lista de Ambientes para o Servidor de Ambientes
                        space.write(ambienteAtual, null, Lease.FOREVER);
                        space.write(listadeambientes, null, Lease.FOREVER);
                        
                    } //Se a distância com a nova localização for MAIOR que 10 metros
                    else {
                        
                        //para cada ambiente na lista de ambientes
                        for (int i = 0; i < tamanho; i++) {
                            //Se não for o ambiente atual do Dispositivo
                            if (!listadeambientes.listaDeAmbientes.get(i).equals(ambienteDoDispositivo)) {
                                Ambiente templateAmbiente = new Ambiente();
                                templateAmbiente.nomeAmbiente = listadeambientes.listaDeAmbientes.get(i);
                                Ambiente ambiente = (Ambiente) space.take(templateAmbiente, null, 5 * 1000);
                                //Se o ambiente encontrado não for nulo
                                if (ambiente != null) {
                                    //Verifica se o nome é único no ambiente
                                    //Se o nome for único
                                    if(!ambiente.dispositivosNoAmbiente.contains(nome)){
                                        System.out.println("(Localizacao) O nome do dispositivo é único no ambiente " + ambiente.nomeAmbiente);
                                        
                                        //Verifica se a distância é condizente com o Ambiente
                                        Float ambienteX = Float.parseFloat(ambiente.xAmbiente);
                                        Float ambienteY = Float.parseFloat(ambiente.yAmbiente);
                                        //Se a distância com a nova localização ainda for MENOR que 10 metros
                                        if (verificarLocalizacao(xDispositivo, yDispositivo, ambienteX, ambienteY) < 10) {
                                            
                                            ArrayList<String> novaListaDispositivosAmbienteAtual = new ArrayList<>();
                                            ArrayList<String> novaListaDispositivosNovoAmbiente = new ArrayList<>();
                                            String nomeDoAmbiente = ambiente.nomeAmbiente;
                                            
                                            //Remove o Dispositivo de seu Ambiente Atual
                                            novaListaDispositivosAmbienteAtual = ambienteAtual.dispositivosNoAmbiente;
                                            novaListaDispositivosAmbienteAtual.remove(nome);
                                            ambienteAtual.dispositivosNoAmbiente = novaListaDispositivosAmbienteAtual;
                                            
                                            //Adiciona o Dispositivo de seu novo Ambiente
                                            novaListaDispositivosNovoAmbiente = ambiente.dispositivosNoAmbiente;
                                            novaListaDispositivosNovoAmbiente.add(nome);
                                            ambiente.dispositivosNoAmbiente = novaListaDispositivosNovoAmbiente;
                                            
                                            //Devolve o AmbienteAtual, NovoAmbiente e Lista de Ambientes para o Servidor de Ambientes
                                            space.write(ambienteAtual, null, Lease.FOREVER);
                                            space.write(ambiente, null, Lease.FOREVER);
                                            space.write(listadeambientes, null, Lease.FOREVER);
                                            //Informe a nova localização para o Dispositivo
                                            client.atualizarLocalizacao(x, y);
                                            
                                            System.out.println("(Localização) Dispositivo" + nome + " Adicionado em" + ambiente.nomeAmbiente + " !");
                                            //Informa o dispositivo em qual ambiente ele está inserido
                                            enviarAmbiente(client, nomeDoAmbiente);
                                            
                                            dispositivoAdicionado = true;
                                            i = tamanho;
                                            
                                        }
                                        //Se a distância for maior que 10 metros, não adicione o dispositivo e devolva o ambiente
                                        else{
                                            System.out.println("(Localizacao) Distância é maior que 10 metros no ambiente " + ambiente.nomeAmbiente);
                                            space.write(ambiente, null, Lease.FOREVER);
                                        }
                                        
                                    }
                                    //Se o nome não for único no ambiente, não adicione o dispositivo e devolva o ambiente
                                    else {
                                        System.out.println("O nome não é único no ambiente " + ambiente.nomeAmbiente);
                                        space.write(ambiente, null, Lease.FOREVER);
                                    }
                                   

                                }
                            }

                        }
                        //Se O dispositivo não encontrou um ambiente condizente com sua nova localização ou com seu nome
                        if (!dispositivoAdicionado) {
                            System.out.println("(Localização) Nenhum ambiente favorável foi encontrado! Criando novo ambiente...");
                            ArrayList<String> listaDeDispositivos = new ArrayList<>();
                            ArrayList<String> arrayListaDeAmbientes = new ArrayList<>();
                            ArrayList<String> novaListaDispositivosAmbienteAtual = new ArrayList<>();
                            arrayListaDeAmbientes = listadeambientes.listaDeAmbientes;
                            
                            //Remove o Dispositivo de seu Ambiente Atual
                            novaListaDispositivosAmbienteAtual = ambienteAtual.dispositivosNoAmbiente;
                            novaListaDispositivosAmbienteAtual.remove(nome);
                            ambienteAtual.dispositivosNoAmbiente = novaListaDispositivosAmbienteAtual;

                            //Crie um ambiente para este dispositivo
                            Ambiente newAmbiente = new Ambiente();
                            newAmbiente.nomeAmbiente = "Ambiente_" + (tamanho + 1);
                            newAmbiente.xAmbiente = x;
                            newAmbiente.yAmbiente = y;
                            listaDeDispositivos.add(nome);
                            newAmbiente.dispositivosNoAmbiente = listaDeDispositivos;
                            
                            String nomedoAmbiente = newAmbiente.nomeAmbiente;
                            System.out.println("(Localização) Dispositivo " + nome + " foi alocado no ambiente " + newAmbiente.nomeAmbiente + " !");
                            //Adicione o ambiente na lista de ambientes

                            arrayListaDeAmbientes.add(newAmbiente.nomeAmbiente);
                            listadeambientes.listaDeAmbientes = arrayListaDeAmbientes;

                            //Insira o Novo Ambiente e devolva a Lista de Ambientes e o Ambiente Atual para o Servidor de Ambientes
                            space.write(ambienteAtual, null, Lease.FOREVER);
                            space.write(newAmbiente, null, Lease.FOREVER);
                            space.write(listadeambientes, null, Lease.FOREVER);
                            
                            //Informe a nova localização para o Dispositivo
                            client.atualizarLocalizacao(x, y);
                            
                            //Informa o dispositivo em qual ambiente ele está inserido
                            enviarAmbiente(client, nomedoAmbiente);
                        }
                    }
                }

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
    
    @Override
    public void enviarAmbiente(ClientInterface client, String nomeAmbiente) throws RemoteException{
        System.out.println("Informando ao Dispositivo o Ambiente em que fora inserido!");
        client.receberAmbiente(nomeAmbiente);
    }
    
    public float verificarLocalizacao(float x1,float y1, float x2,float y2) {
        float x = Math.abs(x2 - x1);
        float y = Math.abs(y2 - y1);
        return (float) Math.hypot(x, y);
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
