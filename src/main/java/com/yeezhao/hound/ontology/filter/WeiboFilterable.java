package com.yeezhao.hound.ontology.filter;

public interface WeiboFilterable {

	/**
	 * 
	 * @param weiboInfo, 可能是微博的正文、用户信息或者其它信息
	 * @return
	 */
	public boolean should2Filtered(String weiboInfo);
	
}
