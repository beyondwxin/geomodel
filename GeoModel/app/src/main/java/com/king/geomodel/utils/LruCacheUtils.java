package com.king.geomodel.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;


/**
 * Created by king on 2016/6/27.
 * 图片缓存工具类
 */
public class LruCacheUtils {

    private static LruCacheUtils lruCacheUtils;
    private static int maxMemory;
    private static int cacheMemory;
    private static LruCache<String, Bitmap> mMemoryCache;

    private LruCacheUtils() {
    }

    public static LruCacheUtils getInstance() {
        if (lruCacheUtils == null) {
            lruCacheUtils = new LruCacheUtils();
        }
        return lruCacheUtils;
    }

    /**
     * 获取缓存大小 为内存大小的1/8
     *
     * @return
     */
    public static int getLruCacheSize() {
        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/8作为缓存的大小。
        cacheMemory = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheMemory) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
            }
        };
        return cacheMemory;
    }

    /**
     * 缓存图片
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromMenCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }

    }

    /**
     * 从缓存中获取图片
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMenCache(String key) {
        LogUtil.e("imageKey",key);
        return mMemoryCache.get(key);

    }

    /**
     * 清除缓存图片
     * @param key
     */
    public  void ClearBitmapToCache(String key){
        if (getBitmapFromMenCache(key)!=null){
            mMemoryCache.remove(key);
        }

    }
}
