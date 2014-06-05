package rede;

/**
 *
 * @author Carlos
 */
import domino.ControladorServidor;
import domino.Jogador;
import domino.Peca;
import java.io.IOException;
import java.net.ServerSocket;

public class ServidorTCP {

    public ServerSocket server; // socket de servidor     
    private ControladorServidor controlador;

    // configura a GUI
    public ServidorTCP(int porta, ControladorServidor aThis) {

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

    // Envia uma mensagem para o 'jogador', na caixa de mensagens do servidor
    public void adicionaMsg(Jogador jogador, String msg) {
        try {
            jogador.output.writeObject("msg " + msg);
            jogador.output.flush();
        } catch (Exception e) {
            System.out.println("Erro ao mandar mensagem para o jogador! (" + msg + ")");
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

        /*
         * if (controlador.jogo.jogadores.size() == 0)
         * controlador.adicionaMsg("Servidor rodando! IP: "+
         * controlador.servidorTcp.server.getInetAddress().getHostAddress()+"
         * Porta: "+12345);
         */

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

    // Envia uma peca comprada para um jogador
    public void enviaPecaComprada (Jogador jogador, Peca peca) {
        try {
            System.out.println("Mandando peca "+peca.toString()+" para o jogador "+jogador.nome+" !");
            jogador.output.writeObject("pega " + peca.toString());
            jogador.output.flush();

        } catch (Exception e) {
            System.out.println("Erro ao mandar peca comprada para o jogador!");
        }
    }

    // Avisar para os outros jogadores a quantidade de pecas disponiveis
    // (exceto para o jogador numjogador
    public void enviaPecasDisponiveisJogadores(int numjogador) {
        try {
            Jogador jogador;

            for (int i = 1; i <= controlador.jogo.jogadores.size() - 1; i++) {
                jogador = controlador.jogo.jogadores.get(i);
                if (jogador.numJogador != numjogador) {
                    jogador.output.writeObject("ndisponiveis " + controlador.jogo.pecasDisponiveis.size());
                    jogador.output.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao mandar o numero de pecas disponiveis para o jogador!");
        }
    }

    public void enviaPecasJogadores() {
        Jogador jogador;
        for (int i = 0; i < controlador.jogo.jogadores.size(); i++) {
            jogador = controlador.jogo.jogadores.get(i);
            if (i != 0) jogador.enviaPecaCliente();
        }
    }

    public void enviaNomesJogadores() {
        try {
            String nomes = "";
            Jogador jogador;
            for (int i = 0; i <= controlador.jogo.jogadores.size() - 1; i++) {
                nomes += controlador.jogo.jogadores.get(i).nome + " ";
            }

            for (int i = 1; i <= controlador.jogo.jogadores.size() - 1; i++) {
                jogador = controlador.jogo.jogadores.get(i);
                jogador.output.writeObject("jogadores " + jogador.numJogador + " " + nomes);
                jogador.output.flush();
            }
        } catch (Exception e) {
            System.out.println("Erro ao mandar fim de rodada o jogador!");
        }
    }

    // Quando mandar este comando, o cliente deve destravar a tela, permitir a jogada e
    // travar a tela novamente
    public void controlaJogadas(int jogadorDavez) {
        Jogador jogador;

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

            if (recebido.contains("pular")) {
                controlador.atualizaTela();
                controlador.jogo.proximoJogador(new Peca(0, 0));
            } else if (recebido.contains("comprar")) { // Tratar opcao de compra de pecas
                System.out.println("Jogador '" + jogador.nome + "' quer comprar pecas!");
                //System.out.println("recebido: '"+recebido+"'");
                
                String indexPecaString = recebido.substring(recebido.indexOf(" ")+1);
                
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
                if (controlador.jogo.jogadaValida(peca, lado, true)) {

                    System.out.println("Jogada valida!");
                    
                    // AQUI TEM QUE ATUALIZAR OS OUTROS JOGADORES DESSA JOGADA!
                    avisaJogada(jogador, peca, lado);

                    // Atualizar tabela de pecas
                    controlador.jogo.atualizaPecasJogador(jogador, peca);

                    controlador.jogo.proximoJogador(peca);
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

    public void avisaFimRodada(Jogador jogador, int pontosA, int pontosB) {
        try {
            jogador.output.writeObject("fimrodada " + pontosA + " " + pontosB);
            jogador.output.flush();
        } catch (Exception e) {
            System.out.println("Erro ao mandar fim de rodada o jogador!");
        }
    }

    public void avisaFimJogo(Jogador jogador, int pontosA, int pontosB) {
        try {
            jogador.output.writeObject("fimjogo " + pontosA + " " + pontosB);
            jogador.output.flush();
        } catch (Exception e) {
            System.out.println("Erro ao mandar fim de jogo para o jogador!");
        }

    }

    public void avisaJogada(Jogador jogador, Peca peca, String lado) {
        for (int i = 0; i <= controlador.jogo.jogadores.size() - 1; i++) {
            if (i == 0) {
                if (lado.equals("esq")) {
                    controlador.jogo.pecasJogo.add(0, peca);
                }
                if (lado.equals("dir")) {
                    controlador.jogo.pecasJogo.add(peca);
                }
                controlador.atualizaTela();
            } else {
                try {
                    if (jogador.numJogador != i) {
                        System.out.println("Peca: "+ peca.toString());
                        String str = "jogada " + peca.toString() + " " + lado + " " + jogador.numJogador;
                        System.out.println("str: '"+str+"'");
                        controlador.jogo.jogadores.get(i).output.writeObject(str);
                        controlador.jogo.jogadores.get(i).output.flush();
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao mandar jogada para o jogador!");
                }

            }
        }
        controlador.jogo.jogadores.get(jogador.numJogador).removePeca(peca);
    }
}
