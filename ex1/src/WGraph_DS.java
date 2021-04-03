package ex1.src;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class WGraph_DS implements weighted_graph, Serializable{
    

    private static class NodeInfo implements node_info, Serializable {

        private static final long serialVersionUID = 3137094734563763916L;
        private int key;
        private String info;
        private double tag;
        private int x;
        private int y;

        public NodeInfo(int key) {
            this.key = key;
            this.tag = -1;
        }

        public NodeInfo(node_info other) {
            this.key = other.getKey();
            this.tag = other.getTag();
            this.info = other.getInfo();
        }

        @Override
        public int getKey() {
            return this.key;
        }

        @Override
        public String getInfo() {
            return this.info;
        }

        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        @Override
        public double getTag() {
            return this.tag;
        }

        @Override
        public void setTag(double t) {
            this.tag = t;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(this.key);
        }

        @Override
        public String toString() {
            return "(key="+this.key+" , tag="+this.tag+" , info="+this.info+")";
        }

        @Override
        public void setX(int x) {
            this.x = x;
        }

        @Override
        public void setY(int y) {
            this.y = y;            
        }

        @Override
        public int X() {
            return this.x;
        }

        @Override
        public int Y() {
            return this.y;
        }
    }

    private static final long serialVersionUID = -4468900051198514988L;


    private HashMap<Integer,node_info> v;
    private HashMap<Integer,HashMap<node_info,edge_info>> e;
    private int MC;
    private int edges;

    public WGraph_DS(){
        this.v = new HashMap<Integer, node_info>();
        this.e = new HashMap<Integer,HashMap<node_info,edge_info>>();
        this.MC = 0;
    }

    public WGraph_DS(weighted_graph other){
        this.v = new HashMap<Integer, node_info>();
        this.e = new HashMap<Integer,HashMap<node_info,edge_info>>();
        
        for (node_info n : other.getV()) {
            int nkey = n.getKey();
            this.v.put(nkey, new NodeInfo(n));
            this.e.put(nkey, new HashMap<node_info, edge_info>());
        }

        for (node_info n : other.getV()) {
            int nkey = n.getKey();
            for (node_info con : other.getV(nkey)) {
                int conkey = con.getKey();
                edge_info edge = other.getEdge(nkey, conkey);
                this.e.get(nkey).put(this.v.get(conkey), edge);
                this.e.get(conkey).put(this.v.get(nkey), edge);
            }
        }
        this.edges = other.edgeSize();
        this.MC = other.getMC();
    }

    
    /** 
     * Retrieve a node by it's key.
     * @param key
     * @return node_info
     */
    @Override
    public node_info getNode(int key) {
        return this.v.get(key);
    }

    
    /** 
     * Check if there is a key 'node1',
     * maping to a node with key 'node2'
     * Note: The order doesn't matter.
     * @param node1
     * @param node2
     * @return boolean
     */
    @Override
    public boolean hasEdge(int node1, int node2) {
        return this.e.containsKey(node1) && this.e.get(node1).containsKey(this.getNode(node2));
    }

    
    /** 
     * Returns the edge value if existed, else: -1. 
     * @param node1
     * @param node2
     * @return double
     */
    @Override
    public edge_info getEdge(int node1, int node2) {
        if(this.hasEdge(node1, node2)){
            return this.e.get(node1).get(this.getNode(node2));
        }
        return null;
    }

    
    /** 
     * Adding a node with a given key to the graph.
     * it also create a new map of connected nodes.
     * Note: By adding a node,
     * @param key
     */
    @Override
    public void addNode(int key) {
        if(!this.v.containsKey(key)){   
            this.v.put(key, new NodeInfo(key));
            this.e.put(key, new HashMap<node_info, edge_info>());
            this.MC ++;
        }
    }

    
    /** 
     * Connecting two nodes.
     * The weight will be mapped twice, for each node, so it will be posible getting neighbors in O(1) complexity.
     * @param node1
     * @param node2
     * @param w - weight
     */
    @Override
    public void connect(int node1, int node2, double w) {
        edge_info to_add = new edge_info(node1, node2, w);
        node_info n2 = this.getNode(node2);
        node_info n1 = this.getNode(node1);
        if(node1 == node2 || n1 == null || n2 == null || (this.hasEdge(node1, node2) && this.e.get(node1).get(n2) == to_add)) return;
        if(!this.hasEdge(node1, node2)){
            this.edges ++;
        }
        this.e.get(node2).put(n1, to_add);
        this.e.get(node1).put(n2, to_add);
        this.MC ++;  
    }

    
    /** 
     * A method to get all nodes in the graph.
     * @return Collection<node_info>
     */
    @Override
    public Collection<node_info> getV() {
        return this.v.values();
    }

    
    /** 
     * Getting a collection of a node with key = 'node_id' neighbors.
     * It's gust the key set of the 'node_id' edges map.
     * @param node_id
     * @return Collection<node_info>
     */
    @Override
    public Collection<node_info> getV(int node_id) {
        if(this.v.containsKey(node_id))
            return this.e.get(node_id).keySet();
        return null;
    }

    
    /** 
     * Iterating over each node connected with 'key', removing the edge between them, and finaly removing the desire node.
     * @param key
     * @return node_info
     */
    @Override
    public node_info removeNode(int key) {
        if(this.v.containsKey(key)){
            Collection<node_info> ni = new HashSet<node_info>(this.getV(key));
            for (node_info n : ni) {
                this.removeEdge(key, n.getKey());
            }
            this.MC ++;
            this.MC -= ni.size();
        }
        return this.v.remove(key);
    }

    
    /** 
     * Removing the mapped value from 'node1' to 'node2' and vice versa.
     * @param node1
     * @param node2
     */
    @Override
    public void removeEdge(int node1, int node2) {
        if(this.hasEdge(node1, node2)){
            this.e.get(node1).remove(this.getNode(node2));
            this.e.get(node2).remove(this.getNode(node1));
            this.edges --;
            this.MC++;
        }
    }

    
    /** 
     * -
     * @return Number of nodes in the graph.
     */
    @Override
    public int nodeSize() {
        return this.v.size();
    }

    
    /** 
     * -
     * @return Number of edges in the graph
     */
    @Override
    public int edgeSize() {
        return this.edges;
    }

    
    /** 
     * @return int
     */
    @Override
	public int getMC() {
		return this.MC;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != this.getClass()) return false;
        weighted_graph o = (weighted_graph)obj;
        if(this.MC != o.getMC() || this.edges != o.edgeSize()) return false;
        for (node_info n : this.getV()) {
            for (node_info ni : this.getV(n.getKey())) {
                if(! o.hasEdge(n.getKey(), ni.getKey())) return false;
                if( o.getEdge(n.getKey(), ni.getKey()) != this.getEdge(n.getKey(), ni.getKey())) return false;
            }
        }
        return (o.nodeSize() == this.nodeSize());
    }

    @Override
    public String toString() {
        String res = "{";
        for (node_info n : this.v.values()) {
            int nkey = n.getKey();
            res+="(";
            for (node_info ni : this.getV(nkey)) {
                res+=" <"+nkey+","+ni.getKey()+">"+this.e.get(nkey).get(ni)+" ";
            }
            res+=") ";
        }
        return res+"}";
    }

}