package craftPlanner.GUI.planning;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import craftPlanner.GUI.actions.DraggableComponent;
import craftPlanner.GUI.actions.GhostText;
import craftPlanner.crafts.Recipe;

public class PlanNode extends DraggableComponent implements ActionListener {
    private static final Random random = new Random();
    private JButton createButton(String name, String action){
        JButton button = new JButton(name);
        button.setActionCommand(action);
        button.addActionListener(this);
        button.getModel().addChangeListener(e -> {
            ButtonModel model = (ButtonModel) e.getSource();
            if (model.isRollover()) {
                moveTo(0);
            }
        });
        return button;
    }
    @Override
    protected void paintChildren(Graphics g) {
        g.setClip(new Rectangle(350,200));
        super.paintChildren(g);
    }
    private void setupFrame(){
        this.setBounds(random.nextInt(0, 50),random.nextInt(0, 50),350, 200);
        this.setOpaque(false);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 5, 5, 5));
        GridBagConstraints c = new GridBagConstraints();
        panel.setSize(350, 200);
        panel.setBackground(Color.GRAY);
        JButton button = createButton("Delete", "KillMe");
        JButton requestButton = createButton("Request", "Request");
        JButton connectButton = createButton("Connect", "Connect");
        JButton disconnectButton = createButton("Disconnect", "Disconnect");
        JTextField requirements = new JTextField(Recipe.CreateRecipeString(r.requirements()));
        requirements.setEditable(false);
        requirements.setFocusable(false);
        JTextField products = new JTextField(Recipe.CreateRecipeString(r.products()));
        products.setEditable(false);
        products.setFocusable(false);
        JTextField info = new JTextField("Products: ");
        info.setEditable(false);
        info.setFocusable(false);
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder());
        JTextField info2 = new JTextField("Requirements: ");
        info2.setEditable(false);
        info2.setFocusable(false);
        info2.setOpaque(false);
        info2.setBorder(BorderFactory.createEmptyBorder());
        JTextField producingFeild = new JTextField("Craft: 0.0");
        producingFeild.setEditable(false);
        producingFeild.setFocusable(false);
        errors = new JTextField("No Problems :)");
        errors.setEditable(false);
        requestTextField = new GhostText(new JTextField(""), "Craft # Here");
        requestTextField.textfield.setEditable(true);
        
        c.insets = new Insets(5,5,5,5);
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.ipady = 0;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 3;
        c.weightx = 0.87;
        panel.add(requirements,c);
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.weightx = 0.13;
        panel.add(info2,c);
        c.ipady = 0;
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 3;
        c.weightx = 0.87;
        panel.add(products,c);
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.weightx = 0.13;
        panel.add(info,c);
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        panel.add(producingFeild,c);
        c.ipady = 0;
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        panel.add(errors,c);
        c.ipady = 0;
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 1;
        panel.add(requestTextField.textfield,c);
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        panel.add(button,c);
        c.ipady = 0;
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        panel.add(requestButton,c);
        c.ipady = 0;
        c.gridx = 2;
        c.gridy = 3;
        c.gridwidth = 1;
        panel.add(connectButton,c);
        c.ipady = 0;
        c.gridx = 2;
        c.gridy = 4;
        c.gridwidth = 1;
        panel.add(disconnectButton,c);
        this.add(panel);
        this.repaint();
    }

    public static PlanNode connectingNode = null;

    Recipe r;
    JTextField errors;
    GhostText requestTextField;

    double requestNum = -1.0;
    int layer = -1;

    ArrayList<NodeConnection> incomingConnections = new ArrayList<>();
    ArrayList<NodeConnection> outgoingConnections = new ArrayList<>();

    public PlanNode(Recipe r){
        this.r = r;
        this.minlayer = 0;
        moveTo(0);
        setupFrame();
    }
    public void actionPerformed(ActionEvent e) {
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
                outgoingConnections.clear();
                checkCraft();
                break;
            default:
                System.err.println("Are you fucking stupid? " + e.getActionCommand());
                break;
        }
    }
    public void killMe(){
        PlanFrame.planFrame.removePlanNode(this);
    }
    public void requestCraft(){
        if(outgoingConnections.size()>0){
            errors.setText("Not final node");
            return;
        }
        if(requestTextField.isEmpty()){
            errors.setText("Request is empty");
            return;
        }
        try {
            requestNum = Double.parseDouble(requestTextField.getText());
        } catch (Exception e) {
            errors.setText("Cant parse \"" + requestTextField.getText() + "\"");
            return;
        }
        checkCraft();
    }
    public void checkCraft(){
        if(requestNum<=0){
            errors.setText("Not crafting");
            return;
        }
        errors.setText("No Problems :)");
    }
    public void connect(){
        if(connectingNode == null){
            connectingNode = this;
            errors.setText("Select Next Node");
            return;
        }
        if(connectingNode == this){
            connectingNode = this;
            errors.setText("Cant connect node to itself");
            return;
        }
        System.out.println(connectingNode.r);
        connectingNode.errors.setText("Select item to supply");
        connectingNode = null;
    }

    public int updateLayer(int parentlayer){
        if(parentlayer>=this.layer)
            this.layer = parentlayer+1;
        int maxlayer = this.layer;
        for (NodeConnection nodeConnection : outgoingConnections) {
            nodeConnection.to.updateLayer(this.layer);
            if(nodeConnection.to.layer>maxlayer)
                maxlayer = nodeConnection.to.layer;
        }
        return maxlayer;
    }

    public boolean isParent(){
        return incomingConnections.size() == 0;
    }

    public boolean update(int layer){
        if(this.layer != layer+1) return true;
        if(isParent()){
            if(requestNum <= 0.0){
                errors.setText("Request ammount invalid");
                return false;
            }
            for (NodeConnection nodeConnection : outgoingConnections) {
                nodeConnection.cost.setCost(r.getCost(nodeConnection.cost.item()) * requestNum);
                nodeConnection.fufilled = false;
            }
            for (NodeConnection nodeConnection : outgoingConnections) {
                if(!nodeConnection.to.update(layer+1))
                    return false;
            }
            return true;
        }
        requestNum = 0.0;
        for (NodeConnection nodeConnection : incomingConnections) {
            double cost = nodeConnection.cost.cost();
            double production = r.getProduction(nodeConnection.cost.item()) * requestNum;
            if(production<cost){
                requestNum = nodeConnection.cost.cost() / r.getProduction(nodeConnection.cost.item());
            }
            nodeConnection.fufilled = true;
        }
        for (NodeConnection nodeConnection : outgoingConnections) {
            nodeConnection.cost.setCost(r.getCost(nodeConnection.cost.item()) * requestNum);
            nodeConnection.fufilled = false;
        }
        for (NodeConnection nodeConnection : outgoingConnections) {
            if(!nodeConnection.to.update(layer+1))
                return false;
        }
        return true;
    }
}