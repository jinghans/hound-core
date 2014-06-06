package com.yeezhao.hound.parser.assist;

import com.yeezhao.hound.util.NER;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * GoogleScholar用户的title字段分析类
 * 分析得到所在单位和所在地的信息。
 *
 * 分析算法：
 * 使用Stanford的NER包识别出Organization 和 Place两类的词语，放到对应的列表中。
 *
 * User: SanDomingo
 * Date: 4/1/14
 * Time: 3:10 PM
 */
public class GSOrgAddrExtractor {
    private static Logger logger = Logger.getLogger(GSOrgAddrExtractor.class);
    private static GSOrgAddrExtractor instance;

    public static GSOrgAddrExtractor getInstance() {
        if (instance == null) {
            instance = new GSOrgAddrExtractor();
        }
        return instance;
    }

    private GSOrgAddrExtractor() {
    }

    /**
     * 从google scholar的个人title中抽取有用信息
     * 目前包括：单位、地点
     * @param title
     * @return
     */
    public Map<String, List<String>> analyz(String title) {
        // remove non-alphabet
        title = title.replaceAll("~", ", ");
        Map<String, List<String>> result = NER.getInstance().classify(title);
        return result;
    }


}
