package craftPlanner.fileIO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.awt.Point;

import javax.swing.filechooser.FileFilter;

import craftPlanner.Settings;
import craftPlanner.GUI.MainFrame;
import craftPlanner.GUI.planning.NodeConnection;
import craftPlanner.GUI.planning.PlanFrame;
import craftPlanner.GUI.planning.PlanNode;
import craftPlanner.crafts.Item;
import craftPlanner.crafts.ItemCost;
import craftPlanner.crafts.Machine;
import craftPlanner.crafts.Recipe;
import craftPlanner.crafts.Registry;

public class FileIO {

    public static enum FileType {
        REGISTRY,
        PLAN,
        COMBINED;
        @Override
        public String toString(){
            switch (this) {
                case REGISTRY:
                    return "Registry";
                case PLAN:
                    return "Plan";
                case COMBINED:
                    return "Combined";
            }
            return "";
        }
    }

    private static final String SECTION_SPLIT = "|";
    private static final String ITEM_SPLIT = "\n";
    private static final String COMPONENT_SPLIT = ":";
    private static final String LIST_SPLIT = ";";
    private static final String COST_SPLIT = ",";
    private static final String FILE_SPLIT = "&";
    private static FileFilter createFilter(String ext, String disc){
        return new FileFilter() {
            public boolean accept(File file){
		        if (file.getName().endsWith(ext)||file.isDirectory()) {
		        	return true;
		        }
                return false;
            }
            @Override
            public String getDescription() {
                return disc;
            }
        };
    }
    private static final FileFilter REGISTRY_FILTER = createFilter(".craftregistry", "Only Registry Files");
    private static final FileFilter PLAN_FILTER = createFilter(".craftplan", "Only Plan Files");
    private static final FileFilter COMBINED_FILTER = createFilter(".craftfile", "Only Combined Files");
    public static FileFilter getRegistryFilter(){
        return REGISTRY_FILTER;
    }
    public static FileFilter getPlanFilter(){
        return PLAN_FILTER;
    }
    public static FileFilter getCombinedFilter(){
        return COMBINED_FILTER;
    }
    private static <T> int indexOf(T[] arr, T i){
        for (int j = 0; j < arr.length; j++) {
            if(arr[j].equals(i))
                return j;
        }
        return -1;
    }

