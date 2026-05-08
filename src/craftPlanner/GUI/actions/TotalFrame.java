package craftPlanner.GUI.actions;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import craftPlanner.GUI.planning.PlanCost;
import craftPlanner.GUI.planning.PlanNode;
import craftPlanner.crafts.ItemCost;
import craftPlanner.crafts.RecipeCost;

public class TotalFrame extends JFrame{    
    public TotalFrame(PlanNode parent){
        this.setTitle("Recipe for: " + parent.toString());
        this.setContentPane(createContentPane(parent));
        this.setVisible(true);
        this.setSize(500, 800);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(500,500));
    }

    public JPanel createContentPane(PlanNode parent){
        JPanel panel = new JPanel(new GridLayout(1, 1));

        JTextArea info = new JTextArea();

        PlanCost cost = parent.toPlanCost();
        String str = "Recipe for: " + parent.toString() + "\n";
        if(!parent.r.isMachineRecipe()){
            str += "-------- ITEMS --------\n";
            for (ItemCost c : cost.baseCost()) {
                str += c.cost() + " of " + c.item().name() + "\n";
            }
        }
        str += "-------- RECIPIES --------\n";
        for (int i = cost.totalCost().length-1; i>=0; i--) {
            RecipeCost c = cost.totalCost()[i];
            if(c.recipe().products().length == 0) continue;
            str += "Craft " + c.cost() + " of " + c.recipe().toString() + "\n";
        }
        if(parent.r.isMachineRecipe()){
            str += "-------- MACHINE RESOURCES --------\n";
            for (ItemCost c : cost.machineCost()) {
                str += c.cost() + " " + c.item().name() + "/s\n";
            }
        }
        str += "-------- EXESS --------\n";
        for (ItemCost c : cost.excessItems()) {
            str += c.cost() + " of " + c.item().name() + "\n";
        }
        info.setText(str);

        info.setRows(info.getRows()+2);
        info.setEditable(false);
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        info.setPreferredSize(new Dimension(800, 180));
        JScrollPane infoScroll = new JScrollPane(info);
        infoScroll.setPreferredSize(new Dimension(1000, 180));
        infoScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        infoScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(infoScroll);

        return panel;
    }
}
