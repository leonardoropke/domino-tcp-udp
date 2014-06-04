/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rede;

/**
 *
 * @author Carlos
 */
// Cliente que lê e exibe as informações enviadas a partir de um Servidor.
import domino.ControladorCliente;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteTCP {

    public ObjectOutputStream output;
    public ObjectInputStream input;
    private String recebido = "";
    private Socket client;
    private ControladorCliente controlador;
    boolean podeler = false;
    public String ip;

    // inicializa chatServer e configura a GUI
    public ClienteTCP(ControladorCliente aThis) {
        this.controlador = aThis;

    }

    public void conecta(final String ip, final int porta, final String nomeJogador) {
        this.ip = ip;

        try {
            client = new Socket(InetAddress.getByName(ip), porta);
            System.out.println("Connected to: "
                    + client.getInetAddress().getHostName());

            output = new ObjectOutputStream(client.getOutputStream());
            output.flush(); // esvazia buffer de saída enviar as informações de cabeçalho

            // configura o fluxo de entrada para objetos
            input = new ObjectInputStream(client.getInputStream());

            System.out.println("Got I/O streams\n");

            output.writeObject(nomeJogador);
            output.flush(); // esvazia os dados para saída      


        } catch (Exception e) {
        }

    }

    public void recebeComandos() {

        new Thread() {

            @Override
            public void run() {

                try {
                    recebido = (String) input.readObject();
                    processaComandos();

                } catch (Exception ex) {
                    System.out.println("Erro ao ler comando do servidor!");
                    System.out.println(ex);
                }

            }
        }.start();

    }

    public void processaComandos() {

        String comando = "";
        boolean fim = false;

        String temp = recebido;

        System.out.println("Recebido do servidor: " + recebido);
        if (temp.equals("jogar")) {
            comando = "jogar";
        } else {
            comando = temp.substring(0, temp.indexOf(" "));
        }
        System.out.println("Comando: '" + comando + "'");
        // 1o comando recebido: 'receber peca1 peca2 peca3...'
        switch (comando) {
            case "jogadores": // OK
                System.out.println("Comando jogadores!!");
                System.out.println("Numero de jogador: " + Integer.parseInt(temp.substring(10, 11)));
                controlador.jogo.recebeJogadores(Integer.parseInt(temp.substring(10, 11)), temp.substring(11, temp.length()));
                break;
            case "receber": // OK
                System.out.println("Comando receber!");
                controlador.jogo.recebePecas(temp.substring(temp.indexOf(" ") + 1, temp.length()));
                break;
            case "ndisponiveis": // OK
                System.out.println("Comando ndisponiveis!");
                controlador.jogo.pecasdisponiveis = Integer.parseInt(temp.substring(temp.indexOf(" ") + 1, temp.length()));
                controlador.gui.mostraPecasDisponiveis(controlador.jogo.pecasdisponiveis);
                break;
            case "pega": // pega [4:2]
                System.out.println("Comando pega!");
                controlador.jogo.recebePecaComprada (temp.substring(temp.indexOf(" ")+1));
                break;
            case "msg": // OK
                System.out.println("Comando mensagem!");
                controlador.gui.adicionaMsg(temp.substring(temp.indexOf(" ") + 1, temp.length()));
                break;
            case "jogada": // Recebe uma jogada no formato 'jogada [3:2] esq
                System.out.println("Comando jogada!");
                controlador.jogo.recebeJogada(temp.substring(temp.indexOf(" ") + 1, temp.length()));
                break;
            case "jogar":
                System.out.println("Comando jogar!");
                controlador.alertaUsuario("Sua vez de jogar!");
                controlador.gui.destravaTela();

                break;
            case "fimdejogo":
                System.out.println("Comando fimdejogo!");
                fim = true;
                break;

        }

        System.out.println("Temp no final: '" + temp + "'");
        recebeComandos();


    }
    
    // Envia uma peca comprada para um jogador
    public void compraPeca (int index) {
        try {
            output.writeObject("comprar " + index);
            output.flush();

        } catch (Exception e) {
            System.out.println("Erro ao pedir pra comprar peca do servidor!");
        }
    }


    // fecha os fluxos e o socket
    private void closeConnection() {
        System.out.println("Closing connection");

        try {
            output.close();
            input.close();
            client.close();
        } catch (IOException ioException) {
        }
    }

    public void pularJogador() {
        try {
            output.writeObject("pular");
            output.flush();

        } catch (Exception e) {
            System.out.println("Erro ao pular jogador!");
        }
        
    }
}
