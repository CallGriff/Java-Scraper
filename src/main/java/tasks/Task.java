package tasks;

import org.jsoup.nodes.Document;
import webpage.Item;
import webpage.WebpageManager;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;


class Task implements Callable {
    private WebpageManager webpageManager;
    private String url;
    public Task(WebpageManager webpageManager, String url) {

        this.webpageManager = webpageManager;
        this.url = url;
    }

    /**
     * obtains the items from the given searchapge
     * @return list of the items on the given searchpage
     * @throws InterruptedException
     */
    @Override
    public ArrayList<Item> call() throws InterruptedException {

        ArrayList<Item>result = new ArrayList<>();

        try{
            Document searchPage = this.webpageManager.getWebpage(this.url);
            result.addAll(this.webpageManager.getSearchedItems(searchPage));

        }catch(TimeoutException e) {

            /* in the event of 3 consecutive connection failures, the thread will sleep, resulting in a TimeoutException
            thrown in the calling method
             */
            System.out.println("Failed to connect to target webpage... Thread timing out");
            Thread.sleep(10000);
        }
        return result;
    }


}
