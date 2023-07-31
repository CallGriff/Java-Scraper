package proxypool;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
public class ProxyPool {

    private ArrayList<Proxy>proxyList;
    private Random random;
    public ProxyPool() {

        this.proxyList = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * obtains the user's given proxies
     * @param scanner reads user input
     */
    @Deprecated
    public void getUserProxies(Scanner scanner) {

        String userInput = "input";

        while(!(userInput.isBlank())) {
            String ipRegex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";

            Proxy.Type proxyType = this.getProxyType(scanner);
            System.out.println("Please enter your proxy (in the form X.X.X.X:port");
            userInput = scanner.nextLine();
            String[] userInputPieces = userInput.split(":");
            if(userInputPieces[0].matches(ipRegex)) {

                try{
                    Proxy proxy = new Proxy(proxyType, new InetSocketAddress(userInputPieces[0],
                            Integer.parseInt(userInputPieces[1])));
                    this.add(proxy);
                }catch(NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * requests the users input for which type of proxy to use
     * @param scanner to read user input
     * @return type of proxy
     */
    private Proxy.Type getProxyType(Scanner scanner) {

        String userInput = "";

        while(!userInput.equals("http|socks")) {
            System.out.println("Which type of proxy would you like to use? (Enter HTTP or SOCKS)");
            userInput = scanner.nextLine();
            userInput = userInput.toLowerCase().trim();
        }

        if(userInput.equals("http")) {
            return Proxy.Type.HTTP;
        } else {
            return Proxy.Type.SOCKS;
        }

    }

    /**
     * retrieves a proxy from the pool
     * proxy is randomly assigned to allow for more diverse web requests during multithreaded task execution
     * if the use doesn't use any proxies, it retrieves the default {@code NO_PROXY} type
     * @return a proxy object from the pool
     */
    public Proxy getNewProxy() {

        if(this.getSize() == 1) {
            return this.proxyList.get(0);
        }

        int randomIndex = this.random.nextInt(this.getSize()-1);
        return this.proxyList.get(randomIndex);
    }

    public int getSize() {
        return this.proxyList.size();
    }

    public void removeBrokenProxy(Proxy proxy) {

        this.proxyList.remove(proxy);

    }

    public void add(Proxy proxy) {
        this.proxyList.add(proxy);
    }

    public String toString() {

        return this.proxyList.toString();

    }

}
