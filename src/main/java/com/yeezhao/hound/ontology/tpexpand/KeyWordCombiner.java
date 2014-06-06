package com.yeezhao.hound.ontology.tpexpand;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
//import java.util.Map.Entry;
import org.apache.log4j.Logger;

import com.yeezhao.hound.ontology.FileHandler;
import com.yeezhao.hound.ontology.Keyword;

public class KeyWordCombiner {
	
	private final Logger LOG = Logger.getLogger(KeyWordCombiner.class);
	private Map<String, Keyword> val2Keyword = new HashMap<String, Keyword>();
	private Set<CombineRule> rules = new HashSet<CombineRule>();					//去除重复的组合规则
	private static enum RULE_PART_TYPE{type, keyword};
	private Map<String, List<Keyword>> path2Keyword = new HashMap<String, List<Keyword>>();	//记录keyword对应的treepath, 如果一个word对应了一个多级
																					//path, 那么这个word将对应多个path，这些path都从顶级path
																					//开始。例如nike对应男鞋#运动鞋，会产生两层对应<nike,男鞋#运动鞋>
																					//和<nike,男鞋>
	public static final String ROOT_TREEPATH = "root";
	/**
	 * 返回某个treepath对应的所有keywords。
	 * @param treepath
	 * @return
	 */
	public List<Keyword> getKwsUnderTreepath(String treepath){
		if(path2Keyword.get(treepath) == null)
			return null;
		return new LinkedList<Keyword>(path2Keyword.get(treepath));
	}
	
	/**
	 * 返回某个word对应的keyword。
	 * @param wordVal
	 * @return
	 */
	public Keyword getCorrKeyword(String wordVal){
		return new Keyword(val2Keyword.get(wordVal));
	}

	public KeyWordCombiner(InputStream combineStream, InputStream keywordStream) throws IOException{
		//初始化两个map
		List<String> kwLines = FileHandler.readKnowlegeFileLines(keywordStream);
		for(String line : kwLines){
			if(line.trim().isEmpty())
				continue;
			String[] segs = line.split("\\|");
			if(segs.length != 2 && segs.length != 3){
				LOG.info("error format string in keyword file, " + line);
				continue;
			}
			Keyword  kw = Keyword.parseKeyword(line);
			
			if(val2Keyword.containsKey(kw)){  //不允许对应多个treepath
				LOG.info("word: " + kw.getValue() + " belong to more than one treepaths");
			}
			val2Keyword.put(kw.getValue(), kw);
			
			Set<String> allPaths = new HashSet<String>(kw.getPaths().getDegradePaths());
			if(allPaths.isEmpty()) //将顶级treepath加入map中
				allPaths.add(ROOT_TREEPATH);
			for(String layerPath : allPaths){
				if(!path2Keyword.containsKey(layerPath)){
					path2Keyword.put(layerPath, new LinkedList<Keyword>());
				}
				if(!path2Keyword.get(layerPath).contains(kw))
					path2Keyword.get(layerPath).add(kw);
			}
		}
		
		//printKeyWord2Node();
		//printPath2Node();
		
		//初始化组合规则
		List<String> combLines = FileHandler.readKnowlegeFileLines(combineStream);
		for(String line : combLines){
			if(line.trim().isEmpty())
				continue;
			rules.add(new CombineRule(line));
		}
	}
	
//	private void printKeyWord2Node(){
//		for(Entry<String, Node> entry : keyWord2Node.entrySet()){
//			System.out.println(entry.getKey() + "\t" + entry.getValue());
//		}
//	}
//	
//	private void printPath2Node(){
//		for(Entry<String, List<Node>> entry : path2Node.entrySet()){
//			System.out.println(entry.getKey());
//			for(Node node : entry.getValue())
//				System.out.print(node.getWord() + ",");
//			System.out.println("\n***");
//		}
//	}
	
