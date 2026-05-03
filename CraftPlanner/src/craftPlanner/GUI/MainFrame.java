package craftPlanner.GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import craftPlanner.Settings;
import craftPlanner.GUI.actions.Actions;
import craftPlanner.GUI.planning.PlanFrame;
import craftPlanner.GUI.planning.PlanNodeEditor;
import craftPlanner.crafts.Item;
import craftPlanner.crafts.Machine;
import craftPlanner.crafts.Recipe;
import craftPlanner.crafts.Registry;
public class MainFrame extends JFrame implements ActionListener, ItemListener{
    public static MainFrame mainFrame;
    Keyboard keyboard;
    JList<Item> itemlist;
    DefaultListModel<Item> listModel;
    JList<Recipe> mecipeList;
    DefaultListModel<Recipe> recipeModel;
    JList<Machine> machineList;
    DefaultListModel<Machine> machineModel;
    JTextArea info;
    public PlanFrame plan;
    public PlanNodeEditor editor;
    public MainFrame(){
        keyboard = new Keyboard(this);
        this.setTitle("calculating");
        this.setJMenuBar(setupMenu());
        this.setContentPane(createContentPane());
        this.setVisible(true);
        this.setSize(1400, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setMinimumSize(new Dimension(600, 400));

        MainFrame.mainFrame = this;

        Settings.autoCreateItems = true;
        Registry.createMachine("Miner",Registry.createItemCosts("100 Energy"));
        Registry.createMachine("Furnace",Registry.createItemCosts("1500 Energy"));
        Registry.createMachineRecipe(Registry.createItemCosts("1 Ore, 2 Coal"), Registry.createItemCosts("1 Iron"), "Furnace", 5.0);
        Registry.createMachineRecipe(Registry.createItemCosts(""), Registry.createItemCosts("1 Ore"), "Miner", 15.0);
        Registry.createMachineRecipe(Registry.createItemCosts(""), Registry.createItemCosts("4 Coal"), "Miner", 12.0);
        Registry.createMachineRecipe(Registry.createItemCosts("1 Iron"), Registry.createItemCosts(""), "Miner", 1.0);
        Registry.createRecipe(Registry.createItemCosts("1 Ore, 2 Coal"), Registry.createItemCosts("1 Iron"));
        Registry.createRecipe(Registry.createItemCosts(""), Registry.createItemCosts("3 Ore"));
        Registry.createRecipe(Registry.createItemCosts(""), Registry.createItemCosts("4 Coal"));
        Settings.autoCreateItems = false;
    }

    public Container createContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout(0,0));
        contentPane.setOpaque(true);

        plan = new PlanFrame(keyboard);
        contentPane.add(plan, BorderLayout.CENTER);

        JPanel itemPane = new JPanel(new BorderLayout(5,5));
        listModel = new DefaultListModel<Item>();
        itemlist = new JList<>(listModel);
        itemlist.setLayoutOrientation(JList.VERTICAL);
        itemlist.setVisibleRowCount(-1);
        JScrollPane itemListScroll = new JScrollPane(itemlist);
        itemListScroll.setPreferredSize(new Dimension(200, 800));
        itemListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        itemListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPane.add(Box.createHorizontalGlue());

        JButton destroyButton = new JButton("Destroy");
        destroyButton.setActionCommand("DestroyItem");
        destroyButton.addActionListener(this);

        buttonPane.add(destroyButton);

        itemPane.add(itemListScroll, BorderLayout.CENTER);
        itemPane.add(buttonPane,BorderLayout.PAGE_END);
        contentPane.add(itemPane, BorderLayout.LINE_END);
        

        JPanel RecipePane = new JPanel(new BorderLayout(5,5));
        recipeModel = new DefaultListModel<Recipe>();
        mecipeList = new JList<>(recipeModel);
        mecipeList.setLayoutOrientation(JList.VERTICAL);
        mecipeList.setVisibleRowCount(-1);
        JScrollPane RecipeListScroll = new JScrollPane(mecipeList);
        RecipeListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        RecipeListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel buttonPaneR = new JPanel();
        buttonPaneR.setLayout(new BoxLayout(buttonPaneR, BoxLayout.LINE_AXIS));
        buttonPaneR.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPaneR.add(Box.createHorizontalGlue());

        JButton destroyButtonR = new JButton("Destroy");
        destroyButtonR.setActionCommand("DestroyRecipe");
        destroyButtonR.addActionListener(this);

        buttonPaneR.add(destroyButtonR);

        JButton addButtonR = new JButton("Add");
        addButtonR.setActionCommand("AddRecipe");
        addButtonR.addActionListener(this);

        buttonPaneR.add(addButtonR);

        RecipePane.add(RecipeListScroll, BorderLayout.CENTER);
        RecipePane.add(buttonPaneR,BorderLayout.PAGE_END);

        JPanel MachinePane = new JPanel(new BorderLayout(5,5));
        machineModel = new DefaultListModel<Machine>();
        machineList = new JList<>(machineModel);
        machineList.setLayoutOrientation(JList.VERTICAL);
        machineList.setVisibleRowCount(-1);
        JScrollPane MachineListScroll = new JScrollPane(machineList);
        MachineListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        MachineListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel buttonPaneM = new JPanel();
        buttonPaneM.setLayout(new BoxLayout(buttonPaneM, BoxLayout.LINE_AXIS));
        buttonPaneM.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPaneM.add(Box.createHorizontalGlue());

        JButton destroyButtonM = new JButton("Destroy");
        destroyButtonM.setActionCommand("DestroyMachine");
        destroyButtonM.addActionListener(this);

        buttonPaneM.add(destroyButtonM);

        JButton addButtonM = new JButton("Add");
        addButtonM.setActionCommand("AddMachine");
        addButtonM.addActionListener(this);

