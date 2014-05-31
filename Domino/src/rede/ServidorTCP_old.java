package rede;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServidorTCP_old {
   
   private int porta;
   private List<PrintStream> clientes;
   
   public ServidorTCP_old (int porta) {
     this.porta = porta;
     this.clientes = new ArrayList<>();
   }
   
   public void executa () throws IOException {
     ServerSocket servidor = new ServerSocket(this.porta);
     
     while (true) {
       // aceita um cliente
       Socket cliente = servidor.accept();
       System.out.println("Nova conexão com o cliente " +   
         cliente.getInetAddress().getHostAddress()
       );
       
       // adiciona saida do cliente à lista
       PrintStream mensagem = new PrintStream(cliente.getOutputStream());
       this.clientes.add(mensagem);
       
       // cria tratador de cliente numa nova thread
       TrataCliente tc = 
           new TrataCliente(cliente.getInputStream(), this);
       new Thread(tc).start();
     }
 
   }
 
   public void distribuiMensagem(String msg) {
     // envia msg para todo mundo
     for (PrintStream cliente : this.clientes) {
       cliente.println(msg);
     }
   }
 }

