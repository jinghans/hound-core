package com.yeezhao.hound.core;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.hadoop.conf.Configuration;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class HoundUtils {
	
	/**
	 * 读取资源文件scholar_location.xml
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document readLocationFile() throws ParserConfigurationException, SAXException, IOException{
		Configuration conf = new Configuration();
		conf.addResource("analyzer-config.xml");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder  db = dbf.newDocumentBuilder();
		return db.parse(conf.getConfResourceAsInputStream(conf.get("file.scholar.location")));
	}
}
