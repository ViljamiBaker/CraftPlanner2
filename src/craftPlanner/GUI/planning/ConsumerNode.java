package craftPlanner.GUI.planning;

import craftPlanner.crafts.Registry;

import craftPlanner.crafts.Item;
import craftPlanner.crafts.ItemCost;
import craftPlanner.crafts.Recipe;

public class ConsumerNode extends PlanNode{
    private ConsumerNode(Recipe r){
        super(r);
    }

    public static ConsumerNode createConsumerNode(Item i){
        Registry.createMachine("Seller", new ItemCost[0]);
        Recipe r = Registry.createMachineRecipe(new ItemCost[]{new ItemCost(i, 1.0)}, new ItemCost[0], new ItemCost[0], "Seller", 1.0, "Sell " + i.name());
        r.hide = true;
        return new ConsumerNode(r);
    }
}
