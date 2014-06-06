package com.yeezhao.hound.proxy;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Site:	http://www.xinxinproxy.com/httpip/xml?
 * Taobao:	http://item.taobao.com/item.htm?id=15305557561
 * QQ: 		7374311
 * orderId:   batch:281121685773302&sesson=second, recency:281121685773302
 * 
 * @author abekwok
 *
 */
public class IpServiceOperatorT1 implements IIpServiceOperator{
	private static String proxyUrl = "http://www.xinxinproxy.com/httpip/xml?session=second#281121685773302#1#all";
	private String host;
	
	private String orderId = null;
	private String ipLocation = null;
	private boolean isNew = false;

	/**
	 * @param ipOrderString ipOrderString host#orderId#isNew#ipLocation
	 * 	isNew: 1,yes; others,no
	 *  ipLocation: all|province
	 * @throws com.yeezhao.amkt.ip.IpServiceException
	 */
	public IpServiceOperatorT1() throws IpServiceException{
		String sps[] = proxyUrl.split("#");
		if(sps.length!=4){
			throw new IpServiceException("can not parse ipOrderString, format error: " + proxyUrl);
		}
		this.host = sps[0];
		this.orderId = sps[1];
		this.isNew = sps[2].equals("1");
		String loc = sps[3];
		if(!loc.equals("all")) this.ipLocation=loc;
	}
	
	public String getHost() {
		return host;
	}



	/**
	 * @param host (default: http://www.xinxinproxy.com/httpip/xml?)
	 */
	public void setHost(String host) {
		this.host = host;
	}



	public String getOrderId() {
		return orderId;
	}



	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}


	public String getIpLocation() {
		return ipLocation;
	}
	public void setIpLocation(String ipLocation) {
		this.ipLocation = ipLocation;
	}

	public boolean isNew() {
		return isNew;
	}

	
	public List<IpObject> getIps(int count) throws IpServiceException {
		String url = null;
		try{
			String loc = (ipLocation!=null&&!ipLocation.equals(IpObject.NOT_USE_LOCATION_FLAG))?("&includeProvinces="+URLEncoder.encode(ipLocation, "UTF-8")):"";
			String old = isNew?"&isNew=1":"";
			url = String.format(host+"&orderId=%s&count=%d%s%s",orderId,count,loc,old);
			System.out.println("getting ips from " + url);
			HttpClient client = new HttpClient();
			GetMethod getMethod = new GetMethod(url);
			client.executeMethod(getMethod);
			int code = getMethod.getStatusCode(); 
			byte[] resByte = getMethod.getResponseBody();
			String html = new String(resByte, "GBK");
			InputStream is = getMethod.getResponseBodyAsStream();
			if(code!=200){
				System.out.println("unknown statusCode, page: ");
				System.out.println(html);
				throw new IpServiceException("fail get page, statusCode:" + code);
			}
			List<IpObject> ipObjects = new LinkedList<IpObject>();
	        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
	        NodeList list = doc.getElementsByTagName("Httpip");
	        int size = list.getLength();
	        for(int i=0;i<size;i++){
	        	NodeList atts = ((Element)list.item(i)).getChildNodes();
	        	IpObject ipObject = new IpObject();
	        	for(int j=atts.getLength()-1;j>=0;j--){
	        		Node att = atts.item(j);
	        		if(att.getNodeName().equals("address")){
	        			ipObject.setHost(att.getTextContent());
	        		}
	        		else if(att.getNodeName().equals("port")){
	        			ipObject.setPort(Integer.parseInt(att.getTextContent()));
	        		}
	        		else if(att.getNodeName().equals("province")){
	        			ipObject.setLocation(att.getTextContent());
	        		}
	        	}
	        	ipObjects.add(ipObject);
	        }
	        if(ipObjects.size()<=0){
	        	throw new IpServiceException("can not get ip" );
	        }
			return ipObjects;
		} catch (Exception e){
			System.out.println("fail get ips, url: " + url);
			throw new IpServiceException(e);
		}
	}
}