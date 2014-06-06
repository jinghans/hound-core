package com.yeezhao.hound.ontology;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Keyword{
	private String value;
	private String type;
	private Set<String> synonyms = new HashSet<String>();
	private PathSet paths = new PathSet();;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Keyword(Keyword kw){
		this.value = kw.value;
		this.type = kw.type;
		for(String synm : kw.synonyms)
			this.synonyms.add(synm);
		this.paths = new PathSet(kw.getPaths());
	}
	
	
	public Set<String> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(Set<String> synonyms) {
		this.synonyms = synonyms;
	}
	public PathSet getPaths() {
		return paths;
	}

	public void setPaths(PathSet paths) {
		this.paths = paths;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public Keyword(String value, String type, Set<String> synonyms){
		this.value = value;
		this.type = type;
		this.synonyms = synonyms;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(value);
		for(String syn : synonyms)
			sb.append("$").append(syn);
		sb.append("|").append(type).append("|").append(paths.toString());
		return sb.toString();
	}
	
	public int hashCode(){
		Set<String> codeSet = new TreeSet<String>();
		codeSet.add(value);
		codeSet.addAll(synonyms);
		int result = 33;
		for(String path : codeSet)
			result = result * 17 + path.hashCode();
		return result;
	}
	
	public boolean equals(Object otherObj){
		if(otherObj == this)
			return true;
		if(!(otherObj instanceof Keyword))
			return false;
		Keyword akw = (Keyword)otherObj;
		if(synonyms.size() != akw.synonyms.size())
			return false;
		Set<String> aset = new HashSet<String>(akw.synonyms);
		aset.add(akw.getValue());
		if(!aset.contains(value))
			return false;
		for(String syn : synonyms)
			if(!aset.contains(syn))
				return false;
		return true;
	}
	
	/**
	 * 
	 * @param line, 需要符合keyword文件格式。
	 * @return 如果格式错误，返回null。
	 */
	public static Keyword parseKeyword(String line){
		String[] parts = line.split("\\|");
		if(parts.length != 3 && parts.length !=  2){
			System.out.println("format error in line: " + line);
			return null;
		}
		
		String[] words = parts[0].split("\\$");
		Set<String> synonyms = new HashSet<String>(Arrays.asList(words));
		synonyms.remove(words[0]);
		Keyword kw = new Keyword(words[0], parts[1], synonyms);
		if(parts.length == 3){ //有可能没有treepath
			Set<String> paths = new HashSet<String>(Arrays.asList(parts[2].split("\\$")));
			kw.setPaths(new PathSet(paths));
		}
		
		return kw;
	}
}