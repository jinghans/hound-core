package com.yeezhao.hound.ontology.tpexpand;

public class Node {
	
	private String value;
	private String type;


	public Node(String value, String type){
		this.value = value;
		this.type = type;
	}
	
	
	/**
	 * 返回词名
	 * @return
	 */
	public String getWord(){
		return value;
	}
	
	/**
	 * 返回词性
	 * @return
	 */
	public String getType(){
		return type;
	}
	
	public String toString(){
		return "val:" + value + ",type=" + type;
	}
	
	/**
	 * 不允许同一个关键词有不同的type，否则会被认为相同的关键词。
	 */
	public boolean equals(Object another){
		if(another == this)
			return true;
		if(!(another instanceof Node))
			return false;
		return value.equals(((Node)another).getWord());
	}
	
	public int hashCode(){
		return value.hashCode();
	}
}
