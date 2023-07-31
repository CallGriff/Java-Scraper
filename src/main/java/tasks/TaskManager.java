package tasks;

import webpage.Item;
import webpage.WebpageManager;

import java.util.ArrayList;
import java.util.concurrent.*;

public class TaskManager {

    private ExecutorService executorService;
    private WebpageManager webpageManager;
    private ArrayList<Item>result;
    public TaskManager(WebpageManager webpageManager) {

        this.webpageManager = webpageManager;
        this.executorService = Executors.newCachedThreadPool();
        this.result = new ArrayList<>();
    }

    public ArrayList<Item>getItemList() {
        return result;
    }

    /**
     * iterates through each page on the website, assigning a new task per page
     * resulting item list from each task is compiled to one master-list of resulting items
     * @param url searchpage url of the given search term without a specified page number
     */
    public void submitTask(String url) {

        ArrayList<Future<ArrayList<Item>>>futureItems = new ArrayList<>();

        int maximumPageNumber = this.webpageManager.getMaximumPageNumber();

        for(int i = 0; i < maximumPageNumber; i++) {

            String nextUrl = url + "&_pgn=" + i;

            futureItems.add(this.executorService.submit(new Task(this.webpageManager, nextUrl)));

        }

        for(int i = 0; i < maximumPageNumber; i++) {

            try {
                ArrayList<Item> futureItemList = futureItems.get(i).get(8, TimeUnit.SECONDS);
                this.result.addAll(futureItemList);
            } catch (ExecutionException e) {
                System.out.println("An exception occurred during task execution");
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted while waiting for the result");
            } catch (TimeoutException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    public void shutdownExecutorService() {

        try {
            System.out.println("Attempting to shutdown executor service...");
            this.executorService.shutdown();
            this.executorService.awaitTermination(0, TimeUnit.SECONDS);

        }catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }finally {
            if(!this.executorService.isTerminated()) {

                System.out.println("cancelling non-finished tasks...");
            }
            this.executorService.shutdownNow();
            System.out.println("shutdown finished");
        }

    }



}
