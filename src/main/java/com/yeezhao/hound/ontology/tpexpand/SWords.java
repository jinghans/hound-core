package com.yeezhao.hound.ontology.tpexpand;

import java.util.List;

public class SWords {
	
	private int category;
	private List<SWord> searchWords;
	
	public SWords(){
		
	}
	
	public SWords(int category,List<SWord> sword){
		this.category=category;
		this.searchWords=sword;
	}
	
	/**
	 * 返回搜索词集合
	 * @return
	 */
	public List<SWord> getSwords(){
		return searchWords;
	}
	
	/**
	 * 返回品类
	 * @return
	 */
	public int getCategory(){
		return category;
	}
}
