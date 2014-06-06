package com.yeezhao.hound.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhibin on 14-5-19.
 */
public class GoogleSev {
	private GetHtml htmlGetter;
	
	public GoogleSev(){
		htmlGetter = new GetHtml();
	}
	
    public Map<String,String> getInfoGoogle(String info)
    {
        HashMap<String,String> infomap = new HashMap<String, String>();
        String prefix = "https://maps.google.com/maps?f=q&source=s_q&hl=en&geocode=&abauth=5378231dJ_MD5LRPPeJEnllLbkqzZS7ZdK4&authuser=0&q=";
        String suffix = "&aq=&vps=5&ei=ZyR4U-mMGs_v8QWmmYGIAw";
        String infomation = info;
        String[] all = infomation.split(" ");
        for(int i =0;i<all.length;i++)
        {
            if(i==all.length-1){prefix = prefix + all[i];}
            else{prefix = prefix + all[i]+"%2C+";}
        }
        prefix += suffix;
        String content = null;
        try{
        	content = htmlGetter.getHtml(prefix);
        } catch(Exception e){
        	e.printStackTrace();
        }
        if(content != null){
	        Document doc = Jsoup.parse(content);
	        Elements nodes = doc.select("span");
	        for (Element el : nodes)
	        {
	            String classname = el.attr("class");
	            if(classname.equals("pp-headline-item pp-headline-address"))
	            {
                 if(!infomap.containsKey("address"))
                 infomap.put("address", el.text());
                }
	            if(classname.equals("telephone"))
                {
                    if(!infomap.containsKey("phone"));
                    {
                        String phone = el.text().replace("()","");
                        infomap.put("phone", phone);
                    }
                }
	            if(classname.equals("pp-place-title"))
                {
                    if(!infomap.containsKey("place_title"));
                    infomap.put("place_title", el.text());
                }
	        }
        }
        return  infomap;
    }

}
