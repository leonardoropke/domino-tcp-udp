// Fig. 24.5: Server.java
// Configura uma classe Server que receber� uma conex�o de um cliente, envia
// uma string ao cliente e fecha a conex�o.
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;      
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame 
{
   private JTextField enterField; // insere a mensagem do usu�rio
   private JTextArea displayArea; // exibe informa��es para o usu�rio
   private ObjectOutputStream output; // gera fluxo de sa�da para o cliente
   private ObjectInputStream input; // gera fluxo de entrada a partir do cliente
   private ServerSocket server; // socket de servidor     
   private Socket connection; // conex�o com o cliente
   private int counter = 1; // contador do n�mero de conex�es

   // configura a GUI
   public Server()
   {
      super( "Server" );

      enterField = new JTextField(); // cria enterField
      enterField.setEditable( false );
      enterField.addActionListener(
         new ActionListener() 
         {
            // envia a mensagem ao cliente
            public void actionPerformed( ActionEvent event )
            {
               sendData( event.getActionCommand() );
               enterField.setText( "" );
            } // fim do m�todo actionPerformed
         } // fim da classe interna an�nima
      ); // fim da chamada para addActionListener

      add( enterField, BorderLayout.NORTH );

      displayArea = new JTextArea(); // cria displayArea
      add( new JScrollPane( displayArea ), BorderLayout.CENTER );

      setSize( 300, 150 ); // configura o tamanho da janela
      setVisible( true ); // mostra a janela
   } // fim do construtor Server

   // configura e executa o servidor
   public void runServer()
   {
      try // configura o servidor para receber conex�es; processa as conex�es
      {
         server = new ServerSocket( 12345, 100 ); // cria ServerSocket

         while ( true ) 
         {
            try 
            {
               waitForConnection(); // espera uma conex�o
               getStreams(); // obt�m fluxos de entrada e sa�da
               processConnection(); // processa a conex�o
            } // fim do try
            catch ( EOFException eofException ) 
            {
               displayMessage( "\nServer terminated connection" );
            } // fim do catch
            finally 
            {
               closeConnection(); // fecha a conex�o
               counter++;
            } // fim de finally
         } // fim do while
      } // fim do try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // fim do catch
   } // fim do m�todo runServer

   // espera que a conex�o chegue e ent�o exibe informa��es sobre a conex�o
   private void waitForConnection() throws IOException
   {
      displayMessage( "Waiting for connection\n" );
      connection = server.accept(); // permite que servidor aceite a conex�o
      displayMessage( "Connection " + counter + " received from: " +
         connection.getInetAddress().getHostName());
   } // fim do m�todo waitForConnection

   // obt�m fluxos para enviar e receber dados
   private void getStreams() throws IOException
   {
      // configura o fluxo de sa�da para objetos
      output = new ObjectOutputStream( connection.getOutputStream() );
      output.flush(); // esvazia buffer de sa�da enviar as informa��es de cabe�alho

      // configura o fluxo de entrada para objetos
      input = new ObjectInputStream( connection.getInputStream() );

      displayMessage( "\nGot I/O streams\n" );
   } // fim do m�todo getStreams

   // processa a conex�o com o cliente
   private void processConnection() throws IOException
   {
      String message = "Connection successful";
      sendData( message ); // envia uma mensagem de conex�o bem-sucedida

      // ativa enterField de modo que usu�rio do servidor possa enviar mensagens
      setTextFieldEditable( true );

      do // processa as mensagens enviadas pelo cliente
      { 
         try // l� e exibe a mensagem
         {
            message = ( String ) input.readObject(); // l� uma nova mensagem
            displayMessage( "\n" + message ); // exibe a mensagem
         } // fim do try
         catch ( ClassNotFoundException classNotFoundException ) 
         {
            displayMessage( "\nUnknown object type received" );
         } // fim do catch

      } while ( !message.equals( "CLIENT>>> TERMINATE" ) );
   } // fim do m�todo processConnection

   // fecha os fluxos e o socket
   private void closeConnection() 
   {
      displayMessage( "\nTerminating connection\n" );
      setTextFieldEditable( false ); // desativa enterField

      try 
      {
         output.close(); // fecha o fluxo de sa�da
         input.close(); // fecha o fluxo de entrada  
         connection.close(); // fecha o socket   
      } // fim do try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // fim do catch
   } // fim do m�todo closeConnection

   // envia a mensagem ao cliente
   private void sendData( String message )
   {
      try // envia o objeto ao cliente
      {
         output.writeObject( "SERVER>>> " + message );
         output.flush(); // esvazia a sa�da para o cliente    
         displayMessage( "\nSERVER>>> " + message );
      } // fim do try
      catch ( IOException ioException ) 
      {
         displayArea.append( "\nError writing object" );
      } // fim do catch
   } // fim do m�todo sendData

   // manipula a displayArea na thread de despacho de eventos
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() // atualiza a displayArea 
            {
               displayArea.append( messageToDisplay ); // acrescenta a mensagem
            } // fim do m�todo run
         } // fim da classe interna an�nima
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do m�todo displayMessage

   // manipula o enterField na thread de despacho de eventos
   private void setTextFieldEditable( final boolean editable )
   {
      SwingUtilities.invokeLater(
         new Runnable()
         {
            public void run() // configura a editabilidade do enterField
            {
               enterField.setEditable( editable );
            } // fim do m�todo run
         }  // fim da classe inner
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do m�todo setTextFieldEditable
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