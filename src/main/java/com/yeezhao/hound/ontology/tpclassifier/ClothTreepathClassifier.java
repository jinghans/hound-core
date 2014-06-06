package com.yeezhao.hound.ontology.tpclassifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.yeezhao.commons.util.Pair;
import com.yeezhao.commons.util.StringUtil;
import com.yeezhao.hound.ontology.FileHandler;
import com.yeezhao.hound.ontology.Keyword;
import com.yeezhao.hound.ontology.OntoUtil;
import com.yeezhao.hound.ontology.OntoUtil.WEIBO_SEX;
import com.yeezhao.hound.ontology.TreepathReader;
import com.yeezhao.hound.ontology.TreepathReader.Treepath;

/**
 * 根据weibo信息得到weibo的treepath。
 * @author user
 *
 */
public class ClothTreepathClassifier implements TreepathClassifiable{
	
	private Map<String, WEIBO_SEX> path2Sex = new HashMap<String, WEIBO_SEX>();
	private Map<String, List<Keyword>> value2Keywords = new HashMap<String, List<Keyword>>();
	//某些treepath要求必须有对应的关键词
	public static final Set<String> INCLUSIVE_PATHS = new HashSet<String>(Arrays.asList(new String[]{
			"童装/童鞋", 
			"全身", "四肢", "腹部", "脸部", 
			"护肤#面膜", "护肤#洁面", 
			"中国#青海#海南", "中国#青海#黄南", "中国#内蒙古#乌海#海南区"
	}));
	private static final int WEIGHT_RADIX = 40; //假设任何一层的treepath的节点个数不超过40个;
	//<channel, order>， order等于-1表示顶层treepath比较重要; order等于1表示低层treepath更重要。
	private static final Map<Integer,Integer> CHANNEL_WEIGHT_ORDER = new HashMap<Integer, Integer>(); 
	static{
		CHANNEL_WEIGHT_ORDER.put(1, 1);
		CHANNEL_WEIGHT_ORDER.put(2, 1);
		CHANNEL_WEIGHT_ORDER.put(3, 1);
		CHANNEL_WEIGHT_ORDER.put(4, 1);
		CHANNEL_WEIGHT_ORDER.put(5, 1);
		CHANNEL_WEIGHT_ORDER.put(1001, -1);
	}
	private int MAX_KEYWORD_LEN = 17;
	private int MIN_KEYWORD_LEN = 1;
	
	public ClothTreepathClassifier(InputStream keywordsIs, InputStream treepathIs) throws IOException{
		List<String> kwLines = FileHandler.readKnowlegeFileLines(keywordsIs);
		for(String line : kwLines){
			if(line.isEmpty())
				continue;
			Keyword kw = Keyword.parseKeyword(line);
			if(!value2Keywords.containsKey(kw.getValue()))
				value2Keywords.put(kw.getValue(), new LinkedList<Keyword>());
			value2Keywords.get(kw.getValue()).add(kw);
			for(String synm : kw.getSynonyms()){
				if(!value2Keywords.containsKey(synm))
					value2Keywords.put(synm, new LinkedList<Keyword>());
				value2Keywords.get(synm).add(kw);
			}
		}
		TreepathReader tpreader = new TreepathReader(treepathIs);
		List<Treepath> paths = tpreader.getAllPaths();
		for(Treepath path : paths)
			path2Sex.put(path.pathValue, path.sex);
	}
	
	/**
	 * 根据性别属性去除掉不符合的treepath。如果某个keyword的所有treepath都被过滤掉，
	 * 那么这个keyword也会被过滤掉。
	 * @param keywords
	 * @param sex
	 */
	private void filterWordBySex(List<Keyword> keywords, int sex){
		WEIBO_SEX weiboSex = WEIBO_SEX.enumValue(sex);
		List<Keyword> filterKws = new LinkedList<Keyword>();
		if(weiboSex != WEIBO_SEX.UNKNOWN){
			for(Keyword kw : keywords){
				Set<String> pathSet = kw.getPaths().getPathSet();
				for(Iterator<String> itor = pathSet.iterator();itor.hasNext();){
					String path = itor.next();
					if(path2Sex.get(path) != WEIBO_SEX.UNKNOWN && path2Sex.get(path) != weiboSex)
						itor.remove();
				}
				if(pathSet.isEmpty())
					filterKws.add(kw);
			}
		}
		keywords.removeAll(filterKws); //去掉无用的keyword
	}
	
