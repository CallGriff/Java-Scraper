package webpage;

import javafx.beans.property.SimpleStringProperty;

public class Item {

    private SimpleStringProperty url;
    private SimpleStringProperty itemName;
    private SimpleStringProperty currentPrice;

    public Item (String url, String itemName, String currentPrice) {

        this.url = new SimpleStringProperty(url);
        this.itemName = new SimpleStringProperty(itemName);
        this.currentPrice = new SimpleStringProperty(currentPrice);
    }

    public SimpleStringProperty getUrl() {
        return this.url;
    }

    public SimpleStringProperty getItemName() {
        return this.itemName;
    }

    public SimpleStringProperty getCurrentPrice() {
        return this.currentPrice;
    }

    public String toString() {

        return this.itemName.toString() + ", " + this.currentPrice.toString() + ", " + this.url.toString();

    }

}
