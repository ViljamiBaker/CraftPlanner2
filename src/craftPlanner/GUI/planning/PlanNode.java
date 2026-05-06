package craftPlanner.GUI.planning;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import craftPlanner.Settings;
import craftPlanner.GUI.MainFrame;
import craftPlanner.GUI.actions.DraggableComponent;
import craftPlanner.crafts.Item;
import craftPlanner.crafts.ItemCost;
import craftPlanner.crafts.Recipe;
import craftPlanner.crafts.RecipeCost;

public class PlanNode extends DraggableComponent{

    private JTextField selectedIndecator;
    private JTextField craftIndecator;
    private JTextField recName;
    private JTextField prodName;
    private JTextField craftCountText;

    private JTextField createTextFeild(String str, boolean highlightable, boolean outline){
        JTextField text = new JTextField(str);
        text.setEditable(false);
        text.setFocusable(highlightable);
        text.setOpaque(outline);
        if(!outline)
            text.setBorder(BorderFactory.createEmptyBorder());
        return text;
    }

    private void setupFrame(){
        this.setBounds(0,0,120,100);
        this.setOpaque(false);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        GridBagConstraints c = new GridBagConstraints();
        panel.setSize(120,100);
        panel.setBackground(new Color(80,80,80));

        selectedIndecator = new JTextField("Deselected");
        selectedIndecator.setBackground(Color.RED);
        selectedIndecator.setEditable(false);
        selectedIndecator.setFocusable(false);
        selectedIndecator.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        selectedIndecator.setHorizontalAlignment(JTextField.CENTER);
        selectedIndecator.addMouseListener(this);
        selectedIndecator.addMouseMotionListener(this);

        craftIndecator = new JTextField("Sad");
        craftIndecator.setBackground(Color.RED);
        craftIndecator.setEditable(false);
        craftIndecator.setFocusable(false);
        craftIndecator.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        craftIndecator.setHorizontalAlignment(JTextField.CENTER);
        craftIndecator.addMouseListener(this);
        craftIndecator.addMouseMotionListener(this);

        JPanel otherStuffPanel = new JPanel(new GridBagLayout());
        otherStuffPanel.setBackground(Color.GRAY);

        c.insets = new Insets(2,2,2,2);
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weighty = 0.07;
        c.weightx = 0.7;
        panel.add(selectedIndecator,c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weighty = 0.07;
        c.weightx = 0.3;
        panel.add(craftIndecator,c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weighty = 0.93;
        c.weightx = 1.0;
        panel.add(otherStuffPanel,c);

        c.weighty = 1.0;

        recName = createTextFeild(Recipe.CreateRecipeString(r.requirements()), true, true);
        c.gridy = 0;
        c.gridwidth = 1;
        otherStuffPanel.add(recName,c);
        prodName = createTextFeild(Recipe.CreateRecipeString(r.products()), true, true);
        c.gridy = 1;
        c.gridwidth = 1;
        otherStuffPanel.add(prodName,c);
        craftCountText = createTextFeild(String.valueOf(craftCount), false, true);
        c.gridy = 2;
        c.gridwidth = 1;
        otherStuffPanel.add(craftCountText,c);

        this.add(panel);
        this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        PlanNodeEditor.editor.setSelectedNode(this);
        super.mousePressed(e);
    }

    public static PlanNode connectingNode = null;

    public Recipe r;

    public double craftCount = -1.0;
    int layer = -1;

    boolean isSelected = false;
    String info = "";

    public ArrayList<NodeConnection> incomingConnections = new ArrayList<>();
    public ArrayList<NodeConnection> outgoingConnections = new ArrayList<>();

    public PlanNode(Recipe r){
        this.r = r;
        this.minlayer = 0;
        moveTo(0);
        setupFrame();
    }

    public boolean isParent(){
        return outgoingConnections.size() == 0;
    }

    public void updateLayer(int parentlayer){
        if(parentlayer>255){
            MainFrame.mainFrame.addInfo("Error: Depth limit exceeded! Do you have a loop somewhere?");
            return;
        }
        this.layer = parentlayer+1;
        for (NodeConnection nodeConnection : incomingConnections) {
            nodeConnection.from.updateLayer(this.layer);
        }
    }

    private void updateDownstream(){
        for (NodeConnection nodeConnection : incomingConnections) {
            if(r.isMachineRecipe()){
                // treat craftCount as crafts/s
                // and treat c.cost as items/s
                nodeConnection.cost.setCost(r.getCost(nodeConnection.cost.item()) * craftCount / r.craftTime());
            }else{
                nodeConnection.cost.setCost(r.getCost(nodeConnection.cost.item()) * craftCount);
            }
            nodeConnection.fufilled = false;
        }
    }

    public boolean update(int layer){
        if(this.layer != layer+1) return true;
        if(!isParent()){
            ItemCost[] crafting = new ItemCost[r.products().length];
            for (int i = 0; i < crafting.length; i++) {
                crafting[i] = new ItemCost(r.products()[i].item(),0);
            }
            for (NodeConnection nc : outgoingConnections) {
                for (int i = 0; i < crafting.length; i++) {
                    if(!crafting[i].item().equals(nc.cost.item())) continue;
                    crafting[i].setCost(crafting[i].cost()+nc.cost.cost());
                }
            }
            craftCount = -1.0;
            for (int i = 0; i < crafting.length; i++) {
                double count = crafting[i].cost();
                if(r.isMachineRecipe()){
                    // treat c.cost as items/s for this
                    double production = r.getProduction(crafting[i].item())/r.craftTime() * craftCount;
                    if(production<count){
                        craftCount = crafting[i].cost() / r.getProduction(crafting[i].item()) * r.craftTime();
                    }
                }else{
                    double production = r.getProduction(crafting[i].item()) * craftCount;
                    if(production<count){
                        craftCount = crafting[i].cost() / r.getProduction(crafting[i].item());
                    }
                }
            }
            if(Settings.requireRoundCrafts)
                craftCount = Math.ceil(craftCount);
        }
        updateDownstream();
        setCraftCountText();
        for (NodeConnection nodeConnection : incomingConnections) {
            if(!nodeConnection.from.update(layer+1)){
                info = "Earlier Step Failed";
                craftStatus(CraftStatus.EARLIER);
                return false;
            }
        }
        ArrayList<ItemCost> item = new ArrayList<>();
        for (ItemCost c : r.requirements()) {
            item.add(c);
        }
        for (NodeConnection nodeConnection : incomingConnections) {
            if(nodeConnection.fufilled){
                for (int i = 0; i < item.size(); i++) {
                    if(item.get(i).item().equals(nodeConnection.cost.item())){
                        item.remove(i);
                        break;
                    }
                }
            }
        }
        if(item.size()>0){
            info = "Not Enough Resources: " + Recipe.CreateRecipeString(item.toArray(new ItemCost[0]));
            craftStatus(CraftStatus.BAD);
            return false;
        }
        if(!isParent()){
            for (NodeConnection nc : outgoingConnections) {
                nc.fufilled = true;
            }
        }
        info = "Craft Successful";
        craftStatus(CraftStatus.GOOD);
        return true;
    }

    public void setCraftCountText(){
        String str = String.valueOf(craftCount);
        str = str.substring(0,Math.min(str.length(), 8));
        craftCountText.setText(str);
    }

    public enum SelectStatus{
        NONE,
        SLECTED,
        CONNECTING
    }
    public enum CraftStatus{
        BAD,
        GOOD,
        EARLIER
    }

    public void select(SelectStatus status){
        Color newcolor = null;
        String newStatus = "";
        switch (status) {
            case NONE:
                newcolor = Color.RED;
                newStatus = "Deselected";
                break;
            case SLECTED:
                newcolor = Color.GREEN;
                newStatus = "Selected";
                break;
            case CONNECTING:
                newcolor = Color.YELLOW;
                newStatus = "Connecting";
                break;
        }
        selectedIndecator.setBackground(newcolor);
        selectedIndecator.setText(newStatus);
        this.repaint();
    }

    public void craftStatus(CraftStatus status){
        Color newcolor = null;
        String newStatus = "";
        switch (status) {
            case BAD:
                newcolor = Color.RED;
                newStatus = "Bad";
                break;
            case GOOD:
                newcolor = Color.GREEN;
                newStatus = "Good";
                break;
            case EARLIER:
                newcolor = Color.YELLOW;
                newStatus = "Earlier";
                break;
        }
        craftIndecator.setBackground(newcolor);
        craftIndecator.setText(newStatus);
        this.repaint();
    }

    public boolean hasConnection(PlanNode n2, Item i){
        for (NodeConnection nc : incomingConnections) {
            if(nc.from.equals(n2)&&nc.cost.item().equals(i)) return true;
        }
        for (NodeConnection nc : n2.incomingConnections) {
            if(nc.from.equals(this)&&nc.cost.item().equals(i)) return true;
        }
        return false;
    }

    public static void connect(PlanNode from, PlanNode to, Item i, double rCount){
        NodeConnection nc = new NodeConnection(from, to, new ItemCost(i, rCount));
        from.outgoingConnections.add(nc);
        to.incomingConnections.add(nc);
    }

    @Override
    public String toString(){
        ItemCost[] products = null;
        if(!r.isEnd()){
            products = ItemCost.clone(r.products(), craftCount);
        }else{
            products = ItemCost.clone(r.requirements(), craftCount);
        }
        return Recipe.CreateRecipeString(products);
    }

    public PlanCost toPlanCost(){
        if(r.isBase()){
            ItemCost[] cost = ItemCost.clone(r.products(), craftCount);
            ItemCost[] machineCost = new ItemCost[0];
            if(r.isMachineRecipe()){
                machineCost = new ItemCost[r.machine().costPerSecond().length];
                for (int i = 0; i < machineCost.length; i++) {
                    ItemCost ic = r.machine().costPerSecond()[i];
                    machineCost[i] = new ItemCost(ic.item(), ic.cost());
                }
            }
            return new PlanCost(new RecipeCost[]{new RecipeCost(r, craftCount)}, cost, machineCost);
        }
        ArrayList<RecipeCost> totalCost = new ArrayList<>();
        ArrayList<ItemCost> baseCost = new ArrayList<>();
        ArrayList<ItemCost> machineCost = new ArrayList<>();

        for (NodeConnection nc : incomingConnections) {
            PlanNode n = nc.from;
            PlanCost pc = n.toPlanCost();
            for (RecipeCost tc : pc.totalCost()) {
                totalCost.add(tc);
            }
            ItemCost.merge(baseCost, pc.baseCost());
            ItemCost.merge(machineCost, pc.machineCost());
        }

        totalCost.add(new RecipeCost(r, craftCount));

        if(r.isMachineRecipe())
            ItemCost.merge(machineCost, r.machine().costPerSecond());

        return new PlanCost(totalCost.toArray(new RecipeCost[0]), baseCost.toArray(new ItemCost[0]), machineCost.toArray(new ItemCost[0]));
    }
}