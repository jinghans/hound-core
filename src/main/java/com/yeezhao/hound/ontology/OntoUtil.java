package com.yeezhao.hound.ontology;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class OntoUtil {
	public static final String KEYWORD_SUFFIX = "_keywords.txt";
	public static final String KEYCOMB_SUFFIX = "_keycomb.txt";
	public static final String TREEPATH_SUFFIX = "_treepath.xml";
	
	public static interface WEIBO_TPCLS_ATTRIBUTES{
		public static final String TEXT = "text";
		public static final String GENDER = "gender";
	}
	
	public static Map<Integer, String> CATE_2NAME = new HashMap<Integer, String>();
	public static Map<String, Integer> NAME_2CATE = new HashMap<String, Integer>();
	static{
		CATE_2NAME.put(1, "csmt");
		NAME_2CATE.put("csmt", 1);
		CATE_2NAME.put(2, "tour");
		NAME_2CATE.put("tour", 2);
		CATE_2NAME.put(3, "ftns");
		NAME_2CATE.put("ftns", 3);
		CATE_2NAME.put(4, "cloth");
		NAME_2CATE.put("cloth", 4);
		CATE_2NAME.put(5, "telc");
		NAME_2CATE.put("telc", 5);
		CATE_2NAME.put(1001, "sjb_locations");
		NAME_2CATE.put("sjb_locations", 1001);
	}
	
	public static enum WEIBO_SEX{
		MALE(1),
		FEMALE(2),
		UNKNOWN(0);
		private final int intValue;
		private WEIBO_SEX(int value){
			intValue = value;
		}
		public int intValue(){
			return intValue;
		}
		private static final Map<Integer, WEIBO_SEX> value2Sex = new HashMap<Integer, WEIBO_SEX>();
		static{
			for(WEIBO_SEX sex : WEIBO_SEX.values())
				value2Sex.put(sex.intValue(), sex);
		}
		public static WEIBO_SEX enumValue(int intValue){
			return value2Sex.get(intValue);
		} 
	}
	
	/**
	 * @param isReverse, 是否倒排顺序
	 */
	@SuppressWarnings("unchecked")
	public static <K, V extends Comparable<? super V>> Entry<K, V>[] sortMapByValue(Map<K, V> map, final boolean isReverse){
		if(map == null)
			return null;
		Entry<K, V>[] entries = map.entrySet().toArray(new Entry[map.size()]);
		Arrays.sort(entries, new Comparator<Entry<K, V>>(){
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				// TODO Auto-generated method stub
				return isReverse ? o2.getValue().compareTo(o1.getValue()) : 
					o1.getValue().compareTo(o2.getValue());
			}
			
		});
		return entries;
	}
}
