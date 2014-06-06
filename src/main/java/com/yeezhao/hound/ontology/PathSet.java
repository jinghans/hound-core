package com.yeezhao.hound.ontology;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class PathSet{
	private Set<String> paths = new TreeSet<String>();
	private Set<String> degradePaths = new HashSet<String>(); //从顶层开始的所有treepath
	public PathSet(){} //treepath set可能为空
	
	public PathSet(PathSet ps){
		for(String path : ps.getPathSet()){
			paths.add(path);
			expandDegradePaths(path);
		}
	}
	
	private void expandDegradePaths(String pathValue){
		String[] layers = pathValue.split("#");
		StringBuffer degradePath = new StringBuffer();
		degradePath.append(layers[0]);
		degradePaths.add(layers[0]);
		for(int i = 1, l = layers.length; i < l; i++){
			degradePath.append("#").append(layers[i]);
			degradePaths.add(degradePath.toString());
		}
	}
	
	/**
	 * 如果path set为空表示path是root path。
	 * @return
	 */
	public boolean isRootPath(){
		return paths.isEmpty();
	}
	
	public Set<String> getDegradePaths() {
		return degradePaths;
	}

	public PathSet(Set<String> paths){
		this.paths = paths;
		for(String path : paths)
			expandDegradePaths(path);
	}
	
	public Set<String> getPathSet(){
		return paths;
	}
	
	public int hashCode(){
		int result = 33;
		for(String path : paths)
			result = result * 17 + path.hashCode();
		return result;
	}
	public boolean equals(Object ps){
		if(ps == this)
			return true;
		if(!(ps instanceof PathSet))
			return false;
		PathSet anpath = (PathSet)ps;
		if(paths.size() != anpath.paths.size())
			return false;
		for(String apath : anpath.paths)
			if(!paths.contains(apath))
				return false;
		return true;
	}
	public String toString(){
		StringBuffer sb = new StringBuffer();
		if(!paths.isEmpty()){
			Iterator<String> itor = paths.iterator();
			sb.append(itor.next());
			while(itor.hasNext())
				sb.append("$").append(itor.next());
		}
		return sb.toString();
	}
}
