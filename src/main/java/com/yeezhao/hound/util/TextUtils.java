package com.yeezhao.hound.util;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * 文本字符串处理类
 * Created by SanDomingo on 5/26/14.
 */
public class TextUtils {
    private static Logger logger = Logger.getLogger(TextUtils.class);

    private static InputStream ensentInputStream;
    private static SentenceDetectorME sdeector;
    static {
        Configuration conf = new Configuration();
        conf.addResource("analyzer-config.xml");
        ensentInputStream = conf.getConfResourceAsInputStream(conf.get("file.en.sentence"));
        try {
            SentenceModel model = new SentenceModel(ensentInputStream);
            sdeector = new SentenceDetectorME(model);
        } catch (Exception e) {
            logger.error("load en-sent file failed!", e);
        }
        try {
            ensentInputStream.close();
        } catch (IOException e) {
            logger.error("error while closing inputstream.", e);
        }
    }

    /**
     * 将输入文本裁剪成不超过指定长度的文本。
     * 裁剪通过对文本截尾来实现，同时保证截取的最小单位为一个句子，
     * 即一句话不会被从中间断开。
     *
     * @param longText 需要被裁剪的长文本
     * @param maxLength 裁剪后允许的最大长度
     * @return 不超过maxLength的文本字符串
     */
    public static String tailorText(String longText, int maxLength) {
        longText = longText.trim();
        int textLen = longText.length();
        if (textLen <= maxLength) {
            return longText;
        }

        // long text needs to be tailored to fit the maxLength
        String[] sentences = sdeector.sentDetect(longText);
        StringBuilder sb = new StringBuilder();
        for (String sentence : sentences) {
            int curSentLen = sentence.length();
            int curTextLen = sb.length();
            if (curTextLen + curSentLen <= maxLength) {
                sb.append(sentence);
            } else {
                break;
            }
        }
        return sb.toString();
    }

    /**
     * 英文长文本分块处理，将长文本划分成若干不超过指定长度的短文本
     * @param longText 要分块的长文本
     * @param maxLength 分块后每一块文本的最大长度(字符个数)
     * @return 按原文本顺序排列的切块后的一个文本字符串列表
     */
    public static List<String> splitText(String longText, int maxLength) {
        List<String> chunks = new LinkedList<String>();
        // split by paragraph first
        String[] paragraphs = longText.split("\n");
        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) {
                continue;
            }
            int pLen = paragraph.length();
            if (pLen >= maxLength) {  // meet a long paragraph
                String[] sentences = sdeector.sentDetect(paragraph);
                StringBuilder sb = new StringBuilder();
                for (String sentence : sentences) {
                    sentence = sentence.trim();
                    if (sentence.isEmpty()) {
                        continue;
                    }
                    int curStrLen = sb.length();
                    int curSentLen = sentence.length();
                    if (curSentLen + curStrLen < maxLength) {
                        sb.append(sentence);
                    } else {
                        chunks.add(sb.toString());
                        sb = new StringBuilder();
                        if (curSentLen < maxLength) {
                            sb.append(sentence);
                        } else { // when a sentence exceeds the maxLength
                            ; // skip it
                        }
                    }
                }
                // sb may have sth. left
                if (sb.length() > 0) {
                    chunks.add(sb.toString());
                }
            } else {  // meet a short paragraph
                chunks.add(paragraph);
            }
        }
        return chunks;
    }
}