        buttonPaneM.add(addButtonM);

        MachinePane.add(MachineListScroll, BorderLayout.CENTER);
        MachinePane.add(buttonPaneM,BorderLayout.PAGE_END);

        JPanel combinedPane = new JPanel(new GridLayout(1,2));
        combinedPane.setPreferredSize(new Dimension(400,800));

        combinedPane.add(MachinePane);
        combinedPane.add(RecipePane);
        contentPane.add(combinedPane, BorderLayout.LINE_START);
        
        GridLayout experimentLayout = new GridLayout(1,2);
        JPanel bottomComponent = new JPanel(experimentLayout);

        JPanel infoPane = new JPanel(new BorderLayout(5,5));
        infoPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        info = new JTextArea();
        info.setText("Info goes here \n");
        info.setFocusable(false);
        info.setEditable(false);
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        info.setPreferredSize(new Dimension(800, 150));
        JScrollPane infoScroll = new JScrollPane(info);
        infoScroll.setPreferredSize(new Dimension(1000, 150));
        infoScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        infoScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        infoPane.add(infoScroll, BorderLayout.CENTER);

        editor = new PlanNodeEditor();

        bottomComponent.add(infoScroll);
        bottomComponent.add(editor);
        contentPane.add(bottomComponent, BorderLayout.PAGE_END);

        this.setGlassPane(new PlanFrame.PlanPanel());
        this.getGlassPane().setVisible(true);

        return contentPane;
    }
    // https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
    private JMenuBar setupMenu(){
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Creation");
        menu.setMnemonic(KeyEvent.VK_C);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

        menu.add(createMenuItem("Create Item", Actions.CREATE_ITEM, KeyEvent.VK_I));
        menu.add(createMenuItem("Create Recipe", Actions.CREATE_RECIPE, KeyEvent.VK_R));
        menu.add(createMenuItem("Create Machine", Actions.CREATE_MACHINE, KeyEvent.VK_M));
        menu.add(createMenuItem("Create Machine Recipe", Actions.CREATE_MACHINE_RECIPE, KeyEvent.VK_T));
        menu.add(createCheckbox("Automatically Add Items", "AutoAdd", KeyEvent.VK_A));
        menu.add(createCheckbox("Require Round Crafts", "ReqRound", KeyEvent.VK_E));
        return menuBar;
    }

    private JMenuItem createMenuItem(String text, String action, int key){
        JMenuItem  menuItem = new JMenuItem(text,key);
        if(key!=0)
            menuItem.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.ALT_DOWN_MASK));
        menuItem.addActionListener(this);
        menuItem.setActionCommand(action);
        return menuItem;
    }
    private JCheckBoxMenuItem createCheckbox(String text, String action, int key){
        JCheckBoxMenuItem  cbItem = new JCheckBoxMenuItem(text);
        if(key!=0)
            cbItem.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.ALT_DOWN_MASK));
        cbItem.addItemListener(this);
        cbItem.setActionCommand(action);
        return cbItem;
    }

    public void addInfo(String e){
        info.append(e + "\n");
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case Actions.CREATE_ITEM:
                Actions.CreateItem();
                break;
            case Actions.CREATE_RECIPE:
                Actions.CreateRecipe();
                break;
            case Actions.CREATE_MACHINE:
                Actions.CreateMachine();
                break;
            case Actions.CREATE_MACHINE_RECIPE:
                Actions.CreateMachineRecipe();
                break;
            case "DestroyItem":
                destroySelectedItems();
                break;
            case "DestroyRecipe":
                destroySelectedRecipes();
                break;
            case "AddRecipe":
                addSelectedRecipes();
                break;
            case "DestroyMachine":
                destroySelectedMachines();
                break;
            default:
                System.err.println("Are you fucking stupid? " + e.getActionCommand());
                break;
        }
        //System.out.println(Thread.currentThread().getStackTrace()[1]);
    }

    private void destroySelectedItems(){
        List<Item> selected = itemlist.getSelectedValuesList();
        for (Item item : selected) {
            Registry.removeItem(item);
        }
        updateRegistery();
    }

    private void destroySelectedRecipes(){
        List<Recipe> selected = mecipeList.getSelectedValuesList();
        for (Recipe recipe : selected) {
            Registry.removeRecipe(recipe);
        }
        updateRegistery();
    }

    private void destroySelectedMachines(){
        List<Machine> selected = machineList.getSelectedValuesList();
        for (Machine Machine : selected) {
            Registry.removeMachine(Machine);
        }
        updateRegistery();
    }

    private void addSelectedRecipes(){
        List<Recipe> selected = mecipeList.getSelectedValuesList();
        for (Recipe recipe : selected) {
            plan.addPlanNode(recipe);
        }
    }

    public void updateRegistery(){
        listModel.removeAllElements();
        for (Item i : Registry.items) {
            listModel.addElement(i);
        }
        recipeModel.removeAllElements();
        for (Recipe i : Registry.Recipes) {
            recipeModel.addElement(i);
        }
        machineModel.removeAllElements();
        for (Machine i : Registry.machines) {
            machineModel.addElement(i);
        }
    }

    public void update(){
        keyboard.update();
    }

    public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        switch (source.getActionCommand()) {
            case "AutoAdd":
                Settings.autoCreateItems = !Settings.autoCreateItems;
                break;
            case "ReqRound":
                Settings.requireRoundCrafts = !Settings.requireRoundCrafts;
                break;
        
            default:
                System.err.println("Are you incredebly fucking stupid? " + source.getActionCommand());
                break;
        }
    }
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }

    public Item[] getSelectedItems(){
        return itemlist.getSelectedValuesList().toArray(new Item[0]);
    }
}
