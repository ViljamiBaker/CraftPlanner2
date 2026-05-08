package craftPlanner.GUI.util;

import javax.swing.DefaultListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import craftPlanner.Settings;

public class TextFilteredListModel<T> extends FilteredListModel<T>{
    public TextFilteredListModel(DefaultListModel<T> source, GhostText filter){
        super(source);
        this.setFilter(new FilteredListModel.Filter() {
        public boolean accept(Object element) {
            if(element instanceof Hideable){
                if(((Hideable)element).hide() && Settings.hideSellAndCreateRecipies)
                    return false;
            }
            if(filter.isEmpty())return true;
            String filtertext = filter.getText();
            return element.toString().toLowerCase().contains(filtertext.toLowerCase());
        }
        });
        filter.textfield.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleUpdate();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                handleUpdate();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {}
        
            private void handleUpdate() {
                doFilter();
            }
        });
    }
}
