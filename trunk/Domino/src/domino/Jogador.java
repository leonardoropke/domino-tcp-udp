package domino;

import java.util.ArrayList;
import org.json.simple.JSONObject;

public class Jogador {
    public String nome;
    public ArrayList<Peca> listaDePecas = new ArrayList<>();

    public Jogador (String nm) {
	this.nome = nm;
    }
    
    public Jogador (JSONObject jogador ){
	this.nome = (String) jogador.get(nome);
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
  
    private int procura (Peca peca) {
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
    
    public JSONObject toJSON(){
     JSONObject jogador = new JSONObject();
     jogador.put("nome", nome);
     return jogador;
    }
    
    public String toJSONString(){
        return toJSON().toJSONString();
    }
}
