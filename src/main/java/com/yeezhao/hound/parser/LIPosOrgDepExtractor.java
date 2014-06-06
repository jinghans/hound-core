package com.yeezhao.hound.parser;

import com.yeezhao.hound.core.HoundConsts;
import com.yeezhao.hound.util.NER;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LinkedIn 职位，单位，部门抽取类
 * 从给定的Title信息（来源于LinkedIn）中抽取一个人的职位信息和单位信息。
 * 例如：
 * title='Vice President of Marketing Small & Medium Business (SMB) at Infogroup'
 * 解析得到:
 * position=‘Vice President'
 * organization='Infogroup'
 * department='Marketing Small & Medium Business (SMB)'
 *
 * 抽取算法：
 * LinkedIn上的title数据是根据用户自己填写的部分数据加上分隔符拼成一句的。
 * 格式比较统一：
 * [position][分隔符1][department][分隔符2][organization]
 * [position][分隔符1][organization]
 * [position]
 * 目前的分割符号包括：连字符，介词，逗号
 * 具体步骤：
 * 0. 如果没有分隔符号，则全部设置为position
 * 1. 找到分隔符2所在位置pos2
 * 2. pos2后面部分判定为organization信息
 * 3. 找到分隔符1所在位置pos1
 *  3.a. if pos1 != -1:
 *      pos1前面部分为position，后面部分为department
 *  3.b. else
 *      全部为position
 *
 * Problem: 目前还没有解决 “MA Contemporary Art | Sotheby's Institute of Art London”这种类型的数据抽取
 * Created by SanDomingo on 4/15/14.
 */
public class LIPosOrgDepExtractor implements BaseExtractor {
    private static Logger logger = Logger.getLogger(LIPosOrgDepExtractor.class);
    public static final String PREP_OF = " of ";
    public static final String PREP_AT = " at ";
    public static final String PREP_IN = " in ";
    public static final String COMMA = ",";
    public static final String HYPHEN = " - ";
    private static LIPosOrgDepExtractor instance;

    public static LIPosOrgDepExtractor getInstance() {
        if (instance == null) {
            instance = new LIPosOrgDepExtractor();
        }
        return instance;
    }

    private LIPosOrgDepExtractor(){
    }

    /**
     * 从title中抽取职位，工作单位和部门信息
     * @param title
     * @return
     */
    public Map<String, String> extractAll(String title) {
        title = title.trim() + " ";
        // deal with the case that title='at GMP International Inc.'
        if (title.startsWith("at ") || title.startsWith("in ")) {
            title = " " + title;
        }
        Map<String, String> result = new HashMap<String, String>();
        result.put(HoundConsts.POSITION, HoundConsts.EMPTY);
        result.put(HoundConsts.ORGANIZATION, HoundConsts.EMPTY);
        result.put(HoundConsts.DEPARTMENT, HoundConsts.EMPTY);

        int pos2;
        pos2 = title.lastIndexOf(PREP_AT);
        if (pos2 >= 0) {
            String organization = title.substring(pos2 + PREP_AT.length()).trim();
            result.put(HoundConsts.ORGANIZATION, organization);
        }
        if (pos2 < 0)
            pos2 = title.length();
        int[] pos1 = getPos1(title, pos2);
        if (pos1[0] >= 0) {
            String department = title.substring(pos1[1], pos2).trim();
            String position = title.substring(0, pos1[0]);
            result.put(HoundConsts.DEPARTMENT, department);
            result.put(HoundConsts.POSITION, position);
        } else {
            String position = title.substring(0, pos2).trim();
            result.put(HoundConsts.POSITION, position);
        }

        // remove organization part in the department field when there is no organization part in the result set
        if ((!result.get(HoundConsts.DEPARTMENT).isEmpty()) && (result.get(HoundConsts.ORGANIZATION).isEmpty())) {
            String dumpDepartment = result.get(HoundConsts.DEPARTMENT);
            // find organization by ner
            List<String> orgs = NER.getInstance().classify(dumpDepartment).get(HoundConsts.ORGANIZATION);
            if (!orgs.isEmpty()) {
                String organization = orgs.get(0);
                // remove organization from department
                int orgPos = dumpDepartment.indexOf(organization);
                String department = removeOrg(dumpDepartment, orgPos, organization.length());
                department = removeExtraPreposition(department);
                result.put(HoundConsts.ORGANIZATION, organization);
                result.put(HoundConsts.DEPARTMENT, department);
            }
        }
        return result;
    }

    /**
     * 删除多余的介词
     * @param department
     * @return
     */
    private String removeExtraPreposition(String department) {
        department = department.replace(",", " ").trim();
        if (department.startsWith("in ") || department.startsWith("of ") || department.startsWith("at ")) {
            return department.substring(3);
        }
        if (department.endsWith(" in") || department.endsWith(" of") || department.endsWith(" at")) {
            return department.substring(0, department.length()-3);
        }
        return department.trim();
    }

    /**
     * 将工作单位和部门在一起的字段中的单位信息移除，并去除相关的连接词语
     * @param dumpDepartment
     * @param orgPos
     * @param length
     * @return
     */
    private String removeOrg(String dumpDepartment, int orgPos, int length) {
        // org in head
        if (orgPos + length < dumpDepartment.length()) {
            return dumpDepartment.substring(orgPos + length);
        } else {
            return dumpDepartment.substring(0, orgPos);
        }
    }

    /**
     * 获得分隔符1所在的位置
     * @param title
     * @param pos2
     * @return pos[2], pos[0]=start(included), pos[1]=end(excluded)
     */
    private int[] getPos1(String title, int pos2) {
        int posStart,posEnd;
        if (pos2 >= 0) {
            title = title.substring(0, pos2);
        }
        posStart = title.indexOf(HYPHEN);
        posEnd = posStart + HYPHEN.length();
        if (posStart < 0) {
            posStart = title.indexOf(PREP_OF);
            posEnd = posStart + PREP_OF.length();
        }
        if (posStart < 0) {
            posStart = title.indexOf(COMMA);
            posEnd = posStart + COMMA.length();
        }
        if (posStart < 0) {
            posStart = title.indexOf(PREP_IN);
            posEnd = posStart + PREP_IN.length();
        }
        if (posStart < 0) {
            posEnd = 0;
        }
        int[] result = {posStart, posEnd};
        return result;
    }

    @Deprecated
    @Override
    public String extract(String input) {
        return extractAll(input).toString();
    }
}
