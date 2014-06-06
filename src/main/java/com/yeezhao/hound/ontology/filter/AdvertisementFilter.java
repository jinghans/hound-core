package com.yeezhao.hound.ontology.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yeezhao.commons.util.Pair;
import com.yeezhao.commons.util.StringUtil;
import com.yeezhao.hound.ontology.FileHandler;

public class AdvertisementFilter implements WeiboFilterable {

	private static AdvertisementFilter filter;
	private static int maxMarkLen = 0;
	private static int minMarkLen = 1000;
	private static Map<String, Integer> adMarks;
	private static final int ADV_LOWER_BOUND = 3;
	
	private AdvertisementFilter(){};
	
	public static AdvertisementFilter initFilter(InputStream advertiseFilterWordsFile) throws IOException{
		if(filter != null)
			return filter;
		adMarks = new HashMap<String, Integer>();
		List<String> lines = FileHandler.readLinesIntoList(advertiseFilterWordsFile);
		for(String line : lines){
			String[] segs = line.split(StringUtil.STR_DELIMIT_1ST);
			if(segs.length == 1)
				adMarks.put(segs[0], 1);
			else
				adMarks.put(segs[0], Integer.parseInt(segs[1]));
			if(segs[0].length() > maxMarkLen) maxMarkLen = segs[0].length();
			if(segs[0].length() < minMarkLen) minMarkLen = segs[0].length();
		}
		return new AdvertisementFilter();
	}
	
	public boolean should2Filtered(String weiboInfo) {
		if(weiboInfo == null || weiboInfo.isEmpty())
			return true;
		List<Pair<Integer, Integer>> poses = StringUtil.backwardMaxMatch(weiboInfo, adMarks, maxMarkLen, minMarkLen);
		//Map<String, Integer> statMap = new HashMap<String, Integer>();
		int adWeights = 0;
		if(!poses.isEmpty())
			for(Pair<Integer, Integer> pos : poses){
				String adv = weiboInfo.substring(pos.first, pos.second);
				adWeights += adMarks.get(adv);
//				if(!statMap.containsKey(adv))
//					statMap.put(adv, adMarks.get(adv));
//				else
//					statMap.put(adv, statMap.get(adv) + adMarks.get(adv));
			}
//		for(Entry<String, Integer> entry : statMap.entrySet())
//			if(entry.getValue() >= ADV_LOWER_BOUND)
//				return true;
		return adWeights >= ADV_LOWER_BOUND;
	}
	
}
