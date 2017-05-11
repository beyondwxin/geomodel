package com.king.geomodel.utils;

import java.util.Map;

/**
 * Created by king on 2016/6/20.
 */
public class MapUtils {
    /**
     * 从map中获取字符串
     *
     * @param map
     * @param key
     * @return
     */
    public static String getStringInMap(Map<String, Object> map, String key) {
        if (map.containsKey(key)) {
            return (String) map.get(key);
        }
        return "";
    }

    /**
     * 从map中获取数字
     *
     * @param map
     * @param key
     * @return
     */
    public static int getIntInMap(Map<String, Object> map, String key) {
        try {
            if (map.containsKey(key)) {
                return Integer.valueOf((String) map.get(key));
            }
        }catch (Exception e){
            return 0;
        }
        return 0;
    }

    /**
     * 从map中获取对象
     *
     * @param map
     * @param key
     * @return
     */
    public static Object getObjectInMap(Map<String, Object> map, String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }
}
