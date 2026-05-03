package craftPlanner.GUI.actions;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
//https://stackoverflow.com/questions/874360/swing-creating-a-draggable-component
public class DraggableComponent
    extends JComponent implements MouseMotionListener,MouseListener {

    private volatile int screenX = 0;
    private volatile int screenY = 0;
    private volatile int myX = 0;
    private volatile int myY = 0;
    public volatile double scale = 1.0;
    protected volatile int minlayer = 0;

    public DraggableComponent() {
        this.addMouseMotionListener(this);
        this.addMouseListener(this);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        Dimension thisSize = this.getSize();
        Container parent = this.getParent();
        if(parent == null) return;
        int deltaX = e.getXOnScreen() - screenX;
        int deltaY = e.getYOnScreen() - screenY;
        int newx = myX + deltaX;
        int newy = myY + deltaY;
        if(thisSize.width<parent.getWidth()){
            newx = Math.max(Math.min(newx, parent.getWidth()-thisSize.width), 0);
        }else{
            newx = Math.max(Math.min(newx, 0), parent.getWidth()-thisSize.width);
        }
        if(thisSize.height<parent.getHeight()){
            newy = Math.max(Math.min(newy, parent.getHeight()-thisSize.height), 0);
        }else{
            newy = Math.max(Math.min(newy, 0), parent.getHeight()-thisSize.height);
        }
        setLocation(newx, newy);
        moveTo(minlayer);
    }
    
    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) {
        screenX = e.getXOnScreen();
        screenY = e.getYOnScreen();
        
        myX = getX();
        myY = getY();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) { }
    
    @Override
    public void mouseEntered(MouseEvent e) { }
    
    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        g2d.scale(scale, scale);
        super.paint(g2d);
    }

    public void moveTo(int idx){
        Container parent = this.getParent();
        if(parent == null) return;
        parent.setComponentZOrder(this, idx);
        parent.repaint();
    }
}