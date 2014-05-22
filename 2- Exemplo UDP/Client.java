// Fig. 24.11: Client.java
// Cliente que envia e recebe pacotes para/de um servidor.
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame 
{
   private JTextField enterField; // para inserir mensagens
   private JTextArea displayArea; // para exibir mensagens
   private DatagramSocket socket; // socket para conectar ao servidor

   // configura o DatagramSocket e a GUI
   public Client()
   {
      super( "Client" );

      enterField = new JTextField( "Type message here" );
      enterField.addActionListener(
         new ActionListener() 
         { 
            public void actionPerformed( ActionEvent event )
            {
               try // cria e envia o pacote
               {
                  // obtém a mensagem no campo de texto
                  String message = event.getActionCommand();
                  displayArea.append( "\nSending packet containing: " +
                     message + "\n" );

                  byte data[] = message.getBytes(); // converte em bytes
         
                  // cria sendPacket
                  DatagramPacket sendPacket = new DatagramPacket( data,
                     data.length, InetAddress.getLocalHost(), 5000 );  

                  socket.send( sendPacket ); // envia o pacote
                  displayArea.append( "Packet sent\n" );
                  displayArea.setCaretPosition( 
                     displayArea.getText().length() );
               } // fim do try
               catch ( IOException ioException ) 
               {
                  displayMessage( ioException.toString() + "\n" );
                  ioException.printStackTrace();
               } // fim do catch
            } // fim do método actionPerformed
         } // fim da classe inner
      ); // fim da chamada para addActionListener

      add( enterField, BorderLayout.NORTH );

      displayArea = new JTextArea();
      add( new JScrollPane( displayArea ), BorderLayout.CENTER );

      setSize( 400, 300 ); // configura o tamanho da janela
      setVisible( true ); // mostra a janela

      try // cria DatagramSocket para envio e recebimento de pacotes
      {
         socket = new DatagramSocket();
      } // fim do try
      catch ( SocketException socketException ) 
      {
         socketException.printStackTrace();
         System.exit( 1 );
      } // fim do catch
   } // fim do construtor Client

   // espera que os pacotes cheguem do Server, exibe o conteúdo do pacote
   public void waitForPackets()
   {
      while ( true ) 
      {
         try // recebe o pacote e exibe o conteúdo
         {
            byte data[] = new byte[ 100 ]; // configura o pacote
            DatagramPacket receivePacket = new DatagramPacket(
               data, data.length );                           

            socket.receive( receivePacket ); // espera o pacote

            // exibe o conteúdo do pacote
            displayMessage( "\nPacket received:" + 
               "\nFrom host: " + receivePacket.getAddress()+ 
               "\nHost port: " + receivePacket.getPort()+ 
               "\nLength: " + receivePacket.getLength()+ 
               "\nContaining:\n\t" + new String(receivePacket.getData(), 
                  0, receivePacket.getLength()) );
         } // fim do try
         catch ( IOException exception ) 
         {
            displayMessage( exception.toString() + "\n" );
            exception.printStackTrace();
         } // fim do catch
      } // fim do while
   } // fim do método waitForPackets

   // manipula a displayArea na thread de despacho de eventos
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable()
         {
            public void run() // atualiza a displayArea 
            {
               displayArea.append( messageToDisplay );
            } // fim do método run
         }  // fim da classe inner
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do método displayMessage
}  // fim da classe Client


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