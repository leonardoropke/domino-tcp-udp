
package domino;

import java.util.ArrayList;
import rede.ClienteTCP;

public class JogoCliente {
    ControladorCliente controlador;
    ArrayList<Peca> pecasJogo = new ArrayList<> ();
    ArrayList<Peca> pecasDisponiveis = new ArrayList<> ();
    ArrayList<Jogador> jogadores = new ArrayList<> ();
    
    ClienteTCP cliente;
    int rodada = 1;
    boolean jogando = false;
    
    public JogoCliente (ControladorCliente cont) {

        this.controlador = cont;
    }

    public void conectar (String ip, int porta, String nomeJogador) {
        
        // Criar aqui a conexao com o servidor!
        cliente = new ClienteTCP ("127.0.0.1");
        
//        cliente.conectar();
        
    }
    
}
