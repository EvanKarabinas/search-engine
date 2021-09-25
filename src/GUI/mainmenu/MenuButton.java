package GUI.mainmenu;


import javafx.scene.control.Button;

public class MenuButton {
    private Button menuButton;
    private boolean active;
    private String label;

    public MenuButton(String label){
        this.menuButton= new Button(label);
        this.menuButton.getStyleClass().add("menu-button-not-active");
        this.active=false;
        this.label=label;


    }
    public Button getMenuButton(){
        return  this.menuButton;
    }

    public void activate(){
        this.active=true;
        System.out.println(this.label+" active!");
        this.menuButton.getStyleClass().clear();
        this.menuButton.getStyleClass().add("menu-button-active");
    }

    public void deactivate(){
        this.active=false;
        System.out.println(this.label+" not active.");
        this.menuButton.getStyleClass().clear();
        this.menuButton.getStyleClass().add("menu-button-not-active");
    }

    public boolean getActive(){
        return this.active;
    }

    public  String getLabel(){
        return this.label;
    }
}
