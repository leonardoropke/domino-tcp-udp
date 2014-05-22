// Fig. 24.5: Server.java
// Configura uma classe Server que receberá uma conexão de um cliente, envia
// uma string ao cliente e fecha a conexão.
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
   private JTextField enterField; // insere a mensagem do usuário
   private JTextArea displayArea; // exibe informações para o usuário
   private ObjectOutputStream output; // gera fluxo de saída para o cliente
   private ObjectInputStream input; // gera fluxo de entrada a partir do cliente
   private ServerSocket server; // socket de servidor     
   private Socket connection; // conexão com o cliente
   private int counter = 1; // contador do número de conexões

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
            } // fim do método actionPerformed
         } // fim da classe interna anônima
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
      try // configura o servidor para receber conexões; processa as conexões
      {
         server = new ServerSocket( 12345, 100 ); // cria ServerSocket

         while ( true ) 
         {
            try 
            {
               waitForConnection(); // espera uma conexão
               getStreams(); // obtém fluxos de entrada e saída
               processConnection(); // processa a conexão
            } // fim do try
            catch ( EOFException eofException ) 
            {
               displayMessage( "\nServer terminated connection" );
            } // fim do catch
            finally 
            {
               closeConnection(); // fecha a conexão
               counter++;
            } // fim de finally
         } // fim do while
      } // fim do try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // fim do catch
   } // fim do método runServer

   // espera que a conexão chegue e então exibe informações sobre a conexão
   private void waitForConnection() throws IOException
   {
      displayMessage( "Waiting for connection\n" );
      connection = server.accept(); // permite que servidor aceite a conexão
      displayMessage( "Connection " + counter + " received from: " +
         connection.getInetAddress().getHostName());
   } // fim do método waitForConnection

   // obtém fluxos para enviar e receber dados
   private void getStreams() throws IOException
   {
      // configura o fluxo de saída para objetos
      output = new ObjectOutputStream( connection.getOutputStream() );
      output.flush(); // esvazia buffer de saída enviar as informações de cabeçalho

      // configura o fluxo de entrada para objetos
      input = new ObjectInputStream( connection.getInputStream() );

      displayMessage( "\nGot I/O streams\n" );
   } // fim do método getStreams

   // processa a conexão com o cliente
   private void processConnection() throws IOException
   {
      String message = "Connection successful";
      sendData( message ); // envia uma mensagem de conexão bem-sucedida

      // ativa enterField de modo que usuário do servidor possa enviar mensagens
      setTextFieldEditable( true );

      do // processa as mensagens enviadas pelo cliente
      { 
         try // lê e exibe a mensagem
         {
            message = ( String ) input.readObject(); // lê uma nova mensagem
            displayMessage( "\n" + message ); // exibe a mensagem
         } // fim do try
         catch ( ClassNotFoundException classNotFoundException ) 
         {
            displayMessage( "\nUnknown object type received" );
         } // fim do catch

      } while ( !message.equals( "CLIENT>>> TERMINATE" ) );
   } // fim do método processConnection

   // fecha os fluxos e o socket
   private void closeConnection() 
   {
      displayMessage( "\nTerminating connection\n" );
      setTextFieldEditable( false ); // desativa enterField

      try 
      {
         output.close(); // fecha o fluxo de saída
         input.close(); // fecha o fluxo de entrada  
         connection.close(); // fecha o socket   
      } // fim do try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // fim do catch
   } // fim do método closeConnection

   // envia a mensagem ao cliente
   private void sendData( String message )
   {
      try // envia o objeto ao cliente
      {
         output.writeObject( "SERVER>>> " + message );
         output.flush(); // esvazia a saída para o cliente    
         displayMessage( "\nSERVER>>> " + message );
      } // fim do try
      catch ( IOException ioException ) 
      {
         displayArea.append( "\nError writing object" );
      } // fim do catch
   } // fim do método sendData

   // manipula a displayArea na thread de despacho de eventos
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() // atualiza a displayArea 
            {
               displayArea.append( messageToDisplay ); // acrescenta a mensagem
            } // fim do método run
         } // fim da classe interna anônima
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do método displayMessage

   // manipula o enterField na thread de despacho de eventos
   private void setTextFieldEditable( final boolean editable )
   {
      SwingUtilities.invokeLater(
         new Runnable()
         {
            public void run() // configura a editabilidade do enterField
            {
               enterField.setEditable( editable );
            } // fim do método run
         }  // fim da classe inner
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do método setTextFieldEditable
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