	public List<String> getCandidateTreepath(int category, Map<String, String> msgInfoMap){
		List<String> pathList = new LinkedList<String>();
		List<Entry<String, Integer>> candPaths = getDebugCandidateTreepaths(category, msgInfoMap).second;
		int size = candPaths.size();
		if(size != 0){
			Entry<String, Integer> standardPath = candPaths.get(0);
			pathList.add(standardPath.getKey());
//			int gender = WEIBO_SEX.UNKNOWN.intValue(); //默认无性别
//			try{
//				gender = Integer.parseInt(msgInfoMap.get(OntoUtil.WEIBO_TPCLS_ATTRIBUTES.GENDER));
//			} catch(Exception e){
//				LOG.error("incorrect Gender format: <" + 
//						msgInfoMap.get(OntoUtil.WEIBO_TPCLS_ATTRIBUTES.GENDER) + ">");
//			}
//			if(size >= 2 && WEIBO_SEX.enumValue(gender) == WEIBO_SEX.UNKNOWN){
//				if(Math.abs(candPaths.get(1).getValue() - standardPath.getValue()) <= 2
//						&& path2Sex.get(candPaths.get(1).getKey()) != path2Sex.get(standardPath.getKey())){
//					pathList.add(candPaths.get(1).getKey());
//				}
//				if(size >=3){ //最多取前三条，当且仅当前三条treepath只是由于性别无法区分
//					if(Math.abs(candPaths.get(2).getValue() - standardPath.getValue()) <= 2
//							&& path2Sex.get(candPaths.get(2).getKey()) != path2Sex.get(standardPath.getKey())
//							&& path2Sex.get(candPaths.get(2).getKey()) != path2Sex.get(candPaths.get(1).getKey())){
//						pathList.add(candPaths.get(2).getKey());
//					}
//				}
//			}
			for(int i = 1, l = candPaths.size(); i < l; i++){
				if(standardPath.getValue() - candPaths.get(i).getValue() <= WEIGHT_RADIX * 1 + WEIGHT_RADIX){
					pathList.add(candPaths.get(i).getKey());
				}
			}
		}
		return pathList;
	}
	
