package com.yeezhao.hound.ontology.tpclassifier;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import com.yeezhao.hound.ontology.OntoUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: congzicun
 * Date: 14/8/13
 * Time: 6:16 PM
 */
public class TestTreepath{
    public static void main(String[] args){
        TreepathClassifierFactory classifier = new TreepathClassifierFactory("/yeezhao/amkt/resources/analyz/model/weibo_knowledge/");
        Configuration conf = HBaseConfiguration.create();
        classifier.setConfig(conf);
        System.out.println(conf);
        Map<String, String> locMap = new HashMap<String, String>();
        locMap.put(OntoUtil.WEIBO_TPCLS_ATTRIBUTES.TEXT, "长春");
        List<String> paths = classifier.getCandidateTreepath(1001, locMap);
        System.out.println(paths.get(0));
    }

}
