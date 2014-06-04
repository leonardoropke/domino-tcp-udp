
package domino;

import java.util.ArrayList;

public class JogoCliente {
    ControladorCliente controlador;
    public ArrayList<Peca> pecasJogo = new ArrayList<> ();
    int pecasDisponiveis;
    public ArrayList<Jogador> jogadores = new ArrayList<> ();
    public String nomeJogador;
    public int numJogador;
    public Jogador jogador;

    
    public int pecasdisponiveis = 0;

    int rodada = 1;
    boolean jogando = false;

    public JogoCliente (String nomeJogador, ControladorCliente cont) {
        jogador = new Jogador(nomeJogador, null, -1);
        this.nomeJogador = nomeJogador;
        this.controlador = cont;
    }
    
    public void removePecaJogo (Peca peca) {
        // Temos que procurar a posicao no array onde esta a peca selecionada...
        int local = procura (peca);
        if (local != -1)
            pecasJogo.remove(local);
        else
            System.out.println("Nao consegui remover a peca: "+peca.toString()+"!!!");
    }
  
    // Metodo pra procurar uma peca em 'pecasJogo'
    // Se encontrar, retornar a posicao da peca
    // Se NAO encontrar, retornar -1
    public int procura (Peca peca) {
        int i;
        for (i=0; i<pecasJogo.size(); i++) {
            if ((pecasJogo.get(i).ladoE == peca.ladoE) && (pecasJogo.get(i).ladoD == peca.ladoD))
                return i;
        }
        return -1;
    }


    public void recebePecas(String strPecas) {
        System.out.println("Pecas recebidas: '"+strPecas+"'");
        Peca peca;
        int esq, dir;
        
        String pecastr="";
        int x=0;
        
        for (int i=0; i<=strPecas.length()-1; i++) {
            if (strPecas.charAt(i) != ' ') pecastr += strPecas.charAt(i);
            
            if (strPecas.charAt(i) == ']') {
                System.out.println("peca: '"+pecastr+"'");
                
                esq = Integer.parseInt(pecastr.substring(1, 2));
                dir = Integer.parseInt(pecastr.substring(3, 4));

                pecastr = "";
                
                peca = new Peca(esq, dir);
                jogador.recebePeca(peca);
            }
        }
        controlador.gui.mostraPecasJogador(jogador.listaDePecas);
        
    }
    
    public void recebeJogadores(int njogador, String strjogadores) {
        System.out.println("Jogadores recebidos: '"+strjogadores+"'");
        
        String jogadorstr="";
        int x=0;
        
        for (int i=1; i<=strjogadores.length()-1; i++) {
            if (strjogadores.charAt(i) != ' ') jogadorstr += strjogadores.charAt(i);
            
            if (strjogadores.charAt(i) == ' ') {
                System.out.println("jogador: '"+jogadorstr+"'");
                
                controlador.gui.atualizaTabelaJogadoresCliente (jogadorstr, x, 6);
                jogadores.add(new Jogador (jogadorstr, null, x));

                controlador.gui.adicionaMsg("Jogador '"+jogadorstr+"' conectado!");
                if (x == njogador) {
                    controlador.gui.setTitle("Domino Mania - "+jogadorstr);
                    numJogador = x;
                }
                
                jogadorstr = "";
                x++;
            }
        }
    }
    
    public void recebeJogada(String strjogada) {
        System.out.println("Jogada: '"+strjogada+"'");

        int esq = Integer.parseInt(strjogada.substring(1, 2));
        int dir = Integer.parseInt(strjogada.substring(3, 4));
        
        String lado = strjogada.substring(6, 9);
        System.out.println("esq: "+esq);
        System.out.println("dir: "+dir);
        System.out.println("lado: "+lado);
        
        Peca peca = new Peca(esq, dir);
        if (lado.equals("esq"))
            controlador.jogo.pecasJogo.add(0, peca);
        else
            controlador.jogo.pecasJogo.add(peca);
        
        
    //public void atualizaTabelaJogadoresCliente(String nomeJogador, int i, int pecas) {
        int jogadorAnterior = numJogador -1;
        int linhaJogador = controlador.gui.pegaJogador(jogadores.get(jogadorAnterior).nome);
        int pecasJogador = controlador.gui.pegaPecasJogador(jogadores.get(jogadorAnterior).nome);
        pecasJogador--;
        
//        controlador.gui.atualizaTabelaJogadoresCliente (jogadores.get(numJogador-1).nome, linhaJogador, pecasJogador);
        controlador.gui.mostraJogo(pecasJogo);

        
    }
    
    public boolean jogadaValida(Jogador jogador, Peca peca, String lado) {
        boolean aceitou = false;
        
        if (lado.equals("Esquerdo")) lado = "esq";
        if (lado.equals("Direito")) lado = "dir";
        
        if (controlador.jogo.pecasJogo.size() == 0) return true;

        Peca pEsq = controlador.jogo.pecasJogo.get(0);
        Peca pDir = controlador.jogo.pecasJogo.get(controlador.jogo.pecasJogo.size() - 1);

        System.out.println("Peca da esquerda: " + pEsq);
        System.out.println("Peca da direita: " + pDir);
        System.out.println("Peca desejada: " + peca);
        System.out.println("Lado: "+lado);
        if (lado.equals("esq")) {
            System.out.println("Tentando encaixar do lado esquerdo!");
            // Tentar encaixar do lado ESQUERDO do jogo atual
            if (peca.ladoE == pEsq.ladoE) {
                // Pode encaixar, mas tem que inverter a peca!
                peca.inverter();
                return true;
            }
            else if (peca.ladoD == pEsq.ladoE) {
                // Pode encaixar perfeitamente!
                return true;
            }
        }

        if (lado.equals("dir")) {
            // Tentar encaixar do lado DIREITO do jogo atual
            System.out.println("Tentando encaixar do lado direito!");
            if (peca.ladoE == pDir.ladoD) {
                // Pode encaixar perfeitamente!
                  return true;
            } else if (peca.ladoD == pDir.ladoD) {
                // Pode encaixar, mas tem que inverter a peca!
                peca.inverter();
                return true;
            }
        }

        return aceitou;
    }

    public void recebePecaComprada(String pecastr) {
        System.out.println("Peca recebidas: '"+pecastr+"'");
        Peca peca;
        int esq, dir;
        
        esq = Integer.parseInt(pecastr.substring(1, 2));
        dir = Integer.parseInt(pecastr.substring(3, 4));
        
        peca = new Peca (esq, dir);
        
        jogador.listaDePecas.add(peca);
        controlador.gui.mostraPecasJogador(jogador.listaDePecas);
        
        controlador.gui.alertaUsuario("Voce comprou a peca "+peca.toString()+" !");
        controlador.gui.travaTela();

    }

    
}
