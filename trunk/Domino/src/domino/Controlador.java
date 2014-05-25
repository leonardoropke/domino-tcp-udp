
package domino;

/**
 *
 * @author Carlos
 */
public class Controlador {
    Jogo jogo;
    Jogador jogadores[];
    int njogadores;
 
    public Controlador () {
        
    }
    
    public void novoJogo (int nJogadores) {
  
        this.njogadores = nJogadores;
        jogo = new Jogo("tcp", njogadores);
        jogadores = new Jogador[njogadores];

        jogo.misturaPecas();

        Jogador jogador1 = new Jogador("Carlos");
        
        jogador1.recebePeca(new Peca(1,5));
        jogador1.recebePeca(new Peca(2,7));
        jogador1.recebePeca(new Peca(4,4));

        System.out.println("Pecas do jogador 1:");
        jogador1.mostraPecas();
        
        jogador1.removePeca(new Peca(1,5));
        System.out.println("Pecas do jogador 1 depois de remover a [1:5]...");
        jogador1.mostraPecas();
//        jogo.iniciar();
    }
}
