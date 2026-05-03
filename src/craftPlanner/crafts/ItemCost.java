package craftPlanner.crafts;

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
        return this.item.equals(i.item) && this.cost == i.cost;
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
}