    private static String toSaveString(ItemCost[] arr, Item[] code){
        if(arr == null) return "[]";
        int iMax = arr.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(arr[i].cost() +  COST_SPLIT + indexOf(code,arr[i].item()));
            if (i == iMax)
                return b.append(']').toString();
            b.append(LIST_SPLIT);
        }
    }

    private static ItemCost[] decodeSaveString(String in, Item[] code){
        in = in.substring(1,in.length()-1);
        if(in.isEmpty()) return new ItemCost[0];
        String[] split = in.split("[" + LIST_SPLIT + "]");
        ItemCost[] ret = new ItemCost[split.length];
        for (int i = 0; i < ret.length; i++) {
            String[] costSplit = split[i].split("[" + COST_SPLIT + "]");
            String item = costSplit[1];
            String cost = costSplit[0];
            ret[i] = new ItemCost(code[Integer.valueOf(item)], Double.valueOf(cost));
        }
        return ret;
    }

    public static void saveRegistery(File file, boolean overwrite){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file,!overwrite))) {
            Item[] items = Registry.items.toArray(new Item[0]);
            for (int i = 0; i < items.length; i++) {
                writer.write(items[i].name() + ITEM_SPLIT);
            }
            writer.write(SECTION_SPLIT + "\n");
            Machine[] machines = Registry.machines.toArray(new Machine[0]);
            for (int i = 0; i < machines.length; i++) {
                writer.write(
                    machines[i].name() + COMPONENT_SPLIT + 
                    toSaveString(machines[i].costPerSecond(),items) + ITEM_SPLIT);
            }
            writer.write(SECTION_SPLIT + "\n");
            Recipe[] recipes = Registry.recipes.toArray(new Recipe[0]);
            for (int i = 0; i < recipes.length; i++) {
                writer.write(
                    toSaveString(recipes[i].requirements(),items) + COMPONENT_SPLIT + 
                    toSaveString(recipes[i].products(),items) + COMPONENT_SPLIT + 
                    toSaveString(recipes[i].costPerSecond(),items) + COMPONENT_SPLIT + 
                    indexOf(machines,recipes[i].machine()) + COMPONENT_SPLIT + 
                    recipes[i].craftTime() + COMPONENT_SPLIT + 
                    (recipes[i].name().length() == 0? " " + COMPONENT_SPLIT:recipes[i].name()) + ITEM_SPLIT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(overwrite)
            changeExtension(file, ".craftregistry");
    }
    private static void trimArr(String[] arr){
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].trim();
        }
    }
    public static void loadRegistery(File file){
        String str = "";
        try {
            str = Files.readString(Paths.get(file.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadRegistery(str);
    }
    public static void loadRegistery(String str){
        if(str.trim().length()<=2) return;
        Registry.items.clear();
        Registry.recipes.clear();
        Registry.machines.clear();
        MainFrame.mainFrame.updateRegistery();

        String[] itemrecmac = str.split("[" + SECTION_SPLIT + "]");
        trimArr(itemrecmac);

        String[] itemsStr = itemrecmac[0].split("[" + ITEM_SPLIT + "]");
        trimArr(itemsStr);
        Item[] items = new Item[itemsStr.length];
        for (int i = 0; i < items.length; i++) {
            String itemStr = itemsStr[i];
            String[] itemSplit = itemStr.split("[" + COMPONENT_SPLIT + "]");
            Item item = Registry.createItem(itemSplit[0]);
            items[i] = item;
        }

        String[] machineStr = itemrecmac[1].split("[" + ITEM_SPLIT + "]");
        trimArr(machineStr);
        Machine[] machines = new Machine[machineStr.length];
        for (int i = 0; i < machines.length; i++) {
            String machinestr = machineStr[i];
            String[] machineplit = machinestr.split("[" + COMPONENT_SPLIT + "]");
            Machine machine = Registry.createMachine(machineplit[0],decodeSaveString(machineplit[1], items));
            machines[i] = machine;
        }

        String[] recipeStr = itemrecmac[2].split("[" + ITEM_SPLIT + "]");
        trimArr(recipeStr);
        Recipe[] recipies = new Recipe[recipeStr.length];
        for (int i = 0; i < recipies.length; i++) {
            String recstr = recipeStr[i];
            String[] recsplit = recstr.split("[" + COMPONENT_SPLIT + "]");
            String req = recsplit[0];
            String prod = recsplit[1];
            String cps = recsplit[2];
            int mac = Integer.valueOf(recsplit[3]);
            double time = Double.valueOf(recsplit[4]);
            String name = recsplit[5];
            name = name.trim();
            Recipe recipie = null;
            if(time < 0.0){
                recipie = Registry.createRecipe(decodeSaveString(req, items), decodeSaveString(prod, items), name);
            }else{
                ItemCost[] costspersec = decodeSaveString(cps, items);
                if(costspersec.length == 0) costspersec = null;
                recipie = Registry.createMachineRecipe(decodeSaveString(req, items), decodeSaveString(prod, items), costspersec, machines[mac].name(), time, name);
            }
            recipies[i] = recipie;
        }
        MainFrame.mainFrame.updateRegistery();
    }

    public static void savePlan(File file, boolean overwrite){
        PlanFrame plan = MainFrame.mainFrame.plan;
        Recipe[] recipes = Registry.recipes.toArray(new Recipe[0]);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file,!overwrite))) {
            for (int i = 0; i < plan.nodes.size(); i++) {
                PlanNode n = plan.nodes.get(i);
                writer.write(
                    indexOf(recipes, n.r) + COMPONENT_SPLIT + 
                    n.craftCount + COMPONENT_SPLIT + 
                    n.getBounds().x + COMPONENT_SPLIT + 
                    n.getBounds().y + ITEM_SPLIT);
            }
            writer.write(SECTION_SPLIT + "\n");
            for (int i = 0; i < plan.nodes.size(); i++) {
                PlanNode n = plan.nodes.get(i);
                for (int j = 0; j < n.incomingConnections.size(); j++) {
                    NodeConnection nc = n.incomingConnections.get(j);
                    writer.write(
                        i + COMPONENT_SPLIT + 
                        plan.nodes.indexOf(nc.from) + COMPONENT_SPLIT + 
                        Registry.items.indexOf(nc.cost.item()) + COMPONENT_SPLIT + 
                        nc.cost.cost() + ITEM_SPLIT);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(overwrite)
            changeExtension(file, ".craftplan");
    }
    public static void loadPlan(File file){
        String str = "";
        try {
            str = Files.readString(Paths.get(file.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadPlan(str);
    }
    public static void loadPlan(String str){
        if(str.trim().length()<=1) return;
        if(Settings.removePlanOnLoad)
            while (MainFrame.mainFrame.plan.nodes.size()>0)
                MainFrame.mainFrame.plan.removePlanNode(MainFrame.mainFrame.plan.nodes.get(0));
        String[] nodeconn = str.split("[" + SECTION_SPLIT + "]");
        trimArr(nodeconn);
        
        String[] nodesStr = nodeconn[0].split("[" + ITEM_SPLIT + "]");
        trimArr(nodesStr);
        PlanNode[] nodes = new PlanNode[nodesStr.length];
        for (int i = 0; i < nodes.length; i++) {
            String nodeStr = nodesStr[i];
            String[] nodeSplit = nodeStr.split("[" + COMPONENT_SPLIT + "]");
            int rIdx = Integer.valueOf(nodeSplit[0]);
            double rCount = Double.valueOf(nodeSplit[1]);
            int rpx = Integer.valueOf(nodeSplit[2]);
            int rpy = Integer.valueOf(nodeSplit[3]);
            PlanNode n = PlanFrame.planFrame.addPlanNode(Registry.recipes.get(rIdx), new Point(rpx,rpy));
            n.craftCount = rCount;
            n.setCraftCountText();
            nodes[i] = n;
        }
        
        String[] consstr = nodeconn[1].split("[" + ITEM_SPLIT + "]");
        trimArr(consstr);
        for (int i = 0; i < consstr.length; i++) {
            String constr = consstr[i];
            String[] consplit = constr.split("[" + COMPONENT_SPLIT + "]");
            int nid1 = Integer.valueOf(consplit[0]);
            int nid2 = Integer.valueOf(consplit[1]);
            int iid = Integer.valueOf(consplit[2]);
            double rCount = Double.valueOf(consplit[3]);
            PlanNode connectingNode = nodes[nid2];
            PlanNode selectedNode = nodes[nid1];
            Item item = Registry.items.get(iid);
            NodeConnection nc = new NodeConnection(connectingNode, selectedNode, new ItemCost(item, rCount));
            connectingNode.outgoingConnections.add(nc);
            selectedNode.incomingConnections.add(nc);
        }
    }

    public static void saveCombined(File file){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        //save
        saveRegistery(file, false);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file,true))) {
            writer.write(FILE_SPLIT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        savePlan(file, false);
        changeExtension(file, ".craftfile");
    }

    public static void loadCombined(File file){
        String str = "";
        try {
            str = Files.readString(Paths.get(file.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] regplan = str.trim().split("[" + FILE_SPLIT + "]");
        trimArr(regplan);
        loadRegistery(regplan[0]);
        loadPlan(regplan[1]);
    }

    public static File changeExtension(File f, String newExtension) {
        int i = f.getName().lastIndexOf('.');
        String name = null;
        if(i == -1){
            name = f.getName();
        }else{
            name = f.getName().substring(0,i);
        }
        try {
            Files.move(Paths.get(f.getPath()), Paths.get(f.getParent() +"\\" + name + newExtension));
        } catch (Exception e) {
            System.out.println(e);
        }
        return f;
    }

    public static void file(File f, boolean save, FileType type){
        switch (type) {
            case REGISTRY:
                if(save){
                    saveRegistery(f, true);
                }else{
                    loadRegistery(f);
                }
                break;
            case PLAN:
                if(save){
                    savePlan(f,true);
                }else{
                    loadPlan(f);
                }
                break;
            case COMBINED:
                if(save){
                    saveCombined(f);
                }else{
                    loadCombined(f);
                }
                break;
        }
    }
}
