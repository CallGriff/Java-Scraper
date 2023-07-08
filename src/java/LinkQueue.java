import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
public class LinkQueue {

    private ArrayList<Node>internalQueue;
    private int queueSize;
    private boolean isEmpty;

    public LinkQueue() {

        this.internalQueue = new ArrayList<>();
        this.queueSize = 0;
        this.isEmpty = true;

    }

    public void enqueue(Node node, int priority) {

        this.internalQueue.add(node);

        this.isEmpty = false;

        this.queueSize++;


    }

    public Node dequeue() {

        if(this.isEmpty) {
            return null;
        }

        Node node = this.internalQueue.get(0);

        this.internalQueue.remove(0);

        if(this.queueSize == 0) {
            this.isEmpty = true;
        }

        return node;


    }

    public int getQueueSize() {
        return this.queueSize;
    }



}
