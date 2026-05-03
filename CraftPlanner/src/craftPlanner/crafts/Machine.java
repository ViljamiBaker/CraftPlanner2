package craftPlanner.crafts;

import java.util.Arrays;

public record Machine(String name, ItemCost[] costPerSecond) {
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Machine)) return false;
        Machine m = (Machine)o;
        return this.name.equals(m.name) && Arrays.equals(this.costPerSecond, m.costPerSecond);
    }
    @Override
    public String toString(){
        return name + " " + Recipe.CreateRecipeString(costPerSecond) + "\s";
    }
    public boolean usesItem(Item i){
        for (ItemCost itemCost : costPerSecond) {
            if(itemCost.item().equals(i))return true;
        }
        return false;
    }
}
