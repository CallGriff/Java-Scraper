package com.example.javascraper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import proxypool.ProxyPool;
import webpage.Item;
import webpage.WebpageManager;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;


public class ProgramController implements Initializable {

    private Webscraper webscraper;

    private ObservableList<Item>itemList;

    private StringBuilder stringBuilder;
    @FXML
    private TextArea consoleOutput;

    @FXML
    private TableView itemTable;

    @FXML
    private TableColumn<Item, String> itemName;

    @FXML
    private TableColumn<Item, String> itemPrice;

    @FXML
    private TableColumn<Item, String> itemUrl;
    @FXML
    private TextArea proxyText;

    @FXML
    private TextField userSearch;

    @FXML
    private Button scrapeButton;
    @FXML
    private RadioButton socksButton;
    @FXML
    private RadioButton httpButton;
    @FXML
    private CheckBox filterNewListings;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.stringBuilder = new StringBuilder();
        this.webscraper = new Webscraper(this.getTypeChoice());

    }


    /**
     * Converts the resulting item list to be displayed in the interface table
     */

    public void convertItemList() {

        ObservableList<Item>observableList = FXCollections.observableArrayList(this.webscraper.getItemList());
        this.setItemList(observableList);

    }

    /**
     * sets the item list to be displayed in the interface table
     * @param observableList resulting item list to be displayed in the table
     */

    public void setItemList(ObservableList<Item>observableList) {
        this.itemList = observableList;
        this.setItemTable();

    }


    /**
     * Sets the data for the resulting table, item urls are set to clickable hyperlinks
     */

    public void setItemTable() {

        this.itemTable.setItems(this.itemList);

        itemName.setCellValueFactory(data -> data.getValue().getItemName());
        itemPrice.setCellValueFactory(data -> data.getValue().getCurrentPrice());
        itemUrl.setCellValueFactory(data -> data.getValue().getUrl());

        // assign hyperlink to url cell

        itemUrl.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);

                    Hyperlink hyperlink = new Hyperlink(url);
                    hyperlink.setOnAction(event -> {

                        try {
                            Desktop.getDesktop().browse(new URI(url));
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    });
                    setGraphic(hyperlink);

            }
        });

    }

    /**
     * triggers the scraping process
     */
    @FXML
    public void onScrapeButtonClicked() {

        String userSearch = this.userSearch.getText();
        this.webscraper.setUserSearch(userSearch);
        this.setProxyPool();

        try {
            this.webscraper.scrape();
        }catch(TimeoutException e) {
            System.out.println("Couldn't connect to target webpage.");
            this.setConsoleOutput("Failed to connect to website. Please check your proxies");
            return;
        }



        this.convertItemList();
        this.setConsoleOutput("Successfully scraped " + this.itemList.size() + " items.");

    }

    /**
     * retrieves the user's choice for scraping newly listed items only
     */
    private WebpageManager.Type getTypeChoice() {

        if(this.filterNewListings.isSelected()) {
            return WebpageManager.Type.NEWLISTINGS;
        } else {
            return WebpageManager.Type.ANYLISTINGS;
        }
    }

    /**
     * reads and stores user's given proxies
     */

    private void setProxyPool() {

        String userProxies = this.proxyText.getText();

        ProxyPool proxyPool = this.webscraper.getProxyPool();

        if(userProxies.isBlank()){
            proxyPool.add(Proxy.NO_PROXY);
            return;
        }

        String[] proxyArray = userProxies.split("\n");

        Proxy.Type proxyType = this.getProxyType();

        for(String proxy: proxyArray) {

            String[] userInputPieces = proxy.split(":");

            if(validateProxy(userInputPieces[0])) {

                try{
                    Proxy p = new Proxy(proxyType, new InetSocketAddress(userInputPieces[0],
                            Integer.parseInt(userInputPieces[1])));
                    proxyPool.add(p);
                }catch(NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        }

        WebpageManager.Type userScraperChoice = this.getTypeChoice();

        WebpageManager webpageManager = new WebpageManager(proxyPool, userScraperChoice);
        this.webscraper.setWebpageManager(webpageManager);
    }

    /**
     * validates a given proxy
     * @param proxy user's given proxy
     * @return
     */
    private boolean validateProxy(String proxy) {

        String ipRegex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
        return proxy.matches(ipRegex);

    }

    /**
     * @return the selected proxy type
     */

    private Proxy.Type getProxyType() {

        if(this.socksButton.isSelected()) {
            return Proxy.Type.SOCKS;
        } else {
            return Proxy.Type.HTTP;
        }

    }

    /**
     * sets the console to display a given message
     * @param text message to be displayed in the interface console
     */
    private void setConsoleOutput(String text) {

        this.stringBuilder.append(text + "\n");

        this.consoleOutput.setText(stringBuilder.toString());

    }

}