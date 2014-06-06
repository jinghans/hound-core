package com.yeezhao.hound.ontology;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FileHandler {
	
	/**
	 * 需要处理注释符。
	 * @param knowledgeFileIs
	 * @return
	 */
	public static List<String> readKnowlegeFileLines(InputStream knowledgeFileIs) throws IOException{
		String ANNOTATION_SYMBOL = "#"; //行注释符
		
		List<String> lines = readLinesIntoList(knowledgeFileIs);
		for(Iterator<String> itor = lines.iterator(); itor.hasNext();){
			String line = itor.next();
			if(line.startsWith(ANNOTATION_SYMBOL))
				itor.remove();
		}
		return lines;
	}
	
	/**
	 * 如果文件没有，或者文件为空否返回empty list，不会为null。
	 * @param fileNameIs
	 * @return
	 */
	public static List<String> readLinesIntoList(InputStream fileNameIs) throws IOException {
		List<String> lines = new LinkedList<String>();
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(fileNameIs));
			String line = null;
			while((line = br.readLine()) != null)
				lines.add(line);
		} catch(IOException e){
			throw e;
		} finally{
			if(br != null)
				br.close();
		}
		return lines;
	}
	
	public static void writeList2File(List<String> lines, String fileName, boolean... append) throws IOException {
		if(lines != null && !lines.isEmpty()){
			BufferedWriter bw = null;
			try{
				bw = new BufferedWriter(new FileWriter(fileName));
				for(String line : lines)
					bw.write(line + "\n");
			} catch(IOException e){
				throw e;
			} finally{
				if(bw != null)
					bw.close();
			}	
		}
	}
	
}
