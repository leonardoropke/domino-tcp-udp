// Fig. 24.13: TicTacToeServer.java
// Essa classe mant�m um jogo da velha para dois clientes.
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
   private JTextArea outputArea; // para gerar sa�da das jogadas
   private Player[] players; // array de Players
   private ServerSocket server; // socket de servidor para conectar com clientes
   private int currentPlayer; // monitora o jogador com a jogada atual
   private final static int PLAYER_X = 0; // constante para o primeiro jogador
   private final static int PLAYER_O = 1; // constante para o segundo jogador
   private final static String[] MARKS = { "X", "O" }; // array de marcas
   private ExecutorService runGame; // executar� os jogadores
   private Lock gameLock; // para bloquear a sincroniza��o do jogo
   private Condition otherPlayerConnected; // para esperar outro jogador
   private Condition otherPlayerTurn; // para esperar a jogada do outro jogador

   // configura o servidor de tic-tac-toe e a GUI que exibe as mensagens
   public TicTacToeServer()
   {
      super( "Tic-Tac-Toe Server" ); // configura o t�tulo da janela

      // cria ExecutorService com uma thread para cada jogador
      runGame = Executors.newFixedThreadPool( 2 );
      gameLock = new ReentrantLock(); // cria um bloqueio para o jogo

      // vari�vel de condi��o para os dois jogadores sendo conectados
      otherPlayerConnected = gameLock.newCondition();

      // vari�vel de condi��o para a jogada do outro jogador
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

      outputArea = new JTextArea(); // cria JTextArea para sa�da
      add( outputArea, BorderLayout.CENTER );
      outputArea.setText( "Server awaiting connections\n" );

      setSize( 300, 300 ); // configura o tamanho da janela
      setVisible( true ); // mostra a janela
   } // fim do construtor TicTacToeServer

   // espera duas conex�es para que o jogo possa ser jogado
   public void execute()
   {
      // espera que cada cliente se conecte
      for ( int I = 0; I < players.length; I++ ) 
      {
         try // espera a conex�o, cria Player, inicia o execut�vel
         {
            players[ I ] = new Player( server.accept(), I );           
            runGame.execute( players[ I ] ); // executa o execut�vel de jogador
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
   } // fim do m�todo execute
   
   // exibe uma mensagem na outputArea
   private void displayMessage( final String messageToDisplay )
   {
      // exibe uma mensagem a partir da thread de despacho de eventos da execu��o
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() // atualiza a outputArea
            {
               outputArea.append( messageToDisplay ); // adiciona mensagem
            } // fim do m�todo run
         } // fim da classe inner
      ); // fim da chamada para SwingUtilities.invokeLater
   } // fim do m�todo displayMessage

   // determina se a jogada � v�lida
   public boolean validateAndMove( int location, int player )
   {
      // enquanto n�o for o jogador atual, deve esperar a jogada
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

      // se a posi��o n�o estiver ocupada, faz a jogada
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

         return true; // notifica o jogador que a jogada foi v�lida
      } // fim do if
      else // a jogada n�o foi v�lida
         return false; // notifica o jogador que a jogada foi inv�lida
   } // fim do m�todo validateAndMove

   // determina se a posi��o est� ocupada
   public boolean isOccupied( int location )
   {
      if ( board[ location ].equals( MARKS[ PLAYER_X ] ) || 
         board [ location ].equals( MARKS[ PLAYER_O ] ) )
         return true; // posi��o est� ocupada
      else
         return false; // posi��o n�o est� ocupada
   } // fim do m�todo isOccupied

   // coloque o c�digo nesse m�todo para determinar se o jogo terminou
   public boolean isGameOver()
   {
      return false; // isso � deixado como um exerc�cio
   } // fim do m�todo isGameOver

   // classe interna privada Player gerencia cada Player como um execut�vel
   private class Player implements Runnable 
   {
      private Socket connection; // conex�o com o cliente
      private Scanner input; // entrada do cliente
      private Formatter output; // sa�da para o cliente
      private int playerNumber; // monitora qual jogador isso �
      private String mark; // marca para esse jogador
      private boolean suspended = true; // se a thread est� suspensa

      // configura a thread Player
      public Player( Socket socket, int number )
      {
         playerNumber = number; // armazena o n�mero desse jogador
         mark = MARKS[ playerNumber ]; // especifica a marca do jogador 
         connection = socket; // armazena o socket para o cliente
         
         try // obt�m fluxos a partir de Socket
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
         output.format( "%d\n", location ); // envia a posi��o da jogada
         output.flush(); // esvazia a sa�da                            
      } // fim do m�todo otherPlayerMoved

      // execu��o da thread de controle
      public void run()
      {
         // envia ao cliente a marca (X ou O), processa as mensagens do cliente
         try 
         {
            displayMessage( "Player " + mark + " connected\n" );
            output.format( "%s\n", mark ); // envia a marca do jogador
            output.flush(); // esvazia a sa�da                            

            // se for o jogador X, espera que o outro jogador chegue
            if ( playerNumber == PLAYER_X ) 
            {
               output.format( "%s\n%s", "Player X connected",
                  "Waiting for another player\n" );          
               output.flush(); // esvazia a sa�da                            

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
               output.flush(); // esvazia a sa�da                            
            } // fim do if
            else
            {
               output.format( "Player O connected, please wait\n" );
               output.flush(); // esvazia a sa�da                            
            } // fim de else

            // enquanto jogo n�o terminou
            while ( !isGameOver() ) 
            {
               int location = 0; // inicializa a posi��o da jogada

               if ( input.hasNext() )
                  location = input.nextInt(); // obt�m a posi��o da jogada

               // verifica uma jogada v�lida
               if ( validateAndMove( location, playerNumber ) ) 
               {
                  displayMessage( "\nlocation: " + location );
                  output.format( "Valid move.\n" ); // notifica o cliente
                  output.flush(); // esvazia a sa�da                            
               } // fim do if
               else // jogada foi inv�lida
               {
                  output.format( "Invalid move, try again\n" );
                  output.flush(); // esvazia a sa�da                            
               } // fim de else
            } // fim do while
         } // fim do try
         finally
         {
            try
            {
               connection.close(); // fecha a conex�o com o cliente
            } // fim do try
            catch ( IOException ioException ) 
            {
               ioException.printStackTrace();
               System.exit( 1 );
            } // fim do catch
         } // fim de finally
      } // fim do m�todo run

      // configura se a thread est� ou n�o suspensa
      public void setSuspended( boolean status )
      {
         suspended = status; // configura o valor do suspenso
      } // fim do m�todo setSuspended
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