package craftPlanner.GUI.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ItemList<T> extends JPanel{
    JList<T> list;
    FilteredListModel<T> model;
    DefaultListModel<T> source;

    public ItemList(ActionListener al, String name, boolean destroy, boolean add, boolean rename){
        this.setLayout(new BorderLayout(0,0));
        GhostText filter = new GhostText(new JTextField(""), "Name here");
        source = new DefaultListModel<T>();
        model = new TextFilteredListModel<T>(source,filter);
        list = new JList<>(model);
        KeyListener[] lsnrs = list.getKeyListeners();
        for (int i = 0; i < lsnrs.length; i++) {
            list.removeKeyListener(lsnrs[i]);
        }
        
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        JScrollPane listScroll = new JScrollPane(list);
        listScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.setPreferredSize(new Dimension(200, 50));
        if(destroy){
            JButton destroyButton = new JButton("Destroy");
            destroyButton.setActionCommand("Destroy" + name);
            destroyButton.addActionListener(al);
            buttonPane.add(destroyButton);
        }

        if(add){
            JButton addButton = new JButton("Add");
            addButton.setActionCommand("Add" + name);
            addButton.addActionListener(al);
            buttonPane.add(addButton);
        }
        if(rename){
            JButton renameButton = new JButton("Rename");
            renameButton.setActionCommand("Rename" + name);
            renameButton.addActionListener(al);
            buttonPane.add(renameButton);
        }
        JPanel doublepane = new JPanel();
        doublepane.setLayout(new BoxLayout(doublepane, BoxLayout.PAGE_AXIS));
        doublepane.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
        doublepane.add(Box.createVerticalGlue());
        doublepane.setPreferredSize(new Dimension(200, 60));

        doublepane.add(filter.textfield);
        doublepane.add(buttonPane);

        this.add(listScroll, BorderLayout.CENTER);
        this.add(doublepane,BorderLayout.PAGE_END);
    }

    public List<T> getSelectedValuesList(){
        return list.getSelectedValuesList();
    }

    public void removeAllValues(){
        model.removeAllElements();
    }

    public void addElement(T element){
        model.addElement(element);
    }

    public void update(){
        model.doFilter();
    }
}
