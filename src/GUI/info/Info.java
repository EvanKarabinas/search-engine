package GUI.info;


import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;


public class Info {
    Label stats;
    VBox info;

    public Info(){
        stats= new Label();
        stats.getStyleClass().add("stats");

        info = new VBox();
        info.getStyleClass().add("info-box");





        this.info.getChildren().addAll(stats);

    }

    public void updateInfo(int numberOfResults, String searchedTerm, double searchTime){
        this.stats.setText(numberOfResults+ " results about "+ "'"+searchedTerm +"'"+" in "+ searchTime +" s.");

    }

    public VBox getInfo(){
        return this.info;
    }
}
