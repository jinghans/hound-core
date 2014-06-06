package com.yeezhao.hound.ontology.merge;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yeezhao.hound.ontology.Keyword;
import com.yeezhao.hound.ontology.PathSet;

/**
 * 合并相同频道的新旧keywords.txt文件。合并原则：
 * 1. 基础规则： 如果新词中的primary word和旧词相同，用另一个词作为新词的primary word，如果没有其它词了，就保留。
 * 2. 基础规则： 如果新词的value，synonyms和某个旧词完全相同，合并它们的treepath，继续使用旧词的primary word。
 * 3. 可选规则： 具有完全相同的treepath的word进行合并，将新添加词作为旧词的同义词加入； 
 * @author hans
 *
 */
public class OntoMerger {
	
	public static void printUsage(){
		String usageStr = "correct parameters: <new_candiates_file> <old_candidates_file> [-mt]\n" +
				"\t new_candidates_file: \n" +
				"\t old_candidates_file: \n" +
				"\t -mt: if given, words with same treepath will be merged.";
		System.out.println(usageStr);
	}
	
	public static List<Keyword> readFromFile(String file){
		List<Keyword> kwList = new LinkedList<Keyword>();
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(file));
			String line = null;
			while((line = br.readLine()) != null){
				if(!line.trim().isEmpty() && !line.startsWith("#")){ //注释行
					Keyword kw = Keyword.parseKeyword(line);
					kwList.add(kw);
				}
			}
		} catch (IOException e){
			e.printStackTrace();
			System.out.println("error in reading file: " + file);
			System.exit(1);
		} finally{
			try {
				br.close();
			} catch (IOException e) {
				System.out.println("error in closing file: " + file);
				System.exit(1);
			}
		}
		return kwList;
	}
	
	public static void write2File(List<Keyword> kws, String outFile){
		BufferedWriter bw = null;
		try{
			bw = new BufferedWriter(new FileWriter(outFile));
			for(Keyword kw : kws){
				bw.write(kw.toString() + "\n");
			}
		} catch (IOException e){
			System.out.println("error in initing file: " + outFile);
			System.exit(1);
		} finally{
			try {
				bw.close();
			} catch (IOException e) {
				System.out.println("error in closing file: " + outFile);
				System.exit(1);
			}
		}
	}
	
	public static void checkSynonymDup(List<Keyword> keywords){
		Map<String, List<String>> loc2Areas = new HashMap<String, List<String>>();
		for(Keyword kw : keywords){
			kw.getSynonyms().add(kw.getValue());
			for(String loc : kw.getSynonyms()){
				if(!loc2Areas.containsKey(loc))
					loc2Areas.put(loc, new LinkedList<String>());
				loc2Areas.get(loc).add(kw.getPaths().toString());
			}
		}
		
		for(Entry<String, List<String>> entry : loc2Areas.entrySet()){
			if(entry.getValue().size() > 1){
				System.out.print(entry.getKey() + "|" + entry.getValue().size() + "\t");
				for(String area : entry.getValue())
					System.out.print(area + ", ");
				System.out.println();
			}
		}
	}
	
	public static void keywordMerge(String newFile, List<Keyword> addKws, String oldFile, List<Keyword> oldKws, boolean mergeUnderSamePath){
		//检查新增知识库与旧知识库的大小，如果比旧知识库大，可能是参数输入反了。
		if(oldKws.size() < addKws.size()){
			System.out.println("old file<" + oldFile +
					"> has less lines than new file<" + newFile +">. " +
					"please check.");
		}
		Map<PathSet, Keyword> wordMap = new HashMap<PathSet, Keyword>();
		for(Keyword kw : oldKws){
			wordMap.put(kw.getPaths(), kw);
		}
		Map<String, Keyword> primaryMap = new HashMap<String, Keyword>();
		Map<Keyword, Keyword> samewordMap = new HashMap<Keyword, Keyword>();
		for(Keyword kw : oldKws){
			primaryMap.put(kw.getValue(), kw);
			samewordMap.put(kw, kw);
		}
		
		StringBuffer sb = new StringBuffer();
		int combineCount = 0, conflictCount = 0, addKeywords = 0, addSynsets = 0, sameCount = 0;
		for(Keyword nkw : addKws){
			sb.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
			Keyword mapWord = wordMap.get(nkw.getPaths());
			if(samewordMap.containsKey(nkw)){
				++sameCount;
				mapWord = samewordMap.get(nkw);
				sb.append("same keywords: \n").append(mapWord.toString()).append("\n");
				mapWord.getPaths().getPathSet().addAll(nkw.getPaths().getPathSet());
				sb.append(nkw.toString()).append("\n");
				sb.append("result string:\n").append(mapWord.toString()).append("\n");
			} else if(mapWord != null && mergeUnderSamePath){//如果有相同的treepath则合并
				++combineCount;
				int oldSize = mapWord.getSynonyms().size();
				sb.append("combine keywords as follows: \n");
				sb.append(mapWord.toString()).append("\n").append(nkw.toString()).append("\n");
				mapWord.getSynonyms().addAll(nkw.getSynonyms()); //合并同义词
				mapWord.getSynonyms().add(nkw.getValue()); //用旧的value做value，新的value加入同义词组
				if(mapWord.getSynonyms().contains(mapWord.getValue()))
					mapWord.getSynonyms().remove(mapWord.getValue());
				sb.append("result string: \n").append(mapWord.toString()).append("\n");
				addSynsets += mapWord.getSynonyms().size() - oldSize;
			} else if(primaryMap.containsKey(nkw.getValue())){ //primary word重复 
				++conflictCount;
				Keyword prmWord = primaryMap.get(nkw.getValue());
				sb.append("keywords conflict with each other: \n");
				sb.append(prmWord.toString()).append("\n").append(nkw.toString()).append("\n");
				if(nkw.getSynonyms().isEmpty()){ //允许这种冲突
					System.out.println("conflicted primary word: " + prmWord.toString());
				} else{
					String primaryValue = nkw.getSynonyms().iterator().next();
					nkw.getSynonyms().remove(primaryValue);
					nkw.getSynonyms().add(nkw.getValue());
					nkw.setValue(primaryValue);
				}
				oldKws.add(nkw);
				addSynsets += nkw.getSynonyms().size();
				sb.append("result string: \n").append(prmWord.toString()).append("\n");
				sb.append(nkw.toString()).append("\n");
			} else{
				sb.append("new keywords: \n");
				sb.append(nkw.toString()).append("\n");
				oldKws.add(nkw);
				wordMap.put(nkw.getPaths(), nkw);
				++addKeywords;
				addSynsets += nkw.getSynonyms().size() + 1;
			}
		}
		//保存调整结果
		write2File(oldKws, oldFile);
		System.out.println("same words: " + sameCount
				+ ", combine words: " + combineCount 
				+ ", conflicts: " + conflictCount
				+ ", new words: " + addKeywords
				+ ", new synonyms: " + addSynsets);
		System.out.println(sb.toString());
	}
	
	public static void main(String[] args) {
		
		args = new String[]{
				"data/add-types", 
				"data/csmt_keywords.txt",
				"-mt"
		};
		
		boolean mergeTreepath = false;
		if(args.length != 2 && args.length != 3){
			printUsage();
			System.exit(1);
		} else if(args.length == 3){
			if(args[2].equals("-mt"))
				mergeTreepath = true;
		}
		
		List<Keyword> addKws = readFromFile(args[0]);
		List<Keyword> oldKws = readFromFile(args[1]);
		
		keywordMerge(args[0], addKws, args[1], oldKws, mergeTreepath);
		//checkSynonymDup(oldKws);
	}
	
}
