package ex1.tests;

import ex1.src.*;
import org.junit.Test;
// import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.management.InvalidAttributeValueException;
public class test_WGraph_Algo {
    private static Random _rnd = null;

    private static weighted_graph graph_creator(int v_size, int e_size, int seed) {
        weighted_graph g = new WGraph_DS();
        _rnd = new Random(seed);
        for(int i=0;i<v_size;i++) {
            
            g.addNode(i);
        }
        while(g.edgeSize() < e_size) {
            int a = nextRnd(0,v_size);
            int b = nextRnd(0,v_size);
            g.connect(a,b,nextRnd(1.0, 10));
        }
        return g;
    }

    private static int nextRnd(int min, int max) {
        double v = nextRnd(0.0+min, (double)max);
        int ans = (int)v;
        return ans;
    }
    private static double nextRnd(double min, double max) {
        double d = _rnd.nextDouble();
        double dx = max-min;
        double ans = d*dx+min;
        return ans;
    }

    private static List<node_info> modifySP(weighted_graph g,int start){
        List<node_info> res = new LinkedList<node_info>();
        node_info cur = g.getNode(start);
        if(cur == null) return null;
        res.add(cur);
        Collection<node_info> ni = g.getV(cur.getKey());
        while(ni != null && !ni.isEmpty() && res.size() < 10){
            cur.setTag(0);
            Iterator<node_info> i = ni.iterator();
            node_info tmp = i.next();
            while(i.hasNext() && tmp.getTag() == 0)
                tmp = i.next();
            // if(tmp == null) System.out.println("+");
            if(tmp.getTag() == 0) return res;
            g.connect(cur.getKey(), tmp.getKey(), nextRnd(0.0, 0.1));
            cur = tmp;
            res.add(cur);
            ni = g.getV(cur.getKey());
        }
        return res;
    }

    private static void remove_file(String file_name) throws IOException {
        try {
            Path path = Paths.get(file_name);
            Files.deleteIfExists(path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
    }

    public static boolean compare(weighted_graph g0, weighted_graph g1) {
        for (node_info n : g0.getV()) {
            for (node_info ni : g0.getV(n.getKey())) {
                if(! g1.hasEdge(n.getKey(), ni.getKey())) return false;
                if( g1.getEdge(n.getKey(), ni.getKey()) != g0.getEdge(n.getKey(), ni.getKey())) return false;
            }
        }
        if( g1.edgeSize() != g0.edgeSize()) return false;
        return (g1.getV().size() == g0.getV().size());
    }

    
    @Test
    public void copy_struct() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        g0.addNode(2);
        g0.connect(2, 1, 7);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        weighted_graph g1 = ga.copy();
        boolean b = true;
        b &= (g1.getNode(1) != null);
        b &= (g1.getNode(2) != null);
        b &= g1.hasEdge(1, 2);
        b &= (g1.getEdge(2, 1) == g0.getEdge(1, 2));
        assertEquals(true, b);
    }

    @Test
    public void copy_independency1() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        g0.addNode(2);
        g0.connect(2, 1, 7);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        weighted_graph g1 = ga.copy();

        assertEquals(false, g0.getNode(1) == g1.getNode(1));
    }

