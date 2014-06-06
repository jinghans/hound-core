package com.yeezhao.hound.core;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.yeezhao.hound.proxy.IpObject;
import com.yeezhao.hound.proxy.ProxyManager;

import java.io.IOException;

/**
 * Created by zhibin on 14-5-18.
 */
public class GetHtml {
	
	private static ProxyManager proxyManager;
	private void initProxyManager(){
		String proxyUrl = "http://proxy.yeezhao.com/getIps?id=testproxy123";
		proxyManager = ProxyManager.getInstance(proxyUrl, 10);
	}
	
	
	public String getHtmlWithProxy(String url) throws InterruptedException{
		if(proxyManager == null){
			initProxyManager();
		}
		
		HttpClient client = getClient();
		IpObject ip = null;
		int awaits = 0;
		while((ip = proxyManager.takeIpObject()) == null && ++awaits <= 10){
			Thread.sleep(500);
		}
		if(ip != null){
			System.out.println("proxy=" + ip.getHost() + ":" + ip.getPort());
			client.getHostConfiguration().setProxy(ip.getHost(), ip.getPort());
			client.getParams().setIntParameter(HttpClientParams.MAX_REDIRECTS, 1); 
		} else {
			System.out.println("proxy=null");
		}
		GetMethod getMethod = new GetMethod(url);
		String content = null;
		try {
			client.executeMethod(getMethod);
			byte[] testbyte = getMethod.getResponseBody();
			content = new String(testbyte);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(ip != null){
				proxyManager.releaseIpObject(ip);
			}
		}
		return content;
	}

	public String getHtml(String url) {
		String htmlurl = url;
		HttpClient httpClient = getClient();
		GetMethod getMethod = new GetMethod(htmlurl);
		try {
//			httpClient.getHostConfiguration().setProxy("10.0.0.172", 80);
			httpClient.executeMethod(getMethod);
			// String test = getMethod.getResponseBodyAsString();
			byte[] testbyte = getMethod.getResponseBody();
			String content = new String(testbyte);
			// System.out.println(content);
			return content;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	private HttpClient getClient(){
		HttpClient httpClient = new HttpClient();
		httpClient
				.getParams()
				.setParameter(
						HttpMethodParams.USER_AGENT,
						"Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803 Fedora/3.5.2-2.fc11 Firefox/3.5.2");
		httpClient.getState().clearCookies();
		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		httpClient.setTimeout(10000);
		
		return httpClient;
	}
}
