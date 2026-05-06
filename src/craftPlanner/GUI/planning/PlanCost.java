package craftPlanner.GUI.planning;

import craftPlanner.crafts.ItemCost;
import craftPlanner.crafts.RecipeCost;

public record PlanCost (
    RecipeCost[] totalCost,
    ItemCost[] baseCost,
    ItemCost[] machineCost
){}
