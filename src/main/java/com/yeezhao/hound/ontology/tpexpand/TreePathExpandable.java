package com.yeezhao.hound.ontology.tpexpand;

import java.util.Collection;
import java.util.List;

/**
 * 根据treepath扩展相关接口
 * @author Administrator
 *
 */
public interface TreePathExpandable {

	/**
	 * 通过treepath扩展词组
	 * @param category
	 * @param treepaths key name value treepath
	 * @return
	 */
	public List<TnTSword> getSearcherWords(int category,Collection<String> treepaths);
	
//	/**
//	 * 查找所有可以扩出<code>node</code>的treepath集合
//	 * @param category
//	 * @param node
//	 * @return
//	 */
//	public Map<String,Collection<String>> getTreePaths(int category,SWord sword);
}
