package craftPlanner.crafts;

import java.util.Arrays;

import craftPlanner.GUI.util.Hideable;

public class Recipe implements Hideable{
    private ItemCost[] requirements;
    private ItemCost[] products; 
    private ItemCost[] costPerSecond;
    private Machine machine;
    private double craftTime;
    private String name;
    public boolean hide = false;
    public Recipe(ItemCost[] requirements, ItemCost[] products, ItemCost[] costPerSecond, Machine machine, double craftTime, String name){
        this.requirements = requirements;
        this.products = products;
        this.costPerSecond = costPerSecond;
        this.machine = machine;
        this.craftTime = craftTime;
        this.name = name;
    }
    public boolean isMachineRecipe(){
        return this.machine!=null;
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
    public final String toString() {
        if(name.length() == 0) return toLongString();
        return name;
    }
    public String toLongString(){
        String recString = Recipe.CreateRecipeString(requirements);
        String prodString = Recipe.CreateRecipeString(products);
        if(recString == null){
            return prodString;
        }
        if(craftTime == -1.0){
            return prodString + " With: " + recString;
        }
        String machineString = "";
        if(costPerSecond.length == 0){
            machineString = machine.toString();
        }else{
            machineString = machine.name() + Recipe.CreateRecipeString(costPerSecond);
        }
        return prodString + " With: " + recString + " Every " + craftTime + " Seconds in " + machineString;
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

    public Item findCommonItem(Recipe r){
        for (ItemCost prod : products) {
            for (ItemCost req : r.requirements) {
                if(prod.item().equals(req.item()))
                    return prod.item();
            }
        }
        return null;
    }

    public boolean isBase(){
        return requirements.length==0;
    }
    public boolean isEnd(){
        return products.length==0;
    }

    public Machine machine() {
        return machine;
    }
    public double craftTime() {
        return craftTime;
    }
    public String name() {
        return name;
    }
    public ItemCost[] products() {
        return products;
    }
    public ItemCost[] costPerSecond() {
        if(costPerSecond != null)
            return costPerSecond;
        if(!isMachineRecipe())
            return new ItemCost[0];
        return machine.costPerSecond();
    }
    public ItemCost[] requirements() {
        return requirements;
    }

    public void setCraftTime(double craftTime) {
        this.craftTime = craftTime;
    }
    public void setMachine(Machine machine) {
        this.machine = machine;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCostPerSecond(ItemCost[] costPerSecond) {
        this.costPerSecond = costPerSecond;
    }
    public void setProducts(ItemCost[] products) {
        this.products = products;
    }
    public void setRequirements(ItemCost[] requirements) {
        this.requirements = requirements;
    }

    public boolean hide(){
        return hide;
    }
}