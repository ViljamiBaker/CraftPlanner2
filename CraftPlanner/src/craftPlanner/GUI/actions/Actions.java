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
            this.setTitle("create new item");
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
            name = new GhostText(new JTextField(""), "Name Here");
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

    public static final String CREATE_Recipe = "CreateRecipe";
    private static class CreateRecipeFrame extends CustomFrame{
        GhostText cost;
        GhostText product;
        public CreateRecipeFrame(){
            cost = new GhostText(new JTextField(""), "Cost Here");
            cost.textfield.setBounds(50,20,200,25);
            this.add(cost.textfield);
            product = new GhostText(new JTextField(""), "Products Here");
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
}
