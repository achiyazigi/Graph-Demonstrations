package ex1.src;

import java.io.Serializable;

import kotlin.Pair;

public class edge_info implements Serializable{

    private static final long serialVersionUID = 1L;
    kotlin.Pair<Integer,Integer> nodes;
    double value;
    boolean inMatch;

    public edge_info(){}

    public edge_info(int key1, int key2, double value){
        this.nodes = new Pair<Integer,Integer>(key1, key2);
        this.value = value;
    }

    public kotlin.Pair<Integer,Integer> getNodes() {
        return nodes;
    }
    public void setNodes(kotlin.Pair<Integer, Integer> nodes) {
        this.nodes = nodes;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    public boolean isInMatch() {
        return inMatch;
    }
    public void setInMatch(boolean inMatch) {
        this.inMatch = inMatch;
    }

    @Override
    public String toString() {
        return "inMatch=" + inMatch + ", nodes=" + nodes + ", value=" + value;
    }

    
    
}
