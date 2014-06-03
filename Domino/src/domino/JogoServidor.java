package domino;

import java.util.ArrayList;
import java.util.Collections;

public class JogoServidor {

    ControladorServidor controlador;
    public ArrayList<Peca> pecasJogo = new ArrayList<>();
    ArrayList<Peca> pecasDisponiveis = new ArrayList<>();
    public ArrayList<Jogador> jogadores = new ArrayList<>();
    int jogadorDavez;

    public int maxJogadores;
    int rodada = 1;
    boolean jogando = false;

    public JogoServidor(int maxJogadores, ControladorServidor cont) {
        this.maxJogadores = maxJogadores;
        this.controlador = cont;
    }

    public void adicionaJogador(Jogador jogador) {
        jogadores.add(jogador);
        controlador.atualizaTabelaJogadores(jogadores);

    }

    public void compraPeca(Jogador jogador, int index) {
        if (index < pecasDisponiveis.size()) {
        Peca peca = pecasDisponiveis.remove(index);
        jogador.listaDePecas.add(peca);
        }else{
        System.out.println("Index da peça escolhida invalido!!!");
        }
    }

    public void removePecaJogo (Peca peca) {
        // Temos que procurar a posicao no array onde esta a peca selecionada...
        int local = procura (peca);
        if (local != -1)
            pecasJogo.remove(local);
        else
            System.out.println("Nao consegui remover a peca: "+peca.toString()+"!!!");
    }
  
    // Metodo pra descobrir se o jogador tem uma determinada peca
    // Se encontrar, retornar a posicao da peca
    // Se NAO encontrar, retornar -1
    public int procura (Peca peca) {
        int i;
        for (i=0; i<pecasJogo.size(); i++) {
            if ((pecasJogo.get(i).ladoE == peca.ladoE) && (pecasJogo.get(i).ladoD == peca.ladoD))
                return i;
        }
        return -1;
    }
    
    public void preparaJogo() {
        // Criando e distribuindo as pecas
        misturaPecas();

        // Distribuindo pecas entre os jogadores
        Peca peca;
        int x = 0; // Controla o array de pecas
        for (int i = 0; i < jogadores.size(); i++) {
            for (int j = 0; j < 6; j++) { // 6 Pecas para cada jogador...
                peca = pecasJogo.get(x); // Pegar uma peca da lista de pecas...
                jogadores.get(i).recebePeca(peca); // Dar a peca para o jogador
                x++;
            }
        }
        for (int i = 0; i < x; i++) {
            System.out.println("Removendo peca " + i + ": " + pecasJogo.get(0));
            pecasJogo.remove(0); // Remover as pecas ja distribuidas da lista de pecas do jogo
        }

        int i;

        // Mandando as pecas de 'pecasJogo'  para 'pecasDisponiveis'
        // O vetor de 'pecasJogo' deve ficar vazio para o inicio do jogo!
        for (i = 0; i <= pecasJogo.size(); i++) {
            pecasDisponiveis.add(pecasJogo.get(0));
            pecasJogo.remove(0);
        }
        pecasDisponiveis.add(pecasJogo.get(0));
        pecasJogo.remove(0);

        System.out.println("\nPecas do jogo atual:");
        for (i = 0; i < pecasJogo.size(); i++) {
            System.out.print(pecasJogo.get(i).toString());
        }
        System.out.println("\nTotal: " + i + " pecas.");

        System.out.println("\nPecas disponiveis:");
        for (i = 0; i < pecasDisponiveis.size(); i++) {
            System.out.print(pecasDisponiveis.get(i).toString());
        }
        System.out.println("\nTotal: " + i + " pecas.");

    }

