package domino;

import java.util.ArrayList;

public class Jogador {
    public String nome;
    public String ip;
    public ArrayList<Peca> listaDePecas = new ArrayList<>();

    public Jogador (String nm, String ip) {
	this.nome = nm;
        this.ip = ip;
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

    public void jogar () {}
    
}