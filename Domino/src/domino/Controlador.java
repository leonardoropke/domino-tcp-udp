
package domino;

import gui.jFrameServidor;
import java.util.ArrayList;

/**
 *
 * @author Carlos
 */
public class Controlador {
    Jogo jogo;
    int njogadores;
    jFrameServidor gui;
 
    public Controlador(jFrameServidor aThis) {
        gui = aThis;
    }

    public void mostraJogoAtual (ArrayList<Peca> pecas, ArrayList<Jogador> jogadores) {
        gui.mostraJogo(pecas);
        gui.mostraPecasJogador(jogadores.get(0).listaDePecas);
        gui.adicionaMsg("Carlos eh foda\n");
        
    }
    
    public void novoJogo (int nJogadores) {
  
        this.njogadores = nJogadores;
        jogo = new Jogo("tcp", njogadores, this);
        
        // Adicionando jogadores.
        // Quando for implementar o TCP, ler essa informaçao dos clientes
        for (int i=0; i<njogadores; i++) {
            Jogador jogador = new Jogador("Jogador"+i);
            jogo.adicionaJogador(jogador);
        }

        // Criando e distribuindo as pecas
        jogo.misturaPecas();
        
        //Peca peca = jogo.pecasJogo.get(0);
        //System.out.println("Peca atual: "+peca);
  
        // Distribuindo pecas entre os jogadores
        Peca peca;
        int x=0; // Controla o array de pecas
        for (int i=0; i<njogadores; i++) {
            for (int j=0; j<6; j++) { // 6 Pecas para cada jogador...
                peca = jogo.pecasJogo.get(x); // Pegar uma peca da lista de pecas...
                jogo.jogadores.get(i).recebePeca(peca); // Dar a peca para o jogador
                x++;
            }
        }
        for (int i=0; i<x; i++) {
            System.out.println("Removendo peca "+i+": "+jogo.pecasJogo.get(0));
            jogo.pecasJogo.remove(0); // Remover as pecas ja distribuidas da lista de pecas do jogo
        }
        
        
        for (int i=0; i<njogadores; i++) {
            System.out.println ("\nMostrando pecas do jogador "+i+":");
            jogo.jogadores.get(i).mostraPecas();
            System.out.println("");
        }
        
        // Mostrando as pecas que sobraram...
        System.out.println("Pecas que sobraram:");
        for (int i=0; i<jogo.pecasJogo.size(); i++) {
            System.out.println("Peca "+i+": "+jogo.pecasJogo.get(i));
        }

        jogo.iniciar();
    }
}