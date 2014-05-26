
package domino;

import gui.jFrame;
import java.util.ArrayList;

/**
 *
 * @author Carlos
 */
public class Controlador {
    Jogo jogo;
    int njogadores;
    jFrame gui;
 
    public Controlador(jFrame aThis) {
        gui = aThis;
    }
    
    public void atualizaJogadores (ArrayList<Jogador> jogadores) {
        gui.atualizaJogadores(jogadores);
    }

    public void mostraJogoAtual (ArrayList<Peca> pecas, ArrayList<Peca> pecasDisponiveis, ArrayList<Jogador> jogadores, int rodada) {
        gui.mostraJogo(pecas);
        gui.mostraPecasDisponiveis (pecasDisponiveis);
        
        gui.mostraPecasJogador(jogadores.get(0).listaDePecas);
        gui.atualizaRodada(rodada);
        gui.adicionaMsg("Jogo iniciado!");
        
    }
    
    public void novoJogo (int nJogadores) {
  
        this.njogadores = nJogadores;
        jogo = new Jogo("tcp", njogadores, this);
        
        // Adicionando jogadores.
        // Quando for implementar o TCP, ler essa informaçao dos clientes
        String nome = gui.getNomeJogador();
        Jogador jogador;
        for (int i=0; i<njogadores; i++) {
            if (i == 0)
                jogador = new Jogador(nome); // O primeiro jogador eh o que esta rodando o servidor!
            else
                jogador = new Jogador("Jogador"+i);
            jogo.adicionaJogador(jogador);
        }
        
        jogo.preparaJogo ();
        jogo.iniciar();
    }
}
