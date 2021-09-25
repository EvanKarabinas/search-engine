package GUI;

import GUI.info.Info;
import GUI.mainmenu.MainMenu;
import GUI.pages.Pages;
import GUI.results.Results;
import GUI.search.Search;

import GUI.sort.SortMenu;
import javafx.application.Application;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.json.simple.parser.ParseException;
import searchengine.LuceneSearch;
import java.io.IOException;




public class App extends Application {

    private VBox layout;
    private LuceneSearch engine;
    private Info info;
    private Search search;
    private MainMenu mainMenu;
    private SortMenu sortmenu;
    private Results resultsContainer;



    public static void main(String[] args) {

        launch(args);

    }
    public void start(Stage primaryStage) throws IOException, ParseException {

        primaryStage.setTitle("Business Search Engine");
        layout = new VBox();
        layout.getStyleClass().add("layout");

        engine = new LuceneSearch();
        //engine.buildDirectory();

        mainMenu = new MainMenu();
        layout.getChildren().add(mainMenu.getMenu());

        search = new Search();
        info = new Info();

        sortmenu=new SortMenu();
        sortmenu.getDropdown().valueProperty().addListener(e->search());

        this.resultsContainer = new Results();





        search.getSearchIconButton().setOnAction(e->search());

        layout.getChildren().add(search.getSearch());
        layout.getChildren().add(info.getInfo());
        layout.getChildren().add(sortmenu.getSortMenu());



        Scene mainScene = new Scene(layout,1080,720);

        mainScene.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER))
            {
                search();
            }
        });

        mainScene.getStylesheets().add("guistyles.css");
        primaryStage.setScene(mainScene);
        primaryStage.show();

    }


    private void search(){
        double searchStartTime;
        double searchStopTime;
        double searchElapsedTime;

        System.out.println(this.sortmenu.getSortType());
        String sortType = this.sortmenu.getSortType();
        try {
            searchStartTime = System.currentTimeMillis();

            if (mainMenu.getActiveButton().equals("Business Name")) {

                engine.searchBusinessName("name", search.getUserInput(),sortType);

            } else if (mainMenu.getActiveButton().equals("Business Type")) {

                engine.searchBusinessName("categories", search.getUserInput(),sortType);

            }else if (mainMenu.getActiveButton().equals("Reviews")) {

                engine.searchBusinessName("reviews", search.getUserInput(),sortType);

            }else if (mainMenu.getActiveButton().equals("Tips")) {

                engine.searchBusinessName("tips", search.getUserInput(),sortType);

            }else{

                engine.searchBusinessName("all", search.getUserInput(),sortType);


            }
            layout.getChildren().remove(resultsContainer.getResults());
            //resultsContainer.updateResults(engine.getSearchResults(), search.getUserInput(),mainMenu.getActiveButton());
            resultsContainer.initPagination(engine.getSearchResults(), search.getUserInput(),mainMenu.getActiveButton());
            layout.getChildren().add(resultsContainer.getResults());


            searchStopTime = System.currentTimeMillis();
            searchElapsedTime = (searchStopTime - searchStartTime) / 1000;

            info.updateInfo(engine.getSearchResults().size(), search.getUserInput(), searchElapsedTime);

        }catch (org.apache.lucene.queryparser.classic.ParseException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}