package webpage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import proxypool.ProxyPool;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class WebpageManager {
    private Document currentPage;
    private ProxyPool proxyPool;
    private int maximumPageNumber;
    private Type scraperType;

    public WebpageManager(ProxyPool proxyPool, Type scraperType) {

        this.proxyPool = proxyPool;
        this.currentPage = null;
        this.maximumPageNumber = 1;
        this.scraperType = scraperType;
    }

    /**
     * returns and sets the target html document
     * @param url target webpage
     * @return html document of target webpage
     * @throws TimeoutException resulting from 3 consecutive connection failures
     */
    public Document getWebpage(String url) throws TimeoutException {
        setCurrentPage(url, 0);
        return this.currentPage;
    }

    /**
     * sets the current page document from a given url
     * rotates to a new proxy if the current proxy fails to connect
     * @param url target webpage
     * @param failedConnections number of failed connections
     * @throws {@code TimeoutException} after 3 consecutive failed attempts
     */
    private void setCurrentPage(String url, int failedConnections) throws TimeoutException {

        Proxy currentProxy = this.proxyPool.getNewProxy();

        try {
            this.currentPage = Jsoup.connect(url)
                    .ignoreHttpErrors(true)
                    .proxy(currentProxy)
                    .get();
        }catch(IOException e) {

            if(failedConnections == 2) {
                throw new TimeoutException();
            }

            failedConnections++;

            System.out.println("Failed to retrieve target webpage. Rotating proxy...");
            this.proxyPool.removeBrokenProxy(currentProxy);
            this.proxyPool.getNewProxy();
            setCurrentPage(url, failedConnections);
        }
    }

    private void setMaximumPageNumber(int maximumPageNumber) {

        this.maximumPageNumber = maximumPageNumber;
    }

    public int getMaximumPageNumber() {
        return this.maximumPageNumber;
    }

    /**
     * finds and sets the final or upper-limit page number
     * website cannot show more than 10,000 items, resulting in the value 167, with 60 items per page
     * @param firstPageUrl url of the first page given by the search term
     * @throws TimeoutException in the event of 3 consecutive failed connection attempts
     */
    public void setMaximumPageNumber(String firstPageUrl) throws TimeoutException {

        getWebpage(firstPageUrl);

        if(checkSingleSearchpage()) {
            return;
        }

        int itemCount = getItemCount();

        if(itemCount > 10000) {
            this.setMaximumPageNumber(167);
            return;
        }

        int candidateTotalItem = itemCount / 60;

        String lastPageUrl = firstPageUrl + "&_pgn=" + candidateTotalItem;

        getWebpage(lastPageUrl);

        this.setMaximumPageNumber(getActualMaxPageNumber());
    }

    /**
     * retrieves the number of item listings on the website
     * @return total listed items
     */
    private int getItemCount() {

        // locate the element by class name

        String totalItemsElement = this.currentPage.selectFirst(".srp-controls__count-heading").text();

        // element is filtered by its field-values to obtain only the total-item number

        if(!totalItemsElement.contains("+")) {
            String[] totalItemsPieces = totalItemsElement.split(" ");
            String totalItems = totalItemsPieces[0].replace(",","");
            return Integer.valueOf(totalItems);
        }

        String[] totalItems = totalItemsElement.split("\\+");
        String items = totalItems[0].replace(",","");

        return Integer.valueOf(items.trim());

    }

    /**
     * retrieves a more accurate maximum page number from the pagination elements of the webpage document
     * {@code setMaximumPageNumber} contains an estimated maximum page number
     * the estimated page number is used to obtain the accurate maximum page number
     * @return correctly-obtained maximum page number
     */
    private int getActualMaxPageNumber() {

        int number = 1;

        Elements elements = this.currentPage.getElementsByAttributeValueContaining("aria-current", "page");
        for(Element element: elements) {
            if(element.className().equals("pagination__item")) {

                String actualPageNumber = element.text();
                number = Integer.valueOf(actualPageNumber.substring(0, actualPageNumber.length()));
                return number;
            }
        }
        return number;
    }

    /**
     * checks if the item has only one search page
     * @return true when there is only one item page
     */
    private boolean checkSingleSearchpage() {

        Elements divElements = this.currentPage.getElementsByClass("s-pagination");
        return divElements.isEmpty();
    }

    /**
     * retrieves all the item listings on the given webpage
     * @param searchPage the html document of the search page
     * @return list of all items on the page
     */
    public ArrayList<Item> getSearchedItems(Document searchPage) {

        ArrayList<Item> searchedItems = new ArrayList<>();
        Elements items = searchPage.select("li.s-item.s-item__dsa-on-bottom.s-item__pl-on-bottom");

        for (Element item : items) {

            // extracts the necessary attributes for each item

            String itemName = item.getElementsByTag("img").attr("alt");
            String itemPrice = item.getElementsByClass("s-item__price").text();
            String url = item.getElementsByClass("s-item__link").attr("href");

            if(checkValidListing(item)) {
                searchedItems.add(new Item(url, itemName.replace(",", "_"), itemPrice));
            }

        }
        return searchedItems;
    }

    /**
     * filters the current item to the user's newly-listed item choice
     * @param item current item iteration
     * @return true or false whether the item aligns with user choice
     */
    private boolean checkValidListing(Element item) {

        if (this.scraperType == Type.ANYLISTINGS) {
            return true;
        } else {
            if(item.hasClass("span.LIGHT_HIGHLIGHT")) {
                return true;
            }
        }
        return false;
    }

    public enum Type {
        ANYLISTINGS, NEWLISTINGS
    }

}
