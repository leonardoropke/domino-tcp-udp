package domino;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

// Eh possivel que tenhamos que Jogador ser Runnable
// pra rodar em threads... Ainda nao esta sendo usado pra nada isso.
public class Jogador implements Runnable {
    public String nome;
    public int numJogador; // Meu numero de jogador
    public int pontos;
    public ArrayList<Peca> listaDePecas = new ArrayList<>();

    public Socket conexao;
    public ObjectOutputStream output; // gera fluxo de saída para o cliente
    public ObjectInputStream input; // gera fluxo de entrada a partir do cliente

    public Jogador (String nm, Socket socket, int numJogador) {
	this.nome = nm;
        this.conexao = socket;
        this.numJogador = numJogador;
        this.pontos = 0;

    }

    public void recebeNome () {

        try {
            output = new ObjectOutputStream( conexao.getOutputStream() );
            output.flush(); // esvazia buffer de saída enviar as informações de cabeçalho

            // Receber o nome do jogador
            input = new ObjectInputStream( conexao.getInputStream() );
            String message = ( String ) input.readObject();

            nome = message;

        } catch (Exception ex) {
            System.out.println("Nao consegui receber o nome do jogador!");
        }

    }
    
    public void recebePeca (Peca peca) {
        listaDePecas.add(peca);
    }

    public void removePeca (Peca peca) {
        // Temos que procurar a posicao no array onde esta a peca selecionada...
        int local = procura (peca);
        if (local != -1)
            listaDePecas.remove(local);
        else
            System.out.println("Nao consegui remover a peca: "+peca.toString()+"!!!");
    }
  
    // Metodo pra descobrir se o jogador tem uma determinada peca
    // Se encontrar, retornar a posicao da peca
    // Se NAO encontrar, retornar -1
    public int procura (Peca peca) {
        int i;
        for (i=0; i<listaDePecas.size(); i++) {
            if ((listaDePecas.get(i).ladoE == peca.ladoE) && (listaDePecas.get(i).ladoD == peca.ladoD))
                return i;
        }
        return -1;
    }
    
    public void mostraPecas () {
        for (int i=0; i < listaDePecas.size(); i++) {
            System.out.print(listaDePecas.get(i).toString());
        }
    }

    @Override
    public void run() {
        System.out.println("Ainda nao faz nada!\n");
    }

    // Ao iniciar o jogo, enviar pecas para o jogador cliente
    public void enviaPecaCliente() {
        System.out.println("Enviando pecas para o jogador: "+nome);
        String pecas = "";

        for (int i=0; i < listaDePecas.size(); i++) {
            System.out.print(listaDePecas.get(i).toString());
            pecas += listaDePecas.get(i).toString()+" ";
        }
        
        try {
            output.writeObject( "receber "+pecas );
            output.flush(); // esvazia a saída para o cliente    

        } catch (Exception ex) {
            System.out.println("Deu bug!");
        }
        
    }
    
}