	/**
	 * 如果组合部分是TYPE类型，所有相同type的词。如果是keyword类型，仅当该keyword有对应的treepath时才可以返回。
	 * @param kws
	 * @param rulePart
	 * @param treepaths
	 * @return 
	 */
	private List<Keyword> filterKeyWord(List<Keyword> kws, String rulePart, Collection<String> treepaths){
		List<Keyword> awords = new LinkedList<Keyword>();
		switch(getPartType(rulePart)){
		case keyword:
			for(String value : rulePart.split("#")){
				if(!val2Keyword.containsKey(value)){
					LOG.debug("keyword'" + value + "' hasn't a treepath");
				} else{
					if(validateKeyWord(val2Keyword.get(value), treepaths))	//对keyword进行校验
						awords.add(val2Keyword.get(value));
				}
			}
			break;
		case type:
			for(Keyword kw : kws)
				if(kw.getType().equals(rulePart))
					awords.add(kw);
		}
		return awords;
	}
	
	/**
	 * 校验在keycomb中出现的关键词是否符合treepaths的要求。
	 * @param kw
	 * @return
	 */
	private boolean validateKeyWord(Keyword kw, Collection<String> treepaths){
		if(kw.getPaths().isRootPath())
			return true;
		for(String treepath : treepaths)
			if(!kw.getPaths().getDegradePaths().contains(treepath))
				return false;
		return true;
	}
	
	/**
	 * 判断组合规则中的keyword是否为'TYPE'类型， 例如'B$不满'中'B'是TYPE，但'不满'是keyword。
	 * @param rulePart
	 * @return
	 */
	private RULE_PART_TYPE getPartType(String rulePart){
		if(rulePart == null || rulePart.isEmpty())
			return RULE_PART_TYPE.keyword;
		if(rulePart.length() == 1 || rulePart.length() == 2){	//假定所有的TYPE类型都以一个或者两个英文字母作为缩写
			boolean enchar = true;
			for(int i = 0; i < rulePart.length(); i++){
				char ch = rulePart.charAt(0);
				if(!(ch >= 'a' && ch <= 'z') &&
						!(ch >= 'A' && ch <= 'Z')){
					enchar = false;
					break;
				}
			}
			if(enchar)
				return RULE_PART_TYPE.type;
		}
		return RULE_PART_TYPE.keyword;
	}
	
	/**
	 * 
	 * @param category
	 * @param candiWordsList
	 * @param treepaths
	 * @return 如果组合不成功，返回empty list而不是null。
	 */
	public List<TnTSword> combineWords(int category, List<Keyword> candiWordsList, Collection<String> treepaths){
		List<TnTSword> tntSwords = new LinkedList<TnTSword>();
		for(CombineRule rule : rules){
			List<List<Keyword>> factorKws = new LinkedList<List<Keyword>>();
			for(String part : rule.getParts()){
				List<Keyword> words = filterKeyWord(candiWordsList, part, treepaths);
				if(words.isEmpty()){
					factorKws.clear();
					break;
				} else
					factorKws.add(words);
			}
			if(!factorKws.isEmpty() && matchTreepath(factorKws, treepaths)){	//将所有的Keyword封装成Nodes
				List<List<Node>> factorNodes = new LinkedList<List<Node>>();
				for(List<Keyword> kws : factorKws){
					List<Node> nodes = new LinkedList<Node>();
					for(Keyword kw : kws){
						nodes.add(new Node(kw.getValue(), kw.getType()));
						for(String synonym : kw.getSynonyms())
							nodes.add(new Node(synonym, kw.getType()));
					}
					factorNodes.add(nodes);
				}
				tntSwords.add(new TnTSword(category, factorNodes));
			}
		}
		return tntSwords;
	}
	
	private boolean matchTreepath(List<List<Keyword>> factorKws, Collection<String> treepaths){
		for(String treepath : treepaths){
			boolean matched = false;
			for(List<Keyword> kws : factorKws){
				if(kws.get(0).getPaths().getDegradePaths().contains(treepath))
					matched = true;
			}
			if(!matched)
				return false;
		}
		return true;
	}
}

class CombineRule{
	private Set<String> parts = new TreeSet<String>();
	public Set<String> getParts() {
		return parts;
	}

	public void setParts(Set<String> parts) {
		this.parts = parts;
	}

	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CombineRule(String ruleLine){
		String[] segs = ruleLine.split("\\|");
		type = segs[1];
		parts.addAll(Arrays.asList(segs[0].split("\\$")));
	}
	
	public int hashCode(){
		return 31 * parts.toString().hashCode() + 17;
	}
	
	public boolean equals(Object another){
		if(another == this)
			return true;
		if(!(another instanceof CombineRule))
			return false;
		return parts.toString().equals(((CombineRule)another).parts.toString());
	}
}
