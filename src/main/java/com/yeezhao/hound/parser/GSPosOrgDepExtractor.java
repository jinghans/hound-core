package com.yeezhao.hound.parser;

import com.yeezhao.hound.core.HoundConsts;
import com.yeezhao.hound.parser.assist.GSDepartmentExtractor;
import com.yeezhao.hound.parser.assist.GSOrgAddrExtractor;
import com.yeezhao.hound.parser.assist.GSPositionExtractor;
import org.apache.log4j.Logger;

import java.util.*;

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
public class GSPosOrgDepExtractor implements BaseExtractor {
    private static Logger logger = Logger.getLogger(GSPosOrgDepExtractor.class);
    private static GSPosOrgDepExtractor instance;

    public static GSPosOrgDepExtractor getInstance() {
        if (instance == null) {
            instance = new GSPosOrgDepExtractor();
        }
        return instance;
    }

    private GSPosOrgDepExtractor(){
    }

    /**
     * 请使用extractAll方法
     * @param input
     * @return
     */
    @Deprecated
    @Override
    public String extract(String input) {
        return Arrays.toString(extractAll(input));
    }

    /**
     * 从title中抽取有效信息
     * @param title
     * @return String[4],顺序依次为position, organization, department, address
     */
    public String[] extractAll(String title) {
        // remove extra single quotes
        if (title.startsWith("'") && title.endsWith("'")) {
            title = title.substring(1, title.length() - 1);
        }
        String[] parts = title.split("~");
        int falseCounter = parts.length;
        Boolean[] partFlag = new Boolean[parts.length];
        for (int i = 0; i < parts.length; i++) {
            partFlag[i] = false;
        }
        Set<String> infoNameSet = getInfoNameSet();
        Map<String, String> resultMap = new HashMap<String, String>(3);
        resultMap.put(HoundConsts.POSITION, HoundConsts.EMPTY);
        resultMap.put(HoundConsts.DEPARTMENT, HoundConsts.EMPTY);
        resultMap.put(HoundConsts.ORGANIZATION, HoundConsts.EMPTY);
        resultMap.put(HoundConsts.LOCATION, HoundConsts.EMPTY);
        String position, organization, department, address;
        List<String> organizations = new ArrayList<String>();
        List<String> addresses = new ArrayList<String>();

        for (int i = 0; i < parts.length; i++) {
            String apart = parts[i];
            // extract position
            position = GSPositionExtractor.getInstance().extract(apart);
            if (!position.isEmpty()) {
                infoNameSet.remove(HoundConsts.POSITION);
                resultMap.put(HoundConsts.POSITION, position);
                partFlag[i] = true;
                falseCounter--;
                // extract department further
                department = GSDepartmentExtractor.getInstance().extract(apart);
                if (!department.isEmpty()) {
                    infoNameSet.remove(HoundConsts.DEPARTMENT);
                    resultMap.put(HoundConsts.DEPARTMENT, department);
                }
            } else { // extract organization
                Map<String, List<String>> result = GSOrgAddrExtractor.getInstance().analyz(apart);
                if (result.containsKey(HoundConsts.ORGANIZATION)) {
                    List<String> orgs = result.get(HoundConsts.ORGANIZATION);
                    if (!orgs.isEmpty()) {
                        organizations.addAll(orgs);
                        infoNameSet.remove(HoundConsts.ORGANIZATION);
                        partFlag[i] = true;
                        falseCounter--;
                    }
                }
                if (result.containsKey(HoundConsts.LOCATION)) {
                    List<String> locs = result.get(HoundConsts.LOCATION);
                    if (!locs.isEmpty()) {
                        addresses.addAll(locs);
                        // no need to remove LOCATION from infoNameSet, because it seldom occurs
                        partFlag[i] = true;
                        falseCounter--;
                    }
                }
            }
        }
        if (!organizations.isEmpty()) {
            String organizationStr = organizations.toString();
            organization = organizationStr.substring(1, organizationStr.length() - 1);
            resultMap.put(HoundConsts.ORGANIZATION, organization);
        }
        if (!addresses.isEmpty()) {
            String addressStr = addresses.toString();
            address = addressStr.substring(1, addressStr.length() - 1);
            resultMap.put(HoundConsts.LOCATION, address);
        }
        // 当已经确认position, organization, department三个中的两个，则将未确认的指定为第三个
        if (infoNameSet.size() == 1 && falseCounter == 1) {
            // find unlabeled part
            for (int i = 0; i < parts.length; i++) {
                if (partFlag[i] == false) {
                    for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                        if ((!entry.getKey().equals(HoundConsts.LOCATION)) && entry.getValue().isEmpty()) {
                            resultMap.put(entry.getKey(), parts[i]);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return new String[]{resultMap.get(HoundConsts.POSITION), resultMap.get(HoundConsts.ORGANIZATION), resultMap.get(HoundConsts.DEPARTMENT), resultMap.get(HoundConsts.LOCATION)};
    }

    private Set<String> getInfoNameSet() {
        Set<String> aset = new HashSet<String>();
        aset.add(HoundConsts.POSITION);
        aset.add(HoundConsts.ORGANIZATION);
        aset.add(HoundConsts.DEPARTMENT);
        return aset;
    }
}
