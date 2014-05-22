// Fig. 24.8: ClientTest.java
// Testa a classe Client.
import javax.swing.JFrame;

public class ClientTest 
{
   public static void main( String args[] )
   {
      Client application; // declara o aplicativo cliente

      // se n�o houver nenhum argumento de linha de comando
      if ( args.length == 0 )
         application = new Client( "127.0.0.1" ); // conecta-se ao host local
      else
         application = new Client( args[ 0 ] ); // utiliza argumentos para se conectar   

      application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      application.runClient(); // executa o aplicativo cliente
   } // fim de main
} // fim da classe ClientTest


/**************************************************************************
 * (C) Copyright 1992-2005 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/