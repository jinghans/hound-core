package com.yeezhao.hound.ontology.tpexpand;

import java.util.List;

public class SWord {
	
	private List<Node> nodes;
	
	public SWord(List<Node> wordList){
		this.nodes = wordList;
	}
	
	/**
	 * 反回所有搜索词组合
	 * @return
	 */
	public List<Node> getWords(){
		return nodes;
	}
	
	public int hashCode(){
		int result = 17;
		for(Node node : nodes){
			result = 31 * result + node.hashCode();
		}
		return result;
	}
	
	/**
	 * 含有相同的node时认为是同一个关键词组合。
	 */
	public boolean equals(Object another){
		if(another == this)
			return true;
		if(!(another instanceof SWord))
			return false;
		SWord aobj = (SWord)another;
		if(aobj.nodes.size() != nodes.size())
			return false;
		else{
			boolean consitent = true;
			for(Node anode : aobj.nodes){
				if(!nodes.contains(anode)){
					consitent = true;
					break;
				}
			}
			return !consitent;
		}
	}
	
}