    public void iniciar() {
        // Descobrir qual é o 1o jogador (quem tiver a maior peca)
        jogadorDavez = procuraJogadorInicial();

        //****************************************************************************************************
        // REMOVER ISSO!!! SO USEI PRA TESTAR!!!
        jogadorDavez = 0;
        //****************************************************************************************************

        System.out.println("A peca inicial foi encontrada com o jogador " + jogadorDavez);
        jogadores.get(jogadorDavez).mostraPecas();

        // O jogador da vez eh o usuario que roda o programa, então nao precisa
        // de comunicacao em rede!
        if (jogadorDavez == 0) {
            controlador.mensagemJogadores("Sua vez de jogar!");
            controlador.gui.destravaTela();
        } else {
            controlador.servidorTcp.controlaJogadas(jogadorDavez);
        }

    }

    public void proximoJogador(Peca pecaJogada) {
        Jogador jogador = controlador.jogo.jogadores.get(jogadorDavez);
        if (jogador.listaDePecas.size() == 0) {
            // jogadorDavez ganhou!
            // 1- Calcular pontuacao!
            // 2- Incrementar pontuacao dele e do outro membro da dupla!
            // 3- Finalizar jogo e avisar todos os outros jogadores!
            int pontos;
            
            removePecaJogo(pecaJogada);
            
            Peca pEsq = controlador.jogo.pecasJogo.get(0);
            Peca pDir = controlador.jogo.pecasJogo.get(controlador.jogo.pecasJogo.size() - 1);

            if (pecaJogada.ladoE == pecaJogada.ladoD) { // Eh carroca!
                if ((pecaJogada.ladoD == pEsq.ladoE) && (pecaJogada.ladoE == pDir.ladoD))
                    pontos = 4;
                else
                    pontos = 2;
            }
            else { // Nao eh carroca!
                if ((pecaJogada.ladoD == pEsq.ladoE) && (pecaJogada.ladoE == pDir.ladoD))
                    pontos = 3;
                else
                    pontos = 1;
            }

            // Incrementando pontos da dupla!
            if (jogadorDavez % 2 == 0) { // Eh par!
                jogador = controlador.jogo.jogadores.get(0);
                jogador.pontos = jogador.pontos + pontos;
                jogador = controlador.jogo.jogadores.get(2);
                jogador.pontos = jogador.pontos + pontos;
//******************************************************************************************
                // Aqui tem que avisar os jogadores do incremento de pontos!!!
            }
            else { // Eh IMPAR!
                jogador = controlador.jogo.jogadores.get(1);
                jogador.pontos = jogador.pontos + pontos;
                jogador = controlador.jogo.jogadores.get(3);
                jogador.pontos = jogador.pontos + pontos;                
            }
            
            // Finalizar jogo e avisar 
            
        }
        else {
        
            // Proximo jogador!
            jogadorDavez = (jogadorDavez + 1) % maxJogadores;
            if (jogadorDavez == 0) {
                controlador.mensagemJogadores("Sua vez de jogar!");
                controlador.gui.destravaTela();
            } else
                controlador.servidorTcp.controlaJogadas(jogadorDavez);
        }
    }

