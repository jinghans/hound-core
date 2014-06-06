package com.yeezhao.hound.proxy;

import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;

public class IpSelector {
	
	private BlockingQueue<IpObject> ipsQueue = new LinkedBlockingQueue<IpObject>();
	
    public BlockingQueue<IpObject> getIpsQueue() {
		return ipsQueue;
	}

	public void setIpsQueue(BlockingQueue<IpObject> ipsQueue) {
		this.ipsQueue = ipsQueue;
	}

	public class IpTester implements Callable<Long> {
        private IpObject ipObject;

        private String testUrl;

        private int timeout;
        
        public IpTester(IpObject ipObject, String testUrl, int timeout) {
            this.ipObject = ipObject;
            this.testUrl = testUrl;
            this.timeout = timeout;
        }

        public Long call() throws Exception {
        	try{
	            long begin = System.currentTimeMillis();
	            getPageByUrl(testUrl, Charset.forName("UTF-8"), ipObject,
	                    timeout);
	            long end = System.currentTimeMillis();
	            ipsQueue.put(ipObject);
	            return end - begin;
        	} catch(Exception e){
        		return 1000000l;
        	}
        }
    }

    private static volatile IpSelector ipSelector;

    private ExecutorService execurtor;

    public static final String DEFAULT_TEST_URL = "http://www.baidu.com/";

    private String testUrl = DEFAULT_TEST_URL;

    private IpSelector() {
        execurtor = Executors.newCachedThreadPool();
    }

    public static IpSelector getInstance() {
        synchronized (IpSelector.class) {
            if (ipSelector == null)
                ipSelector = new IpSelector();
            return ipSelector;
        }
    }

    public void testIp(IpObject ipObject, int timeout) {
        execurtor.submit(new IpTester(ipObject,
                testUrl, timeout));
    }

    public static String getPageByUrl(String url, Charset charset,
            IpObject proxyIp, int timeout) throws Exception {
        GetMethod getMethod = new GetMethod(url);
        HttpClient client = createClientWithProxy(proxyIp, timeout);
        client.getParams().setParameter("http.protocol.cookie-policy",
                CookiePolicy.IGNORE_COOKIES);
        getMethod.getParams().setParameter("http.protocol.cookie-policy",
                CookiePolicy.BROWSER_COMPATIBILITY);
        client.executeMethod(getMethod);
        byte[] resByte = getMethod.getResponseBody();
        String html = new String(resByte, "UTF-8");
        return html;
    }

    public static HttpClient createClientWithProxy(IpObject ipObject,
            int timeout) {
        HttpClient client = new HttpClient();
        Protocol.unregisterProtocol("https");
        if (timeout < 10000) {
            client.getParams().setConnectionManagerTimeout(timeout);
            client.getParams().setSoTimeout(timeout);
        }
        if (ipObject != null) {
            client.getHostConfiguration().setProxy(ipObject.getHost(),
                    ipObject.getPort());
        }
        return client;
    }

    public static void main(String args[]) throws IpServiceException {
        
    }
}
