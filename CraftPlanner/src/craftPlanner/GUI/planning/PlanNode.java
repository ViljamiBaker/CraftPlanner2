package craftPlanner.GUI.planning;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import craftPlanner.GUI.actions.DraggableComponent;
import craftPlanner.crafts.Recipe;

public class PlanNode extends DraggableComponent{
    private static final Random random = new Random();

    private JTextField selectedIndecator;
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
        this.setBounds(random.nextInt(0, 50),random.nextInt(0, 50),100,100);
        this.setOpaque(false);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        GridBagConstraints c = new GridBagConstraints();
        panel.setSize(100,100);
        panel.setBackground(new Color(80,80,80));
        selectedIndecator = new JTextField("Deselected");
        selectedIndecator.setBackground(Color.RED);
        selectedIndecator.setEditable(false);
        selectedIndecator.setFocusable(false);
        selectedIndecator.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        selectedIndecator.setHorizontalAlignment(JTextField.CENTER);
        JPanel otherStuffPanel = new JPanel(new GridBagLayout());
        otherStuffPanel.setBackground(Color.GRAY);

        c.insets = new Insets(2,2,2,2);
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weighty = 0.07;
        panel.add(selectedIndecator,c);
        c.gridy = 1;
        c.gridwidth = 1;
        c.weighty = 0.93;
        panel.add(otherStuffPanel,c);

        c.weighty = 1.0;

        prodName = createTextFeild(Recipe.CreateRecipeString(r.products()), true, true);
        c.gridy = 0;
        c.gridwidth = 1;
        otherStuffPanel.add(prodName,c);
        recName = createTextFeild(Recipe.CreateRecipeString(r.requirements()), true, true);
        c.gridy = 1;
        c.gridwidth = 1;
        otherStuffPanel.add(recName,c);
        craftCountText = createTextFeild(String.valueOf(craftCount), false, true);
        c.gridy = 2;
        c.gridwidth = 1;
        otherStuffPanel.add(craftCountText,c);

        selectedIndecator.addMouseListener(this);
        selectedIndecator.addMouseMotionListener(this);

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

    double craftCount = -1.0;
    int layer = -1;

    boolean isSelected = false;
    String info = "";

    ArrayList<NodeConnection> incomingConnections = new ArrayList<>();
    ArrayList<NodeConnection> outgoingConnections = new ArrayList<>();

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
        if(isParent()){
            if(craftCount <= 0.0){
                info = "Craft Failed";
                return false;
            }
            updateDownstream();
            for (NodeConnection nodeConnection : incomingConnections) {
                if(!nodeConnection.from.update(layer+1)){
                    info = "Craft Failed";
                    return false;
                }
            }
            info = "Craft Successful";
            return true;
        }
        craftCount = -1.0;
        for (NodeConnection nodeConnection : outgoingConnections) {
            double count = nodeConnection.cost.cost();
            if(r.isMachineRecipe()){
                // treat c.cost as items/s for this
                double production = r.getProduction(nodeConnection.cost.item())/r.craftTime() * craftCount;
                if(production<count){
                    craftCount = nodeConnection.cost.cost() / r.getProduction(nodeConnection.cost.item()) * r.craftTime();
                }
            }else{
                double production = r.getProduction(nodeConnection.cost.item()) * craftCount;
                if(production<count){
                    craftCount = nodeConnection.cost.cost() / r.getProduction(nodeConnection.cost.item());
                }
            }
            nodeConnection.fufilled = true;
        }
        updateDownstream();
        craftCountText.setText(String.valueOf(craftCount));;
        for (NodeConnection nodeConnection : incomingConnections) {
            if(!nodeConnection.from.update(layer+1)){
                info = "Craft Failed";
                return false;
            }
        }
        info = "Craft Successful";
        return true;
    }

    public enum SelectStatus{
        NONE,
        SLECTED,
        CONNECTING
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
}