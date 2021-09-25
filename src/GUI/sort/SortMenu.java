package GUI.sort;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class SortMenu {
    private HBox sortMenu;
    private ComboBox dropdown;

    public SortMenu(){
        ObservableList<String> options = FXCollections.observableArrayList(
                "Default",
                        "Rating"
                );
        this.dropdown = new ComboBox(options);
        this.dropdown.setValue("Sort by");
        this.sortMenu=new HBox();
        this.sortMenu.setAlignment(Pos.BASELINE_RIGHT);
        this.sortMenu.getChildren().add(dropdown);
        this.sortMenu.getStyleClass().add("sort-menu");
    }

    public HBox getSortMenu(){
        return this.sortMenu;
    }
    public ComboBox getDropdown(){
        return this.dropdown;
    }

    public String getSortType(){
        return this.dropdown.getValue().toString();

    }
}
