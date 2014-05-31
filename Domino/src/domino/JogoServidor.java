
package domino;

import java.util.ArrayList;
import java.util.Collections;

public class JogoServidor {
    ControladorServidor controlador;
    public ArrayList<Peca> pecasJogo = new ArrayList<> ();
    ArrayList<Peca> pecasDisponiveis = new ArrayList<> ();
    public ArrayList<Jogador> jogadores = new ArrayList<> ();
    int jogadorDavez;

    int maxJogadores;
    int rodada = 1;
    boolean jogando = false;
    
    public JogoServidor (int maxJogadores, ControladorServidor cont) {
        this.maxJogadores = maxJogadores;
        this.controlador = cont;
    }

    public void adicionaJogador (Jogador jogador) {
        jogadores.add(jogador);
        controlador.atualizaTabelaJogadores (jogadores);

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
 
        int i;
        
        // Mandando as pecas de 'pecasJogo'  para 'pecasDisponiveis'
        // O vetor de 'pecasJogo' deve ficar vazio para o inicio do jogo!
        for (i=0; i<=pecasJogo.size(); i++) {
            pecasDisponiveis.add(pecasJogo.get(0));
            pecasJogo.remove(0);
        }
        pecasDisponiveis.add(pecasJogo.get(0));
        pecasJogo.remove(0);
        
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
        // Descobrir qual é o 1o jogador (quem tiver a maior peca)
        jogadorDavez = procuraJogadorInicial ();

        //****************************************************************************************************
        // REMOVER ISSO!!! SO USEI PRA TESTAR!!!
        jogadorDavez = 0;
        //****************************************************************************************************
        
        System.out.println("A peca inicial foi encontrada com o jogador "+jogadorDavez);
        jogadores.get(jogadorDavez).mostraPecas();

        // O jogador da vez eh o usuario que roda o programa, então nao precisa
        // de comunicacao em rede!
        if (jogadorDavez == 0) {
            controlador.mensagemJogadores("Sua vez de jogar!");
            controlador.gui.destravaTela();
        }
        else
            controlador.servidorTcp.controlaJogadas(jogadorDavez);

    }
    
    public void proximoJogador() {
        jogadorDavez++;
        if (jogadorDavez == 0) {
            controlador.mensagemJogadores("Sua vez de jogar!");
            controlador.gui.destravaTela();
        }
        else
            controlador.servidorTcp.controlaJogadas(jogadorDavez);
        
    }

    private int procuraJogadorInicial () {
        int jogadorDavez;
        int x = 6;
        Peca pecaProcurada = new Peca(x, x); // Procurar a peca 6, depois a 5, depois ...
        boolean estaNasDisponiveis = false;

        // Procurar onde esta a maior peca:
        // Pode estar na fila de pecasDisponiveis ou com um dos jogadores
        while (!estaNasDisponiveis) {
            for (int i=0; i<pecasDisponiveis.size(); i++) {
                if (pecasDisponiveis.get(i).ehIgual(pecaProcurada))
                    estaNasDisponiveis = true;
            }
            if (estaNasDisponiveis) {
                x--; // Tentar com a proxima menor peca (5, 4, ...)
                pecaProcurada = new Peca(x, x);
                estaNasDisponiveis = false;
            }
            else
                estaNasDisponiveis = true;
        }
        
        // Achamos a peca inicial!
        System.out.println("Peca inicial:"+pecaProcurada.toString());
        
        // Descobrir com qual jogador esta a peca inicial!
        for (int i=0; i<jogadores.size(); i++) {
            if (jogadores.get(i).procura(pecaProcurada) != -1) // Jogador (i) tem a peca inicial!
                return i;
        }

        return -1;
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
