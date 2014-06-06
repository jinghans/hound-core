package com.yeezhao.hound.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.yeezhao.hound.core.GetHtml;

/**
 * 利用google的翻译，将英文翻译为中文。
 * @author hans
 *
 */
public class TranslateEn2Ch {
	
	private static final String GL_TRANS_PREFIX = "http://translate.google.cn/translate_a/t?client=t&sl=en&tl=zh-CN&hl=zh-CN&sc=2&ie=UTF-8&oe=UTF-8&pc=1&oc=1&otf=1&ssel=0&tsel=0&q=";
	
	/**
	 * 安全的翻译长度是<1800
	 * @param englishParagraph
	 * @return 如果翻译失败则返回为null。
	 */
	public String translateP(String englishParagraph) {
		String query = GL_TRANS_PREFIX;
		try{
			String urlpart = URLEncoder.encode(englishParagraph, "utf8");
			query += urlpart;
			GetHtml getter = new GetHtml();
			String rs = getter.getHtml(query);
			
			StringBuffer chRes = new StringBuffer();
			int pos = rs.indexOf("]]");
			String substr = rs.substring(1, pos + 2);
			Gson gson = new Gson();
			@SuppressWarnings("unchecked")
			List<ArrayList<String>> sentences = gson.fromJson(substr, List.class);
			for(ArrayList<String> sentence : sentences){
				chRes.append(sentence.get(0));
			}
			
			return chRes.toString().trim();
		} catch(Exception e){
			return null;
		}
	}
}
