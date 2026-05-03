package craftPlanner.crafts;

import java.util.Arrays;

public record Recipe(ItemCost[] requirements, ItemCost[] products, Machine machine, double craftTime) {
    public boolean isMachineRecipe(){
        return this.machine()!=null;
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Recipe)) return false;
        Recipe r = (Recipe)o;
        boolean machineEquals;
        if(this.isMachineRecipe())
            machineEquals = this.machine.equals(r.machine) && (this.craftTime == r.craftTime);
        else
            machineEquals = !r.isMachineRecipe();
        return machineEquals && Arrays.equals(this.requirements, r.requirements) && Arrays.equals(this.products, r.products);
    }
    public boolean requiresItem(Item i){
        for (ItemCost itemCost : requirements) {
            if(itemCost.item().equals(i))return true;
        }
        return false;
    }
    public boolean producesItem(Item i){
        for (ItemCost itemCost : products) {
            if(itemCost.item().equals(i))return true;
        }
        return false;
    }
    @Override
    public String toString(){
        String recString = Recipe.CreateRecipeString(requirements);
        String prodString = Recipe.CreateRecipeString(products);
        if(recString == null){
            return prodString;
        }
        if(craftTime == -1.0){
            return prodString + " With: " + recString;
        }
        return prodString + " With: " + recString + " Every " + craftTime + " Seconds in " + machine;
    }

    public static String CreateRecipeString(ItemCost[] requirements) {
        int iMax = requirements.length - 1;
        if (iMax == -1)
            return "Nothing";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(requirements[i].cost() +  " " + requirements[i].item().name());
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public double getCost(Item i){
        for (ItemCost cost : requirements) {
            if(cost.item().equals(i))
                return cost.cost();
        }
        return -1.0;
    }
    public double getProduction(Item i){
        for (ItemCost cost : products) {
            if(cost.item().equals(i))
                return cost.cost();
        }
        return -1.0;
    }
}