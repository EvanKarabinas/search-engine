package GUI.results;

import GUI.pages.Pages;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.util.Version;

import java.util.ArrayList;

public class Results {

    private ScrollPane resultsContainer;
    private VBox results;
    private ResultCard card;
    private Pagination pagination;

    public Results(){

        resultsContainer=new ScrollPane();
        resultsContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        results= new VBox(20);
        //results.setPrefWidth(Double.MAX_VALUE);
        results.getStyleClass().add("results");
        resultsContainer.getStyleClass().add("results-container");



    }

    public void initPagination(ArrayList<Document> docs, String queryString, String typeOfSearch){
        int numOfPages=docs.size()/10;
        if(docs.size()==0){
            numOfPages=1;
        }
        pagination = new Pagination(numOfPages, 0);
        pagination.setMinHeight(540);
        pagination.setPageFactory(pageIndex ->{
            updateResults(docs, queryString, typeOfSearch,pageIndex);

            resultsContainer.setContent(this.results);
            return resultsContainer;});
    }

    public  void updateResults(ArrayList<Document> docs, String queryString, String typeOfSearch,int page){

        results.getChildren().clear();

        int i;

        if(docs.isEmpty()){
            Label noResults=new Label("0 results about '"+queryString+"'.");
            this.results.getChildren().add(noResults);
            return;
        }

        int firstResult=(page)*10;
        int lastResult=(page+1)*10;
        if(docs.size()<10){
            lastResult=docs.size();
        }

        System.out.println("Results  : "+firstResult+" - "+lastResult +" , (page : "+page+" )");

        for (i=firstResult;i<lastResult;i++){

            if(typeOfSearch.equals("Reviews")) {

                int queryIndex = docs.get(i).get("reviews").toLowerCase().indexOf(queryString.toLowerCase());
                //System.out.println("queryIndex"+docs.get(i).get("reviews")+"\n");
                System.out.println("queryIndex "+queryIndex+"\n\n");
                card = new ResultCard((i + 1) + ". " + docs.get(i).get("name"),
                        docs.get(i).get("categories"),
                        docs.get(i).get("stars"),
                        (int) Double.parseDouble(docs.get(i).get("review_count"))+"",
                        findSentenceOffsets(docs.get(i).get("reviews"),queryIndex,queryString),
                        "", queryString);

            }else if(typeOfSearch.equals("Tips")){

                int queryIndex = docs.get(i).get("tips").toLowerCase().indexOf(queryString.toLowerCase());

                card = new ResultCard((i + 1) + ". " + docs.get(i).get("name"),
                        docs.get(i).get("categories"),
                        docs.get(i).get("stars"),
                        (int) Double.parseDouble(docs.get(i).get("review_count"))+"",
                        "",
                        findSentenceOffsets(docs.get(i).get("tips"),queryIndex,queryString),queryString);

            }else if(typeOfSearch.equals("All")){
                int queryIndexReviews = docs.get(i).get("reviews").toLowerCase().indexOf(queryString.toLowerCase());
                int queryIndexTips = docs.get(i).get("tips").toLowerCase().indexOf(queryString.toLowerCase());
                //System.out.println(docs.get(i).get("name"));
                card = new ResultCard((i+1)+". "+docs.get(i).get("name"),docs.get(i).get("categories"),
                        docs.get(i).get("stars"),(int) Double.parseDouble(docs.get(i).get("review_count"))+"",
                        findSentenceOffsets(docs.get(i).get("reviews"),queryIndexReviews,queryString),
                        findSentenceOffsets(docs.get(i).get("tips"),queryIndexTips,queryString),queryString);


            }else{

                //System.out.println(docs.get(i).get("name"));
                card = new ResultCard((i+1)+". "+docs.get(i).get("name"),docs.get(i).get("categories"),
                        docs.get(i).get("stars"),(int) Double.parseDouble(docs.get(i).get("review_count"))+"","","",queryString);
            }



            this.results.getChildren().add(card.getResultCard());
        }


    }
    public Pagination getResults(){
        return  this.pagination;
    }

    public String findSentenceOffsets(String text, int wordIndex,String queryString){

        int lowerOffset=wordIndex;
        int upperOffset=wordIndex+queryString.length();
        while(lowerOffset>0  && text.charAt(lowerOffset)!='\n' && text.charAt(lowerOffset)!='.'
                && text.charAt(lowerOffset)!='!'){
            lowerOffset--;
            if(lowerOffset<=0){
                break;
            }
        }
        if(upperOffset>0){
            while(upperOffset<text.length()  && text.charAt(upperOffset)!='\n' && text.charAt(upperOffset)!='.'
                    && text.charAt(upperOffset)!='!'){
                upperOffset++;
            }
        }

        ArrayList<Integer> offsets = new ArrayList<>();
        System.out.println("("+lowerOffset+","+upperOffset+")\n");
        String output = text.substring(lowerOffset, upperOffset).trim()
                .replace(".","").replace("!","");
        output=output+"...";
        System.out.println("output : "+text+"\n");

        return output;
    }

}
