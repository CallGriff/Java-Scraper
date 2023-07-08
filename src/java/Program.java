import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.File;
import java.util.Set;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) {

        HashMap<String, String>map = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        Document document;
        boolean additionalSearch;

        System.out.print("Enter a search term: ");
        String userSearch = scanner.nextLine();
        userSearch.replace(" ", "+");
        System.out.print("Search for related products? (y/n)");
        String searchRelatedChoice = "";

        while (!(searchRelatedChoice.matches("y|n"))) {

            searchRelatedChoice = scanner.nextLine().trim().toLowerCase();

            if(searchRelatedChoice.equals("y")) {
                additionalSearch = true;
            } else if(searchRelatedChoice.equals("n")) {
                additionalSearch = false;
            } else {
                System.out.println("Error, please type either y or n");
            }

        }

        try {

            /*
            Establish connection to target webpage
             */

            document = Jsoup.connect("https://www.ebay.co.uk/sch/i.html?_from=R40&_trksid=p4432023.m570.l1313&_nkw=" + userSearch).get();

            String title = document.title();
            System.out.println("Title: " + title);

            Elements item = document.select(".s-item.s-item__pl-on-bottom");

            for(int i = 0; i < item.size(); i++) {

                String itemName = item.get(i).getElementsByTag("img").attr("alt");
                String itemPrice = item.get(i).getElementsByClass("s-item__price").text();

                if(!(item.get(i).hasClass("s-item__price")) && (!(itemName.isBlank())) && !(itemPrice.isBlank())){
                    map.putIfAbsent(itemName.replace(",", "-"), itemPrice);
                    System.out.println(itemName + ": " + itemPrice);
                    System.out.println();
                }
            }

            File file = new File("results.csv");

            if(!file.exists()) {
                file.createNewFile();
            }

            PrintWriter printWriter = new PrintWriter(file);

            Set<String> mapSet = map.keySet();

            mapSet.stream().forEach(itm -> printWriter.println(itm + ", " + map.get(itm)));

            printWriter.close();

        } catch(IOException e) {

            e.printStackTrace();

        }

        System.out.println("done");

    }

    public static void getURLs(Element element) {

        System.out.println(element.getElementsByTag("a").attr("href"));

    }


}
