
package domino;

import java.util.ArrayList;
import java.util.Collections;

public class JogoCliente {
    ControladorCliente controlador;
    ArrayList<Peca> pecasJogo = new ArrayList<> ();
    ArrayList<Peca> pecasDisponiveis = new ArrayList<> ();
    ArrayList<Jogador> jogadores = new ArrayList<> ();
    
    Cliente cliente;
    int maxJogadores;
    int rodada = 1;
    String transporte;
    boolean jogando = false;
    
    public JogoCliente (String transporte, int maxJogadores, ControladorCliente cont) {
        this.transporte = transporte;

        this.maxJogadores = maxJogadores;
        this.controlador = cont;
    }


    public void conectar (String ip, int porta, String nomeJogador) {
        
        // Criar aqui a conexao com o servidor!
        cliente = new Cliente (ip, porta, nomeJogador);
        
        cliente.conectar();
        
    }
    
}
