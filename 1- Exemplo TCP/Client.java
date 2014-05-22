// Fig. 24.7: Client.java
// Cliente que l� e exibe as informa��es enviadas a partir de um Servidor.
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;     
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
   private JTextField enterField; // insere informa��es fornecidas pelo usu�rio
   private JTextArea displayArea; // exibe informa��es para o usu�rio
   private ObjectOutputStream output; // gera o fluxo de sa�da para o servidor
   private ObjectInputStream input; // gera o fluxo de entrada a partir do servidor
   private String message = ""; // mensagem do servidor
   private String chatServer; // servidor de host para esse aplicativo
   private Socket client; // socket para comunica��o com o servidor

   // inicializa chatServer e configura a GUI
   public Client( String host )
   {
      super( "Client" );

      chatServer = host; // configura o servidor ao qual esse cliente se conecta

      enterField = new JTextField(); // cria enterField
      enterField.setEditable( false );
      enterField.addActionListener(
         new ActionListener() 
         {
            // envia mensagem ao servidor
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
   } // fim do construtor Client

   // conecta-se ao servidor e processa as mensagens a partir do servidor
   public void runClient() 
   {
      try // conecta-se ao servidor, obt�m fluxos, processa a conex�o
      {
         connectToServer(); // cria um Socket para fazer a conex�o
         getStreams(); // obt�m os fluxos de entrada e sa�da
         processConnection(); // processa a conex�o
      } // fim do try
      catch ( EOFException eofException ) 
      {
         displayMessage( "\nClient terminated connection" );
      } // fim do catch
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // fim do catch
      finally 
      {
         closeConnection(); // fecha a conex�o
      } // fim de finally
   } // fim do m�todo runClient

   // conecta-se ao servidor
   private void connectToServer() throws IOException
   {      
      displayMessage( "Attempting connection\n" );

      // cria Socket fazer a conex�o ao servidor
      client = new Socket( InetAddress.getByName( chatServer ), 12345 );

      // exibe informa��es sobre a conex�o
      displayMessage( "Connected to: " + 
         client.getInetAddress().getHostName() );
   } // fim do m�todo connectToServer

   // obt�m fluxos para enviar e receber dados
   private void getStreams() throws IOException
   {
      // configura o fluxo de sa�da para objetos
      output = new ObjectOutputStream( client.getOutputStream() );     
      output.flush(); // esvazia buffer de sa�da enviar as informa��es de cabe�alho

      // configura o fluxo de entrada para objetos
      input = new ObjectInputStream( client.getInputStream() );

      displayMessage( "\nGot I/O streams\n" );
   } // fim do m�todo getStreams

   // processa a conex�o com o servidor
   private void processConnection() throws IOException
   {
      // ativa enterField de modo que o usu�rio cliente possa enviar mensagens
      setTextFieldEditable( true );

      do // processa as mensagens enviadas do servidor
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

      } while ( !message.equals( "SERVER>>> TERMINATE" ) );
   } // fim do m�todo processConnection

   // fecha os fluxos e o socket
   private void closeConnection() 
   {
      displayMessage( "\nClosing connection" );
      setTextFieldEditable( false ); // desativa enterField

      try 
      {
         output.close(); // fecha o fluxo de sa�da
         input.close(); // fecha o fluxo de entrada
         client.close(); // fecha o socket   
      } // fim do try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // fim do catch
   } // fim do m�todo closeConnection

   // envia mensagem ao servidor
   private void sendData( String message )
   {
      try // envia o objeto ao servidor
      {
         output.writeObject( "CLIENT>>> " + message );
         output.flush(); // esvazia os dados para sa�da      
         displayMessage( "\nCLIENT>>> " + message );
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
               displayArea.append( messageToDisplay );
            } // fim do m�todo run
         }  // fim da classe interna an�nima
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
         } // fim da classe interna an�nima
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do m�todo setTextFieldEditable
} // fim da classe Client


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
