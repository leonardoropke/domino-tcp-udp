
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
        jogadores.add(jogador);
        controlador.atualizaJogadores (jogadores);
    }

    public void preparaJogo () {
        // Criando e distribuindo as pecas
        misturaPecas();
 
        // Distribuindo pecas entre os jogadores
        Peca peca;
        int x=0; // Controla o array de pecas
        for (int i=0; i<jogadores.size(); i++) {
            for (int j=0; j<6; j++) { // 6 Pecas para cada jogador...
                peca = pecasJogo.get(x); // Pegar uma peca da lista de pecas...
                jogadores.get(i).recebePeca(peca); // Dar a peca para o jogador
                x++;
            }
        }
        for (int i=0; i<x; i++) {
            System.out.println("Removendo peca "+i+": "+pecasJogo.get(0));
            pecasJogo.remove(0); // Remover as pecas ja distribuidas da lista de pecas do jogo
        }
        
        for (int i=0; i<jogadores.size(); i++) {
            System.out.println ("\nMostrando pecas do jogador "+i+":");
            jogadores.get(i).mostraPecas();
            System.out.println("");
        }
        
        // Mostrando as pecas que sobraram...
        System.out.println("Pecas que sobraram:");
        for (int i=0; i<pecasJogo.size(); i++) {
            System.out.println("Peca "+i+": "+pecasJogo.get(i));
        }

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
        
    }

    public void iniciar () {
        
        // Atualizando a GUI
        controlador.mostraJogoAtual(pecasJogo, pecasDisponiveis, jogadores, rodada);
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
