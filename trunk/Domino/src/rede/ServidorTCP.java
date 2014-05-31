
package rede;

/**
 *
 * @author Carlos
 */
import domino.ControladorServidor;
import domino.Jogador;
import java.io.IOException;
import java.net.ServerSocket;

public class ServidorTCP {
   
   private ServerSocket server; // socket de servidor     

   private int counter = 1; // contador do número de conexões
   private int porta = 0;
   
   private ControladorServidor controlador;

   // configura a GUI
   public ServidorTCP (int porta, ControladorServidor aThis) {
       this.porta = porta;
       this.controlador = aThis;
       
       try {
            server = new ServerSocket (porta);
        } catch (IOException ex) {
            System.out.println("Erro ao abrir porta "+porta+" !");
        }
       
   }

   // fecha os fluxos e o socket
   private void closeConnection(Jogador jogador) {
       System.out.println("\nTerminando conexao com jogador '"+jogador.nome+"' !");

      try {
         jogador.output.close(); // fecha o fluxo de saída
         jogador.input.close(); // fecha o fluxo de entrada  
         jogador.conexao.close(); // fecha o socket
      } // fim do try
      catch ( IOException ioException ) 
      {
          System.out.println("Nao consegui fechar a conexao!");
      }
   }
   
   public void adicionaJogadores(final int njogadores) {
        final String nomeJogador = "";
        Jogador jogador;

        controlador.gui.adicionaMsg("Esperando conexao de jogadores...");

        // Adicionando jogador 0 (o que roda o programa servidor)
        String nomeJogador0 = controlador.gui.getNomeJogador();
        jogador = new Jogador(nomeJogador0, null, 0);
        controlador.jogo.adicionaJogador(jogador);
        controlador.atualizaNovoJogador(jogador);
        
       
        new Thread() {
            @Override
            public void run() {
                try {
        
        // i<=3
        for (int i=1; i<=njogadores-1; i++) {
            //nomeJogador = "Jogador"+i;
            try // espera a conexão, cria Player, inicia o executável
             {
                
                Jogador jogador2 = new Jogador("", server.accept(), i);
                jogador2.recebeNome();
                controlador.jogo.adicionaJogador(jogador2);
                controlador.atualizaNovoJogador(jogador2);

            }
            catch ( IOException ioException ) 
             {
                 System.out.println("Bug!");
             }


        }
  
                }
                catch (Exception e) { System.out.println(e); }
            }
        }.start();

    }

    public void enviaPecasJogadores() {
        Jogador jogador;
        for (int i=0; i<controlador.jogo.jogadores.size(); i++) {
            jogador = controlador.jogo.jogadores.get(i);
            jogador.enviaPecaCliente();
        }
    }
    
    
}

