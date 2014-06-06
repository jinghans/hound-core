package com.yeezhao.hound.util;

import com.yeezhao.commons.mrepo.base.ModelRepoConfiguration;
import com.yeezhao.commons.mrepo.util.ModelRepository;
import com.yeezhao.hound.core.HoundConsts;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Name Entity Recognition Class
 * User: SanDomingo
 * Date: 3/21/14
 * Time: 10:37 AM
 */
public class NER {
    Logger logger = Logger.getLogger(this.getClass());
    private InputStream serializedClassifierInputStream;
    private static NER instance;
    private static AbstractSequenceClassifier<CoreLabel> classifier;
    private NER() {
        Configuration conf = new Configuration();
        conf.addResource("analyzer-config.xml");
        try {
            serializedClassifierInputStream = ModelRepository.getHDFSInputStream(ModelRepoConfiguration.getInstance(), conf.get("mdl.ner.name"), Integer.valueOf(conf.get("mdl.ner.version")));
            serializedClassifierInputStream = new GZIPInputStream(serializedClassifierInputStream);
            classifier = (AbstractSequenceClassifier<CoreLabel>) CRFClassifier.getClassifier(serializedClassifierInputStream);
            serializedClassifierInputStream.close();
        } catch (Exception e) {
            logger.error("can't create ner classifier", e);
        }
    }

    public static NER getInstance() {
        while (instance == null) {
            instance = new NER();
        }
        return instance;
    }


    public String getLabeledString(String sentence) {
        String txt = classifier.classifyWithInlineXML(sentence);
        return txt;
    }
    /**
     * 从被NER标注过的字符串中提取有用信息，包括单位信息和地址信息
     * @param sentence
     * @return
     */
    public Map<String, List<String>> classify(String sentence) {
        String txt = classifier.classifyWithInlineXML(sentence);
        List<String> orgs = new ArrayList<String>();
        List<String> locs = new ArrayList<String>();
        // search for organization
        int head = 0, rear;
        while (head < txt.length()) {
            head = txt.indexOf(HoundConsts.ORGLL, head);
            if (head < 0) {
                break;
            }
            head += HoundConsts.ORGLLEN;
            rear = txt.indexOf(HoundConsts.ORGRL, head);
            String org = txt.substring(head, rear);
            orgs.add(org);
        }
        // search for place
        head = 0;
        while (head < txt.length()) {
            head = txt.indexOf(HoundConsts.LOCLL, head);
            if (head < 0) {
                break;
            }
            head += HoundConsts.LOCLLLEN;
            rear = txt.indexOf(HoundConsts.LOCRL, head);
            String loc = txt.substring(head, rear);
            locs.add(loc);
        }
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        result.put(HoundConsts.ORGANIZATION, orgs);
        result.put(HoundConsts.LOCATION, locs);
        return result;
    }
}
