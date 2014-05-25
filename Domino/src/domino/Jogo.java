
package domino;

import java.util.ArrayList;
import java.util.Collections;

public class Jogo {
    Controlador controlador;
    ArrayList<Peca> pecasJogo = new ArrayList<> ();
    ArrayList<Peca> pecasDisponiveis = new ArrayList<> ();
    ArrayList<Jogador> jogadores = new ArrayList<> ();
    
    Servidor servidor;
    Cliente cliente;
    int maxJogadores;
    int rodada = 0;
    String transporte;
    boolean jogando = false;
    
    public Jogo (String transporte, int maxJogadores, Controlador cont) {
        this.transporte = transporte;
	this.servidor = new Servidor (transporte);
        this.maxJogadores = maxJogadores;
        this.controlador = cont;
    }

    public void adicionaJogador (Jogador jogador) {
        if (jogadores.size() == maxJogadores)
            System.out.println ("Impossivel adicionar jogador!\nNumero maximo de jogadores ja foi atingido!\n"); 
	else
            jogadores.add(jogador);
    }

    public void jogar () {
        jogando = true;
        System.out.println("Iniciando jogo!");
    }

    public void iniciar () {
        int i;
        
        // Ajustando as pecas do jogo atual e as pecas disponiveis
        for (i=0; i<=pecasJogo.size(); i++) {
            pecasDisponiveis.add(pecasJogo.get(0));
            pecasJogo.remove(0);
        }
        
        System.out.println("\nPecas do jogo atual:");
        for (i=0; i < pecasJogo.size(); i++)
            System.out.print(pecasJogo.get(i).toString());
        System.out.println("\nTotal: "+i+" pecas.");
        
        System.out.println("\nPecas disponiveis:");
        for (i=0; i < pecasDisponiveis.size(); i++)
            System.out.print(pecasDisponiveis.get(i).toString());
        System.out.println("\nTotal: "+i+" pecas.");
        
        // Atualizando a GUI
        controlador.mostraJogoAtual(pecasJogo, jogadores);
    }
    
    public void misturaPecas() {
        int i;
        
        // Gerando pecas
        pecasJogo = geraPecas();
       
        // Misturando pecas
        Collections.shuffle(pecasJogo);

        // Mostrando pecas misturadas
        System.out.println("");
        for (i=0; i < pecasJogo.size(); i++)
            System.out.print(pecasJogo.get(i).toString());
        System.out.println("\nTotal: "+i+" pecas.");
    }
    
    public ArrayList<Peca> geraPecas (){
        //esse método retorna um array com todas a pecas possíveis no dominó
        ArrayList<Peca> pecas = new ArrayList<> ();
        Peca peca;
        for (int i = 1; i <= 7; i++) {
            for (int j = 1; j <= 7; j++) {
                if (j >= i) {
                    peca = new Peca(i,j);
                    pecas.add(peca);
                }
            }
        }
        return pecas;
    }
    
}
