package searchengine;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class JsonParser {

    public void startParsing() throws FileNotFoundException {
        ArrayList<String> business =new ArrayList<String>();
        ArrayList<String> reviews =new ArrayList<String>();
        int businessCounter =0;
        int searchedReviewsCounter=0;
        int millions=1;
        Scanner in = new Scanner(new FileReader("src/business.json"));




        while(in.hasNext()){
            //System.out.println(in.nextLine());


            JSONParser parser = new JSONParser();
            JSONParser reviewParser = new JSONParser();

            //System.out.println("Start Parsing...");

            try {
                Object obj = parser.parse(in.nextLine());

                JSONObject jsonObject = (JSONObject) obj;
                String city= (String) jsonObject.get("city");

                if(city.equals("Toronto")){
                    String businessName= (String) jsonObject.get("name");
                    business.add(businessName);
                    System.out.println("business: "+businessName);
                    businessCounter++;
                    String businessId = (String) jsonObject.get("business_id");
                    Scanner reviewInput = new Scanner(new FileReader("src/review.json"));

                    while(reviewInput.hasNext() && searchedReviewsCounter<1000000){
                        Object reviewObj = reviewParser.parse(reviewInput.nextLine());
                        JSONObject jsonReview = (JSONObject) reviewObj;
                        if(jsonReview.get("business_id").equals(businessId)){
                            String review= (String) jsonReview.get("text");
                            reviews.add(review);
                            //System.out.println("Review for "+businessName+" : "+review);
                            System.out.println("====found review in line: "+ searchedReviewsCounter);
                        }
                        searchedReviewsCounter++;
                        if (searchedReviewsCounter== 1000000*millions){
                            System.out.println("reviews searched: "+ searchedReviewsCounter);
                            millions++;
                        }
                    }
                    System.out.println("Number of reviews for "+businessName+ " : "+reviews.size());


                }
                searchedReviewsCounter=0;



            } catch (ParseException e) {
                e.printStackTrace();
            }



        }
        System.out.println("Number of business:"+businessCounter);

    }
}
