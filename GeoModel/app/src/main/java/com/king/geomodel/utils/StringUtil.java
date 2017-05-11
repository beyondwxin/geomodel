package com.king.geomodel.utils;

/**
 * 文件名	：StringUtil.java
 * 创建日期	：2012-10-15
 * Copyright (c) 2003-2012 北京联龙博通
 * <p/>
 * All rights reserved.
 */

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * StringUtil.java 字符串处理工具类
 *
 * @version 1.0
 */
public class StringUtil {

    private static final String TAG = "StringUtil";
    private static String currentString = "";
    public static final String URL_PREFIX = "http://";
    public static final String URL_PREFIXs = "https://";

    /**
     * 检测字符串是否为空或无内容
     *
     * @param srcString
     * @return
     */
    public static boolean isNull(String srcString) {
        return !(srcString != null && !srcString.equals(""));
    }


    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


    /**
     * 类型转换
     *
     * @param str
     * @return
     */
    public static Integer getIntFromStr(String str) {
        Integer f = 0;
        try {
            f = Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 判断对象或对象数组中每一个对象是否为空: 对象为null，字符序列长度为0，集合类、Map为empty
     *
     * @param obj
     * @return
     */
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null)
            return true;

        if (obj instanceof CharSequence)
            return ((CharSequence) obj).length() == 0;

        if (obj instanceof Collection)
            return ((Collection) obj).isEmpty();

        if (obj instanceof Map)
            return ((Map) obj).isEmpty();

        if (obj instanceof Object[]) {
            Object[] object = (Object[]) obj;
            boolean empty = true;
            for (int i = 0; i < object.length; i++)
                if (!isNullOrEmpty(object[i])) {
                    empty = false;
                    break;
                }
            return empty;
        }
        return false;
    }

