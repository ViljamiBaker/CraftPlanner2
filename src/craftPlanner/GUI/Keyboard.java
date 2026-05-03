package craftPlanner.GUI;

import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener{
    @FunctionalInterface
    public interface KeyboardRunnable {
        public abstract void run(int key);
    }
    HashSet<Integer> keysHeld = new HashSet<>();
    HashSet<Integer> keysPressed = new HashSet<>();
    Keyboard(JFrame frame){
        frame.addKeyListener(this);
        frame.requestFocus();
    }
    @Override
    public void keyPressed(KeyEvent e){
        keysHeld.add(e.getKeyCode());
        keysPressed.add(e.getKeyCode());
        System.out.println(e.getKeyChar());
    }
    @Override
    public void keyReleased(KeyEvent e){
        keysHeld.remove(e.getKeyCode());
    }
    @Override
    public void keyTyped(KeyEvent e){}

    public void update(){
        keysPressed.clear();
        for (KeyboardRunnable runnable : keydownrunnables) {
            for (Integer key : keysHeld) {
                runnable.run(key);
            }
        }
    }

    public boolean keyDown(int key){
        return keysHeld.contains(key);
    }
    public boolean keyPressed(int key){
        return keysPressed.contains(key);
    }

    private ArrayList<KeyboardRunnable> keydownrunnables = new ArrayList<>();

    public void addKeyDownRunnable(KeyboardRunnable r) {
        keydownrunnables.add(r);
    }
}