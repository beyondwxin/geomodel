package com.king.geomodel.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baidu.mobstat.StatService;
import com.king.geomodel.R;
import com.king.geomodel.base.AppManager;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.SharedPreferencesUtil;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * 账号在其他设备上登录
 */
public class LoginOccupiedActivity extends BaseActivity {
    private Button mExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_occupied);

        mExit = (Button) findViewById(R.id.btn_exit);

        mExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                JPushInterface.stopPush(getApplicationContext());
                SharedPreferencesUtil.saveValue(LoginOccupiedActivity.this, CommonValues.USERINFO, "");
                AppManager.getAppManager().finishAllActivity();
                goToLoginActivity();
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
            }
        });
    }

    /**
     * 监听Back键按下事件,方法1:
     * 注意:
     * super.onBackPressed()会自动调用finish()方法,关闭
     * 当前Activity.
     * 若要屏蔽Back键盘,注释该行代码即可
     */
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "LoginOccupiedActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "LoginOccupiedActivity");
    }

}
