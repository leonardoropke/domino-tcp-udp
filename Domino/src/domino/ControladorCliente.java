
package domino;

import gui.jFrame;
import rede.ClienteTCP;
import rede.ClienteUDP;


/**
 *
 * @author Carlos
 */
public class ControladorCliente {
    public JogoCliente jogo;
    public jFrame gui;
    public ClienteTCP clienteTcp;
    ClienteUDP clienteUdp;
 
    public ControladorCliente(jFrame aThis) {
        gui = aThis;
    }
    
    public void alertaUsuario(String msg) {
            gui.alertaUsuario(msg);
    }
   
    // Criar um novo jogo como cliente
    public void novoJogo (String ip, int porta, String nomeJogador) {
        // Conectar-se ao servidor e esperar a resposta!
        jogo = new JogoCliente(nomeJogador, this);

        clienteTcp = new ClienteTCP(this);
        clienteTcp.conecta(ip, porta, nomeJogador);
        gui.adicionaMsg("Conectado ao servidor "+clienteTcp.ip+" !");
        gui.adicionaMsg("Esperando inicio de jogo...");
        clienteTcp.recebeComandos();
        
    }
    
}
