package com.yeezhao.hound.parser.assist;

import com.yeezhao.hound.core.HoundConsts;
import com.yeezhao.hound.parser.BaseExtractor;
import org.apache.log4j.Logger;

/**
 * GoogleScholar所在部门信息抽取类
 * 从GSPosExtractor的extract结果中抽取出院系信息。
 * (目标是改进成：从给定的Title信息（来源于google scholar）中抽取一个人的所属院系信息。)
 *
 * 例如：
 * title=‘Associate Professor, Computer Science, Univ. of California at Santa Barbara’
 * 解析得到:
 * department=Computer Science
 *
 * 抽取算法：
 * GoogleScholar上的用户title中部门一般和职位是写在一起的，
 * 包含部门信息的描述一般遵循这样的格式：[position][of/in][department]
 * 步骤：
 * 1. 找到介词(of/in)的位置
 * 2. 向后选择到该部分结束作为所在部门信息
 *
 * Created by SanDomingo on 4/15/14.
 */
public class GSDepartmentExtractor implements BaseExtractor {
    private static Logger logger = Logger.getLogger(GSDepartmentExtractor.class);
    public static final String PREP_OF = " of ";
    public static final String PREP_IN = " in ";
    private static GSDepartmentExtractor instance;
    public static GSDepartmentExtractor getInstance() {
        if (instance == null) {
            instance = new GSDepartmentExtractor();
        }
        return instance;
    }

    private GSDepartmentExtractor(){
    }

    /**
     *  抽取包含职位的一句中的所在院系信息
     *
     *  目前存在问题：因为无法NER区别出学校和专业，所以目前只处理单一规定格式
     *  [position] of/in [department]
     *
     * @param positionPart
     * @return
     */
    public String extract(String positionPart) {
        int prepPos = -1;
        prepPos = positionPart.indexOf(PREP_OF);
        if (prepPos < 0) {
            prepPos = positionPart.indexOf(PREP_IN);
        }
        if (prepPos < 0) {
            return HoundConsts.EMPTY;
        }
        return positionPart.substring(4+prepPos).trim();
    }

}
