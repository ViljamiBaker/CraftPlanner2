package craftPlanner.GUI.planning;

import java.util.ArrayList;
import java.util.HashMap;

import craftPlanner.crafts.Item;
import craftPlanner.crafts.ItemCost;
import craftPlanner.crafts.Recipe;

public class RecipePlan {
    public Recipe craft;
    public double count;
    // if true we try to make count items/s
    public boolean itemsPerSecond;
    public ArrayList<RecipePlan> previousSteps = new ArrayList<>();
    public ArrayList<RecipePlan> nextSteps = new ArrayList<>();

    public RecipePlan(Recipe craft, double count){
        this.craft = craft;
        this.count = count;
    }

    public void calculate(){
        if(finalRecipe()){
            for (RecipePlan recipePlan : previousSteps) {
                recipePlan.calculate();
            }
            return;
        }
        HashMap<Item,Double> costs = new HashMap<>();
        for (ItemCost cost : craft.products()) {
            costs.put(cost.item(), 0.0);
        }
        for (RecipePlan recipePlan : nextSteps) {
            for (ItemCost cost : recipePlan.craft.requirements()) {
                System.out.println(cost);
            }
        }
    }

    public void addNextStep(RecipePlan nextStep){
        nextSteps.add(nextStep);
        calculate();
    }

    public boolean finalRecipe(){
        return nextSteps.isEmpty();
    }
}
