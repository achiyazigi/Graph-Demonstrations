package ex1.src;

import javax.management.InvalidAttributeValueException;
import javax.swing.*;
import javax.swing.event.MouseInputListener;


import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Gui extends JFrame{
    public static int key_counter = 0;
    
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public weighted_graph g;
    


    private static class Graph_hendler implements ActionListener, Observer{
        final Lock lock = new ReentrantLock();

        final Condition step  = lock.newCondition();


        private class TextBox_hendler implements ActionListener{
            private Gui gui;
            private JFormattedTextField textBox;
            public TextBox_hendler(Gui gui, JFormattedTextField textBox){
                super();
                this.gui = gui;
                this.textBox = textBox;
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                String textBoxName = ((JFormattedTextField)e.getSource()).getName();
                if(textBoxName == "Remove"){
                    try{
                        int key_to_remove = Integer.parseInt(e.getActionCommand());
                        gui.g.removeNode(key_to_remove);
                        // JDialog d = (JDialog)textBox.getRootPane().getParent();
                        // d.dispose();
                        gui.repaint();
                    }
                    catch(Exception ex){
                        System.out.println("wrong input");
                    }
                    textBox.setText("");
                }
                else if(textBoxName == "Connect"){
                    try{

                        String[] skeys = e.getActionCommand().split("-");
                        if(skeys.length != 2){
                            throw new Exception();
                        }
                        int key1 = Integer.parseInt(skeys[0]);
                        int key2 = Integer.parseInt(skeys[1]);
                        gui.g.connect(key1, key2, 0);
                        // JDialog d = (JDialog)textBox.getRootPane().getParent();
                        // d.dispose();
                        gui.repaint();
                    }

                    catch(Exception ex){
                        System.out.println("wrong input");
                    }
                    textBox.setText("");
                }
                else if(textBoxName == "Disconnect"){
                    try{

                        String[] skeys = e.getActionCommand().split("-");
                        if(skeys.length != 2){
                            throw new Exception();
                        }
                        int key1 = Integer.parseInt(skeys[0]);
                        int key2 = Integer.parseInt(skeys[1]);
                        gui.g.removeEdge(key1, key2);
                        // JDialog d = (JDialog)textBox.getRootPane().getParent();
                        // d.dispose();
                        gui.repaint();
                    }

                    catch(Exception ex){
                        System.out.println("wrong input");
                    }
                    textBox.setText("");
                }
            }
        }

        static int x = 20;
        static int y = 20;
        private Gui gui;

        public Graph_hendler(Gui gui){
            super();
            this.gui = gui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand() == "Add Node"){
                gui.g.addNode(key_counter);
                node_info added = gui.g.getNode(key_counter);
                added.setX(x);
                added.setY(y);
                y+=40;
                if(y > this.gui.getHeight() - 80){
                    y = 20;
                    x += 60;
                }
                key_counter++;
                this.gui.repaint();
            }

            if(e.getActionCommand() == "Remove Node"){
                JDialog d = new JDialog(gui, "Enter Key", true);
                d.setSize(180, 60);
                JFormattedTextField textBox = new JFormattedTextField();
                textBox.setName("Remove");
                textBox.addActionListener(new TextBox_hendler(gui, textBox));
                d.add(textBox);
                d.setLocationRelativeTo(gui);
                d.setVisible(true);
            }
            
            if(e.getActionCommand() == "Connect" || e.getActionCommand() == "Disconnect"){
                JDialog d = new JDialog(gui, "Enter Pair: key1-key2", true);
                d.setSize(300, 60);
                JFormattedTextField textBox = new JFormattedTextField();
                textBox.setName(e.getActionCommand());
                
                textBox.addActionListener(new TextBox_hendler(gui, textBox));
                d.add(textBox);
                d.setLocationRelativeTo(gui);
                d.setVisible(true);
            }

            if(e.getActionCommand() == "Max Match"){
                RunnableGraphAlgo ga = new RunnableGraphAlgo(step, this);
                ga.init(gui.g);
                boolean s_b_s = gui.getStepByStepStatus();
                if(s_b_s){
                    try {
                        Executor executor = Executors.newSingleThreadExecutor();
                        executor.execute(ga);
                    } catch (Throwable e1) {
                        e1.printStackTrace();
                    }
                }
                else{
                    weighted_graph_algorithms ga_simple = new WGraph_Algo();
                    ga_simple.init(gui.g);
                    try {
                        ga_simple.maxMatchHungarian();
                    } catch (InvalidAttributeValueException e1) {
                        e1.printStackTrace();
                    }
                    gui.repaint();
                }
            }

        }

        @Override
        public void update(Observable o, Object arg) {
            this.gui.repaint();
        }

    }

    private class Paint_panel extends JPanel implements MouseInputListener{

        private static final long serialVersionUID = 1L;
        boolean onDrag = false;
        node_info src;
        Point temp_line_end;

        public Paint_panel(){
            super(true);
        }

        // @Override
        // public void paint(Graphics canvas) {
        //     BufferedImage buffer_image;
        //     Graphics buffer_graphics;
        //     // Create a new "canvas"
        //     buffer_image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        //     buffer_graphics = buffer_image.getGraphics();
        //     buffer_graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        //     // Draw on the new "canvas"
        //     paintComponent(buffer_graphics);
    
        //     // "Switch" the old "canvas" for the new one
        //     canvas.drawImage(buffer_image, 0, 0, this);
        // }

        @Override
        protected void paintComponent(Graphics canvas) {
            super.paintComponent(canvas);
            if(temp_line_end != null){
                Point s = new Point(this.src.X(), this.src.Y());
                paintTempLine(s, temp_line_end, canvas);

            }
            drawGraph(canvas);
        }
        /** 
         * draw the graph
         * @param canvas
         */
        private void drawGraph(Graphics canvas) {
            for (node_info v : g.getV()) {
                canvas.setColor(Color.BLACK);
                drawNode(v, 5, canvas);
                for (node_info u : g.getV(v.getKey())) {
                    edge_info e = g.getEdge(v.getKey(), u.getKey());
                    drawEdge(e, canvas);
                }
            }
        }

        
        /** 
         * draw a single node
         * @param n
         * @param r
         * @param canvas
         */
        private void drawNode(node_info n, int r, Graphics canvas) {
            
            canvas.fillOval(n.X() - r, n.Y() - r, 2 * r, 2 * r);
            canvas.drawString("" + n.getKey(), n.X() - r, n.Y()- 2 * r);

        }

        
        /** 
         * draw a single edge
         * @param e
         * @param canvas
         */
        private void drawEdge(edge_info e, Graphics canvas) {
            node_info s = g.getNode(e.getNodes().getFirst());
            node_info d = g.getNode(e.getNodes().getSecond());
            int x1 = s.X();
            int y1 = s.Y();
            int x2 = d.X();
            int y2 = d.Y();
            canvas.setColor(Color.DARK_GRAY);
            if(e.isInMatch()){
                canvas.setColor(Color.RED);
            }
            canvas.drawLine(x1, y1, x2, y2);

            /*
            ---print the edge's weight - for future use---
                Font font = canvas.getFont().deriveFont(40);
                canvas.setFont(font);
                canvas.drawString(""+e.getWeight(), (int)(d0.x()/2+s0.x())/2,
                (int)(d0.y()+s0.y())/2-10);
            */
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getButton() == 1){
                g.addNode(key_counter);
                node_info added = g.getNode(key_counter);
                added.setX(e.getX());
                added.setY(e.getY());
                key_counter ++;
            }
            else if(e.getButton() == 3){
                Point remove_spot = e.getPoint();
                for (node_info n : g.getV()) {
                    if(remove_spot.distance(n.X(), n.Y()) < 20){
                        g.removeNode(n.getKey());
                        break;
                    }
                }
            }

            this.repaint();
        }
        @Override
        public void mousePressed(MouseEvent e) {
            Point spoint = e.getPoint();
            for (node_info v : g.getV()) {
                if(spoint.distance(v.X() ,v.Y()) < 20){
                    src = v;
                    onDrag = true;
                    break;
                }
            }
                
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            Point dpoint = e.getPoint();
            if(onDrag && src != null){
                for (node_info v : g.getV()) {
                    if(dpoint.distance(v.X() ,v.Y()) < 20 && v != src){
                        if(e.getButton() == 1){
                            g.connect(v.getKey(), src.getKey(), 0);
                        }
                        else if(e.getButton() == 3){
                            g.removeEdge(v.getKey(), src.getKey());
                        }
                        this.repaint();
                        break;
                    }
                }
            }
            temp_line_end = null;
            src = null;
            onDrag = false;
            this.repaint();
        }
        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void mouseDragged(MouseEvent e) {
            if(src != null){
                temp_line_end = e.getPoint();
                this.repaint();
            }
        }
        private void paintTempLine(Point s, Point d, Graphics canvas) {
            canvas.setColor(Color.BLUE);
            canvas.drawLine((int)s.getX(), (int)s.getY(), (int)d.getX(), (int)d.getY());
        }
        @Override
        public void mouseMoved(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }
    }

    private Graph_hendler gh;
    private JCheckBox stepByStepCheckBox;

    public Gui(String title, weighted_graph g){
        super(title);
        this.gh = new Graph_hendler(this);
        this.g = g;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        JPanel buttons_panel = new JPanel();
        Paint_panel graph_panel = new Paint_panel();
        JPanel rights_reserved_panel = new JPanel();

        JButton addNodeButton = new JButton("Add Node");
        JButton removeNodeButton = new JButton("Remove Node");
        JButton ConnectButton = new JButton("Connect");
        JButton disconnectButton = new JButton("Disconnect");
        JButton maxMatch_hungarianButton = new JButton("Max Match");

        JLabel rightsReserved = new JLabel("<html>All Rights Reseved To: <a href=' '>https://github.com/achiyazigi</a></html>");

        this.stepByStepCheckBox = new JCheckBox("Step By Step");

        buttons_panel.add(addNodeButton);
        buttons_panel.add(removeNodeButton);
        buttons_panel.add(ConnectButton);
        buttons_panel.add(disconnectButton);
        buttons_panel.add(maxMatch_hungarianButton);
        buttons_panel.add(stepByStepCheckBox);

        graph_panel.addMouseListener(graph_panel);
        graph_panel.addMouseMotionListener(graph_panel);
        graph_panel.setToolTipText("<html><p>Left Click anywhere to add node.</p><p>Right Click on a node to remove it.</p><p>Drag between nodes to connect.</p><p>Drag with Right Click to disconnect.</p></html>");

        rights_reserved_panel.add(BorderLayout.CENTER, rightsReserved);

        addNodeButton.addActionListener(gh);
        removeNodeButton.addActionListener(gh);
        ConnectButton.addActionListener(gh);
        maxMatch_hungarianButton.addActionListener(gh);
        disconnectButton.addActionListener(gh);
        
        rightsReserved.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightsReserved.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                try {
         
                    Desktop.getDesktop().browse(new URI("https://github.com/achiyazigi"));
                     
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }                
            }
           
            @Override
            public void mouseEntered(MouseEvent e) {

            }
            
        });

        this.getContentPane().add(BorderLayout.NORTH, buttons_panel);
        this.getContentPane().add(BorderLayout.CENTER, graph_panel);
        this.getContentPane().add(BorderLayout.SOUTH, rights_reserved_panel);
        
    }

    public boolean getStepByStepStatus(){
        return this.stepByStepCheckBox.isSelected();
    }

    public void setStepByStepStatus(boolean status){
        this.stepByStepCheckBox.setSelected(status);
    }



    
    
}
