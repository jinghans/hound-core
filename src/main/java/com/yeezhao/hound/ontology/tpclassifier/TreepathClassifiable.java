package com.yeezhao.hound.ontology.tpclassifier;

import java.util.List;
import java.util.Map;

public interface TreepathClassifiable {
	
	/**
	 * 
	 * @param category
	 * @param msgInfoMap <attrName, attrValue>
	 * @return 如果没有匹配的treepath，返回空的list，不会返回null。
	 */
	public List<String> getCandidateTreepath(int category, Map<String, String> msgInfoMap);
	
}
