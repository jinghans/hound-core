package com.yeezhao.hound.ontology.tpexpand;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.yeezhao.hound.ontology.Keyword;
import com.yeezhao.hound.ontology.OntoUtil;

public class TreepathExpander implements TreePathExpandable {

	private final Logger LOG = Logger.getLogger(TreepathExpander.class);
	private String knowledgeDir;	//不要目录分隔符
	
	private Map<Integer, KeyWordCombiner> combinerMap = new HashMap<Integer, KeyWordCombiner>();
	
	public TreepathExpander(String knowledgeDir){
		this.knowledgeDir = knowledgeDir;
	}
	
	public KeyWordCombiner getCombiner(int category){
		//按频道初始化combiner，组合规则。
		KeyWordCombiner combiner = null;
		if(!combinerMap.containsKey(category)){
			if(!OntoUtil.CATE_2NAME.containsKey(category)){
				LOG.info("category : " + category + " has no prefixs setted.");
			} else{
				try {
					combiner = new KeyWordCombiner(new FileInputStream(knowledgeDir + "/" + OntoUtil.CATE_2NAME.get(category) + OntoUtil.KEYCOMB_SUFFIX),
							new FileInputStream(knowledgeDir + "/" + OntoUtil.CATE_2NAME.get(category) + OntoUtil.KEYWORD_SUFFIX));
					combinerMap.put(category, combiner);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else{
			combiner = combinerMap.get(category);
		}
		return combiner;
	}
	
	
	/**
	 * 校验treepath。目前未做校验。
	 * @param treepath
	 * @return
	 */
	private boolean validateTreepath(String treepath){
		return	true;
	}
	
	public List<TnTSword> getSearcherWords(int category, Collection<String> treepaths) {
		List<TnTSword> searchWords = new LinkedList<TnTSword>();
		//校验 
		for(String path : treepaths){
			if(!validateTreepath(path))
				treepaths.remove(path);
		}
		if(treepaths.isEmpty())
			return searchWords; 
		
		//按频道初始化combiner，组合规则。
		KeyWordCombiner combiner = getCombiner(category);
		
		//挑选treepath覆盖关键词。
		List<Keyword> coveredNodes = coverWords(combiner, treepaths);
		
		//组合关键词。
		if(coveredNodes != null && !coveredNodes.isEmpty()){	//组合关键词
			List<Keyword> rootNodes = combiner.getKwsUnderTreepath(KeyWordCombiner.ROOT_TREEPATH);
			if(rootNodes != null)
				coveredNodes.addAll(rootNodes);
			searchWords = combiner.combineWords(category, coveredNodes, treepaths);
		}
		return searchWords;
	}
	
	/**
	 * 有多个treepath时，取并集。
	 * @param combiner
	 * @param treepaths size可能为1，也可能大于1
	 * @return
	 */
	private List<Keyword> coverWords(KeyWordCombiner combiner, Collection<String> treepaths){
		Iterator<String> itor = treepaths.iterator();
		String treepath = itor.next();
		List<Keyword> coverWords = combiner.getKwsUnderTreepath(treepath);	//多个treepath，取覆盖词的交集。
		if(coverWords == null){
			LOG.info("coverWords is null,illegal treepath: " + treepath);
			return null;
		}
		while(itor.hasNext()){
			treepath = itor.next();
			List<Keyword> anotherWords = combiner.getKwsUnderTreepath(treepath);
			if(anotherWords == null){
				LOG.info("anotherWords is null,illegal treepath: " + treepath);
				continue;
			}
			coverWords.addAll(anotherWords);
		}
		return coverWords;
	}
}
