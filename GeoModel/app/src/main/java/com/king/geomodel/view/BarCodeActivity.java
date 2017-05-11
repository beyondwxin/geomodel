package com.king.geomodel.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.utils.widget.apk.CustomDialog;
import com.king.geomodel.utils.widget.apk.CustomWebView;
import com.king.geomodel.utils.widget.apk.DecodeImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BarCodeActivity extends BaseActivity implements View.OnLongClickListener, View.OnClickListener {

    //    private CustomWebView mCustomWebView;
    private CustomDialog mCustomDialog;
    private WebView mWebView;
    private ArrayAdapter<String> adapter;
    private boolean isQR;//判断是否为二维码
    private Result result;//二维码解析结果
    private String url;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        getTopbar().setLeftText(R.string.tv_apk);
        mWebView = (WebView) findViewById(R.id.webView);
        initSettings();
        initWebView();

    }


    private void initSettings() {
        // 初始化设置
        WebSettings mSettings = mWebView.getSettings();
        mSettings.setJavaScriptEnabled(true);//开启javascript
        mSettings.setDomStorageEnabled(true);//开启DOM
        mSettings.setDefaultTextEncodingName("utf-8");//设置字符编码
        //设置web页面
        mSettings.setAllowFileAccess(true);//设置支持文件流
        mSettings.setSupportZoom(true);// 支持缩放
        mSettings.setBuiltInZoomControls(true);// 支持缩放
        mSettings.setUseWideViewPort(true);// 调整到适合webview大小
        mSettings.setLoadWithOverviewMode(true);// 调整到适合webview大小
        mSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);// 屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
        mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        mSettings.setBlockNetworkImage(true);
        mSettings.setAppCacheEnabled(true);//开启缓存机制
        mSettings.setTextSize(WebSettings.TextSize.NORMAL);//改变文字大小
        mWebView.setOnLongClickListener(this);
    }

    private void initWebView() {
        // 初始WebView化控件
//        mCustomWebView = new CustomWebView(this, this);
        //这里借用翔哥的博客
        mWebView.loadUrl("http://210.72.27.32:6081/simple/apkdown.jsp");//加载页面
        mWebView.setFocusable(true);
        mWebView.setFocusableInTouchMode(true);
        mWebView.setWebViewClient(new MyWebViewClient());
//        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        addContentView(mWebView, lp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        // 长按事件监听（注意：需要实现LongClickCallBack接口并传入对象）
        final WebView.HitTestResult htr = mWebView.getHitTestResult();//获取所点击的内容
        if (htr.getType() == WebView.HitTestResult.IMAGE_TYPE) {//判断被点击的类型为图片
            url = htr.getExtra();
            // 获取到图片地址后做相应的处理
            MyAsyncTask mTask = new MyAsyncTask();
            mTask.execute(url);
            showDialog();
        }
        return false;
    }


    private class MyWebViewClient extends WebViewClient {
        /**
         * 加载过程中 拦截加载的地址url
         *
         * @param view
         * @param url  被拦截的url
         * @return
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String b = url.substring(url.length() - 1, url.length());
            if (TextUtils.equals(b, "1")) {
                Uri uri = Uri.parse(url);
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
                BarCodeActivity.this.startActivity(viewIntent);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        /**
         * 页面加载过程中，加载资源回调的方法
         *
         * @param view
         * @param url
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        /**
         * 页面加载完成回调的方法
         *
         * @param view
         * @param url
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 关闭图片加载阻塞
            view.getSettings().setBlockNetworkImage(false);

        }

        /**
         * 页面开始加载调用的方法
         *
         * @param view
         * @param url
         * @param favicon
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            mWebView.requestFocus();
            mWebView.requestFocusFromTouch();
        }

    }


//    @Override
//    public void onLongClickCallBack(final String imgUrl) {
//        url = imgUrl;
//        // 获取到图片地址后做相应的处理
//        MyAsyncTask mTask = new MyAsyncTask();
//        mTask.execute(imgUrl);
//        showDialog();
//    }

    /**
     * 判断是否为二维码
     * param url 图片地址
     * return
     */
    private boolean decodeImage(String sUrl) {
        result = DecodeImage.handleQRCodeFormBitmap(getBitmap(sUrl));
        if (result == null) {
            isQR = false;
        } else {
            isQR = true;
        }
        return isQR;
    }


    public class MyAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (isQR) {
                handler.sendEmptyMessage(0);
            }


        }

        @Override
        protected String doInBackground(String... params) {
            decodeImage(params[0]);
            return null;
        }
    }

    /**
     * 根据地址获取网络图片
     *
     * @param sUrl 图片地址
     * @return
     * @throws IOException
     */
    public Bitmap getBitmap(String sUrl) {
        try {
            URL url = new URL(sUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                saveMyBitmap(bitmap, "code");//先把bitmap生成jpg图片
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 显示Dialog
     * param v
     */
    private void showDialog() {
        initAdapter();
        mCustomDialog = new CustomDialog(this) {
            @Override
            public void initViews() {
                // 初始CustomDialog化控件
                ListView mListView = (ListView) findViewById(R.id.lv_dialog);
                mListView.setAdapter(adapter);
                mListView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // 点击事件
                        switch (position) {
                            case 0:
                                sendToFriends();//把图片发送给好友
                                closeDialog();
                                break;
                            case 1:
                                Toast.makeText(BarCodeActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                                saveImageToGallery(BarCodeActivity.this);
                                closeDialog();
                                break;
//                            case 2:
//                                Toast.makeText(BarCodeActivity.this, "已收藏", Toast.LENGTH_LONG).show();
//                                closeDialog();
//                                break;
                            case 2:
                                goIntent();
                                closeDialog();
                                break;
                        }

                    }
                });
            }
        };
        mCustomDialog.show();
    }

    /**
     * 初始化数据
     */
    private void initAdapter() {
        adapter = new ArrayAdapter<String>(this, R.layout.item_dialog);
        adapter.add("发送给朋友");
        adapter.add("保存到手机");
//        adapter.add("收藏");
    }

    /**
     * 是二维码时，才添加为识别二维码
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (isQR) {
                    adapter.add("识别图中二维码");
                }
                adapter.notifyDataSetChanged();
            }
        }

        ;
    };

    /**
     * 发送给好友
     */
    private void sendToFriends() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri imageUri = Uri.parse(file.getAbsolutePath());
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
    }

    /**
     * bitmap 保存为jpg 图片
     *
     * @param mBitmap 图片源
     * @param bitName 图片名
     */
    public void saveMyBitmap(Bitmap mBitmap, String bitName) {
        file = new File(Environment.getExternalStorageDirectory() + "/" + bitName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 先保存到本地再广播到图库
     */
    public void saveImageToGallery(Context context) {

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), "code", null);
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void goIntent() {
        Uri uri = Uri.parse(result.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
