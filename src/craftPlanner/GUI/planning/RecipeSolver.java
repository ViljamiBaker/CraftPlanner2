package craftPlanner.GUI.planning;

import java.util.ArrayList;

import craftPlanner.Settings;
import craftPlanner.GUI.MainFrame;
import craftPlanner.crafts.Item;
import craftPlanner.crafts.ItemCost;
import craftPlanner.crafts.Recipe;
import craftPlanner.crafts.Registry;

public class RecipeSolver {
    public static void solve(Item i){
        RecipeSolver.solve(PlanNodeHelper.createConsumerNode(i));
    }
    public static void solve(Recipe r){
        RecipeSolver.solve(new PlanNode(r));
    }
    public static void solve(PlanNode n0){
        ArrayList<PlanNode> nodes = new ArrayList<>();
        ArrayList<PlanNode> nodesToSolve = new ArrayList<>();
        nodesToSolve.add(n0);
        while (!nodesToSolve.isEmpty()) {
            PlanNode n = nodesToSolve.get(0);
            nodesToSolve.remove(0);
            nodes.add(n);
            // find what we dont already make
            ArrayList<Item> itemsToMake = new ArrayList<>();
            for (ItemCost ic: n.r.requirements()) {
                boolean foundItem = false;
                for (PlanNode planNode : nodes) {
                    if(planNode.r.producesItem(ic.item())&&planNode.r.isMachineRecipe()==n.r.isMachineRecipe()){
                        PlanNode.connect(planNode,n,ic.item(), 0.0);
                        foundItem = true;
                        break;
                    }
                }
                if(foundItem)
                    continue;
                itemsToMake.add(ic.item());
            }
            // make it
            for (Item item : itemsToMake) {
                Recipe[] recipesToUse = Registry.getRecipe(item, n.r.isMachineRecipe());
                PlanNode n2;
                if(recipesToUse.length == 0){   
                    System.out.println(Settings.createConsumerifNoRecipe);
                    if(Settings.createConsumerifNoRecipe){
                        MainFrame.mainFrame.addInfo("Info: Creating producer for \"" + item.name() + "\"");
                        n2 = PlanNodeHelper.createProducerNode(item);
                    }else{
                        MainFrame.mainFrame.addInfo("Info: No recipe found for \"" + item.name() + "\"");
                        continue;
                    }
                }else{
                    // TODO: make this smarter
                    Recipe r2 = recipesToUse[0];
                    n2 = new PlanNode(r2);
                }
                PlanNode.connect(n2,n,item, 0.0);
                nodesToSolve.add(n2);
            }
        }
        for (PlanNode planNode : nodes) {
            PlanFrame.planFrame.addPlanNode(planNode);
        }
    }
}
