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
import java.io.File;
import javax.swing.filechooser.FileFilter;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import craftPlanner.Settings;
import craftPlanner.GUI.actions.Actions;
import craftPlanner.GUI.actions.TotalFrame;
import craftPlanner.GUI.planning.ConsumerNode;
import craftPlanner.GUI.planning.PlanFrame;
import craftPlanner.GUI.planning.PlanNodeEditor;
import craftPlanner.GUI.planning.PlanNode.CraftStatus;
import craftPlanner.GUI.util.ItemList;
import craftPlanner.crafts.Item;
import craftPlanner.crafts.Machine;
import craftPlanner.crafts.Recipe;
import craftPlanner.crafts.Registry;
import craftPlanner.fileIO.FileIO;
import craftPlanner.fileIO.FileIO.FileType;
public class MainFrame extends JFrame implements ActionListener, ItemListener{
    public static MainFrame mainFrame;
    Keyboard keyboard;
    ItemList<Item> itemList;
    ItemList<Recipe> recipeList;
    ItemList<Machine> machineList;
    JTextArea info;
    JFileChooser chooser;
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
        this.setMinimumSize(new Dimension(800, 500));
        chooser = new JFileChooser();

        MainFrame.mainFrame = this;
        
        //Registry.createMachine("Miner",Registry.createItemCosts("100 Energy"));
        //Registry.createMachine("Furnace",Registry.createItemCosts("1500 Energy"));
        //Registry.createMachineRecipe(Registry.createItemCosts("1 Ore, 2 Coal"), Registry.createItemCosts("1 Iron"), null, "Furnace", 5.0, "");
        //Registry.createMachineRecipe(Registry.createItemCosts(""), Registry.createItemCosts("1 Ore"), null, "Miner", 15.0, "");
        //Registry.createMachineRecipe(Registry.createItemCosts(""), Registry.createItemCosts("4 Coal"), null, "Miner", 12.0, "");
        //Registry.createMachineRecipe(Registry.createItemCosts("1 Iron"), Registry.createItemCosts(""), null, "Miner", 1.0, "");
        //Registry.createRecipe(Registry.createItemCosts("1 Ore, 2 Coal"), Registry.createItemCosts("1 Iron"), "");
        //Registry.createRecipe(Registry.createItemCosts(""), Registry.createItemCosts("3 Ore"), "");
        //Registry.createRecipe(Registry.createItemCosts(""), Registry.createItemCosts("4 Coal"), "");
    }

    public Container createContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout(0,0));
        contentPane.setOpaque(true);

        plan = new PlanFrame(keyboard);
        contentPane.add(plan, BorderLayout.CENTER);

        itemList = new ItemList<Item>(this,"Item", true, false, false);
        contentPane.add(itemList, BorderLayout.LINE_END);
        recipeList = new ItemList<Recipe>(this,"Recipe", true, true, true);

        machineList = new ItemList<Machine>(this,"Machine", true, false, false);

        JPanel combinedPane = new JPanel(new GridLayout(1,2));
        combinedPane.setPreferredSize(new Dimension(440,770));

        combinedPane.add(machineList);
        combinedPane.add(recipeList);
        contentPane.add(combinedPane, BorderLayout.LINE_START);
        
        GridLayout experimentLayout = new GridLayout(1,2);
        JPanel bottomComponent = new JPanel(experimentLayout);

        JPanel infoPane = new JPanel(new BorderLayout(5,5));
        infoPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        info = new JTextArea();
        info.setText("Info goes here \n");
        info.setRows(info.getRows()+2);
        info.setEditable(false);
        info.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        info.setPreferredSize(new Dimension(800, 180));
        JScrollPane infoScroll = new JScrollPane(info);
        infoScroll.setPreferredSize(new Dimension(1000, 180));
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
            case Actions.RENAME_RECIPE:
                Actions.RenameRecipe();
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


            case "SaveReg":
                file(true, FileType.REGISTRY);
                break;
            case "LoadReg":
                file(false, FileType.REGISTRY);
                break;
            case "SavePlan":
                file(true, FileType.PLAN);
                break;
            case "LoadPlan":
                file(false, FileType.PLAN);
                break;
            case "SaveCombined":
                file(true, FileType.COMBINED);
                break;
            case "LoadCombined":
                file(false, FileType.COMBINED);
                break;

            case "CreateConsumer":
                Item[] selItems = getSelectedItems();
                if(selItems.length<=0) return;
                ConsumerNode n = ConsumerNode.createConsumerNode(selItems[0]);
                plan.addPlanNode(n);
                updateRegistery();
                break;

            case "PlanSelected":
                if(editor.selectedNode != null){
                    if(editor.selectedNode.status != CraftStatus.GOOD){
                        addInfo("Warn: Selected node is not happy, plan may not be accurate");
                    }
                    new TotalFrame(editor.selectedNode);
                }
                break;

            case "Quit":
                this.setVisible(false);
                this.dispose();
                System.exit(0);
                break;
            default:
                System.err.println("Are you fucking stupid? " + e.getActionCommand());
                break;
        }
    }

    public void file(boolean save, FileType type){
        chooser.setAcceptAllFileFilterUsed(false);
        for (FileFilter filter : chooser.getChoosableFileFilters()) {
            chooser.removeChoosableFileFilter(filter);
        }
        chooser.removeChoosableFileFilter(FileIO.getPlanFilter());
        switch (type) {
            case REGISTRY:
                chooser.addChoosableFileFilter(FileIO.getRegistryFilter());
                break;
            case PLAN:
                chooser.addChoosableFileFilter(FileIO.getPlanFilter());
                break;
            case COMBINED:
                chooser.addChoosableFileFilter(FileIO.getCombinedFilter());
                break;
        }
        int returnVal = -1;
        if(save){
            returnVal = chooser.showSaveDialog(this);
        }else{
            returnVal = chooser.showOpenDialog(this);
        }

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                FileIO.file(file, save, type); 
            } catch (Exception e) {
                MainFrame.mainFrame.addInfo("File: " + (save? "Save": "Load") + " failed " + e.getMessage());
            }
            MainFrame.mainFrame.addInfo("File:" + (save? "Saving": "Loading") + " " + type.toString() + " to: " + file.getPath());
        } else {
            MainFrame.mainFrame.addInfo("File:" + (save? "Save": "Load") + " command cancelled by user");
        }
    }

    private JMenuItem createMenuItem(String text, String action, int key){
        return createMenuItem(text, action, key, InputEvent.ALT_DOWN_MASK);
    }
    private JMenuItem createMenuItem(String text, String action, int key, int modifiers){
        JMenuItem  menuItem = new JMenuItem(text,key);
        if(key!=0)
            menuItem.setAccelerator(KeyStroke.getKeyStroke(key, modifiers));
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
    
    // https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
    private JMenuBar setupMenu(){
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        menuBar.add(file);
        
        file.add(createMenuItem("Quit", "Quit", KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        file.addSeparator();
        file.add(createMenuItem("Save Registery", "SaveReg", KeyEvent.VK_S, InputEvent.SHIFT_DOWN_MASK));
        file.add(createMenuItem("Load Registery", "LoadReg", KeyEvent.VK_L, InputEvent.SHIFT_DOWN_MASK));
        file.addSeparator();
        file.add(createMenuItem("Save Plan", "SavePlan", KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        file.add(createMenuItem("Load Plan", "LoadPlan", KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        file.addSeparator();
        file.add(createMenuItem("Save Combined", "SaveCombined", KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        file.add(createMenuItem("Load Combined", "LoadCombined", KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        file.addSeparator();
        file.add(createCheckbox("Remove Plan on Load", "RemPlan", 0));
        
        JMenu creation = new JMenu("Creation");
        creation.setMnemonic(KeyEvent.VK_C);
        menuBar.add(creation);

        creation.add(createMenuItem("Create Item", Actions.CREATE_ITEM, KeyEvent.VK_I));
        creation.add(createMenuItem("Create Recipe", Actions.CREATE_RECIPE, KeyEvent.VK_R));
        creation.add(createMenuItem("Create Machine", Actions.CREATE_MACHINE, KeyEvent.VK_M));
        creation.add(createMenuItem("Create Machine Recipe", Actions.CREATE_MACHINE_RECIPE, KeyEvent.VK_T));
        creation.addSeparator();
        creation.add(createCheckbox("Automatically Add Items", "AutoAdd", 0));
        creation.add(createCheckbox("Require Round Crafts", "ReqRound", 0));

        JMenu plan = new JMenu("Plan");
        plan.setMnemonic(KeyEvent.VK_P);
        menuBar.add(plan);

        plan.add(createMenuItem("Plan Selected Node", "PlanSelected", KeyEvent.VK_P));
        plan.add(createMenuItem("Create Consumer of Item", "CreateConsumer", 0));
        plan.add(createCheckbox("Show Consumer/Producer Recipies", "HideConsProd", 0));
        //plan.add(createCheckbox("Automatically Add Items", "AutoAdd", 0));
        //plan.add(createCheckbox("Require Round Crafts", "ReqRound", 0));

        return menuBar;
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
            case "RemPlan":
                Settings.removePlanOnLoad = !Settings.removePlanOnLoad;
                break;
            case "HideConsProd":
                Settings.hideSellAndCreateRecipies = !Settings.hideSellAndCreateRecipies;
                break;
        
            default:
                System.err.println("Are you incredebly fucking stupid? " + source.getActionCommand());
                break;
        }
    }

    public void addInfo(String e){
        info.append(e + "\n");
        info.setRows(info.getRows()+1);
        info.setColumns(Math.max(info.getColumns(), e.length()/2 + 2));
    }

    private void destroySelectedItems(){
        List<Item> selected = itemList.getSelectedValuesList();
        for (Item item : selected) {
            Registry.removeItem(item);
        }
        updateRegistery();
    }

    private void destroySelectedRecipes(){
        List<Recipe> selected = recipeList.getSelectedValuesList();
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
        List<Recipe> selected = recipeList.getSelectedValuesList();
        for (Recipe recipe : selected) {
            plan.addPlanNode(recipe);
        }
    }

    public void updateRegistery(){
        itemList.removeAllValues();
        for (Item i : Registry.items) {
            itemList.addElement(i);
        }
        itemList.update();
        recipeList.removeAllValues();
        for (Recipe i : Registry.recipes) {
            recipeList.addElement(i);
        }
        recipeList.update();
        machineList.removeAllValues();
        for (Machine i : Registry.machines) {
            machineList.addElement(i);
        }
        machineList.update();
    }

    public void update(){
        keyboard.update();
    }

    public Item[] getSelectedItems(){
        return itemList.getSelectedValuesList().toArray(new Item[0]);
    }
    public Recipe[] getSelectedRecipies(){
        return recipeList.getSelectedValuesList().toArray(new Recipe[0]);
    }
    public Machine[] getSelectedMachines(){
        return machineList.getSelectedValuesList().toArray(new Machine[0]);
    }
}
