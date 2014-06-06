package com.yeezhao.hound.ontology;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AlgorithmUtil {
	/**
	 * 对多个list做笛卡尔乘积，返回所有的组合，不去重。
	 * @param <T>
	 * @param factorList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<List<T>> cartesianProduct(List<List<T>> factorList){
		if(factorList.size() <= 1){
			List<List<T>> result = new LinkedList<List<T>>();
			for(T t : factorList.get(0))
				result.add(Arrays.asList(t));
			return result;
		}
		Collection<T> topFactor = factorList.remove(0);
		List<List<T>> tmpProduct = cartesianProduct(factorList);
		List<List<T>> result = new LinkedList<List<T>>();
		for(T t : topFactor){
			for(Collection<T> lft : tmpProduct){
				List<T> col = new LinkedList<T>();
				col.addAll(lft);
				col.add(t);
				result.add(col);
			}
		}
		return result;
	}
	
	/**
	 * 计算集合的所有子集, 不包括空子集。
	 * @param <T>
	 * @param dataset
	 * @return
	 */
	public static <T> List<Collection<T>> subsets(Collection<T> dataset){
		List<T> list = new LinkedList<T>();
		list.addAll(dataset);
		List<Collection<T>> sets = new LinkedList<Collection<T>>();
		for(int i = 1, l = (1 << dataset.size()); i < l; i++){
			Set<T> subset = new HashSet<T>();
			int tmp = i;
			int pos = dataset.size() - 1;
			while(pos >= 0 && tmp > 0){
				if((tmp & 0x1) == 1){
					subset.add(list.get(pos));
				}
				pos--;
				tmp = tmp >> 1;
			}
			sets.add(subset);
		}
		return sets;
	}
	
	public static <T> void printNestedCollections(Collection<Collection<T>> collections){
		System.out.println("size: " + collections.size());
		for(Collection<T> col : collections){
			for(T t : col)
				System.out.print(t + ",");
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		List<List<Integer>> results = new LinkedList<List<Integer>>();
		List<List<Integer>> lists = new LinkedList<List<Integer>>();
		lists.add(Arrays.asList(new Integer[]{1, 3, 5}));
//		lists.add(Arrays.asList(new Integer[]{2, 4}));
//		lists.add(Arrays.asList(new Integer[]{7, 8, 9, 6}));
		results = cartesianProduct(lists);
		System.out.println("size: " + results.size());
		for(Collection<Integer> col : results){
			for(Integer t : col)
				System.out.print(t + ",");
			System.out.println();
		}
		
//		Collection<Integer> numSet = new HashSet<Integer>();
//		numSet.addAll(Arrays.asList(new Integer[]{1, 2, 3}));
//		List<Collection<Integer>> sets = subsets(numSet);
//		printNestedCollections(sets);
	}
}
