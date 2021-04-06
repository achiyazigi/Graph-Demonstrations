package ex1.src;

import java.io.Serializable;

import kotlin.Pair;

public class edge_info implements Serializable{

    private static final long serialVersionUID = -1532360748404142704L;
    kotlin.Pair<Integer,Integer> nodes;
    double value;
    boolean inMatch;

    public edge_info(){}

    public edge_info(int key1, int key2, double value){
        this.nodes = new Pair<Integer,Integer>(key1, key2);
        this.value = value;
    }

    public edge_info(edge_info other){
        this.inMatch = other.inMatch;
        this.value = other.value;
        this.nodes = new Pair<Integer,Integer>(other.nodes.getFirst(), other.nodes.getSecond());
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
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != this.getClass()) return false;
        edge_info o = (edge_info)obj;
        if(this.nodes.getFirst() == o.nodes.getFirst() &&
           this.nodes.getSecond() == o.nodes.getSecond() &&
           this.value == o.value &&
           this.inMatch == o.inMatch){
               
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "inMatch=" + inMatch + ", nodes=" + nodes + ", value=" + value;
    }

    
    
}
