
package domino;

/**
 *
 * @author Carlos
 */
public class Controlador {
    Jogo jogo;
    int njogadores;
 
    public Controlador () {
        
    }
    
    public void novoJogo (int nJogadores) {
  
        this.njogadores = nJogadores;
        jogo = new Jogo("tcp", njogadores);
        
        // Adicionando jogadores.
        // Quando for implementar o TCP, ler essa informaçao dos clientes
        for (int i=0; i<njogadores; i++) {
            Jogador jogador = new Jogador("Jogador"+i);
            jogo.adicionaJogador(jogador);
        }

        // Criando e distribuindo as pecas
        jogo.misturaPecas();
        
        Peca peca = jogo.pecasJogo.get(0);
        System.out.println("Peca atual: "+peca);
  
        /*
        // Distribuindo pecas entre os jogadores
        Peca peca;
        for (int i=0; i<njogadores; i++) {
            for (int j=0; j<6; j++) { // 6 Pecas para cada jogador...
                peca = jogo.pecasJogo.get(j); // Pegar uma peca da lista de pecas...
                jogo.jogadores.get(i).recebePeca(peca); // Dar a peca para o jogador
                jogo.pecasJogo.remove(j); // Remover a peca da lista de pecas do jogo
            }
        }
        
        for (int i=0; i<njogadores; i++) {
            System.out.println ("Mostrando pecas do jogador "+i+":");
            jogo.jogadores.get(i).mostraPecas();
        }
*/
        //        jogo.iniciar();
    }
}
