package craftPlanner.GUI.planning;

import craftPlanner.crafts.Registry;

import java.awt.Color;

import craftPlanner.crafts.Item;
import craftPlanner.crafts.ItemCost;
import craftPlanner.crafts.Recipe;

public class PlanNodeHelper{

    public static PlanNode createConsumerNode(Item i){
        Registry.createMachine("Seller", new ItemCost[0]);
        Recipe r = Registry.createMachineRecipe(new ItemCost[]{new ItemCost(i, 1.0)}, new ItemCost[0], new ItemCost[0], "Seller", 1.0, "Sell " + i.name());
        r.hide = true;
        PlanNode p = new PlanNode(r);
        p.panel.setBackground(new Color(110, 110, 110));
        return p;
    }
    public static PlanNode createProducerNode(Item i){
        Registry.createMachine("Buyer", new ItemCost[0]);
        Recipe r = Registry.createMachineRecipe(new ItemCost[0], new ItemCost[]{new ItemCost(i, 1.0)}, new ItemCost[0], "Buyer", 1.0, "Buy " + i.name());
        r.hide = true;
        PlanNode p = new PlanNode(r);
        p.panel.setBackground(new Color(140, 38, 38));
        return p;
    }
}
