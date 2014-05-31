/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rede;

/**
 *
 * @author Carlos
 */

// Fig. 24.7: Client.java
// Cliente que lê e exibe as informações enviadas a partir de um Servidor.
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.*;

public class ClienteTCP extends JFrame 
{
   private JTextField enterField; // insere informações fornecidas pelo usuário
   private JTextArea displayArea; // exibe informações para o usuário
   private ObjectOutputStream output; // gera o fluxo de saída para o servidor
   private ObjectInputStream input; // gera o fluxo de entrada a partir do servidor
   private String message = ""; // mensagem do servidor
   private String chatServer; // servidor de host para esse aplicativo
   private Socket client; // socket para comunicação com o servidor

   // inicializa chatServer e configura a GUI
   public ClienteTCP( String host )
   {
      super( "Client" );

      chatServer = host; // configura o servidor ao qual esse cliente se conecta

      enterField = new JTextField(); // cria enterField
      enterField.setEditable( false );
      enterField.addActionListener(
         new ActionListener() 
         {
            // envia mensagem ao servidor
            @Override
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
   } // fim do construtor Client

   // conecta-se ao servidor e processa as mensagens a partir do servidor
   public void runClient() 
   {
      try // conecta-se ao servidor, obtém fluxos, processa a conexão
      {
         connectToServer(); // cria um Socket para fazer a conexão
         getStreams(); // obtém os fluxos de entrada e saída
         processConnection(); // processa a conexão
      } // fim do try
      catch ( EOFException eofException ) 
      {
         displayMessage( "\nClient terminated connection" );
      } // fim do catch
      catch ( IOException ioException ) 
      {
      } // fim do catch
      finally 
      {
         closeConnection(); // fecha a conexão
      } // fim de finally
   } // fim do método runClient

   // conecta-se ao servidor
   private void connectToServer() throws IOException
   {      
      displayMessage( "Attempting connection\n" );

      // cria Socket fazer a conexão ao servidor
      client = new Socket( InetAddress.getByName( chatServer ), 12345 );

      // exibe informações sobre a conexão
      displayMessage( "Connected to: " + 
         client.getInetAddress().getHostName() );
   } // fim do método connectToServer

   // obtém fluxos para enviar e receber dados
   private void getStreams() throws IOException
   {
      // configura o fluxo de saída para objetos
      output = new ObjectOutputStream( client.getOutputStream() );     
      output.flush(); // esvazia buffer de saída enviar as informações de cabeçalho

      // configura o fluxo de entrada para objetos
      input = new ObjectInputStream( client.getInputStream() );

      displayMessage( "\nGot I/O streams\n" );
   } // fim do método getStreams

   // processa a conexão com o servidor
   private void processConnection() throws IOException
   {
      // ativa enterField de modo que o usuário cliente possa enviar mensagens
      setTextFieldEditable( true );

      do // processa as mensagens enviadas do servidor
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

      } while ( !message.equals( "SERVER>>> TERMINATE" ) );
   } // fim do método processConnection

   // fecha os fluxos e o socket
   private void closeConnection() 
   {
      displayMessage( "\nClosing connection" );
      setTextFieldEditable( false ); // desativa enterField

      try 
      {
         output.close(); // fecha o fluxo de saída
         input.close(); // fecha o fluxo de entrada
         client.close(); // fecha o socket   
      } // fim do try
      catch ( IOException ioException ) 
      {
      } // fim do catch
   } // fim do método closeConnection

   // envia mensagem ao servidor
   private void sendData( String message )
   {
      try // envia o objeto ao servidor
      {
         output.writeObject( "CLIENT>>> " + message );
         output.flush(); // esvazia os dados para saída      
         displayMessage( "\nCLIENT>>> " + message );
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
            @Override
            public void run() // atualiza a displayArea 
            {
               displayArea.append( messageToDisplay );
            } // fim do método run
         }  // fim da classe interna anônima
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do método displayMessage

   // manipula o enterField na thread de despacho de eventos
   private void setTextFieldEditable( final boolean editable )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            @Override
            public void run() // configura a editabilidade do enterField
            {
               enterField.setEditable( editable );
            } // fim do método run
         } // fim da classe interna anônima
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do método setTextFieldEditable
} // fim da classe Client
