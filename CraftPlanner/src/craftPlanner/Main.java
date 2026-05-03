package craftPlanner;

import java.util.concurrent.TimeUnit;

import craftPlanner.GUI.MainFrame;

public class Main {
    public static void main(String[] args) {
        new MainFrame();
        
        while(true){
            long t = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            MainFrame.mainFrame.update();
            long t2 = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            try {Thread.sleep((int)Math.max(16-(t2-t),0));}catch(InterruptedException e){}
        }
    }
}
