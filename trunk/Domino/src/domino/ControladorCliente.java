
package domino;

import gui.jFrame;
import java.util.ArrayList;

/**
 *
 * @author Carlos
 */
public class ControladorCliente {
    JogoCliente jogo;
    jFrame gui;
 
    public ControladorCliente(jFrame aThis) {
        gui = aThis;
    }
    
    public void atualizaTabelaJogadores (ArrayList<Jogador> jogadores) {
        gui.atualizaTabelaJogadores(jogadores);
    }

    public void mostraJogoAtual (ArrayList<Peca> pecas, ArrayList<Peca> pecasDisponiveis, ArrayList<Jogador> jogadores, int rodada) {
        gui.mostraJogo(pecas);
        gui.mostraPecasDisponiveis (pecasDisponiveis);
        
        gui.mostraPecasJogador(jogadores.get(0).listaDePecas);
        gui.atualizaRodada(rodada);
        //gui.adicionaMsg("JogoServidor iniciado!");
        
    }
    
    // Criar um novo jogo como cliente
    public void novoJogo (String ip, int porta, String nomeJogador) {
        // Conectar-se ao servidor e esperar a resposta!
        jogo = new JogoCliente(this);
        jogo.conectar(ip, porta, nomeJogador);
    }
    
}