    @Test
    public void copy_independency2() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        g0.addNode(2);
        g0.connect(2, 1, 7);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        weighted_graph g1 = ga.copy();
        g0.removeEdge(1, 2);
        assertEquals(true, g1.hasEdge(1, 2) && !g0.hasEdge(1, 2));
    }

    @Test
    public void copy_independency3() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        weighted_graph g1 = ga.copy();
        double g1_1st_tag = g1.getNode(1).getTag();
        g0.getNode(1).setTag(g1_1st_tag + 1);
        assertEquals(false, g1.getNode(1).getTag() == g0.getNode(1).getTag());
    }

    @Test
    public void SP_basic() {
        weighted_graph g0 = new WGraph_DS();
        for (int i = 0; i < 3; i++) {
            g0.addNode(i);
        }
        g0.connect(0, 1, 1);
        g0.connect(1, 2, 1);
        g0.connect(0, 2, 3);

        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        List<node_info> SP = ga.shortestPath(0, 2);
        int[] expected = { 0, 1, 2 };
        boolean b = true;
        int i = 0;
        for (node_info n : SP) {
            b &= n.getKey() == expected[i++];
        }
        assertEquals(true, b);
    }

    @Test
    public void SP_regular() {
        weighted_graph g0 = new WGraph_DS();
        for (int i = 0; i < 10; i++) {
            g0.addNode(i);
        }
        g0.connect(0, 1, 1); // 2 10
        g0.connect(0, 2, 2); // (5)---(8)__________
        g0.connect(1, 4, 1); // /5 \3 \
        g0.connect(2, 5, 5); // (2)----\-----\10 \
        g0.connect(2, 7, 10); // 2/ \ 4 \ 4 \
        g0.connect(4, 3, 1); // (0) 1 _ (4)-----(7)--------(9)
        g0.connect(4, 5, 3); // 1\ / 1| /
        g0.connect(4, 7, 4); // (1) (3) /
        g0.connect(3, 6, 1); // 1\ 1 /
        g0.connect(5, 8, 2); // (6)----------
        g0.connect(6, 9, 1); //
        g0.connect(7, 9, 4); //
        g0.connect(9, 8, 10); //
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        List<node_info> SP = ga.shortestPath(0, 9);
        int[] expected = { 0, 1, 4, 3, 6, 9 };
        boolean b = true;
        int i = 0;
        for (node_info n : SP) {
            b &= n.getKey() == expected[i++];
        }
        assertEquals(true, b);
    }


    @Test
    public void SP_advanced() {
        _rnd = new Random(1);
        for (int i = 0; i < 400000; i++) {
            weighted_graph g = graph_creator(50, nextRnd(0, 100), i);           
            int start = nextRnd(0, g.getV().size());
            List<node_info> expected = modifySP(g,start);
            weighted_graph_algorithms ga = new WGraph_Algo();
            ga.init(g);
            List<node_info> SP = ga.shortestPath(start, ((node_info)(expected.toArray()[expected.size()-1])).getKey());
            boolean b = expected.equals(SP);
            if(!b){
                System.out.println("failed with this graph:");
                System.out.println("(graph_craetor("+"50, "+g.edgeSize()+", "+i+")");
                System.out.println(expected);
                System.out.println(SP);
                fail();
            }
        }
    }

    @Test
    public void SP_reversed() {
        weighted_graph g0 = new WGraph_DS();
        for (int i = 0; i < 10; i++) {
            g0.addNode(i);
        }
        g0.connect(0, 1, 1);
        g0.connect(0, 2, 2);
        g0.connect(1, 4, 1);
        g0.connect(2, 5, 5);
        g0.connect(2, 7, 10);
        g0.connect(4, 3, 1);
        g0.connect(4, 5, 3);
        g0.connect(4, 7, 4);
        g0.connect(3, 6, 1);
        g0.connect(5, 8, 2);
        g0.connect(6, 9, 1);
        g0.connect(7, 9, 4);
        g0.connect(9, 8, 10);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        List<node_info> SP = ga.shortestPath(9, 0);
        int[] expected = { 9, 6, 3, 4, 1, 0 };
        boolean b = true;
        int i = 0;
        for (node_info n : SP) {
            b &= n.getKey() == expected[i++];
        }
        assertEquals(true, b);
    }

    @Test
    public void SP_no_path() {
        weighted_graph g0 = new WGraph_DS();
        for (int i = 0; i < 10; i++) {
            g0.addNode(i);
        }
        g0.connect(0, 1, 1);
        g0.connect(1, 4, 1);
        g0.connect(2, 5, 5);
        g0.connect(2, 7, 10);
        g0.connect(3, 6, 1);
        g0.connect(5, 8, 2);
        g0.connect(6, 9, 1);
        g0.connect(7, 9, 4);
        g0.connect(9, 8, 10);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        List<node_info> SP = ga.shortestPath(0, 9);
        assertEquals(null, SP);
    }

    @Test
    public void SP_node_to_itself() {
        weighted_graph g0 = new WGraph_DS();
        for (int i = 0; i < 10; i++) {
            g0.addNode(i);
        }
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        boolean b = true;
        for (node_info n : g0.getV()) {
            b &= ga.shortestPath(n.getKey(), n.getKey()).size() == 1;
            b &= ga.shortestPath(n.getKey(), n.getKey()).get(0) == n;
        }
        assertEquals(true, b);
    }

    @Test
    public void SP_dist_basic() {
        weighted_graph g0 = new WGraph_DS();
        for (int i = 0; i < 3; i++) {
            g0.addNode(i);
        }
        g0.connect(0, 1, 1);
        g0.connect(1, 2, 1);
        g0.connect(0, 2, 3);

        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        assertEquals(2, ga.shortestPathDist(0, 2));
    }

    @Test
    public void SP_dist_regular() {
        weighted_graph g0 = new WGraph_DS();
        for (int i = 0; i < 10; i++) {
            g0.addNode(i);
        }
        g0.connect(0, 1, 1); // 2 10
        g0.connect(0, 2, 2); // (5)---(8)__________
        g0.connect(1, 4, 1); // /5 \3 \
        g0.connect(2, 5, 5); // (2)----\-----\10 \
        g0.connect(2, 7, 10); // 2/ \ 4 \ 4 \
        g0.connect(4, 3, 1); // (0) 1 _ (4)-----(7)--------(9)
        g0.connect(4, 5, 3); // 1\ / 1| /
        g0.connect(4, 7, 4); // (1) (3) /
        g0.connect(3, 6, 1); // 1\ 1 /
        g0.connect(5, 8, 2); // (6)----------
        g0.connect(6, 9, 1); //
        g0.connect(7, 9, 4); //
        g0.connect(9, 8, 10); //
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        assertEquals(5, ga.shortestPathDist(0, 9));
    }

    @Test
    public void save_basic() throws IOException {
        String file_name = System.getProperty("user.dir")+"\\g0";
        weighted_graph g0 = new WGraph_DS();
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        boolean b = ga.save(file_name);
        remove_file(file_name);
        assertEquals(true, b);
    }

    @Test
    public void load_basic() throws IOException {
        weighted_graph g1 = new WGraph_DS();
        weighted_graph_algorithms ga = new WGraph_Algo();
        String file_name = System.getProperty("user.dir")+"\\g0";
        ga.init(g1);
        remove_file(file_name);
        ga.save(file_name);
        ga.load(file_name);
        g1 = ga.getGraph();
        boolean b = true;
        b &= g1.edgeSize() == 0;
        b &= g1.getV().isEmpty();
        b &= g1.getV().size() == 0;
        assertEquals(true, b); 
    }

    @Test
    public void load() throws IOException {
        weighted_graph g0 = new WGraph_DS();
        for (int i = 0; i < 10; i++) {
            g0.addNode(i);
        }
        g0.connect(0, 1, 1); // 2 10
        g0.connect(0, 2, 2); // (5)---(8)__________
        g0.connect(1, 4, 1); // /5 \3 \
        g0.connect(2, 5, 5); // (2)----\-----\10 \
        g0.connect(2, 7, 10); // 2/ \ 4 \ 4 \
        g0.connect(4, 3, 1); // (0) 1 _ (4)-----(7)--------(9)
        g0.connect(4, 5, 3); // 1\ / 1| /
        g0.connect(4, 7, 4); // (1) (3) /
        g0.connect(3, 6, 1); // 1\ 1 /
        g0.connect(5, 8, 2); // (6)----------
        g0.connect(6, 9, 1); //
        g0.connect(7, 9, 4); //
        g0.connect(9, 8, 10); //
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        String file_name = System.getProperty("user.dir")+"\\g0";
        System.out.println(file_name);
        remove_file(file_name);
        ga.save(file_name);
        ga.init(new WGraph_DS());
        ga.load(file_name);
        weighted_graph g1 = ga.getGraph();
        
        assertEquals(g0, g1); 
        remove_file(file_name);
    }

    @Test
    public void isConnected_empty() {
        weighted_graph g0 = new WGraph_DS();
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        assertEquals(true, ga.isConnected());
    }

    @Test
    public void isConnected_1_node() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(0);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        assertEquals(true, ga.isConnected());
    }

    @Test
    public void isConnected_basic1() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(0);
        g0.addNode(100);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        assertEquals(false, ga.isConnected());
    }

    @Test
    public void isConnected_basic2() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(0);
        g0.addNode(100);
        g0.connect(100, 0, 77.999998);
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        assertEquals(true, ga.isConnected());
    }

    @Test
    public void isConnected_advanced1() {
        weighted_graph_algorithms ga = new WGraph_Algo();
        for (int i = 0; i < 1000000; i++) {
            weighted_graph g0 = new WGraph_DS();
            g0.addNode(0);
            _rnd = new Random(1);
            int v = nextRnd(0, 100);
            for (int j = 1; j < v; j++) {
                g0.addNode(j);
                g0.connect(nextRnd(0, j-1), j, j);
            }
            ga.init(g0);
            if(!ga.isConnected()){
                System.out.println("failed with this graph:");
                System.out.println(g0);
                fail();
            }
        }
    }

    @Test
    public void isConnected_advanced2() {
        weighted_graph_algorithms ga = new WGraph_Algo();
        for (int i = 0; i < 1000000; i++) {
            weighted_graph g0 = new WGraph_DS();
            g0.addNode(0);
            _rnd = new Random(1);
            int v = nextRnd(2, 100);
            for (int j = 1; j < v/2; j++) {
                g0.addNode(j);
                g0.connect(nextRnd(0, j-1), j, j);
            }
            g0.addNode(v/2);
            for (int j = v/2+1; j < v; j++) {
                g0.addNode(j);
                g0.connect(nextRnd(0, j-1), j, j);
            }
            ga.init(g0);
            if(ga.isConnected()){
                System.out.println("nodes 0-"+(v/2-1)+" are not connected to nodes "+v/2+"-"+(v-1));
                System.out.println("failed with this graph:");
                System.out.println(g0);
                fail();
            }
        }
    }

    @Test
    public void runtime1() {
        weighted_graph g0 = new WGraph_DS();
        for (int i = 0; i < 100000; i++) {
            g0.addNode(i);
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 100000; j++) {
                g0.connect(i, j, i+j);
            }
        }
        long start = System.currentTimeMillis();
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        ga.copy();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                ga.shortestPath(i, j);
                ga.shortestPathDist(i, j);
            }
        }
        ga.save("g0");
        ga.load("g0");
        try {
            remove_file("g0");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println((System.currentTimeMillis()-start)/1000.0+"s'");
    }
    @Test
    public void runtime2() {
        long start = System.currentTimeMillis();
        weighted_graph g0 = graph_creator(1000000, 10000000, 1);
        long cur = System.currentTimeMillis()-start;
        System.out.println("graph created after " + cur/1000.0+"s'");
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        ga.copy();
        cur = System.currentTimeMillis()-cur;
        System.out.println("performed deep copy in "+cur/1000.0+"s'");
        int p = nextRnd(0, 1000000);
        int q = nextRnd(0, 1000000);
        ga.shortestPath(p, q);
        cur = System.currentTimeMillis()-cur;
        System.out.println("performed shortest path in "+cur/1000.0+"s'");
        ga.shortestPathDist(p,q);
        cur = System.currentTimeMillis()-cur;
        System.out.println("performed shortest path dist in "+cur/1000.0+"s'");
        ga.save("g0");
        cur = System.currentTimeMillis()-cur;
        System.out.println("performed save in "+cur/1000.0+"s'");
        ga.load("g0");
        cur = System.currentTimeMillis()-cur;
        System.out.println("performed load in "+cur/1000.0+"s'");
        try {
            remove_file("g0");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("overall: "+(System.currentTimeMillis()-start)/1000.0+"s'");
    }

    @Test
    public void get_graph(){
        weighted_graph g0 = new WGraph_DS();
        weighted_graph_algorithms ga = new WGraph_Algo();
        ga.init(g0);
        assertEquals(ga.getGraph().hashCode(), g0.hashCode());
    }

    @Test
    public void maxMatch() throws InvalidAttributeValueException{
        WGraph_DS g0 = new WGraph_DS();
        g0.addNode(0);
        g0.addNode(1);
        g0.addNode(2);
        g0.addNode(3);
        g0.addNode(4);
        g0.addNode(5);
        g0.connect(0, 1, 0);
        g0.connect(1, 2, 0);
        g0.connect(2, 3, 0);
        g0.connect(0, 3, 0);
        g0.connect(1, 4, 0);
        g0.connect(0, 5, 0);
        WGraph_Algo ga = new WGraph_Algo();
        ga.init(g0);
        ga.maxMatchHungarian();
    }
}
