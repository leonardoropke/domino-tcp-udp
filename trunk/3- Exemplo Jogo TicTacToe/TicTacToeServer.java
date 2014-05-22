// Fig. 24.13: TicTacToeServer.java
// Essa classe mantém um jogo da velha para dois clientes.
import java.awt.BorderLayout;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TicTacToeServer extends JFrame 
{
   private String[] board = new String[ 9 ]; // tabuleiro do jogo-da-velha
   private JTextArea outputArea; // para gerar saída das jogadas
   private Player[] players; // array de Players
   private ServerSocket server; // socket de servidor para conectar com clientes
   private int currentPlayer; // monitora o jogador com a jogada atual
   private final static int PLAYER_X = 0; // constante para o primeiro jogador
   private final static int PLAYER_O = 1; // constante para o segundo jogador
   private final static String[] MARKS = { "X", "O" }; // array de marcas
   private ExecutorService runGame; // executará os jogadores
   private Lock gameLock; // para bloquear a sincronização do jogo
   private Condition otherPlayerConnected; // para esperar outro jogador
   private Condition otherPlayerTurn; // para esperar a jogada do outro jogador

   // configura o servidor de tic-tac-toe e a GUI que exibe as mensagens
   public TicTacToeServer()
   {
      super( "Tic-Tac-Toe Server" ); // configura o título da janela

      // cria ExecutorService com uma thread para cada jogador
      runGame = Executors.newFixedThreadPool( 2 );
      gameLock = new ReentrantLock(); // cria um bloqueio para o jogo

      // variável de condição para os dois jogadores sendo conectados
      otherPlayerConnected = gameLock.newCondition();

      // variável de condição para a jogada do outro jogador
      otherPlayerTurn = gameLock.newCondition();

      for ( int i = 0; i < 9; i++ )
         board[ i ] = new String( "" ); // cria o tabuleiro de jogo-da-velha
      players = new Player[ 2 ]; // cria array de jogadores
      currentPlayer = PLAYER_X; // configura o jogador atual como o primeiro jogador
 
      try
      {
         server = new ServerSocket( 12345, 2 ); // configura ServerSocket
      } // fim do try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
         System.exit( 1 );
      } // fim do catch

      outputArea = new JTextArea(); // cria JTextArea para saída
      add( outputArea, BorderLayout.CENTER );
      outputArea.setText( "Server awaiting connections\n" );

      setSize( 300, 300 ); // configura o tamanho da janela
      setVisible( true ); // mostra a janela
   } // fim do construtor TicTacToeServer

   // espera duas conexões para que o jogo possa ser jogado
   public void execute()
   {
      // espera que cada cliente se conecte
      for ( int I = 0; I < players.length; I++ ) 
      {
         try // espera a conexão, cria Player, inicia o executável
         {
            players[ I ] = new Player( server.accept(), I );           
            runGame.execute( players[ I ] ); // executa o executável de jogador
         } // fim do try
         catch ( IOException ioException ) 
         {
            ioException.printStackTrace();
            System.exit( 1 );
         } // fim do catch
      } // fim do for

      gameLock.lock(); // bloqueia o jogo para sinalizar a thread do jogador X

      try
      {
         players[ PLAYER_X ].setSuspended( false ); // retoma o jogador X
         otherPlayerConnected.signal(); // acorda a thread do jogador X
      } // fim do try
      finally
      {
         gameLock.unlock(); // desbloqueia o jogo depois de sinalizar para o jogador X
      } // fim de finally
   } // fim do método execute
   
   // exibe uma mensagem na outputArea
   private void displayMessage( final String messageToDisplay )
   {
      // exibe uma mensagem a partir da thread de despacho de eventos da execução
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() // atualiza a outputArea
            {
               outputArea.append( messageToDisplay ); // adiciona mensagem
            } // fim do método run
         } // fim da classe inner
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do método displayMessage

   // determina se a jogada é válida
   public boolean validateAndMove( int location, int player )
   {
      // enquanto não for o jogador atual, deve esperar a jogada
      while ( player != currentPlayer ) 
      {
         gameLock.lock(); // bloqueia o jogo para que o outro jogador prossiga

         try 
         {
            otherPlayerTurn.await(); // espera a jogada do jogador
         } // fim do try
         catch ( InterruptedException exception )
         {
            exception.printStackTrace();
         } // fim do catch
         finally
         {
            gameLock.unlock(); // desbloqueia o jogo depois de esperar
         } // fim de finally
      } // fim do while

      // se a posição não estiver ocupada, faz a jogada
      if ( !isOccupied( location ) )
      {
         board[ location ] = MARKS[ currentPlayer ]; // configura uma jogada no tabuleiro
         currentPlayer = ( currentPlayer + 1 ) % 2; // troca o jogador

         // deixa que novo jogador atual saiba que a jogada ocorreu
         players[ currentPlayer ].otherPlayerMoved( location );

         gameLock.lock(); // bloqueia o jogo para sinalizar ao outro jogador a prosseguir

         try 
         {
            otherPlayerTurn.signal(); // sinaliza que o outro jogador continue
         } // fim do try
         finally
         {
            gameLock.unlock(); // desbloqueia o jogo depois de sinalizar
         } // fim de finally

         return true; // notifica o jogador que a jogada foi válida
      } // fim do if
      else // a jogada não foi válida
         return false; // notifica o jogador que a jogada foi inválida
   } // fim do método validateAndMove

   // determina se a posição está ocupada
   public boolean isOccupied( int location )
   {
      if ( board[ location ].equals( MARKS[ PLAYER_X ] ) || 
         board [ location ].equals( MARKS[ PLAYER_O ] ) )
         return true; // posição está ocupada
      else
         return false; // posição não está ocupada
   } // fim do método isOccupied

   // coloque o código nesse método para determinar se o jogo terminou
   public boolean isGameOver()
   {
      return false; // isso é deixado como um exercício
   } // fim do método isGameOver

   // classe interna privada Player gerencia cada Player como um executável
   private class Player implements Runnable 
   {
      private Socket connection; // conexão com o cliente
      private Scanner input; // entrada do cliente
      private Formatter output; // saída para o cliente
      private int playerNumber; // monitora qual jogador isso é
      private String mark; // marca para esse jogador
      private boolean suspended = true; // se a thread está suspensa

      // configura a thread Player
      public Player( Socket socket, int number )
      {
         playerNumber = number; // armazena o número desse jogador
         mark = MARKS[ playerNumber ]; // especifica a marca do jogador 
         connection = socket; // armazena o socket para o cliente
         
         try // obtém fluxos a partir de Socket
         {
            input = new Scanner( connection.getInputStream() );    
            output = new Formatter( connection.getOutputStream() );
         } // fim do try
         catch ( IOException ioException ) 
         {
            ioException.printStackTrace();
            System.exit( 1 );
         } // fim do catch
      } // fim do construtor Player

      // envia uma mensagem de que o outro jogador fez uma jogada
      public void otherPlayerMoved( int location )
      {
         output.format( "Opponent moved\n" );                       
         output.format( "%d\n", location ); // envia a posição da jogada
         output.flush(); // esvazia a saída                            
      } // fim do método otherPlayerMoved

      // execução da thread de controle
      public void run()
      {
         // envia ao cliente a marca (X ou O), processa as mensagens do cliente
         try 
         {
            displayMessage( "Player " + mark + " connected\n" );
            output.format( "%s\n", mark ); // envia a marca do jogador
            output.flush(); // esvazia a saída                            

            // se for o jogador X, espera que o outro jogador chegue
            if ( playerNumber == PLAYER_X ) 
            {
               output.format( "%s\n%s", "Player X connected",
                  "Waiting for another player\n" );          
               output.flush(); // esvazia a saída                            

               gameLock.lock(); // bloqueia o jogo para esperar o segundo jogador  

               try 
               {
                  while( suspended )
                  {
                     otherPlayerConnected.await(); // espera o jogador O
                  } // fim do while
               } // fim de try
               catch ( InterruptedException exception ) 
               {
                  exception.printStackTrace();
               } // fim do catch
               finally
               {
                  gameLock.unlock(); // desbloqueia o jogo depois do segundo jogador 
               } // fim de finally

               // envia uma mensagem de que o outro jogador se conectou
               output.format( "Other player connected. Your move.\n" );
               output.flush(); // esvazia a saída                            
            } // fim do if
            else
            {
               output.format( "Player O connected, please wait\n" );
               output.flush(); // esvazia a saída                            
            } // fim de else

            // enquanto jogo não terminou
            while ( !isGameOver() ) 
            {
               int location = 0; // inicializa a posição da jogada

               if ( input.hasNext() )
                  location = input.nextInt(); // obtém a posição da jogada

               // verifica uma jogada válida
               if ( validateAndMove( location, playerNumber ) ) 
               {
                  displayMessage( "\nlocation: " + location );
                  output.format( "Valid move.\n" ); // notifica o cliente
                  output.flush(); // esvazia a saída                            
               } // fim do if
               else // jogada foi inválida
               {
                  output.format( "Invalid move, try again\n" );
                  output.flush(); // esvazia a saída                            
               } // fim de else
            } // fim do while
         } // fim do try
         finally
         {
            try
            {
               connection.close(); // fecha a conexão com o cliente
            } // fim do try
            catch ( IOException ioException ) 
            {
               ioException.printStackTrace();
               System.exit( 1 );
            } // fim do catch
         } // fim de finally
      } // fim do método run

      // configura se a thread está ou não suspensa
      public void setSuspended( boolean status )
      {
         suspended = status; // configura o valor do suspenso
      } // fim do método setSuspended
   } // fim da classe Player
} // fim da classe TicTacToeServer


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