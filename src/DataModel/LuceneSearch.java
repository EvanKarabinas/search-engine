package searchengine;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;

import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;


public class LuceneSearch {

    private Directory Index;
    private PerFieldAnalyzerWrapper analyzer;
    private ArrayList<Document> searchResults ;
    private HashMap <String, HashMap<String,ArrayList<String>>> businessIds;

    private static final String INDEX_DIRECTORY = "/Users/evank/Desktop/lucene_directory";

    public LuceneSearch(){
        searchResults = new ArrayList<Document>();

        Map<String,Analyzer> analyzerMap = new HashMap<>();
        analyzerMap.put("reviews",new WhitespaceAnalyzer());
        analyzerMap.put("tips", new WhitespaceAnalyzer());
        this.analyzer =new PerFieldAnalyzerWrapper (new EnglishAnalyzer(), analyzerMap);

    }


    public void buildDirectory() throws IOException, ParseException {

        findBusinessIdsByCityName("Toronto");
        mergeBusinessWithReviewsAndTips();
        buildLuceneDirectory();
    }

    public void mergeBusinessWithReviewsAndTips() throws FileNotFoundException, ParseException {
        int linesRead=0;
        int thousand=1;
        System.out.println("Merging business with tips...");
        long startTime = System.currentTimeMillis();

        //-------------------Parsing and Merging Tips------------------------------------------

        Scanner in = new Scanner(new FileReader("src/tip.json"));
        while(in.hasNext()) {
            linesRead++;
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(in.nextLine());
            JSONObject jsonObject = (JSONObject) obj;
            String business_id= (String) jsonObject.get("business_id");
            String tip= (String) jsonObject.get("text");

            if(businessIds.containsKey(business_id)){
                businessIds.get(business_id).get("tips").add(tip);
            }

            if(linesRead==thousand*100000){
                System.out.println("\r"+"Read "+linesRead+" lines from tips.json.");
                thousand++;
            }
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Finished merging Reviews with business in "+ elapsedTime/1000+"s");

        //-------------------Parsing and Merging Reviews---------------------------------------

        System.out.println("Merging business with reviews...");
        linesRead=0;
        thousand=1;
        Scanner inReviewFile = new Scanner(new FileReader("src/review.json"));
        startTime = System.currentTimeMillis();


        while(inReviewFile.hasNext()) {
            linesRead++;
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(inReviewFile.nextLine());
            JSONObject jsonObject = (JSONObject) obj;
            String business_id= (String) jsonObject.get("business_id");
            String review= (String) jsonObject.get("text");

            if(businessIds.containsKey(business_id)){
                businessIds.get(business_id).get("reviews").add(review);
            }

            if(linesRead==thousand*100000){
                System.out.println("\r"+"Read "+linesRead+" lines from reviews.json.");
                thousand++;
            }
        }

        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        System.out.println("Finished merging Reviews with business in "+ elapsedTime/1000+"s");
    }

    public void buildLuceneDirectory() throws IOException {

        int i;

        System.out.println("Building Lucene Directory...");
        long startTime = System.currentTimeMillis();

        this.Index= FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriterConfig indexWriterConfig=new IndexWriterConfig(this.analyzer);
        IndexWriter writer= new IndexWriter(this.Index,indexWriterConfig);

        for ( String key : businessIds.keySet() ) {
            String business_id=key;
            String business_name="";
            for (String name : businessIds.get(key).get("name")){
                business_name+=name;
            }
            //System.out.println(business_name);

            String categories="";
            for (String category : businessIds.get(key).get("categories")){
                categories+=category;
            }
            //System.out.println(categories+"\n\n");

            String stars ="";
            for (String star : businessIds.get(key).get("stars")){
                stars+=star;
            }
            String reviewCount= "";
            for (String reviewC : businessIds.get(key).get("review_count")){
                reviewCount+=reviewC;
            }

            String reviews="";
            for (String review : businessIds.get(key).get("reviews")){
                reviews+=review;
            }

            String tips="";
            for (String tip : businessIds.get(key).get("tips")){
                tips+=tip;
            }


            Document document =new Document();
            document.add(new TextField("business_id",business_id,Field.Store.YES));
            document.add(new TextField("name",business_name,Field.Store.YES));
            document.add(new TextField("categories",categories,Field.Store.YES));
            try{
                //document.add(new NumericDocValuesField("stars_sort",doubleToSortableLong(Double.parseDouble(stars))));
                document.add(new StoredField("stars",Double.parseDouble(stars)));
            }catch (NumberFormatException e){
               // document.add(new NumericDocValuesField("stars_sort",0));
                document.add(new StoredField("stars",0));
            }
            try{
                //document.add(new NumericDocValuesField("review_count_sort",doubleToSortableLong(Double.parseDouble(reviewCount))));
                document.add(new StoredField("review_count",Double.parseDouble(reviewCount)));
            }catch (NumberFormatException e){
                //document.add(new NumericDocValuesField("review_count_sort",0));
                document.add(new StoredField("review_count",0));
            }

            document.add(new TextField("reviews",reviews,Field.Store.YES));
            document.add(new TextField("tips",tips,Field.Store.YES));
            writer.addDocument(document);
            System.out.println(business_name +" : "+ document.get("stars")+" / "+document.get("review_count"));
            //System.out.println("Business with id : "+ key+" successfully added in Directory!");
        }
        writer.close();
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("Finished building Lucene Directory in "+ elapsedTime/1000+"s");

    }


    public HashMap<String, HashMap<String,ArrayList<String>>> findBusinessIdsByCityName(String city) throws FileNotFoundException, ParseException {

        System.out.println("Searching for business IDs from "+city+" in file : business.json");
        businessIds=new HashMap();
        Scanner in = new Scanner(new FileReader("src/business.json"));
        int reviewsCounter=0;
        int businessCounter=0;
        int maxReviews=0;
        int minReviews=10000000;
        int averageReviews;
        int reviewsInt;

        while(in.hasNext()){
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(in.nextLine());
            JSONObject jsonObject = (JSONObject) obj;

            String categories = (String) jsonObject.get("categories");
            String stars = String.valueOf( jsonObject.get("stars"));
            String reviewCount = String.valueOf( jsonObject.get("review_count"));
            //ArrayList<String> emptyList = new ArrayList<>();
            //emptyList.add("-");



            String cityFromFile= (String) jsonObject.get("city");

            if(cityFromFile.equals(city)){

                businessCounter++;

                HashMap<String,ArrayList<String>> businessInfo = new HashMap<String,ArrayList<String>>();

                ArrayList<String> businessNameList = new ArrayList<>();
                businessNameList.add((String) jsonObject.get("name"));
                businessInfo.put("name",businessNameList);

                if (categories != null){
                    ArrayList<String> categoriesList = new ArrayList<>();
                    categoriesList.add(categories);
                    businessInfo.put("categories",categoriesList);

                }else{
                    businessInfo.put("categories",new ArrayList<>());
                }

                if (stars != null){
                    ArrayList<String> starsList = new ArrayList<>();
                    starsList.add(stars);
                    businessInfo.put("stars",starsList);
                }else{
                    businessInfo.put("stars",new ArrayList<>());
                }

                if (reviewCount != null){
                    ArrayList<String> reviewCountList = new ArrayList<>();
                    reviewCountList.add(reviewCount);
                    businessInfo.put("review_count",reviewCountList);

                    //-------ONLY USE FOR THE REPORT----------------
                    reviewsInt=Integer.parseInt(String.valueOf( jsonObject.get("review_count")));
                    reviewsCounter=reviewsCounter+ reviewsInt;
                    if(reviewsInt>maxReviews){
                        maxReviews=reviewsInt;
                    }
                    if(reviewsInt<minReviews){
                        minReviews=reviewsInt;
                    }
                    //----------------------------------------------

                }else{
                    businessInfo.put("review_count",new ArrayList<>());
                }

                businessInfo.put("reviews",new ArrayList<>());
                businessInfo.put("tips",new ArrayList<>());

                businessIds.put((String) jsonObject.get("business_id"),businessInfo);
            }
        }

        //-------ONLY USE FOR THE REPORT----------------
        averageReviews=reviewsCounter/businessCounter;

        System.out.println("---------Report Data---------");
        System.out.println("* Business : "+businessCounter);
        System.out.println("* Total Reviews : "+reviewsCounter);
        System.out.println("* Min Reviews : "+minReviews);
        System.out.println("* Max Reviews : "+maxReviews);
        System.out.println("* Average Reviews : "+averageReviews);
        System.out.println("-----------------------------\n");
        //----------------------------------------------

        return businessIds;
    }

    public void searchBusinessName(String inField,String queryString, String sortType) throws org.apache.lucene.queryparser.classic.ParseException, IOException {

        TopDocs topDocs;

        //this.analyzer=new EnglishAnalyzer(EnglishAnalyzer.getDefaultStopSet());
        this.Index= FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexReader indexReader = DirectoryReader.open(this.Index);
        IndexSearcher searcher = new IndexSearcher(indexReader);

        Query query;

        if(inField.equals("name") || inField.equals("categories")){

            query = new QueryParser(inField,analyzer).parse(QueryParser.escape(queryString));

        }else if(inField.equals("reviews") || inField.equals("tips")) {

            PhraseQuery.Builder builder = new PhraseQuery.Builder();

            String[] words = queryString.split(" ");
            int i = 0;
            for (String word : words) {
                builder.add(new Term(inField, word.toLowerCase()), i);
                i++;
            }
            query = builder.build();


        }else{
            Query nameQuery = new QueryParser("name",analyzer).parse(QueryParser.escape(queryString));
            Query categoryQuery = new QueryParser("categories",analyzer).parse(QueryParser.escape(queryString));

            PhraseQuery.Builder reviewsQueueryBuilder = new PhraseQuery.Builder();
            String[] words = queryString.split(" ");
            int i = 0;
            for (String word : words) {
                reviewsQueueryBuilder.add(new Term("reviews", word.toLowerCase()), i);
                i++;
            }
            PhraseQuery reviewsQuery = reviewsQueueryBuilder.build();

            PhraseQuery.Builder tipsQueryBuilder = new PhraseQuery.Builder();
            words = queryString.split(" ");
            for (String word : words) {
                tipsQueryBuilder.add(new Term("tips", word.toLowerCase()), i);
                i++;
            }
            PhraseQuery tipsQuery = tipsQueryBuilder.build();


            query = new BooleanQuery.Builder()
                    .add(nameQuery, BooleanClause.Occur.MUST)
                    .add(categoryQuery, BooleanClause.Occur.MUST)
                    .add(reviewsQuery, BooleanClause.Occur.MUST)
                    .add(tipsQuery, BooleanClause.Occur.MUST)
                    .build();

        }

        topDocs = searcher.search(query, 200000);
        /*SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("%20","%20");
        QueryScorer queryScorer = new QueryScorer(query);
        Highlighter highlighter = new Highlighter(formatter, queryScorer);
        highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer, Integer.MAX_VALUE));
        highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
        highlighter.getBestFragment(this.analyzer, fieldName, fieldValue);*/

        this.searchResults.clear();

        for(ScoreDoc scoreDoc : topDocs.scoreDocs){
            this.searchResults.add(searcher.doc(scoreDoc.doc));

        }

        if(sortType.equals("Rating")){

            this.searchResults.sort((o1, o2) ->
            {
                Double starso1 = Double.parseDouble(o1.get("stars"));
                Double starso2 = Double.parseDouble(o2.get("stars"));

                int sComp = starso2.compareTo(starso1);

                if (sComp != 0) {
                    return sComp;
                }

                Double reviewsCounto1 = Double.parseDouble(o1.get("review_count"));
                Double reviewsCounto2 = Double.parseDouble(o2.get("review_count"));
                return reviewsCounto2.compareTo(reviewsCounto1);
            });

        }

    }


    public ArrayList<Document> getSearchResults() {
        return this.searchResults;
    }








}
