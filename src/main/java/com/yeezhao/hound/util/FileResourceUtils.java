package com.yeezhao.hound.util;

import org.apache.hadoop.conf.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class FileResourceUtils {

	public static InputStream getResourceStream(Configuration conf,String name){
		return conf.getConfResourceAsInputStream(conf.get(name));
	}
	
	public static Reader getResourceReader(Configuration conf,String name){
		return conf.getConfResourceAsReader(conf.get(name));
	}
	
	public static List<String> getResourcesAsStrings(Configuration conf,String name) throws IOException{
		BufferedReader br=new BufferedReader(FileResourceUtils.getResourceReader(conf, name));
		try{
			String line=br.readLine();
			List<String> list=new ArrayList<String>();
			for(;line!=null;line=br.readLine()){
				list.add(line);
			}
			return list;
		}finally{
			br.close();
		}
	}

}
