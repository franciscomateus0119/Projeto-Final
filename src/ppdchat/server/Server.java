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
    public Map<String, ClientInterface> clientsByName = new HashMap<>();
    public Map<ClientInterface, String> namesByClient = new HashMap<>();
    
    Lookup finder;
    JavaSpace space;
    
    public Server() throws RemoteException{
        super();
        clients = new ArrayList<>();
    }
    
    @Override
    public void registerClient(ClientInterface client, String nome, String x, String y) throws RemoteException{
        System.out.println("Novo Dispositivo!");
        clients.add(client);
        System.out.println("Nº de dispositivos: " + clients.size());
        System.out.println("Encontrando um ambiente para o dispositivo... ");
        procurarAmbiente(client, nome, x, y);        
    }
    
    @Override
    public void registerClientName(ClientInterface client, String nome){
        names.add(nome);
        clientsByName.put(nome, client);
        namesByClient.put(client, nome);
        System.out.println("Cliente " + client + " de nome "+nome+" adicionado ao HashMap clientsByName");
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
    public void receberArquivos(ArrayList<byte []> mydata, ArrayList<String> filenames, String dispositivoAlvo, String dispositivoOrigem) throws RemoteException{
        String ambienteDispOrigem = clientsByName.get(dispositivoOrigem).enviarAmbienteAtual();
        ClientInterface dispositivo = clientsByName.get(dispositivoAlvo);
        String ambienteDispAlvo = dispositivo.enviarAmbienteAtual();
        if(ambienteDispOrigem.equals(ambienteDispAlvo)){
            enviarArquivos(mydata, filenames,  dispositivo, dispositivoOrigem);
        }
        else{
            clientsByName.get(dispositivoOrigem).mostrarAlertaAmbienteIncompativel();
        }
    }
    

    
    @Override
    public void enviarArquivos(ArrayList<byte []> mydata, ArrayList<String> filenames, ClientInterface client, String nomeOrigem) throws RemoteException{
        client.receberArquivos(mydata, filenames, nomeOrigem);
        clientsByName.get(nomeOrigem).mostrarAlertaEnvioSucesso();
    }
    
    @Override
    public void procurarAmbiente(ClientInterface client, String nome,String x, String y) throws RemoteException{
        ListaDeAmbientes template = new ListaDeAmbientes();
        boolean distanciaIncompativel = false;
        boolean dispositivoAdicionado = false;
        if (template == null) {
            System.out.println("Template nulo!");
        }
        Dispositivo dispositivoT = new Dispositivo();
        dispositivoT.nomeDispositivo = nome;
        dispositivoT.xDispositivo = x;
        dispositivoT.yDispositivo = y;
        try{//procura a lista de ambientes
            Dispositivo dispositivo = (Dispositivo) space.take(dispositivoT, null, 3*1000);
            if(dispositivo == null){
                System.out.println("Dispositivo não encontrado no servidor!");
                dispositivo = new Dispositivo();
                dispositivo.nomeDispositivo = nome;
                dispositivo.xDispositivo = x;
                dispositivo.yDispositivo = y;
            }
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
                System.out.println("Adicionando " + novoAmbiente.nomeAmbiente + " na lista de ambientes...");
                arrayListaDeAmbientes.add("Ambiente_1");
                ListaDeAmbientes novaListaDeAmbientes = new ListaDeAmbientes();
                novaListaDeAmbientes.listaDeAmbientes = arrayListaDeAmbientes;
                
                String nomeAmbiente = novoAmbiente.nomeAmbiente;
                dispositivo.ambienteAtual = nomeAmbiente;
                
                //Envia o Ambiente e a Lista de Ambientes para o Servidor de Ambientes (Espaço de Tuplas)
                space.write(dispositivo, null, Lease.FOREVER);
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
                             
                             //Se a distância entre os dispositivo for menor que 10
                             //compara a distância entre os dispositivo do ambiente
                             ArrayList<String> listaDeDispositivos = new ArrayList<>();
                             listaDeDispositivos = ambiente.dispositivosNoAmbiente;
                             int tamanhoListaDispositivos = listaDeDispositivos.size();
                             //Para cada dispositivo do ambiente
                             distanciaIncompativel = false;
                             for(int j = 0; j<tamanhoListaDispositivos;j++){
                                 //Verifica se a distância é compativel
                                 String dispositivoX = clientsByName.get(listaDeDispositivos.get(j)).getClientX();
                                 String dispositivoY = clientsByName.get(listaDeDispositivos.get(j)).getClientY();
                                 Float ambienteDispositivoX = Float.parseFloat(dispositivoX);
                                 Float ambienteDispositivoY = Float.parseFloat(dispositivoY);
                                 //Se a distância entre os dispositivo for maior que 10 (Distância Incompatível)
                                 if (verificarLocalizacao(Float.parseFloat(x), Float.parseFloat(y), ambienteDispositivoX, ambienteDispositivoY) > 10) {
                                     System.out.println("Distância entre " +nome+ " e " + listaDeDispositivos.get(j) +" é incompatível: "
                                             + verificarLocalizacao(Float.parseFloat(x), Float.parseFloat(y), ambienteDispositivoX, ambienteDispositivoY));
                                     j = tamanhoListaDispositivos;
                                     distanciaIncompativel = true;
                                 }
                                 else{
                                     System.out.println("Distância entre " +nome+ " e " +  listaDeDispositivos.get(j)+" é de: "
                                             + verificarLocalizacao(Float.parseFloat(x), Float.parseFloat(y), ambienteDispositivoX, ambienteDispositivoY));
                                 }
                             }
                             //Se a distância for compatível
                             if(!distanciaIncompativel){
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
                                 System.out.println("Dispositivo " + nome + " Adicionado em " + ambiente.nomeAmbiente+ " !");
                                 //Informa o dispositivo em qual ambiente ele está inserido
                                 enviarAmbiente(client, nomeDoAmbiente);
                             }
                             
                             //Se a distância for maior que 10 metros, não adicione o dispositivo e devolva o ambiente
                             
                             else{
                                System.out.println("Distância é maior que 10 metros entre os Dispositivos do ambiente " + ambiente.nomeAmbiente);
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
                    
                    String nomeAmbiente = newAmbiente.nomeAmbiente;
                    dispositivo.ambienteAtual = nomeAmbiente;
                    
                    //Insira o novo Ambiente e devolva a Lista de Ambientes para o Servidor de Ambientes
                    space.write(dispositivo, null, Lease.FOREVER);
                    space.write(newAmbiente, null, Lease.FOREVER);
                    space.write(listadeambientes, null, Lease.FOREVER);
                    
                    //Informa o dispositivo em qual ambiente ele está inserido
                    enviarAmbiente(client, nomedoAmbiente);
                    
                    
                }       
            }
        }catch(Exception e){e.printStackTrace();}
        
    }
    
    @Override
    public void receberPedidoNovaLocalizacao(ClientInterface client, String x, String y, String nome, String ambienteDoDispositivo) throws RemoteException {
        //Recebe pedido do cliente para mudar localização
        //Procura pela lista de ambientes
        ListaDeAmbientes template = new ListaDeAmbientes();
        boolean dispositivoAdicionado = false;
        boolean distanciaIncompativel = false;
        if (template == null) {
            System.out.println("Template nulo!");
        }
        Dispositivo dispositivoT = new Dispositivo();
        dispositivoT.nomeDispositivo = nome;
        dispositivoT.xDispositivo = x;
        dispositivoT.yDispositivo = y;
        try {
            Dispositivo dispositivo = (Dispositivo) space.take(dispositivoT, null, 3*1000);
            if(dispositivo == null){
                System.out.println("Dispositivo não encontrado no servidor!");
                dispositivo = new Dispositivo();
                dispositivo.nomeDispositivo = nome;
                dispositivo.xDispositivo = x;
                dispositivo.yDispositivo = y;
            }
            ListaDeAmbientes listadeambientes = (ListaDeAmbientes) space.take(template, null, 10 * 1000);
            //Se a lista de ambientes for encontrada
            if (listadeambientes != null) {
                System.out.println("Lista de Ambientes FOI encontrada!");
                int tamanho = listadeambientes.listaDeAmbientes.size();
                //Procura o ambiente Verifica se a nova distância condiz com o Ambiente Atual
                Ambiente templateAmbienteAtual = new Ambiente();
                templateAmbienteAtual.nomeAmbiente = ambienteDoDispositivo;
                Ambiente ambienteAtual = (Ambiente) space.take(templateAmbienteAtual, null, 5 * 1000);
                //Se o ambiente atual do dispositivo for encontrado!
                if (ambienteAtual != null) {
                    float xDispositivo = Float.parseFloat(x);
                    float yDispositivo = Float.parseFloat(y);
                    ArrayList<String> listaDeDispositivos = new ArrayList<>();
                    listaDeDispositivos = ambienteAtual.dispositivosNoAmbiente;
                    int tamanhoListaDispositivos = listaDeDispositivos.size();
                    //Para cada dispositivo do ambiente
                    distanciaIncompativel = false;
                    for (int j = 0; j < tamanhoListaDispositivos; j++) {
                        //Verifica se a distância é compativel
                        String dispositivoX = clientsByName.get(listaDeDispositivos.get(j)).getClientX();
                        String dispositivoY = clientsByName.get(listaDeDispositivos.get(j)).getClientY();
                        Float ambienteDispositivoX = Float.parseFloat(dispositivoX);
                        Float ambienteDispositivoY = Float.parseFloat(dispositivoY);
                        //Se a distância entre os dispositivo for maior que 10 (Distância Incompatível)
                        if (verificarLocalizacao(xDispositivo, yDispositivo, ambienteDispositivoX, ambienteDispositivoY) > 10) {
                            System.out.println("Distância entre " + nome + " e " + listaDeDispositivos.get(j) + " é incompatível: "
                                    + verificarLocalizacao(Float.parseFloat(x), Float.parseFloat(y), ambienteDispositivoX, ambienteDispositivoY));
                            j = tamanhoListaDispositivos;
                            distanciaIncompativel = true;
                        } else {
                            System.out.println("Distância entre " + nome + " e " + listaDeDispositivos.get(j) + " é de: "
                                    + verificarLocalizacao(Float.parseFloat(x), Float.parseFloat(y), ambienteDispositivoX, ambienteDispositivoY));
                        }
                    }
                    //Se a distância for compatível
                    if (!distanciaIncompativel) {
                        //Atualiza a localização do Dispositivo
                        client.atualizarLocalizacao(x, y);
                        String newX = x;
                        String newY = y;
                        dispositivo.xDispositivo = newX;
                        dispositivo.yDispositivo = newY;
                        //Devolve o Ambiente e a Lista de Ambientes para o Servidor de Ambientes
                        String nomeAmbiente = ambienteAtual.nomeAmbiente;
                        dispositivo.ambienteAtual = nomeAmbiente;
                        space.write(dispositivo, null, Lease.FOREVER);
                        space.write(ambienteAtual, null, Lease.FOREVER);
                        space.write(listadeambientes, null, Lease.FOREVER);
                    } //Se a distância for maior que 10 metros, não adicione o dispositivo e devolva o ambiente
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
                                            
                                            String newX = x;
                                            String newY = y;
                                            String nomeAmbiente = ambienteAtual.nomeAmbiente;
                                            dispositivo.ambienteAtual = nomeAmbiente;
                                            dispositivo.xDispositivo = newX;
                                            dispositivo.yDispositivo = newY;
                                            
                                            //Devolve o AmbienteAtual, NovoAmbiente e Lista de Ambientes para o Servidor de Ambientes
                                            space.write(dispositivo, null, Lease.FOREVER);
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
                            listaDeDispositivos = new ArrayList<>();
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
                            
                            String newX = x;
                            String newY = y;
                            dispositivo.ambienteAtual = nomedoAmbiente;
                            dispositivo.xDispositivo = newX;
                            dispositivo.yDispositivo = newY;

                            //Insira o Novo Ambiente e devolva a Lista de Ambientes e o Ambiente Atual para o Servidor de Ambientes
                            space.write(dispositivo, null, Lease.FOREVER);
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
    
    @Override
    public ArrayList<String> getNames() throws RemoteException{
        return names;
    }
    
}
