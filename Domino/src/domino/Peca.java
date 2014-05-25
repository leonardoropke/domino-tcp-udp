package domino;

import org.json.simple.JSONObject;

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
    
    public JSONObject toJSON(){
    JSONObject peca = new JSONObject();
    peca.put("ladoE", ladoE);
    peca.put("ladoD", ladoD);
    return peca;
    }
    
    public String toJSONString(){
     return toJSON().toJSONString();
    }
}
