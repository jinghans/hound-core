package com.yeezhao.hound.core;

import com.yeezhao.hound.ontology.OntoUtil;
import com.yeezhao.hound.ontology.tpclassifier.TreepathClassifierFactory;
import com.yeezhao.hound.util.FileResourceUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhibin on 14-5-19.
 */
public class JudgeLoc {

	private TreepathClassifierFactory classifier;
	private Configuration configuration;
    private HashMap<String,String> locmap;

    public JudgeLoc() {
		configuration = new Configuration();
		configuration.addResource("analyzer-config.xml");
		classifier = new TreepathClassifierFactory(
				configuration.getConfResourceAsInputStream(configuration.get("file.location.keyword")),
				configuration.getConfResourceAsInputStream(configuration.get("file.location.treepath")));
        locmap = (HashMap<String,String>)getLocmap(configuration,"location.map");
	}

	/**
	 * 返回规整后的层级地址
	 * @param locationValue
	 * @return
	 */
	public String formatLocation(String locationValue) {
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put(OntoUtil.WEIBO_TPCLS_ATTRIBUTES.TEXT, locationValue);
		List<String> paths = classifier.getCandidateTreepath(1001, valueMap);
		String locFormat = paths.isEmpty() ? "其他" : paths.get(0);
		String[] locs = locFormat.split("#");
		if (locFormat.contains("China") && (locs.length == 3)) {
			String area = "";
			if (locmap.containsKey(locs[2])) {
				area = locmap.get(locs[2]);
				locFormat = locs[0] + "#" + locs[1] + "#" + area + "#" + locs[2];
			}

		}
		if (locFormat.contains("United States") && (locs.length == 3)) {
			if (locmap.containsKey(locs[2])) {
				String state = "";
				if (locmap.containsKey(locs[2])) {
					state = locmap.get(locs[2]);
					locFormat = locs[0] + "#" + locs[1] + "#" + state;
				}

			}
		}
		return locFormat;
	}
	
	/**
	 * 对应为中文地址
	 * @param enAddress
	 * @return
	 */
	public String toChAddress(String enAddress){
		String chAddress = enAddress;
		if(StringUtils.isEmpty(enAddress)){
			return chAddress;
		}
		Document locDoc = null;
		try{
			locDoc = HoundUtils.readLocationFile();
			Node rootNode = locDoc.getElementsByTagName("configuration").item(0);
			StringBuilder sb = new StringBuilder();
			transPath(enAddress, rootNode, sb);
			chAddress = sb.toString();
		} catch(Exception e){
			e.printStackTrace();
			chAddress = enAddress;
		}
		chAddress = chAddress.isEmpty() ? chAddress : chAddress.substring(0 , chAddress.length() - 1);
		return chAddress;
	}
	
	private void transPath(String enPathValue, Node parentNode, StringBuilder resultBuf){
		if(parentNode.getChildNodes().getLength() == 0 || StringUtils.isEmpty(enPathValue))
			return;
		String currentPath = enPathValue;
		String leftPath = "";
		int pos = enPathValue.indexOf("#");
		pos = pos == -1 ? enPathValue.length() : pos;
		currentPath = enPathValue.substring(0, pos);
		leftPath = enPathValue.substring(pos == enPathValue.length() ? pos : pos + 1);

		NodeList cnodes = parentNode.getChildNodes();
		boolean matched = false;
		Node matchedNode = null;
		for(int i = 0, l = cnodes.getLength(); i < l; i++){
			Node cnode = cnodes.item(i);
			if(cnode.getNodeType() == Node.ELEMENT_NODE){
				Node locNode = cnode.getAttributes().getNamedItem("key");
				if(locNode != null){
					String key = locNode.getFirstChild().getTextContent();
					if(key.equals(currentPath)){
						matched = true;
						matchedNode = cnode;
						String ch = cnode.getAttributes().getNamedItem("ch").getFirstChild().getTextContent();
						resultBuf.append(ch).append("#");
					}
				}
			}
		}
		if(!matched){
			return;
		}
		
		transPath(leftPath, matchedNode, resultBuf);
	}
	

	private Map<String, String> getLocmap(Configuration conf, String name) {
		HashMap<String, String> locmap = new HashMap<String, String>();
		try {
			ArrayList<String> loc4short = (ArrayList<String>) FileResourceUtils
					.getResourcesAsStrings(conf, name);
			for (String sholoc : loc4short) {
				String[] onepair = sholoc.split("\\|");
				locmap.put(onepair[1], onepair[0]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return locmap;
	}

	public static void main(String[] args) {
		JudgeLoc location = new JudgeLoc();
		String loc = location.formatLocation("China, Guangdong");
		System.out.println(loc);
	}

}