    /**
     * 字符串是否全数字（无符号、小数点）
     *
     * @param str
     * @return
     */
    public static boolean isDigit(String str) {
        char c;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c < '0' || c > '9')
                return false;
        }
        return true;
    }



    /***
     * MD5加密 生成32位md5码
     * @param inStr 待加密字符串
     * @return 返回32位md5码
     */
    public static String md5Encode(String inStr)  {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray = inStr.getBytes("UTF-8");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }


    }
    /**
     * 自动播放文本
     *
     * @param target
     * @param start
     * @param end
     * @param duration
     * @param scale
     */
    public static void autoTextPlay(final TextView target, final float start,
                                    final float end, long duration, final int scale) {

        ValueAnimator animator = ValueAnimator.ofFloat(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                String temp = "###,###,###,###,###,###,###,##0";
                if (scale > 0)
                    temp += ".";
                for (int i = 0; i < scale; i++)
                    temp += "0";
                FloatEvaluator evalutor = new FloatEvaluator();
                DecimalFormat format = new DecimalFormat(temp);
                float fraction = valueAnimator.getAnimatedFraction();
                float currentValue = evalutor.evaluate(fraction, start, end);
                target.setText(format.format(currentValue));
            }
        });
        animator.setDuration(duration);
        animator.start();
    }




    /**
     * 高亮放大字符串的一部分字符
     *
     * @param str      字符串
     * @param lightStr 需要高亮的部分字符串
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder lightStr(String str, String lightStr) {
        int fstart = str.indexOf(lightStr);
        int fend = fstart + lightStr.length();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(Color.RED), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new RelativeSizeSpan(1.3f), fstart, fend, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new StyleSpan(Typeface.BOLD),fstart, fend, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
    }
    /**
     * 高亮放大字符串的一部分字符
     *
     * @param str      字符串
     * @param lightStr 需要高亮的部分字符串
     * @param color 颜色
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder lightStr(String str, String lightStr,String color) {
        int fstart = str.indexOf(lightStr);
        int fend = fstart + lightStr.length();
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(Color.parseColor(color)), fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new RelativeSizeSpan(1.3f), fstart, fend, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
    }

    /**
     * 获取用户星级
     *
     * @param score
     * @return
     */
    public static float getStar(float score) {
        if (score > 100) {
            return 5f;
        }
        float step = 0.5f;
        float startemp = score / 20;
        float decimalPart = startemp - (int) startemp;
        if (decimalPart > step) {
            decimalPart = step;
        } else {
            decimalPart = 0;
        }
        float star = (int) startemp + decimalPart;
        System.out.println(star);
        return star;
    }

    /**
     * 使用gzip进行压缩
     */
    public static String zip(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(primStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return new String(Base64.encode(out.toByteArray(),Base64.DEFAULT));
    }

    /**
     *
     * <p>
     * Description:使用gzip进行解压缩
     * </p>
     *
     * @param compressedStr
     * @return
     */
    public static String unzip(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed = null;
        String decompressed = null;
        try {
            compressed=Base64.decode(compressedStr,Base64.DEFAULT);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }

        return decompressed;
    }
    /**
     * desc:将数组转为16进制
     * @param bArray
     * @return
     * modified:
     */
    public static String bytesToHexString(byte[] bArray) {
        if(bArray == null){
            return null;
        }
        if(bArray.length == 0){
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }
    /**
     * desc:将16进制的数据转为数组
     * <p>创建人：聂旭阳 , 2014-5-25 上午11:08:33</p>
     * @param data
     * @return
     * modified:
     */
    public static byte[] stringToBytes(String data){
        String hexString=data.toUpperCase().trim();
        if (hexString.length()%2!=0) {
            return null;
        }
        byte[] retData=new byte[hexString.length()/2];
        for(int i=0;i<hexString.length();i++)
        {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch1;
            if(hex_char1 >= '0' && hex_char1 <='9')
                int_ch1 = (hex_char1-48)*16;   //// 0 的Ascll - 48
            else if(hex_char1 >= 'A' && hex_char1 <='F')
                int_ch1 = (hex_char1-55)*16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch2;
            if(hex_char2 >= '0' && hex_char2 <='9')
                int_ch2 = (hex_char2-48); //// 0 的Ascll - 48
            else if(hex_char2 >= 'A' && hex_char2 <='F')
                int_ch2 = hex_char2-55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1+int_ch2;
            retData[i/2]=(byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }

    /**
     * 获取网址，自动补全
     *
     * @param url
     * @return
     */
    public static String getCorrectUrl(String url) {
        Log.i(TAG, "getCorrectUrl : \n" + url);
        if (isNotEmpty(url, true) == false) {
            return "";
        }

        if (!url.endsWith("/") && !url.endsWith(".html")) {
            url = url + "/";
        }

        if (isUrl(url) == false) {
            return URL_PREFIX + url;
        }
        return url;
    }

    /**
     * 判断字符是否非空
     *
     * @param s
     * @param trim
     * @return
     */
    public static boolean isNotEmpty(String s, boolean trim) {
        //		Log.i(TAG, "getTrimedString   s = " + s);
        if (s == null) {
            return false;
        }
        if (trim) {
            s = s.trim();
        }
        if (s.length() <= 0) {
            return false;
        }

        currentString = s;

        return true;
    }

    /**
     * 判断字符类型是否是网址
     *
     * @param url
     * @return
     */
    public static boolean isUrl(String url) {
        if (isNotEmpty(url, true) == false) {
            return false;
        } else if (!url.startsWith(URL_PREFIX) && !url.startsWith(URL_PREFIXs)) {
            return false;
        }

        currentString = url;
        return true;
    }

    /**
     * List转成String
     * @param list
     * @return
     */
    public static String listToString(List<String> list){
        if (list==null) {
            return null;
        }
        StringBuilder result=new StringBuilder();
        boolean flag=false;
        for (String string : list) {
            if (flag) {
                result.append(",");
            }else {
                flag=true;
            }
            result.append(string);
        }
        return result.toString();
    }

    /**
     * String 转成List
     * @param listText
     * @return
     */
    public static List<String> StringToList(String listText) {
        if (listText == null || listText.equals("")) {
            return null;
        }
        listText = listText.substring(1);

        List<String> list = new ArrayList<>();
        String[] text = listText.split(",");
        for (String str : text) {
            list.add(str);
        }
        return list;
    }
}
