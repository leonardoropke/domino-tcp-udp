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
}
