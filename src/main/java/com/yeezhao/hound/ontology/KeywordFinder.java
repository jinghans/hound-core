package com.yeezhao.hound.ontology;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 查找keyword对应的treepath。或者某个查询词可能的treepath。
 * @author user
 *
 */
public class KeywordFinder {
	
	private Map<String, PathSet> wordMap = new HashMap<String, PathSet>();
	
	public KeywordFinder(InputStream kwFile) throws IOException {
		List<String> lines = FileHandler.readKnowlegeFileLines(kwFile);
		for(String line : lines){
			if(!line.isEmpty()){
				Keyword kw = Keyword.parseKeyword(line.trim());
				wordMap.put(kw.getValue(), kw.getPaths());
				for(String synm : kw.getSynonyms())
					wordMap.put(synm, kw.getPaths());
			}
		}
	}
	
	public Set<String> getPossiblePaths(String searchWord){
		PathSet ps = wordMap.get(searchWord);
		if(ps != null){
			return new PathSet(ps).getDegradePaths();
		}
		return null;
	}
	
	public Set<String> getCombPaths(List<String> searchWords){
		if(searchWords.isEmpty())
			return null;
		Set<String> pset = getPossiblePaths(searchWords.get(0));
		if(pset == null)
			return null;
		for(int i = 1, l = searchWords.size(); i < l; i++){
			Set<String> tmpPset = getPossiblePaths(searchWords.get(i));
			if(tmpPset != null && !tmpPset.isEmpty()){
				if(pset.isEmpty()){ //root path
					pset = tmpPset;
					continue;
				}
				for(Iterator<String> itor = pset.iterator(); itor.hasNext();){
					String sw = itor.next();
					if(!tmpPset.contains(sw))
						itor.remove();
				}
			}
		}
		return pset;
	}
}
