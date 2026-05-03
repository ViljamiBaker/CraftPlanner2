package craftPlanner.crafts;

import java.util.ArrayList;

import craftPlanner.Settings;
import craftPlanner.GUI.MainFrame;
import craftPlanner.GUI.planning.PlanFrame;
import craftPlanner.GUI.planning.PlanNode;

public class Registry {
    public static ArrayList<Item> items = new ArrayList<>();
    public static ArrayList<Recipe> recipes = new ArrayList<>();
    public static ArrayList<Machine> machines = new ArrayList<>();

    public static Item getItem(Item i){
        return getItem(i.name());
    }
    public static Item getItem(String name){
        for (Item item : items) {
            if(item.name().equals(name))return item;
        }
        return null;
    }
    public static Item createItem(String name){
        Item item = new Item(name);
        Item oldItem = getItem(name);
        if(oldItem!=null) return oldItem;
        items.add(item);
        MainFrame.mainFrame.updateRegistery();
        return item;
    }
    public static void removeItem(Item item){
        items.remove(item);
        ArrayList<Recipe> RecipesToRemove = new ArrayList<>();
        for (Recipe r : recipes) {
            if(r.producesItem(item)||r.requiresItem(item))
                RecipesToRemove.add(r);
        }
        for (Recipe r : RecipesToRemove){
            MainFrame.mainFrame.addInfo("Info: Removing recipe \"" + r + "\"");
            removeRecipe(r);
        }
        
        ArrayList<Machine> machinesToRemove = new ArrayList<>();
        for (Machine m : machines) {
            if(m.usesItem(item))
                machinesToRemove.add(m);
        }
        for (Machine r : machinesToRemove){
            MainFrame.mainFrame.addInfo("Info: Removing machine \"" + r + "\"");
            removeMachine(r);
        }
    }

    public static Recipe getRecipe(Recipe Recipe){
        for (Recipe r : recipes) {
            if(Recipe.equals(r))return r;
        }
        return null;
    }
    public static Recipe createRecipe(ItemCost[] requirements, ItemCost[] products){
        return createMachineRecipe(requirements, products, null, -1.0);
    }
    public static Recipe createMachineRecipe(ItemCost[] requirements, ItemCost[] products, String machine, double craftTime){
        Machine m = getMachine(machine);
        Recipe Recipe = new Recipe(requirements, products, m, (m==null?-1.0:craftTime));
        Recipe oldRecipe = getRecipe(Recipe);
        if(oldRecipe!=null) return oldRecipe;
        recipes.add(Recipe);
        MainFrame.mainFrame.updateRegistery();
        return Recipe;
    }
    public static void removeRecipe(Recipe r){
        recipes.remove(r);
        for (PlanNode n : PlanFrame.planFrame.nodes) {
            if(n.r.equals(r))
                PlanFrame.planFrame.removePlanNode(n);
        }
    }

    // assume all machines with the same name are the same, 
    // this is not technically correct but idc
    public static Machine getMachine(String name){
        for (Machine m : machines) {
            if(m.name().equals(name))return m;
        }
        return null;
    }
    public static Machine getMachine(Machine machine){
        for (Machine m : machines) {
            if(machine.equals(m))return m;
        }
        return null;
    }
    public static Machine createMachine(String name, ItemCost[] costPerSecond){
        Machine machine = new Machine(name, costPerSecond);
        Machine oldMachine = getMachine(machine);
        if(oldMachine!=null) return oldMachine;
        machines.add(machine);
        return machine;
    }
    public static void removeMachine(Machine m){
        machines.remove(m);
        ArrayList<Recipe> recipesToRemove = new ArrayList<>();
        for (Recipe r : recipes) {
            if(!r.isMachineRecipe()) continue;
            if(r.machine().equals(m))
                recipesToRemove.add(r);
        }
        for (Recipe r : recipesToRemove){
            MainFrame.mainFrame.addInfo("Info: Removing recipe \"" + r + "\"");
            removeRecipe(r);
        }
    }

    public static ItemCost[] createItemCosts(String str){
        if(str.trim().length()==0){
            return new ItemCost[0];
        }
        String[] splits = str.trim().split("[,]");
        ItemCost[] costs = new ItemCost[splits.length];
        for (int i = 0; i < splits.length; i ++) {
            String string = splits[i];
            String clean = string.trim();
            String[] arr = clean.split(" ");
            double count = -1;
            try {
                count = Double.valueOf(arr[0]);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error: Cant convert \"" + arr[0] + "\" to value");
            }
            clean = "";
            for (int j = 1; j < arr.length; j++) {
                clean += arr[j];
            }
            Item item = getItem(clean);
            if(item==null){
                if(Settings.autoCreateItems){
                    item = createItem(clean);
                    MainFrame.mainFrame.addInfo("Info: Automatically created item: \"" + clean + "\"");
                }else{
                    throw new IllegalArgumentException("Error: Item: \"" + clean + "\" Doesn't exist");
                }
            }
            costs[i] = new ItemCost(item, count);
        }
        return costs;
    }
}
