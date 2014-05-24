// Fig. 24.9: Server.java
// Servidor que recebe e envia pacotes de/para um cliente.
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Server extends JFrame 
{
   private JTextArea displayArea; // exibe os pacotes recebidos
   private DatagramSocket socket; // socket para conectar ao cliente

   // configura o DatagramSocket e a GUI
   public Server()
   {
      super( "Server" );

      displayArea = new JTextArea(); // cria displayArea
      add( new JScrollPane( displayArea ), BorderLayout.CENTER );
      setSize( 400, 300 ); // configura o tamanho da janela
      setVisible( true ); // mostra a janela

      try // cria DatagramSocket para envio e recebimento de pacotes
      {
         socket = new DatagramSocket( 5000 );
      } // fim do try
      catch ( SocketException socketException ) 
      {
         socketException.printStackTrace();
         System.exit( 1 );
      } // fim do catch
   } // fim do construtor Server

   // espera que os pacotes cheguem, exibe os dados e ecoa o pacote para o cliente
   public void waitForPackets()
   {
      while ( true ) 
      {
         try // recebe o pacote, exibe o conteúdo, retorna uma cópia ao cliente
         {
            byte data[] = new byte[ 100 ]; // configura o pacote
            DatagramPacket receivePacket =                 
               new DatagramPacket( data, data.length );    

            socket.receive( receivePacket ); // espera receber o pacote

            // exibe informações a partir do pacote recebido
            displayMessage( "\nPacket received:" + 
               "\nFrom host: " + receivePacket.getAddress()+ 
               "\nHost port: " + receivePacket.getPort()+ 
               "\nLength: " + receivePacket.getLength()+ 
               "\nContaining:\n\t" + new String(receivePacket.getData(), 
                  0, receivePacket.getLength()) );

            sendPacketToClient( receivePacket ); // envia o pacote ao cliente
         } // fim do try
         catch ( IOException ioException )
         {
            displayMessage( ioException.toString() + "\n" );
            ioException.printStackTrace();
         } // fim do catch
      } // fim do while
   } // fim do método waitForPackets

   // ecoa o pacote para o cliente
   private void sendPacketToClient( DatagramPacket receivePacket ) 
      throws IOException
   {
      displayMessage( "\n\nEcho data to client..." );

      // cria o pacote a enviar
      DatagramPacket sendPacket = new DatagramPacket(          
         receivePacket.getData(), receivePacket.getLength(),   
         receivePacket.getAddress(), receivePacket.getPort() );

      socket.send( sendPacket ); // envia o pacote ao cliente
      displayMessage( "Packet sent\n" );
   } // fim do método sendPacketToClient

   // manipula a displayArea na thread de despacho de eventos
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() // atualiza a displayArea 
            {
               displayArea.append( messageToDisplay ); // exibe a mensagem
            } // fim do método run
         } // fim da classe interna anônima
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do método displayMessage
} // fim da classe Server


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
