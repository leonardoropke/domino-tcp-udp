
package domino;

import gui.jFrame;
import java.util.ArrayList;

/**
 *
 * @author Carlos
 */
public class ControladorServidor {
    JogoServidor jogo;
    int njogadores;
    jFrame gui;
 
    public ControladorServidor(jFrame aThis) {
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
        gui.adicionaMsg("Jogo iniciado!");
        
    }
    
    public void novoJogo (int nJogadores) {
  
        this.njogadores = nJogadores;
        jogo = new JogoServidor("tcp", njogadores, this);
        
        // Adicionando jogadores.
        // Quando for implementar o TCP, ler essas informacoes dos clientes!!!!
        Jogador jogador;
        String nomeJogador;
        for (int i=0; i<njogadores; i++) {
            if (i == 0)
                nomeJogador = gui.getNomeJogador(); // O primeiro jogador eh o que esta rodando o servidor!
            else
                nomeJogador = "Jogador"+i;
            jogador = new Jogador(nomeJogador);
            jogo.adicionaJogador(jogador);
            gui.adicionaMsg("Jogador '"+nomeJogador+"' conectado!");
        }
        
        jogo.preparaJogo ();
        atualizaTabelaJogadores (jogo.jogadores);
        mostraJogoAtual(jogo.pecasJogo, jogo.pecasDisponiveis, jogo.jogadores, jogo.rodada);
        
        jogo.iniciar();

    }
}
