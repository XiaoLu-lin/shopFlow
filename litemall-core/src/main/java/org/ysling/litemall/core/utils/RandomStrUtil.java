package org.ysling.litemall.core.utils;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 随机生成字符串工具类
 **/
public class RandomStrUtil {
    /**
     * 随机产生类型枚举
     */
    public enum TYPE {
        /**
         * 小字符型
         */
        LETTER,
        /**
         * 大写字符型
         */
        CAPITAL,
        /**
         * 数字型
         */
        NUMBER,
        /**
         * 大+小字符 型
         */
        LETTER_CAPITAL,
        /**
         * 小字符+数字 型
         */
        LETTER_NUMBER,
        /**
         * 大写字符+数字
         */
        CAPITAL_NUMBER,
        /**
         * 大+小字符+数字 型
         */
        LETTER_CAPITAL_NUMBER,
    }

    private static final String[] LOWERCASE = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private static final String[] CAPITAL = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private static final String[] NUMBER = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    /**
     * 静态随机数
     */
    private static final SecureRandom RANDOM = new SecureRandom();


    /**
     * 获取当前时间字符串
     * @return 17位字符串
     */
    public static String getDateSn() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        LocalDateTime localDateTime = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        return df.format(localDateTime);
    }

    /**
     * 获取随机组合码
     *
     * @param size  位数
     * @param type 类型
     */
    public static String getRandom(int size, TYPE type) {
        ArrayList<String> temp = new ArrayList<>();
        StringBuilder code = new StringBuilder();
        return getCode(size , type , code , temp);
    }

    /**
     * 获取当前时间字符串17位 + 获取随机组合码
     *
     * @param size  总位数位数 17+？
     * @param type  类型
     */
    public static String getRandom(int size , TYPE type , boolean is) {
        ArrayList<String> temp = new ArrayList<>();
        StringBuilder code = new StringBuilder();
        if (is){
            if (size > 17){
                code.append(getDateSn());
                size = size - 17;
            }
        }
        return getCode(size , type , code , temp);
    }


    private static String getCode(int size, TYPE type, StringBuilder code, ArrayList<String> temp){
        switch (type) {
            case LETTER:
                temp.addAll(Arrays.asList(LOWERCASE));
                break;
            case CAPITAL:
                temp.addAll(Arrays.asList(CAPITAL));
                break;
            case NUMBER:
                temp.addAll(Arrays.asList(NUMBER));
                break;
            case LETTER_CAPITAL:
                temp.addAll(Arrays.asList(LOWERCASE));
                temp.addAll(Arrays.asList(CAPITAL));
                break;
            case LETTER_NUMBER:
                temp.addAll(Arrays.asList(LOWERCASE));
                temp.addAll(Arrays.asList(NUMBER));
                break;
            case CAPITAL_NUMBER:
                temp.addAll(Arrays.asList(CAPITAL));
                temp.addAll(Arrays.asList(NUMBER));
                break;
            case LETTER_CAPITAL_NUMBER:
                temp.addAll(Arrays.asList(LOWERCASE));
                temp.addAll(Arrays.asList(CAPITAL));
                temp.addAll(Arrays.asList(NUMBER));
                break;
            default:
        }
        for (int i = 0; i < size; i++) {
            code.append(temp.get(RANDOM.nextInt(temp.size())));
        }
        return code.toString();
    }

    /**
     * 随机排序字符串
     */
    public static String shuffle(String code) {
        List<String> list=Arrays.asList(code.split(""));
        Collections.shuffle(list);
        StringBuilder buffer = new StringBuilder();
        for(String s:list){
            buffer.append(s);
        }
        return buffer.toString();
    }

    /**
     * 获取2位数字4位字母随机组合
     */
    public static String getCode() {
        String number = RandomStrUtil.getRandom(2, TYPE.NUMBER);
        String capital = RandomStrUtil.getRandom(4, TYPE.CAPITAL);
        return RandomStrUtil.shuffle(number + capital);
    }

    /**
     * 获取数字与字母随机组合
     * @param numberSize  数字数量
     * @param capitalSize 字母数量
     * @return 随机字符串
     */
    public static String getCode(Integer numberSize , Integer capitalSize) {
        String number = RandomStrUtil.getRandom(numberSize, TYPE.NUMBER);
        String capital = RandomStrUtil.getRandom(capitalSize, TYPE.CAPITAL);
        return RandomStrUtil.shuffle(number + capital);
    }

//    public static void main(String[] args) {
//        String code = getCode(16, 16);
//        System.out.println(code);
//    }
}

