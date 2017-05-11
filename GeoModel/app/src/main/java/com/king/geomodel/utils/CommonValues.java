package com.king.geomodel.utils;

import android.net.Uri;

/**
 * Created by king on 2016/9/18.
 */
public class CommonValues {

    public static String APP_VERSION = "1.1";
    public static boolean DEBUG = true;
    public static final String DEFAULT_ENCORD = "UTF-8";// 默认通讯编码方式
    public static final String HTTP = "http";// 注册通讯访问方式，以及UI工具类处理地址时使用
    public static final String HTTPS = "https";// 注册通讯访问方式，以及UI工具类处理地址时使用

    public static final String FIRST_OPEN = "first_open";

    public static final String DEFAULT_SHAREDPREFERENCES_NAME = "model_sp";
    public static final String LOCAL_FOLDER_NAME = "local_folder_name";//跳转到相册页的文件夹名称

    public static final String DB_NAME = "geomodel";
    /**
     * 头像地址
     */
    public static final String URI_HEAD = "";

    //apk下载地址
    public static final String APKDOWNLOADURL = "http://openinstall.io/c/gkbsen/1";
    /**
     * http
     * 200 - 服务器返回成功
     * 404 - 请求的网页不存在
     * 503 – 服务器异常
     */
    public static final String SUCCESS_STATUS = "200";
    public static final String NOTFIND_STATUS = "404";
    public static final String EXCEPTION_STATUS = "503";

    /**
     * 判断用户是否存在 00：存在  01:错误
     */
    public static final String USER_EXIST = "00";
    public static final String USER_ERROR = "01";

    /**
     * 版本更新 0:无需更新 1：更新 2：获取服务器更新信息失败 3：下载新版本失败
     */
    public static final int UPDATA_NONEED = 0;
    public static final int UPDATA_CLIENT = 1;
    public static final int GET_UNDATAINFO_ERROR = 2;
    public static final int DOWN_ERROR = 3;

    /**
     * 引导页显示开关
     */
    public static final String SHOWGUIDE = "show_guide";

    /**
     * registrationId
     */
    public static final String REGISTRATION_ID = "registrationId";
    /**
     * tokenCode
     */
    public static final String TOKENCODE = "token";
    /**
     * userInfo
     */
    public static final String USERINFO = "userInfo";
    /**
     * 是否自动登录
     */
    public static final String ISAUTO_LOGIN = "is_auto_login";

    /**
     * 外网endpoint
     */
    public static final String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";

    /**
     * accessKeyId
     */
    public static final String ACCESSKEYID = "LTAIjBmOPjJ4LesF";

    /**
     * accessKeySecret
     */
    public static final String ACCESSKEYSECRET = "PHQtGuRzjHJbt9aF9vWSttVV66UAM7";

    /**
     * academyBucket
     */
    public static final String ACADEMYBUCKET = "geomodel";

    /**
     * uploadObject
     */
    public static final String UPLOADOBJECT = "geomodel/picture";

    /**
     * downloadObject
     */
    public static final String DOWNLOADOBJECT = "geomodel/picture";
}
