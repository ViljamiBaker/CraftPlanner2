package craftPlanner.GUI.planning;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import craftPlanner.GUI.MainFrame;
import craftPlanner.GUI.actions.GhostText;
import craftPlanner.GUI.planning.PlanNode.SelectStatus;
import craftPlanner.crafts.Item;
import craftPlanner.crafts.ItemCost;
import craftPlanner.crafts.Recipe;

public class PlanNodeEditor extends JPanel implements ActionListener{
    public static PlanNodeEditor editor = null;
    PlanNode selectedNode = null;
    PlanNode connectingNode = null;

    GhostText nodeinfo;
    GhostText thisinfo;
    JTextField products;
    JTextField requirements;
    JTextField producingFeild;
    JTextField machine;
    JTextField time;
    GhostText requestTextField;

    private JButton createButton(String name, String action){
        JButton button = new JButton(name);
        button.setActionCommand(action);
        button.addActionListener(this);
        return button;
    }
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
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GridBagConstraints c = new GridBagConstraints();
        panel.setBackground(Color.WHITE);
        JButton button = createButton("Delete", "KillMe");
        JButton requestButton = createButton("Request", "Request");
        JButton connectButton = createButton("<html><u>C</u>onnect</html>", "Connect");
        connectButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.SHIFT_DOWN_MASK), "CONNECT");
        connectButton.getActionMap().put("CONNECT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectButton.doClick(); // Simulates a physical click
            }
        });
        JButton disconnectButton = createButton("Disconnect", "Disconnect");
        requirements = createTextFeild("", false, true);
        JTextField inforeq = createTextFeild("Products: ", false, false);
        products = createTextFeild("", false, true);
        JTextField infoprod = createTextFeild("Requirements: ", false, false);
        machine = createTextFeild("",false,true);
        JTextField infopmac = createTextFeild("Machine: ", false, false);
        time = createTextFeild("",false,true);
        JTextField infoptime = createTextFeild("Time: ", false, false);
        producingFeild = createTextFeild("Craft: 0.0",false,true);
        thisinfo = new GhostText(new JTextField(),"Info here");
        thisinfo.textfield.setEditable(false);
        thisinfo.textfield.setFocusable(false);
        nodeinfo = new GhostText(new JTextField(),"Node info here");
        nodeinfo.textfield.setEditable(false);
        nodeinfo.textfield.setFocusable(false);
        requestTextField = new GhostText(new JTextField(""), "Craft # Here");
        requestTextField.textfield.setEditable(true);
        
        c.insets = new Insets(5,5,5,5);
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(infoprod,c);
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 10;
        c.gridheight = 1;
        panel.add(requirements,c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        panel.add(inforeq,c);
        c.gridx = 2;
        c.gridy = 1;
        c.gridwidth = 10;
        panel.add(products,c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        panel.add(infopmac,c);
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 4;
        panel.add(machine,c);
        c.gridx = 6;
        c.gridy = 2;
        c.gridwidth = 2;
        panel.add(infoptime,c);
        c.gridx = 8;
        c.gridy = 2;
        c.gridwidth = 4;
        panel.add(time,c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 4;
        panel.add(producingFeild,c);
        c.gridx = 4;
        c.gridy = 3;
        c.gridwidth = 4;
        panel.add(requestButton,c);
        c.gridx = 8;
        c.gridy = 3;
        c.gridwidth = 4;
        panel.add(requestTextField.textfield,c);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 4;
        panel.add(nodeinfo.textfield,c);
        c.gridx = 4;
        c.gridy = 4;
        c.gridwidth = 8;
        panel.add(thisinfo.textfield,c);

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 4;
        panel.add(button,c);
        c.gridx = 4;
        c.gridy = 5;
        c.gridwidth = 4;
        panel.add(connectButton,c);
        c.gridx = 8;
        c.gridy = 5;
        c.gridwidth = 4;
        panel.add(disconnectButton,c);
        this.add(panel);
        this.repaint();
    }

    public PlanNodeEditor(){
        GridLayout experimentLayout = new GridLayout(1,1);
        this.setLayout(experimentLayout);
        setupFrame();
        editor = this;
    }

    public void actionPerformed(ActionEvent e) {
        if(selectedNode == null){
            thisinfo.setText("No node selected");
            return;
        }
        MainFrame.mainFrame.getGlassPane().repaint();
        thisinfo.setText("");
        switch (e.getActionCommand()) {
            case "KillMe":
                killMe();
                break;
            case "Request":
                requestCraft();
                break;
            case "Connect":
                connect();
                break;
            case "Disconnect":
                disconnect();
                break;
            default:
                System.err.println("Are you fucking stupid? " + e.getActionCommand());
                break;
        }
    }
    public void disconnect(){
        for (NodeConnection nc : selectedNode.outgoingConnections) {
            nc.to.incomingConnections.remove(nc);
        }
        selectedNode.outgoingConnections.clear();
    }
    public void killMe(){
        PlanFrame.planFrame.removePlanNode(selectedNode);
        deselectNode();
    }
    public void requestCraft(){
        if(selectedNode.outgoingConnections.size()>0){
            thisinfo.setText("Not final node");
            return;
        }
        if(requestTextField.isEmpty()){
            thisinfo.setText("Request is empty");
            return;
        }
        try {
            selectedNode.craftCount = Double.parseDouble(requestTextField.getText());
        } catch (Exception e) {
            thisinfo.setText("Cant parse \"" + requestTextField.getText() + "\"");
            return;
        }
        PlanFrame.planFrame.updateAllLayers();
        selectedNode.update(-1);
        setSelectedNode(selectedNode);
    }
    public void checkCraft(){
        if(selectedNode.craftCount<=0){
            thisinfo.setText("Not crafting");
            return;
        }
        thisinfo.setText("");
    }

    private void deselectConnectedNodes(){
        connectingNode.select(SelectStatus.NONE);
        connectingNode = null;
        selectedNode.select(SelectStatus.NONE);
        deselectNode();
    }

    public void connect(){
        if(connectingNode == null){
            connectingNode = selectedNode;
            thisinfo.setText("Select Next Node");
            selectedNode.select(SelectStatus.CONNECTING);
            deselectNode();
            return;
        }
        if(connectingNode == selectedNode){
            thisinfo.setText("Cant connect node to itself");
            selectedNode.select(SelectStatus.NONE);
            deselectNode();
            connectingNode = null;
            return;
        }
        if(connectingNode.r.isMachineRecipe()!=selectedNode.r.isMachineRecipe()){
            thisinfo.setText("Cannot connect machine and normal recipies");
            deselectConnectedNodes();
            return;
        }
        Item[] selectedItems = MainFrame.mainFrame.getSelectedItems();
        if(selectedItems.length == 0){
            Item i = connectingNode.r.findCommonItem(selectedNode.r);
            if(i == null){
                thisinfo.setText("Select item to supply");
                return;
            }
            selectedItems = new Item[]{i};
        }
        for (Item item : selectedItems) {
            if(!connectingNode.r.producesItem(item)){
                thisinfo.setText("Node does not produce \"" + item + "\"");
                deselectConnectedNodes();
                return;
            }
            if(!selectedNode.r.requiresItem(item)){
                thisinfo.setText("Node does not consume \"" + item + "\"");
                deselectConnectedNodes();
                return;
            }
            if(selectedNode.hasConnection(connectingNode, item)){
                thisinfo.setText("Nodes already connected with \"" + item + "\"");
                deselectConnectedNodes();
                return;
            }
            NodeConnection nc = new NodeConnection(connectingNode, selectedNode, new ItemCost(item, 0.0));
            connectingNode.outgoingConnections.add(nc);
            selectedNode.incomingConnections.add(nc);
        }
        MainFrame.mainFrame.addInfo("Connected \"" + connectingNode.r + "\" to \"" + selectedNode.r + "\"");
        deselectConnectedNodes();
        thisinfo.setText("");
    }

    public void setSelectedNode(PlanNode n){
        if(selectedNode!=null){
            if(selectedNode == connectingNode){
                selectedNode.select(SelectStatus.CONNECTING);
            }else{
                selectedNode.select(SelectStatus.NONE);
            }
        }
        selectedNode = n;
        if(selectedNode == null){
            deselectNode();
            return;
        }
        selectedNode.select(SelectStatus.SLECTED);
        products.setText(Recipe.CreateRecipeString(n.r.products()));
        requirements.setText(Recipe.CreateRecipeString(n.r.requirements()));
        nodeinfo.setText(n.info);
        if(n.r.isMachineRecipe()){
            machine.setText(n.r.machine().name());
        }else{
            machine.setText("null");
        }
        String str = String.valueOf(n.r.craftTime());
        str = str.substring(0,Math.min(str.length()-1, 8));
        time.setText(str);
        this.repaint();
        producingFeild.setText("Crafting: " + n.craftCount);
    }

    public void deselectNode(){
        selectedNode = null;
        products.setText("");
        requirements.setText("");
        nodeinfo.setText("");
        producingFeild.setText("");
        machine.setText("");
        time.setText("");
        this.repaint();
    }
}
