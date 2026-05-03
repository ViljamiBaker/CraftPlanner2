package craftPlanner.GUI.planning;

import craftPlanner.crafts.ItemCost;
// this assumes that
// A: from PRODUCES cost.item
// B: to CONSUMES cost.item
public class NodeConnection {
    PlanNode from;
    PlanNode to;
    ItemCost cost;
    boolean fufilled = false;
    public NodeConnection(PlanNode from, PlanNode to, ItemCost cost){
        this.from = from;
        this.to = to;
        this.cost = cost;
    }
}
