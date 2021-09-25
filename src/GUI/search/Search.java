package GUI.search;


import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class Search {

    private HBox search;
    private TextField searchInput;
    private Button searchIconButton;

    public  Search(){

        this.search=new HBox();

        Label logo2 = new Label("engine.");
        logo2.getStyleClass().add("logo2");

        this.searchInput = new TextField();
        this.searchInput.setMaxHeight(20); //sets height of the TextArea to 400 pixels
        this.searchInput.setPrefWidth(740);

        Image searchIconImg = new Image("search.png");
        ImageView searchIcon = new ImageView(searchIconImg);
        searchIcon.setFitHeight(30);
        searchIcon.setFitWidth(30);


        this.searchIconButton=new Button();
        this.searchIconButton.getStyleClass().add("search-icon-button");
        this.searchIconButton.setGraphic(searchIcon);


        this.search.getChildren().addAll(logo2,this.searchInput,this.searchIconButton);
    }

    public HBox getSearch(){
        return  this.search;
    }

    public String getUserInput(){
        return  this.searchInput.getText();
    }

    public Button getSearchIconButton(){
        return this.searchIconButton;
    }

}
