package proxypool;

import org.junit.Test;

import java.net.Proxy;

import static org.junit.Assert.assertEquals;
public class ProxyPoolTest {

    @Test
    public void noProxies() {

        ProxyPool proxyPool = new ProxyPool();

        proxyPool.add(Proxy.NO_PROXY);

        Proxy proxy = proxyPool.getNewProxy();

        proxyPool.removeBrokenProxy(Proxy.NO_PROXY);

        assertEquals(proxyPool.getNewProxy(),proxyPool.getNewProxy());

    }

}
