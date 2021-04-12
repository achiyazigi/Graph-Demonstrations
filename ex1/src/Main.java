package ex1.src;

import java.awt.EventQueue;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public final static PrintStream original_stream = System.out;
    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        String os_name = System.getProperty("os.name");
        if(os_name.toLowerCase().startsWith("mac")){
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        System.out.println("running on "+ os_name);
        EventQueue.invokeAndWait(new Runnable() {
            
            @Override
            public void run() {
                
                Gui win = new Gui("HUNGARIAN - Algorithm Visualizer", new WGraph_DS());
                win.setVisible(true);
            
            }
        });
    }
}
