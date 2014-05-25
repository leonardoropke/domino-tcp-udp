/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package domino;

import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author Raissa2
 */
public class TrataCliente implements Runnable {
 
   private InputStream cliente;
   private ServidorTCP servidor;
 
   public TrataCliente(InputStream cliente, ServidorTCP servidor) {
     this.cliente = cliente;
     this.servidor = servidor;
   }
 
   public void run() {
     // quando chegar uma msg, distribui pra todos
     Scanner s = new Scanner(this.cliente);
     while (s.hasNextLine()) {
       servidor.distribuiMensagem(s.nextLine());
     }
     s.close();
   }
 }