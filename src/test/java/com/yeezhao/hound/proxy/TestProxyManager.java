package com.yeezhao.hound.proxy;

import org.junit.Test;

public class TestProxyManager {
	
	@Test
	public void testGetIps() throws InterruptedException{
		String proxyUrl = "http://proxy.yeezhao.com/getIps?id=testproxy123";
		ProxyManager ipool = ProxyManager.getInstance(proxyUrl, 10);
		
		for(int i = 0; i < 100; i++){
			IpObject ip = null;
			while((ip = ipool.takeIpObject()) == null){
				Thread.sleep(3000);
			}
			System.out.println(ip.getHost());
		}
	}
}
