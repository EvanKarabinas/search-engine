package GUI.mainmenu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;



public class MainMenu {

    private HBox menu;
    private MenuButton allB;
    private MenuButton businessNameB;
    private MenuButton businessTypeB ;
    private MenuButton reviewsB;
    private MenuButton tipsB;



    public MainMenu(){

        this.menu = new HBox();

        Label logo1 = new Label("search");
        logo1.getStyleClass().add("logo1");

        allB = new MenuButton("All");
        businessNameB = new  MenuButton("Business Name");
        businessTypeB = new  MenuButton("Business Type");
        reviewsB = new MenuButton("Reviews");
        tipsB = new MenuButton("Tips");

        businessNameB.activate();
        allB.deactivate();
        businessTypeB.deactivate();
        reviewsB.deactivate();
        tipsB.deactivate();

        allB.getMenuButton().setOnAction(event -> {allB.activate(); businessNameB.deactivate();
                                                    businessTypeB.deactivate();reviewsB.deactivate();
                                                    tipsB.deactivate();});

        businessNameB.getMenuButton().setOnAction(event -> {businessNameB.activate(); allB.deactivate();
            businessTypeB.deactivate();reviewsB.deactivate();
            tipsB.deactivate();});

        businessTypeB.getMenuButton().setOnAction(event -> {businessTypeB.activate(); businessNameB.deactivate();
            allB.deactivate();reviewsB.deactivate();
            tipsB.deactivate();});

        reviewsB.getMenuButton().setOnAction(event -> {reviewsB.activate(); businessNameB.deactivate();
            allB.deactivate();businessTypeB.deactivate();
            tipsB.deactivate();});

        tipsB.getMenuButton().setOnAction(event -> {tipsB.activate(); businessNameB.deactivate();
            allB.deactivate();reviewsB.deactivate();
            businessTypeB.deactivate();});



        this.menu.getChildren().addAll(logo1,businessNameB.getMenuButton()
                                        ,businessTypeB.getMenuButton(),reviewsB.getMenuButton()
                                        ,tipsB.getMenuButton(),allB.getMenuButton());

    }

    public String getActiveButton(){

        if(allB.getActive()){
            return allB.getLabel();
        }
        if(businessNameB.getActive()){
            return businessNameB.getLabel();
        }
        if(businessTypeB.getActive()){
            return businessTypeB.getLabel();
        }
        if(reviewsB.getActive()){
            return reviewsB.getLabel();
        }
        if(tipsB.getActive()){
            return tipsB.getLabel();
        }

        return  null;
    }

    public HBox getMenu() {
        return this.menu;
    }
}
