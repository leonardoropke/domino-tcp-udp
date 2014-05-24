package domino;

import java.util.ArrayList;

public class Jogo {
    ArrayList<Peca> jogo = new ArrayList<> ();
    ArrayList<Jogador> jogadores = new ArrayList<> ();
    Servidor servidor;
    int njogadores = 0;
    int rodada = 0;
    String transporte;
    boolean jogando = false;
    
    public Jogo (String transporte) {
        this.transporte = transporte;
	this.servidor = new Servidor (transporte);
    }

    public void adicionaJogador (Jogador jogador) {
        if (njogadores == 4) {
            System.out.println ("Impossivel adicionar jogador!\nNumero maximo de jogadores ja foi atingido!\n"); 
	}
	else {
            jogadores.add(jogador);
            njogadores++;
	}
    }

    public void iniciar () {
        jogando = true;
        System.out.println("Iniciando jogo!");
    }
}
