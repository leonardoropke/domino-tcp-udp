package rede;

/**
 *
 * @author Carlos
 */
import domino.ControladorServidor;
import domino.Jogador;
import domino.Peca;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;

public class ServidorTCP {

    private ServerSocket server; // socket de servidor     

    private int counter = 1; // contador do número de conexões
    private int porta = 0;

    private ControladorServidor controlador;

    // configura a GUI
    public ServidorTCP(int porta, ControladorServidor aThis) {
        this.porta = porta;
        this.controlador = aThis;

        try {
            server = new ServerSocket(porta);
        } catch (IOException ex) {
            System.out.println("Erro ao abrir porta " + porta + " !");
        }

    }

    // fecha os fluxos e o socket
    private void closeConnection(Jogador jogador) {
        System.out.println("\nTerminando conexao com jogador '" + jogador.nome + "' !");

        try {
            jogador.output.close(); // fecha o fluxo de saída
            jogador.input.close(); // fecha o fluxo de entrada  
            jogador.conexao.close(); // fecha o socket
        } // fim do try
        catch (IOException ioException) {
            System.out.println("Nao consegui fechar a conexao!");
        }
    }

    public void adicionaJogadores(final int njogadores) {
        final String nomeJogador = "";
        Jogador jogador;

        controlador.gui.adicionaMsg("Esperando conexao de jogadores...");

        // Adicionando jogador 0 (o que roda o programa servidor)
        String nomeJogador0 = controlador.gui.getNomeJogador();
        jogador = new Jogador(nomeJogador0, null, 0);
        controlador.jogo.adicionaJogador(jogador);
        controlador.atualizaNovoJogador(jogador);

        new Thread() {
            @Override
            public void run() {
                try {

                    // i<=3
                    for (int i = 1; i <= njogadores - 1; i++) {
                        //nomeJogador = "Jogador"+i;
                        try // espera a conexão, cria Player, inicia o executável
                        {

                            Jogador jogador2 = new Jogador("", server.accept(), i);
                            jogador2.recebeNome();
                            controlador.jogo.adicionaJogador(jogador2);
                            controlador.atualizaNovoJogador(jogador2);

                        } catch (IOException ioException) {
                            System.out.println("Bug!");
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }.start();

    }

    public void enviaPecasJogadores() {
        Jogador jogador;
        for (int i = 0; i < controlador.jogo.jogadores.size(); i++) {
            jogador = controlador.jogo.jogadores.get(i);
            jogador.enviaPecaCliente();
        }
    }

    // Quando mandar este comando, o cliente deve destravar a tela, permitir a jogada e
    // travar a tela novamente
    public void controlaJogadas(int jogadorDavez) {
        String recebido;
        String comando;
        Jogador jogador;
        Peca peca;
        String pecaString;
        String lado;

        jogador = controlador.jogo.jogadores.get(jogadorDavez);

        mandarJogar(jogador);

    }

    private void mandarJogar(Jogador jogador) {
        try {
            jogador.output.writeObject("jogar");
            jogador.output.flush();
        } catch (Exception e) {
            System.out.println("Erro ao mandar o jogador jogar!");
        }

        receberJogada(jogador);
    }

    private void receberJogada(Jogador jogador) {
        String recebido;
        String comando;

        Peca peca;
        String pecaString;
        String lado;

        try {
             // Mandamos o cliente jogar. Agora precisamos esperar 2 tipos de resposta:
            // 1- O cliente quer comprar pecas: enviar peca desejada para ele (e comunicar os outros jogadores)
            // 2- O cliente fez uma jogada: receber pecas (e comunicar os outros jogadores)

            recebido = (String) jogador.input.readObject();

            if (recebido.contains("comprar")) { // Tratar opcao de compra de pecas
                System.out.println("Jogador '" + jogador.nome + "' quer comprar pecas!");
                comando = recebido.substring(0, recebido.indexOf(" "));
                String indexPecaString = recebido.substring(recebido.indexOf(" "));
                int indexPecaDisponivel = Integer.parseInt(indexPecaString);
                controlador.jogo.compraPeca(jogador, indexPecaDisponivel);

            } else if (recebido.contains("jogar")) { // Tratar jogada
                System.out.println("Jogador '" + jogador.nome + "' quer jogar!");
                // O comando deve vir no formato: 'jogar [x:x] esq'
                comando = recebido.substring(0, recebido.indexOf(" "));
                 //System.out.println("Comando: '"+comando+"'");

                pecaString = recebido.substring(recebido.indexOf("["), recebido.indexOf("]") + 1);
                //System.out.println("pecaString: '"+pecaString+"'");
                int esq, dir;
                esq = Integer.parseInt(pecaString.substring(1, 2));
                dir = Integer.parseInt(pecaString.substring(3, 4));
                peca = new Peca(esq, dir);
                 //System.out.println("Peca: '"+peca.toString()+"'");

                lado = recebido.substring(recebido.indexOf("]") + 2, recebido.length());

                System.out.println("Lado: '" + lado + "'");

                // Aqui ja sabemos a peca e o lado. Validar a jogada!
                if (jogadaValida(jogador, peca, lado)) {
                    System.out.println("Jogada valida!");
                    controlador.atualizaTela();
                    controlador.jogo.proximoJogador();
                    return;
                } else { // Jogada nao eh valida! Avisar o jogador e esperar outra jogada!
                    System.out.println("Jogada invalida! Tente outra vez!");
                    jogador.output.writeObject("jogadanegada");
                    jogador.output.flush();
                    mandarJogar(jogador); // Isso faz o jogo entrar num loop de esperar uma jogada valida do cliente
                }
            }

        } catch (Exception ex) {
            System.out.println("Deu bug!" + ex);
        }
    }

    private boolean jogadaValida(Jogador jogador, Peca peca, String lado) {
        boolean aceitou = false;
        Peca pEsq = controlador.jogo.pecasJogo.get(0);
        Peca pDir = controlador.jogo.pecasJogo.get(controlador.jogo.pecasJogo.size() - 1);

        System.out.println("Peca da esquerda: " + pEsq);
        System.out.println("Peca da direita: " + pDir);
        System.out.println("Peca desejada: " + peca);

        // Tentar encaixar do lado ESQUERDO do jogo atual
        if (peca.ladoE == pEsq.ladoE) {
            // Pode encaixar, mas tem que inverter a peca!
            peca.inverter();
            controlador.jogo.pecasJogo.add(0, peca);
            avisarOutrosJogadores(jogador, peca, lado); // AQUI TEMOS QUE AVISAR OS OUTROS JOGADORES DE UMA JOGADA!
            return true;
        } else if (peca.ladoD == pEsq.ladoE) {
            // Pode encaixar perfeitamente!
            controlador.jogo.pecasJogo.add(0, peca);
            avisarOutrosJogadores(jogador, peca, lado); // AQUI TEMOS QUE AVISAR OS OUTROS JOGADORES DE UMA JOGADA!
            return true;
        }

        // Tentar encaixar do lado DIREITO do jogo atual
        if (peca.ladoE == pDir.ladoD) {
            // Pode encaixar perfeitamente!
            controlador.jogo.pecasJogo.add(peca);
            avisarOutrosJogadores(jogador, peca, lado); // AQUI TEMOS QUE AVISAR OS OUTROS JOGADORES DE UMA JOGADA!
            return true;
        } else if (peca.ladoD == pDir.ladoD) {
            // Pode encaixar, mas tem que inverter a peca!
            peca.inverter();
            controlador.jogo.pecasJogo.add(peca);
            avisarOutrosJogadores(jogador, peca, lado); // AQUI TEMOS QUE AVISAR OS OUTROS JOGADORES DE UMA JOGADA!
            return true;
        }

        return aceitou;
    }

    // Uma jogada foi feita por um jogador. Avisar outros usuarios!
    private void avisarOutrosJogadores(Jogador jogadorJogou, Peca peca, String lado) {
        Jogador jogador;
        System.out.println("Avisar outros jogadores!!!");
        for (int i = 0; i <= controlador.jogo.maxJogadores; i++) {
            jogador = controlador.jogo.jogadores.get(i);
            if (jogador.numJogador == 0) {
                if(lado == "dir"){
                    controlador.jogo.pecasJogo.add(peca);
                }else{
                    controlador.jogo.pecasJogo.add(0, peca);
                }
                
            } else if (jogador.numJogador != jogadorJogou.numJogador) {
                try {
                    String jogada = "jogada " + peca.toString() + lado;
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
