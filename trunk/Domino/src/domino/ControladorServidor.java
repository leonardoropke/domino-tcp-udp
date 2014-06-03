package domino;

import gui.jFrame;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rede.ServidorTCP;
import rede.ServidorUDP;

/**
 *
 * @author Carlos
 */
public class ControladorServidor {

    public JogoServidor jogo;
    int njogadores;
    public jFrame gui;
    public ServidorTCP servidorTcp;
    public ServidorUDP servidorUdp;

    public ControladorServidor(jFrame aThis) {
        gui = aThis;
    }

    public void alertaUsuario(String msg) {
            gui.alertaUsuario(msg);
    }

    public void adicionaMsg (String msg) {
        for (int i=0; i<=njogadores-1; i++) {
            if (i == 0)
                gui.adicionaMsg(msg);
            else
                servidorTcp.adicionaMsg (jogo.jogadores.get(i), msg);
        }
    }

    public void atualizaTela() {
        gui.mostraJogo(jogo.pecasJogo);
        gui.mostraPecasDisponiveis(jogo.pecasDisponiveis);
        gui.atualizaRodada(jogo.rodada);

    }

    public void atualizaTabelaJogadores(ArrayList<Jogador> jogadores) {
        gui.atualizaTabelaJogadores(jogadores);
    }

    public void mostraJogoAtual(ArrayList<Peca> pecas, ArrayList<Peca> pecasDisponiveis, ArrayList<Jogador> jogadores, int rodada) {
        gui.mostraJogo(pecas);
        gui.mostraPecasDisponiveis(pecasDisponiveis);
        gui.atualizaRodada(rodada);

    }

    public void novoJogo(int nJogadores) {

        this.njogadores = nJogadores;
//        this.njogadores = 2;

        jogo = new JogoServidor(njogadores, this);

        // O metodo adicionaJogadores() roda numa thread para permitir que a
        // interface grafica continue funcionando enquanto espera pelos outros
        // jogadores se conectarem.
        // A cada nova conexao, ela executa atualizaNovoJogador()
        servidorTcp = new ServidorTCP(12345, this);
        servidorTcp.adicionaJogadores(njogadores);
    }

    // Cada vez que um novo jogador se conectar, atualizar o jogo e a interface.
    // Se o numero maximo de jogadores for alcancado, distribuir as pecas e
    // comecar o jogo!
    public void atualizaNovoJogador(Jogador jogador) {
        atualizaTabelaJogadores(jogo.jogadores);
        mostraJogoAtual(jogo.pecasJogo, jogo.pecasDisponiveis, jogo.jogadores, jogo.rodada);
        
        //String ip = servidorTcp.server.getInetAddress().getHostAddress();
        String ip="";
        try {
            ip = servidorTcp.server.getInetAddress().getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ControladorServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        if (jogo.jogadores.size() == 1)
            gui.adicionaMsg("Servidor rodando! IP: "+ ip+" Porta: "+12345);        
        
        gui.adicionaMsg("Jogador '" + jogador.nome + "' conectado!");

        // Temos todos os jogadores! Iniciar o jogo!
        if (jogo.jogadores.size() == njogadores) {
            comecaRodada();
        }

    }

    // Esse metodo eh chamado a cada nova rodada!
    public void comecaRodada() {
        jogo.preparaJogo(); // Distribui pecas entre os jogadores...
        gui.mostraPecasJogador(jogo.jogadores.get(0).listaDePecas);
        servidorTcp.enviaNomesJogadores();
        servidorTcp.enviaPecasJogadores(); // Enviar as pecas para os jogadores...

        atualizaTabelaJogadores(jogo.jogadores); // Atualiza interface grafica...
        mostraJogoAtual(jogo.pecasJogo, jogo.pecasDisponiveis, jogo.jogadores, jogo.rodada);

        adicionaMsg("Jogo iniciado!");
        jogo.iniciar(); // Comecar!

    }
    
}