    private int procuraJogadorInicial() {
        int jogadorDavez;
        int x = 6;
        Peca pecaProcurada = new Peca(x, x); // Procurar a peca 6, depois a 5, depois ...
        boolean estaNasDisponiveis = false;

        // Procurar onde esta a maior peca:
        // Pode estar na fila de pecasDisponiveis ou com um dos jogadores
        while (!estaNasDisponiveis) {
            for (int i = 0; i < pecasDisponiveis.size(); i++) {
                if (pecasDisponiveis.get(i).ehIgual(pecaProcurada)) {
                    estaNasDisponiveis = true;
                }
            }
            if (estaNasDisponiveis) {
                x--; // Tentar com a proxima menor peca (5, 4, ...)
                pecaProcurada = new Peca(x, x);
                estaNasDisponiveis = false;
            } else {
                estaNasDisponiveis = true;
            }
        }

        // Achamos a peca inicial!
        System.out.println("Peca inicial:" + pecaProcurada.toString());

        // Descobrir com qual jogador esta a peca inicial!
        for (int i = 0; i < jogadores.size(); i++) {
            if (jogadores.get(i).procura(pecaProcurada) != -1) // Jogador (i) tem a peca inicial!
            {
                return i;
            }
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
        for (i = 0; i < pecasJogo.size(); i++) {
            System.out.print(pecasJogo.get(i).toString());
        }
        System.out.println("\nTotal: " + i + " pecas.");
    }

    public ArrayList<Peca> geraPecas() {
        //esse método retorna um array com todas a pecas possíveis no dominó
        ArrayList<Peca> pecas = new ArrayList<>();
        Peca peca;
        for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 6; j++) {
                if (j >= i) {
                    peca = new Peca(i, j);
                    pecas.add(peca);
                }
            }
        }
        return pecas;
    }
    
    public boolean jogadaValida(Jogador jogador, Peca peca, String lado) {
        boolean aceitou = false;
        
        if (lado.equals("Esquerdo")) lado = "esq";
        if (lado.equals("Direito")) lado = "dir";

        Peca pEsq = controlador.jogo.pecasJogo.get(0);
        Peca pDir = controlador.jogo.pecasJogo.get(controlador.jogo.pecasJogo.size() - 1);

        System.out.println("Peca da esquerda: " + pEsq);
        System.out.println("Peca da direita: " + pDir);
        System.out.println("Peca desejada: " + peca);
        System.out.println("Lado: "+lado);
        if (lado.equals("esq")) {
            System.out.println("Tentando encaixar do lado esquerdo!");
            // Tentar encaixar do lado ESQUERDO do jogo atual
            if (peca.ladoE == pEsq.ladoE) {
                // Pode encaixar, mas tem que inverter a peca!
                peca.inverter();
                avisarOutrosJogadores(jogador, peca, lado); // AQUI TEMOS QUE AVISAR OS OUTROS JOGADORES DE UMA JOGADA!
                return true;
            }
            else if (peca.ladoD == pEsq.ladoE) {
                // Pode encaixar perfeitamente!
                avisarOutrosJogadores(jogador, peca, lado); // AQUI TEMOS QUE AVISAR OS OUTROS JOGADORES DE UMA JOGADA!
                return true;
            }
        }
        
        if (lado.equals("dir")) {
            // Tentar encaixar do lado DIREITO do jogo atual
            System.out.println("Tentando encaixar do lado direito!");
            if (peca.ladoE == pDir.ladoD) {
                // Pode encaixar perfeitamente!
                avisarOutrosJogadores(jogador, peca, lado); // AQUI TEMOS QUE AVISAR OS OUTROS JOGADORES DE UMA JOGADA!
                return true;
            } else if (peca.ladoD == pDir.ladoD) {
                // Pode encaixar, mas tem que inverter a peca!
                peca.inverter();
                avisarOutrosJogadores(jogador, peca, lado); // AQUI TEMOS QUE AVISAR OS OUTROS JOGADORES DE UMA JOGADA!
                return true;
            }
        }

        return aceitou;
    }

    // Uma jogada foi feita por um jogador. Avisar outros usuarios!
    public void avisarOutrosJogadores(Jogador jogadorJogou, Peca peca, String lado) {
        Jogador jogador;
        
        System.out.println("Avisar outros jogadores!!!");
        for (int i = 0; i < controlador.jogo.maxJogadores; i++) {
            jogador = controlador.jogo.jogadores.get(i);
            if (jogador.numJogador == 0) {
                if(lado.equals("dir")){
                    controlador.jogo.pecasJogo.add(peca);
                }else{
                    controlador.jogo.pecasJogo.add(0, peca);
                }
                
            } else if (jogador.numJogador != jogadorJogou.numJogador) {
                try {
                    String jogada = "jogada " + peca.toString() +" "+ lado;
                    System.out.println("Jogada: '" + jogada + "'");
                    jogador.output.writeObject(jogada);
                    jogador.output.flush();

                } catch (Exception e) {
                    System.out.println("Nao consegui avisar os jogadores da jogada de '" + jogador.nome + "'!");
                }
            }

        }

    }

}
