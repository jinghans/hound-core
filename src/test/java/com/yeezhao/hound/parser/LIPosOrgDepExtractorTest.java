package com.yeezhao.hound.parser;

import org.junit.Test;

/**
 * Created by SanDomingo on 5/28/14.
 */
public class LIPosOrgDepExtractorTest {
    @Test
    public void testExtract() throws Exception {
        LIPosOrgDepExtractor extractor = LIPosOrgDepExtractor.getInstance();
        String title = "Second-year Master of Science in Finance Student in Clark University";
        String result = extractor.extract(title);
        System.out.println(result);
    }
}
