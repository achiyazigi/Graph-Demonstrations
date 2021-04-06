package ex1.src;

import java.util.Collection;

import java.util.HashSet;
import java.util.LinkedList;

import java.util.Observer;

import java.util.concurrent.locks.Condition;

import javax.management.InvalidAttributeValueException;



public class RunnableGraphAlgo extends WGraph_Algo implements Runnable{
    private weighted_graph g;
    private boolean done = false;
    private Observer observer;
    Condition step;

    public RunnableGraphAlgo(Condition step, Observer o) {
        super();
        this.observer = o;
        this.step = step;
    }
    


    @Override
    public void run() {
        try {
            this.maxMatchHungarian();
        } catch (InvalidAttributeValueException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void maxMatchHungarian() throws InvalidAttributeValueException{
        this.setDone(false);
        HashSet<edge_info> M = new HashSet<>();
        kotlin.Pair<Collection<node_info>,Collection<node_info>> ab = this.bipartite();
        LinkedList<edge_info> P = null;
        
        while((P = augmenting(ab))!=null){
            
            System.out.println("found aug path:\n"+P);
            recolor(P);
            this.observer.update(null, null);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        this.setDone(true);
        for (node_info n : this.g.getV()) {
            int n_key = n.getKey();
            for (node_info ni : this.g.getV(n_key)) {
                edge_info e = g.getEdge(n_key, ni.getKey());
                if(e.isInMatch()){
                    M.add(e);
                }
            }
        }
        System.out.println(M);
    }

    @Override
    public void init(weighted_graph g){
        super.init(g);
        this.g = super.getGraph();
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

}
