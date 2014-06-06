package com.yeezhao.hound.parser.assist;

import com.yeezhao.hound.core.HoundConsts;
import com.yeezhao.hound.parser.BaseExtractor;
import org.apache.log4j.Logger;

/**
 * GoogleScholar职位抽取类
 * 从给定的Title信息（来源于google scholar）中抽取一个人的职位信息。
 * 例如：
 * title=‘Associate Professor, Computer Science, Univ. of California at Santa Barbara’
 * 解析得到:
 * position=‘Associate Professor'
 *
 * 抽取算法：
 * 基于职位关键词的匹配抽取。
 * 步骤：
 * 1. 找到匹配的关键词
 * 2. 从该关键词开始，向前选取到分隔符号(~)的位置部分，作为职位。
 *
 * Created by SanDomingo on 4/15/14.
 */
public class GSPositionExtractor implements BaseExtractor {
    private static Logger logger = Logger.getLogger(GSPositionExtractor.class);
    public static final String[] positions = new String[]{"professor","researcher", "scientist",
            "fellow", "assistant", "student", "phd", "engineer", "physicist", "postdoctoral", "profesor", "prof "};
    private static GSPositionExtractor instance;
    public static GSPositionExtractor getInstance() {
        if (instance == null) {
            instance = new GSPositionExtractor();
        }
        return instance;
    }

    private GSPositionExtractor(){
    }
    /**
     * 从title中抽取个人职位
     * @param title
     * @return
     */
    @Override
    public String extract(String title) {
        // remove "."(for Ph.d)
        String rawTitle = title.replace(".", "");
        title = title.replace(".", "").toLowerCase();
        // get the role word position
        int pos = -1;
        int positionLen = 0;
        for (String position : positions) {
            pos = title.indexOf(position);
            if (pos >= 0) {
                positionLen = position.length();
                break;
            }
        }
        // extract the role
        if (pos < 0) {
            return HoundConsts.EMPTY;
        } else {
            pos += positionLen;
            // in case of position is not in the first part of the title
            // e.g 'Director~ Professor of Program in Bioinformatics and Integrative Biology~ University of Massachusetts Medical School'
            int pre = title.indexOf("~");
            if ( pre >= pos) {
                pre = 0;
            } else{
                pre += 1; // skip the "~"
            }
            return rawTitle.substring(pre, pos).trim();
        }
    }
}
