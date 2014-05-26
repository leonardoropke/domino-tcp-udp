package domino;

public class Peca {
    public int ladoE, ladoD;

    public Peca (int esq, int dir) {
        this.ladoE = esq;
        this.ladoD = dir;
    }
 
    @Override
    public String toString () {
        return "[" + this.ladoE + ":" + this.ladoD + "]";
    }
    
    public boolean ehIgual (Peca pecaRecebida) {
        if ((ladoE == pecaRecebida.ladoE) && (ladoD == pecaRecebida.ladoD))
            return true;
        else
            return false;
    }
    
}
