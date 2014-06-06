package com.yeezhao.hound.ontology.tpclassifier;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.yeezhao.hound.ontology.OntoUtil;

public class TreepathClassifierFactory implements TreepathClassifiable {

    private static final Logger LOG = Logger.getLogger(TreepathClassifierFactory.class);
    private Map<Integer, TreepathClassifiable> tpClssifierMap = new HashMap<Integer, TreepathClassifiable>();
    private String knowledgeDir;
    private Configuration conf = null;
    private InputStream keywordsIs;
    private InputStream treepathIs;

    public TreepathClassifierFactory(String knowledgeDir) {
        this.knowledgeDir = knowledgeDir;
    }
    
    public TreepathClassifierFactory(InputStream keywordsIs, InputStream treepathIs) {
    	this.keywordsIs = keywordsIs;
    	this.treepathIs = treepathIs;
    }

    public void setConfig(Configuration conf) {
        this.conf = conf;
    }

    /**
     * @return 返回最佳匹配treepath(可能有多个)，如果没有找到对应的treepath，返回空的list。
     */
    public List<String> getCandidateTreepath(int category, Map<String, String> msgInfoMap) {
        TreepathClassifiable classifier = getTPClassifier(category);
        return classifier.getCandidateTreepath(category, msgInfoMap);
    }

    private TreepathClassifiable getTPClassifier(int category) {
        //按频道初始化combiner，组合规则。
        TreepathClassifiable classifier = null;
        if (!tpClssifierMap.containsKey(category)) {
            if (!OntoUtil.CATE_2NAME.containsKey(category)) {
                LOG.info("category : " + category + " has no prefixs setted.");
            } else {
                if (conf == null) {
                	if(knowledgeDir != null){
	                    try {
	                        classifier = new ClothTreepathClassifier(
	                                new FileInputStream(knowledgeDir + "/" + OntoUtil.CATE_2NAME.get(category) + OntoUtil.KEYWORD_SUFFIX),
	                                new FileInputStream(knowledgeDir + "/" + OntoUtil.CATE_2NAME.get(category) + OntoUtil.TREEPATH_SUFFIX));
	                        tpClssifierMap.put(category, classifier);
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
                	} else if(keywordsIs != null){
                		try {
	                        classifier = new ClothTreepathClassifier(keywordsIs, treepathIs);
	                        tpClssifierMap.put(category, classifier);
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
                	}
                } else {
                    try {
                        FileSystem fs = FileSystem.get(conf);
                        classifier = new ClothTreepathClassifier(
                                fs.open(new Path(knowledgeDir + "/" + com.yeezhao.hound.ontology.OntoUtil.CATE_2NAME.get(category) + OntoUtil.KEYWORD_SUFFIX)),
                                fs.open(new Path(knowledgeDir + "/" + com.yeezhao.hound.ontology.OntoUtil.CATE_2NAME.get(category) + OntoUtil.TREEPATH_SUFFIX)));
                        tpClssifierMap.put(category, classifier);

                    } catch (IOException e) {
                        e.printStackTrace();
                        LOG.error("", e);
                        return null;
                    }

                }
            }
        } else {
            classifier = tpClssifierMap.get(category);
        }
        return classifier;
    }

}
