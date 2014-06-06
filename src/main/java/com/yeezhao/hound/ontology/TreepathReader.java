package com.yeezhao.hound.ontology;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.yeezhao.hound.ontology.OntoUtil.WEIBO_SEX;


public class TreepathReader {
	
	private static final Logger LOG = Logger.getLogger(TreepathReader.class);
	
	public static class Treepath{
		public String pathValue;
		public WEIBO_SEX sex;
		public Treepath(String value, WEIBO_SEX sex){
			this.pathValue = value;
			this.sex = sex;
		}
		public String toString(){
			return pathValue + ", sex=" + sex.toString();
		}
	}
	
	private class NodeSex{
		private Node node;
		private String nodeValue;
		private WEIBO_SEX sex = WEIBO_SEX.UNKNOWN;
		public NodeSex(Node node){
			this.node = node;
			Element ele = (Element)node;
			String sexAttr = ele.getAttribute("sex"); //sex字段
			if(!sexAttr.isEmpty())
				sex = WEIBO_SEX.enumValue(Integer.parseInt(sexAttr)); //假设输入值只有0/1/2三种情况
			nodeValue = ele.getAttribute("name");
			if(nodeValue.isEmpty())
				nodeValue = ele.getAttribute("key");
		}
		public Node getNode(){
			return node;
		}
		public void setNodeSex(WEIBO_SEX sex){
			this.sex = sex;
		}
		public WEIBO_SEX getSex(){
			return sex;
		}
		public boolean equals(Object obj){ //在这个应用中只需要判断两个node是否完全一样。
			return obj == this;
		}
		public int hashCode(){
			return nodeValue.hashCode();
		}
	}
	
	private Document pathDoc;
	public TreepathReader(InputStream treepathXml){
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		try {
		    DocumentBuilder dombuilder=dbf.newDocumentBuilder();	           
		    pathDoc = dombuilder.parse(treepathXml);
		    if(pathDoc == null)
				return;
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public List<Treepath> getAllPaths(){
		List<Treepath> paths = new LinkedList<Treepath>();
		List<NodeSex> candidateNodes = new LinkedList<NodeSex>();
		Set<NodeSex> nodeSet = new HashSet<NodeSex>();	//包含所有的根节点、中间节点和叶节点
		NodeList propertyNodes = pathDoc.getElementsByTagName("property");
		for(int i = 0, l = propertyNodes.getLength(); i < l; i++){
			if(propertyNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
				candidateNodes.add(new NodeSex(propertyNodes.item(i)));
		}
		getPathsUnderNode(candidateNodes, nodeSet);
		for(NodeSex node : nodeSet){
			paths.add(getPath(node));
		}
		return paths;
	}
	
	private void getPathsUnderNode(List<NodeSex> iteratorNodes, Set<NodeSex> allNodes){
		while(!iteratorNodes.isEmpty()){
			NodeSex pnodeSex = iteratorNodes.remove(0);
			Node pnode = pnodeSex.getNode();
			allNodes.add(pnodeSex);
			if(pnode.hasChildNodes()){
				NodeList cnodes = pnode.getChildNodes();
				for(int i = 0, l = cnodes.getLength(); i < l; i++){
					if(cnodes.item(i).getNodeType() == Node.ELEMENT_NODE){
						NodeSex cnode = new NodeSex(cnodes.item(i));
						if(pnodeSex.getSex() != WEIBO_SEX.UNKNOWN)
							cnode.setNodeSex(pnodeSex.getSex());
						iteratorNodes.add(cnode);
						allNodes.add(cnode);
					}
				}
				getPathsUnderNode(iteratorNodes, allNodes);
			} 
		}
	} 
	
	/**
	 * 
	 * @param sexNode
	 * @return 如果xml格式错误，返回为null。
	 */
	private Treepath getPath(NodeSex sexNode){
		StringBuffer sb = new StringBuffer();
		Node node = sexNode.getNode();
		while(!node.getNodeName().equals("configuration")){
			Element ele = (Element)node;
			String value = ele.getAttribute("name");
			if(value.isEmpty())
				value = ele.getAttribute("key");
			if(value.isEmpty()){
				LOG.error("***error format while reading treepath node value.");
				return null;
			}
			sb.append(value).append("#");
			node = node.getParentNode();
		}
		String pathValue = sb.substring(0, sb.toString().length() - 1);
		String[] segs = pathValue.split("#");
		sb = new StringBuffer().append(segs[segs.length - 1]);
		for(int i = segs.length - 2; i >= 0; i--)
			sb.append("#").append(segs[i]);
		return new Treepath(sb.toString(), sexNode.getSex());
	}
	
	public static void main(String[] args) throws IOException {
		String file = "cloth_treepath.xml";
		FileInputStream fileStream = new FileInputStream(new File(file));
		TreepathReader reader = new TreepathReader(fileStream);
		List<Treepath> paths = reader.getAllPaths();
		for(Treepath path : paths){
			System.out.println(path.toString());
		}
		fileStream.close();
	}
}
