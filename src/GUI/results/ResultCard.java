package GUI.results;


import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;

public class ResultCard {
    private Label businessName ;
    private Label categories ;
    private Label starsAndReviewsCount ;
    private Label reviews1 ;
    private Label reviews2 ;
    private Label reviews3 ;
    private TextFlow reviews;
    private TextFlow tips;
    private Label tips1;
    private Label tips2;
    private Label tips3;
    private VBox resultCard;


    public ResultCard (String businessName, String categories, String stars, String reviewCount, String reviews, String tips,String queryString ){


        resultCard = new VBox();

        this.businessName= new Label(businessName);
        this.businessName.getStyleClass().add("card-business-name");
        this.categories= new Label(categories);
        this.categories.getStyleClass().add("card-categories");
        this.starsAndReviewsCount= new Label(stars+" | Reviews : "+reviewCount);

        resultCard.getChildren().addAll(this.businessName,
                this.categories,this.starsAndReviewsCount);

        if (!reviews.equals("")){
            int indexQuery=reviews.toLowerCase().indexOf(queryString.toLowerCase().replaceAll("[:?!@#$%^&*().,;/]",""));

            this.reviews1= new Label("Review : "+reviews.substring(0,indexQuery));
            this.reviews2= new Label(reviews.substring(indexQuery,indexQuery+queryString.length()));
            this.reviews3= new Label(reviews.substring(indexQuery+queryString.length()));

            this.reviews= new TextFlow();
            this.reviews.getChildren().addAll(this.reviews1,this.reviews2,this.reviews3);
            this.reviews.getStyleClass().add("card-reviews");
            this.reviews2.getStyleClass().add("card-reviews-query-string");
            resultCard.getChildren().add(this.reviews);
        }

        if (!tips.equals("")){
            int indexQuery=tips.toLowerCase().indexOf(queryString.toLowerCase().replaceAll("[:?!@#$%^&*().,;/]",""));

            this.tips1= new Label("Tip : "+tips.substring(0,indexQuery));
            this.tips2= new Label(tips.substring(indexQuery,indexQuery+queryString.length()));
            this.tips3= new Label(tips.substring(indexQuery+queryString.length()));

            this.tips= new TextFlow();
            this.tips.getChildren().addAll(this.tips1,this.tips2,this.tips3);
            this.tips.getStyleClass().add("card-tips");
            this.tips2.getStyleClass().add("card-reviews-query-string");
            resultCard.getChildren().add(this.tips);

        }





    }
    public VBox getResultCard(){
        return this.resultCard;
    }


}
