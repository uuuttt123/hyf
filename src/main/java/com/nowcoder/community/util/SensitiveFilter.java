package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }
            tempNode = subNode;

            if(i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    public String filter(String text) {
        if(text == null) return null;
        TrieNode temp = rootNode;
        int startindex = 0;
        int endindex = 0;
        StringBuilder sb = new StringBuilder();
        while(endindex < text.length()) {
            char c = text.charAt(endindex);
            if(isSymbol(c)) {
                if(temp == rootNode) {
                    sb.append(c);
                    startindex ++;
                }
                endindex ++;
                continue;
            }
            TrieNode nextNode = temp.getSubNode(c);
            if(nextNode == null) {
                sb.append(text.charAt(startindex));
                startindex ++;
                endindex = startindex;
                temp = rootNode;
            }else {
                temp = nextNode;
                if(temp.isKeywordEnd()) {
                    temp = rootNode;
                    sb.append(REPLACEMENT);
                    startindex = ++endindex;
                }
                else if(endindex < text.length() - 1) endindex ++;
            }
        }
        sb.append(text.substring(startindex));
        return sb.toString();
    }

    private boolean isSymbol(Character c) {
        //不在ascii表内且不属于东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    private class TrieNode {
        private boolean isKeywordEnd = false;

        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

    }
}
