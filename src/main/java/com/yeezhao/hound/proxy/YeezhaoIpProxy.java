package com.yeezhao.hound.proxy;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class YeezhaoIpProxy implements IIpServiceOperator{
	
	private String proxyUrl;
	
	public YeezhaoIpProxy(String yeezhaoProxyUrl){
		this.proxyUrl = yeezhaoProxyUrl;
	}
	
	public List<IpObject> getIps(int count) throws IpServiceException {
		String queryUrl = proxyUrl + "&count=" + count;
		System.out.println("getting ips from " + queryUrl);
		try{
			HttpClient client = new HttpClient();
			GetMethod getMethod = new GetMethod(queryUrl);
			client.executeMethod(getMethod);
			int code = getMethod.getStatusCode(); 
			byte[] resByte = getMethod.getResponseBody();
			String html = new String(resByte, "UTF-8");
			InputStream is = getMethod.getResponseBodyAsStream();
			if(code!=200){
				System.out.println("unknown statusCode, page: ");
				System.out.println(html);
				throw new IpServiceException("fail get page, statusCode:" + code);
			}
			List<IpObject> ipObjects = new LinkedList<IpObject>();
	        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
	        NodeList list = doc.getElementsByTagName("ip");
	        int size = list.getLength();
	        for(int i=0;i<size;i++){
	        	NodeList atts = ((Element)list.item(i)).getChildNodes();
	        	IpObject ipObject = new IpObject();
	        	for(int j=atts.getLength()-1;j>=0;j--){
	        		Node att = atts.item(j);
	        		if(att.getNodeName().equals("host")){
	        			ipObject.setHost(att.getFirstChild().getNodeValue());
	        		}
	        		else if(att.getNodeName().equals("port")){
	        			ipObject.setPort(Integer.parseInt(att.getFirstChild().getNodeValue()));
	        		}
	        		else if(att.getNodeName().equals("location")){
	        			ipObject.setLocation(att.getFirstChild().getNodeValue());
	        		}
	        	}
	        	ipObjects.add(ipObject);
	        }
	        if(ipObjects.size()<=0){
	        	throw new IpServiceException("can not get ip" );
	        }
			return ipObjects;
		} catch (Exception e){
			System.out.println("fail get ips, url: " + queryUrl);
			throw new IpServiceException(e);
		}
	}
	
}
