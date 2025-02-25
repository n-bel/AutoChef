package com.g04autochef.view.tableConstructors;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Vector;

/**
 * @param <ViewListener> the type of listener for calling the right getAttributes method from the controller
 * Interface that will contain the useful methods for constructing a table view
 */
public interface TableViewable<ViewListener extends TableViewable.TableViewListener> {

    default void defineTableView(ViewListener controller, TableView<?> tableView,
                                 Vector<TableColumn<?, ?>> tableColumns) {
        Vector<String> attributeIngredient = controller.getAttributes();
        constructionTableView(tableView, attributeIngredient, tableColumns);
    }

    default void constructionTableView (TableView<?> tv, Vector<String> attributeList, Vector<TableColumn<?, ?>> columnList){
        for (int i=0; i<columnList.size(); i++){
            columnList.get(i).setCellValueFactory(new PropertyValueFactory<>(attributeList.get(i)));
        }
    }

    interface TableViewListener {
        default Vector<String> getAttributes() {return null;}
    }
}
