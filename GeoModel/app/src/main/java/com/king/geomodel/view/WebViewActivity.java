/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package com.king.geomodel.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.base.NetBroadcastReceiver;
import com.king.geomodel.databinding.ActivityWebviewBinding;
import com.king.geomodel.utils.NetUtil;
import com.king.geomodel.utils.StringUtil;
import com.king.geomodel.utils.listener.OnBottomDragListener;

/**
 * Created by king on 2016/9/9.
 * WebView页面
 */
public class WebViewActivity extends BaseActivity implements OnBottomDragListener, NetBroadcastReceiver.netEventHandler {
    private ActivityWebviewBinding mDataBinding;
    private WebView mWebView;
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        mWebView = mDataBinding.webView;
        getTopbar().setLeftImageListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getTopbar().setLeftText(getIntent().getStringExtra("title"));

        initWebView();
        NetBroadcastReceiver.mListeners.add(this);
    }

    /**
     * 初始化WebView
     */
    public void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setAllowFileAccess(true);
        // 设置是否可缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        //设置 缓存模式
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.requestFocus();
        url = StringUtil.getCorrectUrl(getIntent().getStringExtra("url"));
        if (StringUtil.isNotEmpty(url, true) == false) {
            finish();
            return;
        }


    }

    public void loadData() {
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });
    }

    public void onDragBottom(boolean rightToLeft) {
        if (rightToLeft) {
            if (mWebView.canGoForward()) {
                mWebView.goForward();
            }
            return;
        }
        onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "WebViewActivity");
        if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE) {
            mDataBinding.webView.setVisibility(View.GONE);
            mDataBinding.llFail.setVisibility(View.VISIBLE);
            Toast.makeText(this, getResources().getString(R.string.toast_netfail), Toast.LENGTH_LONG).show();
            return;
        }
        loadData();

    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "WebViewActivity");
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            try {
                mWebView.destroyDrawingCache();
                mWebView.destroy();
            } catch (Exception e) {
            }
        }
        mWebView = null;
    }


    @Override
    public void onNetChange() {
        if (NetUtil.getNetworkState(this) == NetUtil.NETWORN_NONE) {
            Toast.makeText(this, getResources().getString(R.string.toast_netfail), Toast.LENGTH_LONG).show();
        } else {
            mDataBinding.webView.setVisibility(View.VISIBLE);
            mDataBinding.llFail.setVisibility(View.GONE);
            Toast.makeText(this, getResources().getString(R.string.toast_netsuccess), Toast.LENGTH_LONG).show();
            loadData();
        }
    }
}