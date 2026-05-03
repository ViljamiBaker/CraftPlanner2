package craftPlanner.GUI.planning;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import craftPlanner.GUI.Keyboard;
import craftPlanner.GUI.MainFrame;
import craftPlanner.crafts.Recipe;

public class PlanFrame extends JPanel{
    public static PlanFrame planFrame = null;
    private class PlanPanel extends JPanel{
        public PlanPanel(LayoutManager lm){
            super(lm);
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.setColor(Color.black);
            g.drawLine(10, 10, 1000, 1000);
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
        paintpanel = new PlanPanel(null);
        paintpanel.setPreferredSize(new Dimension(2000, 2000));
        panel.add(paintpanel,0);
        
        JScrollPane panelScroll = new JScrollPane(panel);
        panelScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelScroll.getVerticalScrollBar().setUnitIncrement(16);
        panelScroll.getHorizontalScrollBar().setUnitIncrement(16);
        this.add(panelScroll,BorderLayout.CENTER);
        PlanFrame.planFrame = this;
    }

    public void addPlanNode(Recipe r){
        PlanNode node = new PlanNode(r);
        panel.add(node);
        panel.repaint();
        SwingUtilities.updateComponentTreeUI(panel);
        nodes.add(node);
    }
    public void removePlanNode(PlanNode n){
        // son im crine
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                panel.remove(n);
                panel.repaint();
                SwingUtilities.updateComponentTreeUI(panel);
                nodes.remove(n);
                if(MainFrame.mainFrame.editor.selectedNode.equals(n))
                MainFrame.mainFrame.editor.deselectNode();
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
        //// update
        //for (PlanNode planNode : nodes) {
        //    if(planNode.isParent()){
        //        planNode.update(-1);
        //    }
        //}
    }
}
