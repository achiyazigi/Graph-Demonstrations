package ex1.src;

public class Main {
    public static void main(String[] args) {
        String os_name = System.getProperty("os.name");
        if(os_name.toLowerCase().startsWith("mac")){
            System.out.println("running on "+ os_name);
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
        Gui win = new Gui("HUNGARIAN - Algorithm Visualizer", new WGraph_DS());
        win.setVisible(true);
    }
}
