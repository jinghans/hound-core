package com.yeezhao.hound.parser;

/**
 * Created by SanDomingo on 4/15/14.
 */
public interface BaseExtractor {
    /**
     * 从输入字符串中抽取需要的信息
     * @param input
     * @return
     */
    public String extract(String input);
}