	@SuppressWarnings("unchecked")
	public Pair<String,List<Entry<String, Integer>>> getDebugCandidateTreepaths(int category, Map<String, String> valuesMap){
		List<Entry<String, Integer>> candPaths = new LinkedList<Entry<String, Integer>>();
		if(value2Keywords.isEmpty() || path2Sex.isEmpty()){
			return new Pair<String,List<Entry<String, Integer>>>("",candPaths);
		}
		
		String content = valuesMap.get(OntoUtil.WEIBO_TPCLS_ATTRIBUTES.TEXT);
		List<Pair<Integer, Integer>> kwPoses = StringUtil.backwardMaxMatch(content, 
				value2Keywords, MAX_KEYWORD_LEN, MIN_KEYWORD_LEN);
		if(kwPoses.isEmpty())
			return new Pair<String,List<Entry<String, Integer>>>("",candPaths);
		List<Keyword> keywords = new LinkedList<Keyword>();
		for(Pair<Integer, Integer> pos : kwPoses){
			List<Keyword> kws = value2Keywords.get(content.substring(pos.first, pos.second));
			for(Keyword kw : kws)
					keywords.add(new Keyword(kw)); 		//deep copy
		}
		
		filterWordByKwType(keywords);
		
		if(valuesMap.get(OntoUtil.WEIBO_TPCLS_ATTRIBUTES.GENDER) != null)
			filterWordBySex(keywords, Integer.parseInt(valuesMap.get(OntoUtil.WEIBO_TPCLS_ATTRIBUTES.GENDER)));
		
		StringBuffer sb = new StringBuffer();
		if(!keywords.isEmpty()){
			sb.append(keywords.get(0).getValue()).append(":").append(keywords.get(0).getType());
			for(int i = 1; i < keywords.size(); i++)
				sb.append("$").append(keywords.get(i).getValue()).append(":").append(keywords.get(i).getType());
		}
		
		Map<PathLayer, Integer> layerStatMap = new HashMap<PathLayer, Integer>();
		Map<PathLayer, Double> layer2ShareHold = new HashMap<PathLayer, Double>();
		int maxDepth = -1;
		for(Keyword kw : keywords){
			double shareHold = 1.0/kw.getPaths().getPathSet().size();
			for(String path : kw.getPaths().getPathSet()){
				String[] pathSegs = path.split("#"); //path用#作为层级分隔符
				if(pathSegs.length > maxDepth) //找出深度最大的treepath
					maxDepth = pathSegs.length;
				StringBuffer expandPath = new StringBuffer();
				for(int i = 0; i < pathSegs.length; i++){
					expandPath.append(pathSegs[i]).append("#");
					PathLayer pl = new PathLayer(pathSegs[i], i, 
							expandPath.substring(0, expandPath.length() - 1), shareHold);
					int val = (layerStatMap.get(pl) == null) ? 1 : (layerStatMap.get(pl) + 1);
					if(layer2ShareHold.get(pl) == null || shareHold > layer2ShareHold.get(pl)){ //选择最大的sharehold
						pl.sharehold = shareHold;
						layerStatMap.remove(pl);
						layer2ShareHold.put(pl, shareHold);
					}
					layerStatMap.put(pl, val);
				}
			}
		}
		
		filterByInclusiveLayers(layerStatMap);
		
		Entry<PathLayer, Integer>[] orderedLayers = layerStatMap.entrySet().toArray(new Entry[layerStatMap.size()]);
		Arrays.sort(orderedLayers, new Comparator<Entry<PathLayer, Integer>>(){
			public int compare(Entry<PathLayer, Integer> o1, Entry<PathLayer, Integer> o2) { 
				int cmp = ((Integer)o2.getValue()).compareTo((Integer)o1.getValue());
				if(cmp == 0){
					if(((PathLayer)o1.getKey()).sharehold > ((PathLayer)o2.getKey()).sharehold)//如果sharehold越大排名越靠前
						return -1;
					else
						return 1;
				}
				return cmp;
			}
		});
		Map<Integer, List<PathLayer>> layer2Path = new HashMap<Integer, List<PathLayer>>(); //list中的顺序也按照count值从大到小倒序排列
		for(Entry<PathLayer, Integer> entry : orderedLayers){
			if(!layer2Path.containsKey(entry.getKey().layer))
				layer2Path.put(entry.getKey().layer, new LinkedList<PathLayer>());
			layer2Path.get(entry.getKey().layer).add(entry.getKey());
		}
		Map<PathLayer, Integer> layerWeightMap = new HashMap<PathLayer, Integer>();
		for(Entry<Integer, List<PathLayer>> entry : layer2Path.entrySet()){
			int order = 0;
			for(PathLayer pl : entry.getValue())
				layerWeightMap.put(pl, WEIGHT_RADIX - order++); //按照顺序递减
		}
		Map<String, Integer> pathWeightMap = new HashMap<String, Integer>(); //一条path包含了多层layer，一条path的权值由多层的layer权值加和而成;
		for(Keyword kw : keywords)											 //得到所有候选的treepath
			for(String path : kw.getPaths().getPathSet())
				pathWeightMap.put(path, 0);
		Map<String, Integer> tmpWeightMap = new HashMap<String, Integer>();
		for(Entry<String, Integer> entry : pathWeightMap.entrySet()){
			int weight = 0;
			String[] pathSegs = entry.getKey().split("#");
			for(int i = 0; i < pathSegs.length; i++){
				int power = i;
				if(CHANNEL_WEIGHT_ORDER.get(category) == -1)
					power = (i*-1) -1 + maxDepth; //[0,1,2] --> [2,1,0]
				PathLayer pl = new PathLayer(pathSegs[i], i, null, 0.0d);//因为获取layerpath只需要value和layer两个参数值，所以后两个参数设为null和0
				if(layerWeightMap.containsKey(pl)) //有可能被过滤掉了
					weight += Math.pow(WEIGHT_RADIX, power) * layerWeightMap.get(pl);
				else{ //如果不包含某曾layer说明这层layer被过滤掉
					weight = 0;
					break;
				}
			}
			entry.setValue(weight);
			if(weight != 0)
				tmpWeightMap.put(entry.getKey(), entry.getValue());
		}
		Entry<String, Integer>[] orderedPaths = OntoUtil.sortMapByValue(tmpWeightMap, true);
		return new Pair<String,List<Entry<String, Integer>>>(sb.toString(),Arrays.asList(orderedPaths));
	}
	
