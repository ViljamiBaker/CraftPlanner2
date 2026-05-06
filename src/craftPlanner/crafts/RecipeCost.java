package craftPlanner.crafts;

import java.util.ArrayList;

public class RecipeCost{
    private Recipe recipe;
    private double cost;
    public RecipeCost(Recipe item,double cost){
        this.recipe = item;
        this.cost = cost;
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof RecipeCost)) return false;
        RecipeCost i = (RecipeCost)o;
        return this.recipe.equals(i.recipe) && this.cost == i.cost;
    }
    public Recipe recipe(){
        return this.recipe;
    }
    public double cost() {
        return cost;
    }
    public void setRecipe(Recipe item) {
        this.recipe = item;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    public static void merge(ArrayList<RecipeCost> a, RecipeCost[] b){
        ArrayList<RecipeCost> excess = new ArrayList<>();
        for (RecipeCost r : b) {
            int idx = a.indexOf(r);
            if(idx == -1){
                excess.add(r);
                continue;
            }
            RecipeCost og = a.get(idx);
            og.setCost(og.cost + r.cost);
            a.set(idx, og);
        }
        for (RecipeCost r : excess) {
            a.add(r);
        }
    }
    public static RecipeCost[] clone(RecipeCost[] a, double scalar){
        RecipeCost[] b = new RecipeCost[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = new RecipeCost(a[i].recipe(), a[i].cost()*scalar);
        }
        return b;
    }
}
