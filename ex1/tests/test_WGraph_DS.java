package ex1.tests;

import ex1.src.*;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;


public class test_WGraph_DS{
    @Test
    public void has_egde() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(2);
        g0.addNode(1);
        g0.connect(2, 1, 3.2);
        assertEquals(true, g0.hasEdge(1, 2));
    }

    @Test
    public void MC() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        g0.addNode(2);
        g0.addNode(3);
        g0.addNode(4);
        g0.addNode(4);
        g0.connect(3, 1, 3.1);
        g0.connect(1, 4, 4.1);
        g0.connect(1, 4, 4.2);
        g0.removeNode(4);
        g0.removeEdge(1, 3);
        assertEquals(9, g0.getMC());
    }

    @Test
    public void node_size() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        g0.addNode(2);
        g0.addNode(3);
        g0.addNode(4);
        g0.addNode(4);
        g0.connect(3, 1, 3.1);
        g0.connect(1, 4, 4.1);
        g0.connect(1, 4, 4.2);
        g0.removeNode(4);
        g0.removeEdge(1, 3);
        assertEquals(3, g0.nodeSize());
    }

    @Test
    public void edge_size() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        g0.addNode(2);
        g0.addNode(3);
        g0.addNode(4);
        g0.addNode(4);
        g0.connect(3, 1, 3.1);
        g0.connect(1, 4, 4.1);
        g0.connect(3, 4, 4.2);
        g0.removeNode(4);
        assertEquals(1, g0.edgeSize());
    }

    @Test
    public void get_v_ni() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        g0.addNode(2);
        g0.addNode(3);
        g0.addNode(8);
        g0.connect(1, 2, 2.1);
        g0.connect(1, 3, 3.1);
        g0.connect(1, 8, 8.1);
        g0.removeNode(3);
        Collection<node_info> col = g0.getV(1);
        node_info n2 = g0.getNode(2);
        node_info n3 = g0.getNode(3);
        node_info n8 = g0.getNode(8);
        assertEquals(true, col.contains(n2) && col.contains(n8) && !col.contains(n3));
    }

    @Test
    public void node_to_itself() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        g0.addNode(1);
        g0.connect(1, 1, 3.1);
        g0.connect(1, 1, 4.1);
        assertEquals(0, g0.edgeSize() + g0.getV(1).size());
    }

    @Test
    public void get_edge() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        g0.addNode(2);
        g0.connect(1, 2, 2.1);
        assertEquals(2.1, g0.getEdge(1, 2));
    }

    @Test
    public void get_notEx_edge() {
        weighted_graph g0 = new WGraph_DS();
        g0.addNode(1);
        g0.addNode(2);
        assertEquals(-1, g0.getEdge(1, 2));
    }
}