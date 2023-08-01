package javascraper;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import com.example.javascraper.Webscraper;
import webpage.WebpageManager;

public class WebscraperTest {

    @Test
    public void longSearchTest() {

        Webscraper webscraper = new Webscraper(WebpageManager.Type.ANYLISTINGS);

        String search = "Lorem ipsum dolor sit amet Ut quia fugit ut aperiam delectus sit praesentium perspiciatis";

        webscraper.setUserSearch(search);

        String sanitizedSearch = webscraper.sanitizedUrl();

        assertEquals("https://www.ebay.co.uk/sch/i.html?_from=R40&_nkw=Lorem+ipsum+dolor+sit+amet+Ut+quia+"
                + "fugit+ut+aperiam+delectus+sit+praesentium+perspiciatis", sanitizedSearch);

    }

}
