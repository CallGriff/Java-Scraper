import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.util.Set;

public class Program {

    public static void main(String[] args) {

        HashMap<String, String>map = new HashMap<>();

        System.out.println("running...");
        Document document;

        try {

            document = Jsoup.connect("https://www.ebay.co.uk/").get(); // Test-site

            String title = document.title();
            System.out.println("Title: " + title);

            Elements item = document.select(".vl-carousel__item");

            for(int i = 0; i < item.size(); i++) {

                String itemName = item.get(i).getElementsByTag("img").attr("alt");
                String itemPrice = item.get(i).getElementsByClass("vl-item__displayPrice").text();

                if(!(item.get(i).hasClass("vl-item__displayPrice")) && (!(itemName.isBlank())) && !(itemPrice.isBlank())){
                    map.putIfAbsent(itemName, itemPrice);
                    //System.out.println(itemName + ": " + itemPrice);
                    //System.out.println();
                }


            }

            File file = new File("results.csv");

            if(!file.exists()) {
                file.createNewFile();
            }

            PrintWriter printWriter = new PrintWriter(file);

            Set<String> mapSet = map.keySet();

            mapSet.stream().forEach(itm -> printWriter.println(itm +", " + map.get(itm)));

            printWriter.close();

        } catch(IOException e) {

            e.printStackTrace();

        }

        System.out.println("done");

    }
}
