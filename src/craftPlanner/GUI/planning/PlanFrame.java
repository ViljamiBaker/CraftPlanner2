package craftPlanner.GUI.planning;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import craftPlanner.GUI.Keyboard;
import craftPlanner.GUI.MainFrame;
import craftPlanner.crafts.Recipe;

public class PlanFrame extends JPanel{
    public static PlanFrame planFrame = null;
    public static class PlanPanel extends JPanel{
        public PlanPanel(){
            this.setBounds(0,0,2000,2000);
            this.setOpaque(false);
            this.setVisible(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(5));
            Point p0 = SwingUtilities.convertPoint(MainFrame.mainFrame.plan, 0,0, this.getParent());
            Rectangle rect0 = new Rectangle(p0.x,p0.y,MainFrame.mainFrame.plan.getSize().width,MainFrame.mainFrame.plan.getSize().height);
            Area clip = new Area(rect0);
            for (PlanNode n1 : PlanFrame.planFrame.nodes) {
                Point p1 = SwingUtilities.convertPoint(n1, 0,0, this.getParent());
                Rectangle rect = new Rectangle(p1.x,p1.y,n1.getSize().width,n1.getSize().height);
                clip.subtract(new Area(rect));
            }
            g2d.setClip(clip);
            for (PlanNode planNode : PlanFrame.planFrame.nodes) {
                if(planNode.outgoingConnections.size() == 0)
                    paintChildren(g2d,planNode);
            }
        }
        private boolean contains(double min, double max, double x){
            return x>=min&&x<=max;
        }
        public void paintChildren(Graphics2D g2d, PlanNode n1){
            Point center1 = SwingUtilities.convertPoint(n1, n1.getSize().width/2,n1.getSize().height/2, this.getParent());
            Point n1Min = SwingUtilities.convertPoint(n1, 0,0, this.getParent());
            Point n1Max = new Point(n1Min.x + n1.getSize().width, n1Min.y + n1.getSize().height);
            for (NodeConnection c : n1.incomingConnections) {
                PlanNode n2 = c.from;
                Point center2 = SwingUtilities.convertPoint(n2, n2.getSize().width/2,n2.getSize().height/2, this.getParent());
                Point2D delta = new Point2D.Double(center1.x - center2.x, center1.y - center2.y);
                double length = delta.distance(0, 0);
                delta = new Point2D.Double((delta.getX()/length),(delta.getY()/length));
                double txl = (n1Min.x - center2.x)/delta.getX();
                double txh = (n1Max.x - center2.x)/delta.getX();
                double tyl = (n1Min.y - center2.y)/delta.getY();
                double tyh = (n1Max.y - center2.y)/delta.getY();
                double t = Double.POSITIVE_INFINITY;
                if(txl>0.0&&txl<t&&contains(n1Min.y,n1Max.y,center2.y + delta.getY() * txl)) t = txl;
                if(txh>0.0&&txh<t&&contains(n1Min.y,n1Max.y,center2.y + delta.getY() * txh)) t = txh;
                if(tyl>0.0&&tyl<t&&contains(n1Min.x,n1Max.x,center2.x + delta.getX() * tyl)) t = tyl;
                if(tyh>0.0&&tyh<t&&contains(n1Min.x,n1Max.x,center2.x + delta.getX() * tyh)) t = tyh;
                g2d.drawLine(center1.x,center1.y,center2.x,center2.y);
                g2d.fillOval((int)(center2.x + delta.getX() * t)-10, (int)(center2.y + delta.getY() * t)-10, 20, 20);
                paintChildren(g2d,n2);
            }
        }  
        @Override
        public boolean contains(int x, int y) {
            return false;
        }
    }

    Keyboard keyboard;
    JPanel panel;
    PlanPanel paintpanel;
    public ArrayList<PlanNode> nodes = new ArrayList<>();

    public PlanFrame(Keyboard keyboard){
        this.setVisible(true);
        this.setBackground(Color.GREEN);
        this.getGraphics();
        this.setLayout(new BorderLayout(0,0));

        panel = new JPanel(null);
        panel.setPreferredSize(new Dimension(2000, 2000));
        panel.setBackground(Color.WHITE);
        //paintpanel = new PlanPanel();
        //paintpanel.setOpaque(false);
        //paintpanel.setBackground(new Color(0,0,0,0));
        //panel.add(paintpanel);
        
        JScrollPane panelScroll = new JScrollPane(panel);
        panelScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelScroll.getVerticalScrollBar().setUnitIncrement(16);
        panelScroll.getHorizontalScrollBar().setUnitIncrement(16);
        this.add(panelScroll,BorderLayout.CENTER);
        PlanFrame.planFrame = this;
    }
    public PlanNode addPlanNode(Recipe r){
        return addPlanNode(r, SwingUtilities.convertPoint(this, 10 + (nodes.size() % 10) * 10,10 + (nodes.size() % 10) * 6, panel));
    }
    public PlanNode addPlanNode(Recipe r, Point p){
        PlanNode node = new PlanNode(r);
        panel.add(node);
        panel.repaint();
        SwingUtilities.updateComponentTreeUI(panel);
        nodes.add(node);
        node.setLocation(p);
        return node;
    }
    public void removePlanNode(PlanNode n){
        nodes.remove(n);
        // son im crine
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                panel.remove(n);
                panel.repaint();
                SwingUtilities.updateComponentTreeUI(panel);
                if(n.equals(MainFrame.mainFrame.editor.selectedNode))
                    MainFrame.mainFrame.editor.deselectNode();
                for (NodeConnection nc : n.incomingConnections) {
                    nc.from.outgoingConnections.remove(nc);
                }
                n.incomingConnections.clear();
                for (NodeConnection nc : n.outgoingConnections) {
                    nc.to.incomingConnections.remove(nc);
                }
                n.outgoingConnections.clear();
            }
        });
    }

    public void updateAllLayers(){
        // layers
        for (PlanNode planNode : nodes) {
            planNode.layer = -1;
        }
        for (PlanNode planNode : nodes) {
            if(planNode.isParent()){
                planNode.updateLayer(-1);
            }
        }
    }
}
