package com.example.javascraper;

import proxypool.ProxyPool;
import tasks.TaskManager;
import webpage.Item;
import webpage.WebpageManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class Webscraper {

    private TaskManager taskManager;
    private WebpageManager webpageManager;
    private ProxyPool proxyPool;
    private ArrayList<Item>itemList;
    private String userSearch;

    public Webscraper(WebpageManager.Type scraperType) {

        this.proxyPool = new ProxyPool();
        this.webpageManager = new WebpageManager(this.proxyPool, scraperType);
        this.taskManager = new TaskManager(this.webpageManager);

    }

    public void setUserSearch(String userSearch) {
        this.userSearch = userSearch;
    }

    public void setTaskManager(TaskManager taskManager) {

        this.taskManager = taskManager;

    }

    public void setWebpageManager(WebpageManager webpageManager) {

        this.webpageManager = webpageManager;
    }

    private void setItemList(ArrayList<Item>itemList) {
        this.itemList = itemList;
    }

    public ProxyPool getProxyPool() {
        return this.proxyPool;
    }

    public ArrayList<Item> getItemList() {
        return this.itemList;
    }

    /**
     * processes user's search choice to a valid url
     * @return valid url search page
     */
    public String sanitizedUrl() {

        return ("https://www.ebay.co.uk/sch/i.html?_from=R40&_nkw=" + this.userSearch)
                .replace(" ", "+");

    }

    /**
     * performs the scraping process
     */
    public void scrape() {

        System.out.println("Attempting to scrape " + this.userSearch + "...");

        String firstPageUrl = this.sanitizedUrl();

        try{
            this.webpageManager.setMaximumPageNumber(firstPageUrl);
        } catch(TimeoutException e) {
            System.out.println(e.getMessage());
        }

        this.setTaskManager(new TaskManager(this.webpageManager));

        this.taskManager.submitTask(firstPageUrl);

        this.setItemList(this.taskManager.getItemList());

        this.taskManager.shutdownExecutorService();

        this.saveData();

        System.out.println("Successfully scraped " + this.itemList.size() + " items");

    }

    /**
     * saves the resulting list of items to the root folder
     */
    private void saveData() {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");
        String timeStamp = now.format(formatter);

        try {

            File file = new File(this.userSearch + "_" + timeStamp + ".csv");

            if(!file.exists()) {
                file.createNewFile();
            }

            PrintWriter printWriter = new PrintWriter(file);
            itemList.stream().forEach(itm -> printWriter.println(itm.toString()));
            printWriter.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}