package domino;

import java.util.ArrayList;
import java.util.Collections;
import rede.ServidorTCP;

public class JogoServidor {

    ControladorServidor controlador;
    public ArrayList<Peca> pecasJogo = new ArrayList<>();
    public ArrayList<Peca> pecasDisponiveis = new ArrayList<>();
    public ArrayList<Jogador> jogadores = new ArrayList<>();
    int jogadorDavez;
    public int maxJogadores;
    int rodada = 1;
    int conta = 0;
    boolean jogando = false;

    public JogoServidor(int maxJogadores, ControladorServidor cont) {
        this.maxJogadores = maxJogadores;
        this.controlador = cont;
    }

    public void adicionaJogador(Jogador jogador) {
        jogadores.add(jogador);
        controlador.atualizaTabelaJogadores(jogadores);

    }

    // Um cliente pediu uma peca. Remover essa peca do array de pecas disponiveis,
    // enviar a peca para ele e avisar os outros jogadores e atualizar interface
    public void compraPeca(Jogador jogadorComprou, int index) {
        Jogador jogador;

        System.out.println("Jogador " + jogadorComprou.nome + "(" + jogadorComprou.numJogador + ") quer comprar a peca " + controlador.jogo.pecasDisponiveis.get(index));

        // Remover a peca da lista de pecas disponiveis
        Peca peca = pecasDisponiveis.remove(index);

        // Dar a peca para o jogador que pediu
        for (int i = 0; i <= controlador.jogo.jogadores.size() - 1; i++) {
            jogador = controlador.jogo.jogadores.get(i);
            System.out.println("Jogador: " + jogador.nome + " | " + jogador.numJogador);

            if (i == 0) {
                if (i == jogadorComprou.numJogador) { // Quem comprou foi o jogador 0
                    jogador.listaDePecas.add(peca);

                    // Atualizando a interface
                    controlador.gui.mostraPecasJogador(jogador.listaDePecas);

                    controlador.gui.alertaUsuario("Voce comprou a peca " + peca.toString() + " !");
                    controlador.gui.travaTela();
                }
            } else {
                if (jogador.numJogador == jogadorComprou.numJogador) {
                    System.out.println("O jogador que comprou foi: " + jogador.nome + "(" + jogador.numJogador + ")");
                    controlador.servidorTcp.enviaPecaComprada(jogador, peca);
                } else {
                    controlador.servidorTcp.enviaPecasDisponiveisJogadores(jogador.numJogador);
                }
            }
        }

        controlador.gui.mostraPecasDisponiveis(pecasDisponiveis.size());

        controlador.jogo.proximoJogador(peca);

    }

    public void removePecaJogo(Peca peca) {
        // Temos que procurar a posicao no array onde esta a peca selecionada...
        int local = procura(peca);
        if (local != -1) {
            pecasJogo.remove(local);
        } else {
            System.out.println("Nao consegui remover a peca: " + peca.toString() + "!!!");
        }
    }

