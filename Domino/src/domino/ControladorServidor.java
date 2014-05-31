
package domino;

import gui.jFrame;
import java.util.ArrayList;
import rede.ServidorTCP;
import rede.ServidorUDP;

/**
 *
 * @author Carlos
 */
public class ControladorServidor {
    public JogoServidor jogo;
    int njogadores;
    public jFrame gui;
    ServidorTCP servidorTcp;
    ServidorUDP servidorUdp;
 
    public ControladorServidor(jFrame aThis) {
        gui = aThis;
    }
    
    public void atualizaTabelaJogadores (ArrayList<Jogador> jogadores) {
        gui.atualizaTabelaJogadores(jogadores);
    }

    public void mostraJogoAtual (ArrayList<Peca> pecas, ArrayList<Peca> pecasDisponiveis, ArrayList<Jogador> jogadores, int rodada) {
        gui.mostraJogo(pecas);
        gui.mostraPecasDisponiveis (pecasDisponiveis);
        
//        gui.mostraPecasJogador(jogadores.get(0).listaDePecas);
        gui.atualizaRodada(rodada);
        
    }
    
    public void novoJogo (int nJogadores) {
  
        this.njogadores = nJogadores;
        this.njogadores = 2;
        
        jogo = new JogoServidor(njogadores, this);
        
        // O metodo adicionaJogadores() roda numa thread para permitir que a
        // interface grafica continue funcionando enquanto espera pelos outros
        // jogadores se conectarem.
        // A cada nova conexao, ela executa atualizaNovoJogador()
        servidorTcp = new ServidorTCP(12345, this);
        servidorTcp.adicionaJogadores (njogadores);
      }
    
    // Cada vez que um novo jogador se conectar, atualizar o jogo e a interface.
    // Se o numero maximo de jogadores for alcancado, distribuir as pecas e
    // comecar o jogo!
    public void atualizaNovoJogador (Jogador jogador) {
        atualizaTabelaJogadores (jogo.jogadores);
        mostraJogoAtual(jogo.pecasJogo, jogo.pecasDisponiveis, jogo.jogadores, jogo.rodada);
        
        gui.adicionaMsg("Jogador '"+jogador.nome+"' conectado!");

        // Temos todos os jogadores! Iniciar o jogo!
        if (jogo.jogadores.size() == njogadores) {
            jogo.preparaJogo (); // Distribui pecas entre os jogadores...
            gui.mostraPecasJogador(jogo.jogadores.get(0).listaDePecas);
            servidorTcp.enviaPecasJogadores(); // Enviar as pecas para os jogadores...

            atualizaTabelaJogadores (jogo.jogadores); // Atualiza interface grafica...
            mostraJogoAtual(jogo.pecasJogo, jogo.pecasDisponiveis, jogo.jogadores, jogo.rodada);
            
            jogo.iniciar(); // Comecar!
            gui.adicionaMsg("Jogo iniciado!");
        }
    }
}
