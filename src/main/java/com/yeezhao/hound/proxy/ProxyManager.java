package com.yeezhao.hound.proxy;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 管理代理ip,采用生产者消费者模型
 * 设定pool大小，每当ip个数达到下限，http获取代理ip列表。ip使用之后，返回到rest队列。
 * 自动检查rest队列，挑选可用的ip放入可用队列。
 * @author Lucifer
 *
 */
public class ProxyManager {
    private AtomicBoolean proxyReady = new AtomicBoolean(false); 
    
    private LinkedBlockingQueue<IpObject> ipsQueue = new LinkedBlockingQueue<IpObject>();
    private LinkedBlockingQueue<IpObject> ipsRestQueue = new LinkedBlockingQueue<IpObject>();
    
    private int MIN_DELAY_PROXY_TIME = 3000;  	//in milliseconds，同一个ip连续两次调用的时间间隔
    private int IP_CHECK_INTERVAL = 10;  		//in seconds
    private int fixedPoolSize = 30;
    private String proxyUrl;
    
   private ProxyManager(String proxyUrl, int poolSize){
	   this.fixedPoolSize = poolSize;
	   this.proxyUrl = proxyUrl;
   }
   
    class IpReload implements Runnable{

		public void run() {
			while(true){
				try {
					Iterator<IpObject> ipItor = ipsRestQueue.iterator();
					long now = System.currentTimeMillis();
					while(ipItor.hasNext()){
						IpObject ip = ipItor.next();
						if(ip.getNextUseTime() <= now){
							ipsQueue.put(ip);
							ipsRestQueue.remove(ip);
						}
					}
					TimeUnit.SECONDS.sleep(IP_CHECK_INTERVAL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
    	
    }
    
    class ProxySupply implements Runnable {
	IpSelector selector;
	
	public void run() {
		selector = IpSelector.getInstance();
		selector.setIpsQueue(ipsQueue);
		
		IpManager ipManager = null;
		try {
//			ipManager = new IpManager(new IpServiceOperatorT1());
			ipManager = new IpManager(new YeezhaoIpProxy(proxyUrl));
		} catch (Exception e) {
			e.printStackTrace();
		}
		while (true) {
		try {
			int cacheSize = ipsQueue.size() + ipsRestQueue.size();
			int reloadSize = fixedPoolSize - cacheSize;
		    if (reloadSize <= 0) {
		    	mySleep(30);
		    	continue;
		    } else{
			    List<IpObject> ips = ipManager.getIps(reloadSize);
			    if (CollectionUtils.isNotEmpty(ips)) {
			    	Collections.shuffle(ips);
			    	for (IpObject ipObject : ips) {
			    		selector.testIp(ipObject, 5000);
					}
			    }
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    mySleep(10);
		}
	    }
	}
    }
    
    private void executeSupplyProxy() {
 	try {
 	    ExecutorService ecs = Executors
 		    .newScheduledThreadPool(2, new ThreadFactory() {

 		    public Thread newThread(Runnable r) {
 			    Thread t = new Thread(r);
 			    t.setDaemon(true);
 			    return t;
 			}
 		    });

 	    ecs.execute(new ProxySupply());
 	    ecs.execute(new IpReload());
 	    ecs.shutdown();
 	    System.out.println("executeSupplyProxy is running");
 	} catch (Exception e) {
 	    e.printStackTrace();
 	    LOG.error("获取代理时出错");
 	}

     }

    private void mySleep(int second) {
	try {
	    TimeUnit.SECONDS.sleep(second);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }
    
    public boolean isReady(){
	return proxyReady.get();
    }
    
    public int poolSize(){
    	return ipsQueue.size();
    }
    
    public IpObject takeIpObject() throws InterruptedException{
	 return ipsQueue.poll();
    }
    
    public void releaseIpObject(IpObject ip){
	try {
		ip.setNextUseTime(System.currentTimeMillis() + MIN_DELAY_PROXY_TIME);
		ipsRestQueue.put(ip);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }
    
    
    public static final Logger LOG = LoggerFactory.getLogger(ProxyManager.class);
    private static ProxyManager proxyManager=null;
    public static synchronized ProxyManager getInstance(String proxyUrl, int poolSize){
	if (proxyManager==null) {
	    proxyManager=new ProxyManager(proxyUrl, poolSize);
	    	proxyManager.executeSupplyProxy();
		try {
		    TimeUnit.MILLISECONDS.sleep(500);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	return proxyManager;
    }
}
