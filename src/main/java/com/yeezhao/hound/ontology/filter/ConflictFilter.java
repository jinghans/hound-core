package com.yeezhao.hound.ontology.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yeezhao.commons.util.Pair;
import com.yeezhao.commons.util.StringUtil;
import com.yeezhao.hound.ontology.FileHandler;
import com.yeezhao.hound.ontology.Keyword;

public class ConflictFilter implements WeiboFilterable{
	
	private static ConflictFilter filter;
	private static int maxMarkLen = 0;
	private static int minMarkLen = 1000;
	private static Map<String, Keyword> conflictMap;  //<mark, candidate conflict word>
	
	private ConflictFilter(){}
	
	public static ConflictFilter initFilter(InputStream conflictWordFile) throws IOException{
		if(filter != null)
			return filter;
		conflictMap = new HashMap<String, Keyword>();
		List<String> lines = FileHandler.readKnowlegeFileLines(conflictWordFile);
		for(String line : lines){
			Keyword kw = Keyword.parseKeyword(line);
			conflictMap.put(kw.getValue(), kw);
			if(kw.getValue().length() > maxMarkLen) maxMarkLen = kw.getValue().length();
			if(kw.getValue().length() < minMarkLen) minMarkLen = kw.getValue().length();
		}
		return new ConflictFilter();
	}

	public boolean should2Filtered(String weiboInfo) {
		if(weiboInfo == null || weiboInfo.isEmpty())
			return true;
		List<Pair<Integer, Integer>> poses = StringUtil.backwardMaxMatch(weiboInfo, conflictMap, maxMarkLen, minMarkLen);
		if(!poses.isEmpty())
			for(Pair<Integer, Integer> pos : poses){
				String mark = weiboInfo.substring(pos.first, pos.second);
				for(String synWord : conflictMap.get(mark).getSynonyms()){
					if(weiboInfo.contains(synWord))
						return true;
				}
			}
		return false;
	}
	
}
