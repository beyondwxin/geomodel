package com.king.geomodel.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.WindowManager;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.fm.openinstall.OpenInstall;
import com.king.geomodel.R;
import com.king.geomodel.utils.ChannelUtil;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.LruCacheUtils;
import com.king.geomodel.utils.NetUtil;
import com.king.greenDAO.dao.DaoMaster;
import com.king.greenDAO.dao.DaoSession;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zhy.autolayout.config.AutoLayoutConifg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by king on 2016/9/18.
 */
public class BaseApplication extends Application {
//    private static BaseApplication mZBaseApplication;
//
//    public DaoSession daoSession;
//    public SQLiteDatabase db;
//    public DaoMaster.DevOpenHelper helper;
//    public DaoMaster daoMaster;
//    public DisplayImageOptions defaultOptions;
//
//    public static int mNetWorkState;
//    private List<Activity> list = new ArrayList<Activity>();
//    private Display display;
//    public static OSS oss;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        AutoLayoutConifg.getInstance().useDeviceSize().init(this);
//        OpenInstall.init(this);
//        //打开调试，便于看到Log
//        OpenInstall.setDebug(true);
//        mZBaseApplication = this;
//        init();
//        initOSS();
//
//        setupDatabase();
//
//        LruCacheUtils.getLruCacheSize();
//
//        getDisplayOptions();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//                getApplicationContext())
//                .defaultDisplayImageOptions(defaultOptions)
//                .memoryCache(new WeakMemoryCache())
//                .threadPoolSize(5)
//                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
//                .memoryCacheSize(2 * 1024 * 1024)
//                .discCacheSize(2 * 1024 * 1024).build();
//
//        ImageLoader.getInstance().init(config);
//
//        ChannelUtil.getChannel(this);//美团渠道打包
//    }
//
//
//    public DisplayImageOptions getDisplayOptions() {
//        BitmapFactory.Options decodingOptions = new BitmapFactory.Options();
//        defaultOptions =
//                new DisplayImageOptions.Builder()
//                        .showImageOnLoading(R.drawable.dangkr_no_picture_small)
//                        .showImageOnFail(R.drawable.dangkr_no_picture_small)
//                        .showImageForEmptyUri(R.drawable.dangkr_no_picture_small)
//                        .decodingOptions(decodingOptions).cacheOnDisk(true)
//                        .bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true)
//                        .imageScaleType(ImageScaleType.EXACTLY)
//                        .build();
//        return defaultOptions;
//    }
//
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//    }
//
//    public static BaseApplication getInstance() {
//        return mZBaseApplication;
//    }
//
//    public void addActivity(Activity activity) {
//        list.add(activity);
//    }
//
//
//    private void setupDatabase() {
//        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
//        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
//        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
//        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
//        helper = new DaoMaster.DevOpenHelper(this, CommonValues.DB_NAME, null);
//        db = helper.getWritableDatabase();
//        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
//        daoMaster = new DaoMaster(db);
//        daoSession = daoMaster.newSession();
//    }
//
//    public void initData() {
//        mNetWorkState = NetUtil.getNetworkState(this);
//    }
//
//    /**
//     * 初始化
//     */
//    private void init() {
//        //本地图片辅助类初始化
//        if (display == null) {
//            WindowManager windowManager = (WindowManager)
//                    getSystemService(Context.WINDOW_SERVICE);
//            display = windowManager.getDefaultDisplay();
//        }
//    }
//
//    /**
//     * 初始化OSS存储对象
//     */
//    private void initOSS() {
//
//        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(CommonValues.ACCESSKEYID, CommonValues.ACCESSKEYSECRET);
//
//        ClientConfiguration conf = new ClientConfiguration();
//        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
//        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
//        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
//        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
//        OSSLog.enableLog();
//        oss = new OSSClient(getApplicationContext(), CommonValues.ENDPOINT, credentialProvider, conf);
//    }
//
//
//    public String getCachePath() {
//        File cacheDir;
//        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
//            cacheDir = getExternalCacheDir();
//        else
//            cacheDir = getCacheDir();
//        if (!cacheDir.exists())
//            cacheDir.mkdirs();
//        return cacheDir.getAbsolutePath();
//    }
//
//    /**
//     * @return
//     * @Description： 获取当前屏幕1/4宽度
//     */
//    public int getQuarterWidth() {
//        return display.getWidth() / 4;
//    }
//
//    public DaoSession getDaoSession() {
//        return daoSession;
//    }
//
//    public SQLiteDatabase getDb() {
//        return db;
//    }
//
//
//    /**
//     * 检查网络状态
//     *
//     * @param context
//     * @return
//     */
//    public boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivity = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivity == null) {
//            return false;
//        } else {
//            NetworkInfo[] info = connectivity.getAllNetworkInfo();
//            if (info != null) {
//                for (int i = 0; i < info.length; i++) {
//                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//                        NetworkInfo netWorkInfo = info[i];
//                        if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//                            return true;
//                        } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 退出app
//     *
//     * @param context
//     */
//    public void exit(Context context) {
//        for (Activity activity : list) {
//            activity.finish();
//        }
//        System.exit(0);
//    }


}
