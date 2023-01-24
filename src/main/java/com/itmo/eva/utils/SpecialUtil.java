package com.itmo.eva.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
/**
 * 特殊字符操作工具类
 *
 * @author chenjiahan
 */
public class SpecialUtil {
    private static final String DEFAULT_QUERY_REGEX = "[_!$^&*+=|{}';'\",<>/?~！#￥%……&*——|{}【】‘；：”“'。，、？]";
    /**
     * 获取查询过滤的非法字符
     *
     * @return
     */
    protected static String getQueryRegex() {
        return DEFAULT_QUERY_REGEX;
    }
 
    /**
     * 判断集合中是否有特殊字符
     * @param list
     * @return  true为包含，false为不包含
     */
    public static boolean isSpecialList(ArrayList<String> list){
        boolean flag=false;
        flag=list.stream().filter(str->isSpecialChar(str)).findAny().isPresent();
        return flag;
    }
 
    /**
     * 判断是否含有特殊字符
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = getQueryRegex()+"|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
 
    /**
     * 判断查询参数中是否以特殊字符开头
     *
     * @param value
     * @return  特殊字符开头则返回true，否则返回false
     */
    public static boolean  specialSymbols(String value) {
        if ((value).isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile(getQueryRegex());
        Matcher matcher = pattern.matcher(value);
        char[] specialSymbols = getQueryRegex().toCharArray();
        // 是否以特殊字符开头
        boolean isStartWithSpecialSymbol = false;
        for (int i = 0; i < specialSymbols.length; i++) {
            char c = specialSymbols[i];
            if (value.indexOf(c) == 0) {
                isStartWithSpecialSymbol = true;
                break;
            }
        }
        return matcher.find() && isStartWithSpecialSymbol;
    }
 
 
    /**
     * 判断字符串中特殊字符的首次开始下标
     * @param targetStr 目标字符串
     * @return 返回对应下标,从1开始
     */
    public static int findSpecialIndex(String targetStr){
        int start=-1;
        Pattern pattern=Pattern.compile(getQueryRegex());
        Matcher matcher=pattern.matcher(targetStr);
        if (matcher.find()){
            //匹配到结果在源字符串的起始索引
            start=matcher.start();
        }
        return start;
    }

    /**
     * 验证字符串是否为指定日期格式
     * @param oriDateStr 待验证字符串
     * @param pattern 日期字符串格式, 例如 "yyyy-MM-dd"
     * @return 有效性结果, true 为正确, false 为错误
     */
    public static boolean dateStrIsValid(String oriDateStr, String pattern) {
        if (StringUtils.isBlank(oriDateStr) || StringUtils.isBlank(pattern)) {
            return true;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        try {
            Date date = dateFormat.parse(oriDateStr);
            return !oriDateStr.equals(dateFormat.format(date));
        } catch (ParseException e) {
            return true;
        }
    }




}