    // Metodo pra procurar uma peca em 'pecasJogo'
    // Se encontrar, retornar a posicao da peca
    // Se NAO encontrar, retornar -1
    public int procura(Peca peca) {
        int i;
        for (i = 0; i < pecasJogo.size(); i++) {
            if ((pecasJogo.get(i).ladoE == peca.ladoE) && (pecasJogo.get(i).ladoD == peca.ladoD)) {
                return i;
            }
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
            for (int j = 0; j < (pecasJogo.size() - 4) / jogadores.size(); j++) { // 6 Pecas para cada jogador...
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
//        jogadorDavez = 0;
        //****************************************************************************************************

        System.out.println("A peca inicial foi encontrada com o jogador " + jogadorDavez);
        jogadores.get(jogadorDavez).mostraPecas();

        // O jogador da vez eh o usuario que roda o programa, então nao precisa
        // de comunicacao em rede!
        if (jogadorDavez == 0) {
            controlador.alertaUsuario("Sua vez de jogar!");
            controlador.gui.destravaTela();
        } else {

            controlador.servidorTcp.controlaJogadas(jogadorDavez);
        }

    }

    public void proximoJogador(Peca pecaJogada) {
        int pontosA = 0, pontosB = 0;
        
        Jogador jogador = controlador.jogo.jogadores.get(jogadorDavez);
        
        System.out.println("Jogador: "+jogador.nome+"("+jogador.numJogador+") | npecas: "+jogador.listaDePecas.size());

        if (jogador.listaDePecas.size() == 0) { // Acabou a rodada! jogadorDavez ganhou!
            
            System.out.println("Jogador "+jogador.nome+" acabou as pecas!");

            // 1- Calcular pontuacao!
            // 2- Incrementar pontuacao dele e do outro membro da dupla!
            // 3- Finalizar jogo e avisar todos os outros jogadores!
            int pontos;

            removePecaJogo(pecaJogada);

            // Calcular quantos pontos serao ganhos pela dupla vencedora
            Peca pEsq = controlador.jogo.pecasJogo.get(0);
            Peca pDir = controlador.jogo.pecasJogo.get(controlador.jogo.pecasJogo.size() - 1);
            if (pecaJogada.ladoE == pecaJogada.ladoD) { // Eh carroca!
                if ((pecaJogada.ladoD == pEsq.ladoE) && (pecaJogada.ladoE == pDir.ladoD)) {
                    pontos = 4;
                } else {
                    pontos = 2;
                }
            } else { // Nao eh carroca!
                if ((pecaJogada.ladoD == pEsq.ladoE) && (pecaJogada.ladoE == pDir.ladoD)) {
                    pontos = 3;
                } else {
                    pontos = 1;
                }
            }

            System.out.println("Calculando pontos...");
            
            // Incrementando pontos da dupla!
            if (jogadorDavez % 2 == 0) { // Eh par! Dupla A!
                jogador = controlador.jogo.jogadores.get(0);
                jogador.pontos = jogador.pontos + pontos;
                jogador = controlador.jogo.jogadores.get(2);
                jogador.pontos = jogador.pontos + pontos;
            } else { // Eh IMPAR! Dupla B!
                jogador = controlador.jogo.jogadores.get(1);
                jogador.pontos = jogador.pontos + pontos;
                jogador = controlador.jogo.jogadores.get(3);
                jogador.pontos = jogador.pontos + pontos;
            }

            pontosA = controlador.jogo.jogadores.get(0).pontos + controlador.jogo.jogadores.get(2).pontos;
            pontosB = controlador.jogo.jogadores.get(1).pontos + controlador.jogo.jogadores.get(3).pontos;
            
            System.out.println("Pontos A: "+pontosA);
            System.out.println("Pontos B: "+pontosB);

            acabouRodada (pontosA, pontosB);

        } else {
            conta++;

            // Checar se deu empate
            Jogador jogadortemp;
            int empatou = 0;
            for (int i = 0; i < jogadores.size() - 1; i++) {
                jogadortemp = jogadores.get(i);
                if (!podeJogar(jogadortemp)) {
                    empatou++;
                }
            }
            if (empatou == 4) {
                acabouRodada (pontosA, pontosB);
            }

            // Proximo jogador!
            jogadorDavez = (jogadorDavez + 1) % maxJogadores;
            controlador.adicionaMsg("Esta na vez do jogador " + jogadores.get(jogadorDavez).nome + "!");
            if (jogadorDavez == 0) {
                controlador.alertaUsuario("Sua vez de jogar!");
                controlador.gui.destravaTela();
            } else {
                
                                        new Thread() {
                            @Override
                            public void run() {
                                
                                try {
                                    controlador.servidorTcp.controlaJogadas(jogadorDavez);
                                } catch (Exception e) {
                                    System.out.println(e);
                                }

                            }
                        }.start();

            }
        }
    }

    public void acabouRodada(int pontosA, int pontosB) {
        // Finalizar jogo e avisar
        Jogador jogador;
        
        System.out.println("Avisando todos os jogadores do fim da rodada!");
        
        for (int i = 0; i <= maxJogadores - 1; i++) {
            if (i == 0) {
                System.out.println("Avisando servidor!");
                fimdeRodada(pontosA, pontosB);

            } else {
                jogador = controlador.jogo.jogadores.get(i);
                
                System.out.println("Avisando jogador '"+jogador.nome+"' ...");
                
                controlador.servidorTcp.avisaFimRodada(jogador, pontosA, pontosB);
            }
        }

        rodada++;
        if ((rodada == 14) || (pontosA >= 7) || (pontosB >= 7)) {
            fimdeJogo(pontosA, pontosB);
        } else {
            controlador.comecaRodada(); // Comeca nova rodada            
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

    public boolean jogadaValida(Peca peca, String lado, boolean girar) {
        boolean aceitou = false;

        if (lado.equals("Esquerdo")) {
            lado = "esq";
        }
        if (lado.equals("Direito")) {
            lado = "dir";
        }

        if (controlador.jogo.pecasJogo.size() == 0) {
            return true;
        }

        Peca pEsq = controlador.jogo.pecasJogo.get(0);
        Peca pDir = controlador.jogo.pecasJogo.get(controlador.jogo.pecasJogo.size() - 1);
/*
        System.out.println("Peca da esquerda: " + pEsq);
        System.out.println("Peca da direita: " + pDir);
        System.out.println("Peca desejada: " + peca);
        System.out.println("Lado: " + lado);
*/
        if (lado.equals("esq")) {
            System.out.println("Tentando encaixar do lado esquerdo!");
            // Tentar encaixar do lado ESQUERDO do jogo atual
            if (peca.ladoE == pEsq.ladoE) {
                // Pode encaixar, mas tem que inverter a peca!
                if (girar) {
                    peca.inverter();
                }
//                avisarOutrosJogadores(jogador, peca, lado);
                return true;
            } else if (peca.ladoD == pEsq.ladoE) {
                // Pode encaixar perfeitamente!
//                avisarOutrosJogadores(jogador, peca, lado);
                return true;
            }
        }

        if (lado.equals("dir")) {
            // Tentar encaixar do lado DIREITO do jogo atual
            System.out.println("Tentando encaixar do lado direito!");
            if (peca.ladoE == pDir.ladoD) {
                // Pode encaixar perfeitamente!
//                avisarOutrosJogadores(jogador, peca, lado);
                return true;
            } else if (peca.ladoD == pDir.ladoD) {
                // Pode encaixar, mas tem que inverter a peca!
                if (girar) {
                    peca.inverter();
                }
//                avisarOutrosJogadores(jogador, peca, lado);
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
                if (lado.equals("dir")) {
                    controlador.jogo.pecasJogo.add(peca);
                } else {
                    controlador.jogo.pecasJogo.add(0, peca);
                }

            } else if (jogador.numJogador != jogadorJogou.numJogador) {
                try {
                    String jogada = "jogada " + peca.toString() + " " + lado;
                    System.out.println("Jogada: '" + jogada + "'");
                    jogador.output.writeObject(jogada);
                    jogador.output.flush();

                } catch (Exception e) {
                    System.out.println("Nao consegui avisar os jogadores da jogada de '" + jogador.nome + "'!");
                }
            }

        }

    }

    // Fim de rodada! Avisar apenas o jogador 0 aqui!
    private void fimdeRodada(int pontosA, int pontosB) {
        controlador.gui.alertaUsuario("Fim de rodada!");
        controlador.gui.atualizaPlacar(pontosA, pontosB);
        if (pontosA > pontosB) {
            controlador.gui.alertaUsuario("Dupa A ganhou!!");
        }
        else if (pontosA < pontosB) {
            controlador.gui.alertaUsuario("Dupa B ganhou!!");
        }
        else {
            controlador.gui.alertaUsuario("DEU EMPATE!");
        }

    }

    // Acabou o jogo! Avisar todos os jogadores!
    private void fimdeJogo(int pontosA, int pontosB) {
        for (int i = 0; i <= maxJogadores - 1; i++) {
            if (i == 0) {
                controlador.gui.atualizaPlacar(pontosA, pontosB);

                if (pontosA > pontosB) {
                    controlador.gui.alertaUsuario("Fim de jogo! Dupla A ganhou!");
                } else {
                    controlador.gui.alertaUsuario("Fim de jogo! Dupla B ganhou!");
                }
            } else {
                controlador.servidorTcp.avisaFimJogo(controlador.jogo.jogadores.get(i), pontosA, pontosB);
            }
        }

    }

    public void atualizaPecasJogador(Jogador jogador, Peca peca) {
        controlador.jogo.jogadores.get(jogador.numJogador).removePeca(peca);
        controlador.gui.atualizaTabelaJogadores(controlador.jogo.jogadores);
    }

    private boolean podeJogar(Jogador jogadortemp) {
        for (int i = 0; i < jogadortemp.listaDePecas.size(); i++) {
            if ((jogadaValida(jogadortemp.listaDePecas.get(i), "esq", false))
                    || (jogadaValida(jogadortemp.listaDePecas.get(i), "dir", false))) {
                return true;
            }
        }
        return false;
    }
}
