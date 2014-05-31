/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttest;

/**
 *
 * @author Carlos
 */
import javax.swing.JFrame;

public class ClientTest 
{
   public static void main( String args[] )
   {
      Client application; // declara o aplicativo cliente

      // se não houver nenhum argumento de linha de comando
      if ( args.length == 0 )
         application = new Client( "127.0.0.1" ); // conecta-se ao host local
      else
         application = new Client( args[ 0 ] ); // utiliza argumentos para se conectar   

      application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      application.runClient(); // executa o aplicativo cliente
   } // fim de main
} // fim da classe ClientTest
