package craftPlanner.crafts;

public record Item(String name){
    @Override
    public boolean equals(Object o){
        if(!(o instanceof Item)) return false;
        return ((Item)o).name.equals(this.name);
    }
    @Override
    public String toString(){
        return name;
    }
}