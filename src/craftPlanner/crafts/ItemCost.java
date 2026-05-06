package craftPlanner.crafts;

import java.util.ArrayList;

public class ItemCost{
    private Item item;
    private double cost;
    public ItemCost(Item item,double cost){
        this.item = item;
        this.cost = cost;
    }
    @Override
    public boolean equals(Object o){
        if(!(o instanceof ItemCost)) return false;
        ItemCost i = (ItemCost)o;
        return this.item.equals(i.item);// && this.cost == i.cost; // deja vu
    }
    public Item item(){
        return this.item;
    }
    public double cost() {
        return cost;
    }
    public void setItem(Item item) {
        this.item = item;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    public static void merge(ArrayList<ItemCost> a, ItemCost[] b){
        ArrayList<ItemCost> excess = new ArrayList<>();
        for (ItemCost c : b) {
            int idx = a.indexOf(c);
            if(idx == -1){
                excess.add(c);
                continue;
            }
            ItemCost og = a.get(idx);
            og.setCost(og.cost + c.cost);
            a.set(idx, og);
        }
        for (ItemCost c : excess) {
            a.add(c);
        }
    }
    public static ItemCost[] clone(ItemCost[] a, double scalar){
        ItemCost[] b = new ItemCost[a.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = new ItemCost(a[i].item(), a[i].cost()*scalar);
        }
        return b;
    }
}
