package com.yiport.utils;

import java.util.*;

/**
 * 敏感词工具类
 *
 */
public class SensitiveWordsUtils
{
    /**
     * 一个敏感词子树结束标志
     */
    private static final String END_FLAG = "@END";

    /**
     * 无效字符
     * <p>防止敏感词中使用一些字符隔开而不被过滤</p>
     */
    public static final String INVALID_CHAR = " \n*-+=#@!$%^&()！￥……，。,./[]{}【】·~`‘’':;：；|、\\1234567890";

    /**
     * 初始化敏感词
     *
     * @param sensitiveWords 敏感词集合
     */
    public static Map<Object, Object> initSensitiveWordsMap(Set<String> sensitiveWords)
    {
        if (sensitiveWords == null || sensitiveWords.isEmpty())
        {
            throw new IllegalArgumentException("敏感词不能为空");
        }
        Map<Object, Object> sensitiveWordsMap = new HashMap<>(sensitiveWords.size());
        String currentWord;
        Map<Object, Object> currentMap;
        Map<Object, Object> subMap;
        Iterator<String> iterator = sensitiveWords.iterator();
        while (iterator.hasNext())
        {
            currentWord = iterator.next();
            if (currentWord == null)
            {  //敏感词不为空
                continue;
            }
            currentMap = sensitiveWordsMap;
            for (int i = 0; i < currentWord.length(); i++)
            {
                char c = currentWord.charAt(i);
                subMap = (Map<Object, Object>) currentMap.get(c);
                if (subMap == null)
                {
                    subMap = new HashMap<>();
                    currentMap.put(c, subMap);
                    currentMap = subMap;
                }
                else
                {
                    currentMap = subMap;
                }
                if (i == currentWord.length() - 1)
                {
                    //如果是最后一个字符，则put一个结束标志，这里只需要保存key就行了，value为null可以节省空间。
                    //如果不是最后一个字符，则不需要存这个结束标志，同样也是为了节省空间。
                    currentMap.put(END_FLAG, "null");
                }
            }
        }
        return sensitiveWordsMap;
    }

    /**
     * 匹配规则
     */
    public enum MatchType
    {
        MIN_MATCH("最小匹配规则"),
        MAX_MATCH("最大匹配规则");

        String desc;

        MatchType(String desc)
        {
            this.desc = desc;
        }
    }

    /**
     * 获取过滤后的文本和匹配到的敏感词
     *
     * @param text      要过滤的文本
     * @param matchType 匹配规则
     * @return 结果
     */
    public static Map<String, Object> getFilterResult(String text, MatchType matchType, Map<Object, Object> sensitiveWordsMap)
    {
        if (text == null || text.trim().length() == 0)
        {
            throw new IllegalArgumentException("文本不能为空");
        }
        Map<String, Object> result = new HashMap<>(2);
        Set<String> sensitiveWords = new HashSet<>();
        for (int i = 0; i < text.length(); i++)
        {
            int sensitiveWordLength = getSensitiveWordLength(text, i, matchType, sensitiveWordsMap);
            if (sensitiveWordLength > 0)
            {
                String sensitiveWord = text.substring(i, i + sensitiveWordLength);
                text = text.replaceAll(sensitiveWord, getPlaceholder(sensitiveWordLength));
                sensitiveWords.add(sensitiveWord);
                if (matchType == MatchType.MIN_MATCH)
                {
                    break;
                }
                i = i + sensitiveWordLength - 1;
            }
        }
        result.put("text", text);
        result.put("word", sensitiveWords);
        return result;
    }

    /**
     * 获取匹配到的敏感词长度
     *
     * @param text       要过滤的文本
     * @param startIndex 起始索引
     * @param matchType  匹配规则
     * @return 结果
     */
    public static int getSensitiveWordLength(String text, int startIndex, MatchType matchType, Map<Object, Object> sensitiveWordsMap)
    {
        if (text == null || text.trim().length() == 0)
        {
            throw new IllegalArgumentException("文本不能为空");
        }
        char currentChar;
        Map<Object, Object> currentMap = sensitiveWordsMap;
        int wordLength = 0;
        boolean endFlag = false;  // 结束标志
        for (int i = startIndex; i < text.length(); i++)
        {
            currentChar = text.charAt(i);
            if (INVALID_CHAR.indexOf(currentChar) > -1 && i != startIndex)
            {
                wordLength++;
                continue;
            }
            Map<Object, Object> subMap = (Map<Object, Object>) currentMap.get(String.valueOf(currentChar));
            if (subMap == null)
            {
                break;
            }
            else
            {
                wordLength++;
                if (subMap.containsKey(END_FLAG))       // 匹配到敏感词结束标志
                {
                    endFlag = true;
                    if (matchType == MatchType.MIN_MATCH)
                    {
                        break;
                    }
                    else
                    {
                        currentMap = subMap;
                    }
                }
                else
                {
                    currentMap = subMap;
                }
            }
        }
        if (!endFlag)
        {
            wordLength = 0;
        }
        else        // 去除敏感词中包含的无效字符
        {
            for (int i = 0; i < wordLength; i++)
            {
                char[] chars = INVALID_CHAR.toCharArray();
                for (int j = 0; j < chars.length; j++)
                {
                    if (text.substring(startIndex, startIndex + wordLength).endsWith(String.valueOf(chars[j])))
                    {
                        wordLength--;
                    }
                }
            }
        }
        return wordLength;
    }

    /**
     * 获取替换占位符
     *
     * @param length 被替换词长度
     * @return 占位符
     */
    public static String getPlaceholder(int length)
    {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++)
        {
            stringBuilder.append('*');
        }
        return stringBuilder.toString();
    }
}
