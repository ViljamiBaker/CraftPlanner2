package craftPlanner.GUI.actions;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import craftPlanner.GUI.MainFrame;
import craftPlanner.crafts.ItemCost;
import craftPlanner.crafts.Registry;

public class Actions {
    private static class CustomFrame extends JFrame implements ActionListener{
        public CustomFrame(){
            this.setVisible(true);
            Rectangle bounds = MainFrame.mainFrame.getBounds();
            this.setBounds((int)bounds.getCenterX()-150,(int)bounds.getCenterY()-100,300, 200);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setResizable(false);
            this.setLayout(null);

            JButton confirm = new JButton("Confirm");
            confirm.setBounds(150,80,100,50);
            confirm.setActionCommand("O");
            confirm.addActionListener(this);
            this.add(confirm);

            JButton cancel = new JButton("Cancel");
            cancel.setBounds(50,80,100,50);
            cancel.setActionCommand("X");
            cancel.addActionListener(this);
            this.add(cancel);
            cancel.requestFocus();
        }
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("O")){
                runEvent();
            }
            this.dispose();
        }

        public void runEvent(){}
    }
    public static final String CREATE_ITEM = "CreateItem";
    private static class CreateItemFrame extends CustomFrame{
        GhostText name;
        public CreateItemFrame(){
            this.setTitle("Create new item");
            name = new GhostText(new JTextField(""), "Name here");
            name.textfield.setBounds(50,32,200,25);
            this.add(name.textfield);
        }

        @Override
        public void runEvent(){
            String text = name.getText();
            if(text.length()>0){
                Registry.createItem(text.trim());
            }
            name.delete();
        }
    }
    public static void CreateItem(){
        new CreateItemFrame();
    }

    public static final String CREATE_RECIPE = "CreateRecipe";
    private static class CreateRecipeFrame extends CustomFrame{
        GhostText cost;
        GhostText product;
        public CreateRecipeFrame(){
            this.setTitle("Create new recipe");
            cost = new GhostText(new JTextField(""), "Cost here");
            cost.textfield.setBounds(50,20,200,25);
            this.add(cost.textfield);
            product = new GhostText(new JTextField(""), "Products here");
            product.textfield.setBounds(50,45,200,25);
            this.add(product.textfield);
            
        }
        @Override
        public void runEvent(){
            ItemCost[] costs = null;
            ItemCost[] products = null;
            try {
                costs = Registry.createItemCosts(cost.getText());
                products = Registry.createItemCosts(product.getText());
            } catch (Exception e) {
                if(e.getClass().equals(IllegalArgumentException.class)){
                    MainFrame.mainFrame.addInfo(e.getMessage());
                }
            }
            cost.delete();
            product.delete();
            if(costs == null||products == null) return;
            Registry.createRecipe(costs, products);
        }
    }
    public static void CreateRecipe(){
        new CreateRecipeFrame();
    }

    public static final String CREATE_MACHINE = "CreateMachine";
    private static class CreateMachineFrame extends CustomFrame{
        GhostText namefeild;
        GhostText requirement;
        public CreateMachineFrame(){
            this.setTitle("Create new machine");
            namefeild = new GhostText(new JTextField(""), "Name here");
            namefeild.textfield.setBounds(50,20,200,25);
            this.add(namefeild.textfield);
            requirement = new GhostText(new JTextField(""), "Requirements here");
            requirement.textfield.setBounds(50,45,200,25);
            this.add(requirement.textfield);
            
        }
        @Override
        public void runEvent(){
            String name = namefeild.getText();
            ItemCost[] products = null;
            try {
                products = Registry.createItemCosts(requirement.getText());
            } catch (Exception e) {
                if(e.getClass().equals(IllegalArgumentException.class)){
                    MainFrame.mainFrame.addInfo(e.getMessage());
                }
            }
            namefeild.delete();
            requirement.delete();
            if(name.length()==0||products == null) return;
            Registry.createMachine(name, products);
        }
    }
    public static void CreateMachine(){
        new CreateMachineFrame();
    }

    public static final String CREATE_MACHINE_RECIPE = "CreateMachineRecipe";
    private static class CreateMachineRecipeFrame extends CustomFrame{
        GhostText namefeild;
        GhostText product;
        GhostText requirement;
        GhostText timefeild;
        public CreateMachineRecipeFrame(){
            this.setTitle("Create new machine recipe");
            product = new GhostText(new JTextField(""), "Products here");
            product.textfield.setBounds(50,5,200,25);
            this.add(product.textfield);
            requirement = new GhostText(new JTextField(""), "Requirements here");
            requirement.textfield.setBounds(50,30,200,25);
            this.add(requirement.textfield);
            namefeild = new GhostText(new JTextField(""), "Name here");
            namefeild.textfield.setBounds(50,55,100,25);
            this.add(namefeild.textfield);
            timefeild = new GhostText(new JTextField(""), "Time here");
            timefeild.textfield.setBounds(150,55,100,25);
            this.add(timefeild.textfield);
            
        }
        @Override
        public void runEvent(){
            String name = namefeild.getText();
            ItemCost[] products = null;
            ItemCost[] requirements = null;
            double time = -1.0;
            try {
                products = Registry.createItemCosts(product.getText());
                requirements = Registry.createItemCosts(requirement.getText());
                time = Double.valueOf(timefeild.getText());
            } catch (Exception e) {
                if(e.getClass().equals(IllegalArgumentException.class)){
                    MainFrame.mainFrame.addInfo(e.getMessage());
                }
            }
            namefeild.delete();
            product.delete();
            requirement.delete();
            timefeild.delete();
            if(name.length()==0||products == null) return;
            Registry.createMachineRecipe(requirements, products, name, time);
        }
    }
    public static void CreateMachineRecipe(){
        new CreateMachineRecipeFrame();
    }
}