	private void filterWordByKwType(List<Keyword> keywords){
		Map<String, Boolean> filterMap = new HashMap<String, Boolean>();
		for(Keyword  kw : keywords){
			if(kw.getType().equals("B") || kw.getType().equals("AM"))
				filterMap.put(kw.getValue(), true);
			else
				filterMap.put(kw.getValue(), false);
		}
		for(Iterator<Keyword> itor = keywords.iterator(); itor.hasNext();){
			Keyword kw = itor.next();
			if(filterMap.get(kw.getValue()))
				itor.remove();
			else if(kw.getPaths().isRootPath()) //不用顶层treepath
				itor.remove();
		}
	}
	
	/**
	 * 因为童装/童鞋包含了衣服、鞋子、内衣目录，keyword对应的treepath权重过大，导致把很多和童装/童鞋无关的
	 * weibo分到了该目录。现在的过滤规则是当某个treepath只属于童装/童鞋时才保留treepah的排名，否则过滤。
	 * @param layerStatMap
	 * @return
	 */
	private void filterByInclusiveLayers(Map<PathLayer, Integer> layerStatMap){
		if(!INCLUSIVE_PATHS.isEmpty() && !layerStatMap.isEmpty()){
			Map<PathLayer, Integer> tmpMap = new HashMap<PathLayer, Integer>();
			Map<String, Boolean> inclusiveMap = new HashMap<String, Boolean>(); //是否包含inclusive keywords
			for(String pathValue : INCLUSIVE_PATHS){
				inclusiveMap.put(pathValue, false);
				for(Entry<PathLayer, Integer> entry : layerStatMap.entrySet())
					if(entry.getKey().pathValue.equals(pathValue) &&
							entry.getKey().sharehold == 1.0d){
						inclusiveMap.put(pathValue, true);
						break;
					}
			}
			for(Entry<PathLayer, Integer> entry : layerStatMap.entrySet()){
				
				if(INCLUSIVE_PATHS.contains(entry.getKey().pathValue) &&
						!inclusiveMap.get(entry.getKey().pathValue));
				else
					tmpMap.put(entry.getKey(), entry.getValue());
			}
			layerStatMap.clear();
			layerStatMap.putAll(tmpMap);
		}
	}
	
	private static class PathLayer{
		public String layerValue;
		public String pathValue;
		public double sharehold = 0.0d;
		public int layer = -1; //默认从-1开始，第一层为0
		public PathLayer(String pathValue, int layer, String completePath, double shareHold){
			this.layerValue = pathValue;
			this.layer = layer;
			this.pathValue = completePath;
			this.sharehold = shareHold;
		}
		public boolean equals(Object other){
			if(other == this)
				return true;
			if(!(other instanceof PathLayer))
				return false;
			PathLayer opl = (PathLayer)other;
			if(opl.layerValue == null || layerValue == null || opl.layer == -1 || layer == -1)
				return false;
			return opl.layerValue.equals(layerValue) && opl.layer == layer;
		}
		public int hashCode(){
			int result = 33;
			result = result * 17 + layerValue.hashCode();
			result = result * 17 + new Integer(layer).hashCode();
			return result;
		}
	}
}
