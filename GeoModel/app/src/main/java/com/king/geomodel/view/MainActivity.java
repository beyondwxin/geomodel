package com.king.geomodel.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.baidu.mobstat.StatService;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.databinding.ActivityMainBinding;
import com.king.geomodel.eventbus.AnyEvent;
import com.king.geomodel.eventbus.HeadEvent;
import com.king.geomodel.http.MyJSON;
import com.king.geomodel.http.RequestCenter;
import com.king.geomodel.http.ZCResponse;
import com.king.geomodel.model.serializable.SUserInfo;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.utils.CircleImageView;
import com.king.geomodel.utils.CommonProgressDialog;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.DeviceInfo;
import com.king.geomodel.utils.DownLoadApkManager;
import com.king.geomodel.utils.SharedPreferencesUtil;
import com.king.geomodel.utils.ToastUtil;
import com.king.geomodel.utils.album.LocalImageHelper;
import com.king.geomodel.utils.album.MatrixImageView;
import com.king.geomodel.utils.dialogUtils.DialogManager;
import com.king.geomodel.utils.recycleview.DividerItemDecoration;
import com.king.geomodel.utils.recycleview.WrapContentLinearLayoutManager;
import com.king.geomodel.utils.widget.EmptyRecyclerView;
import com.king.geomodel.view.pojo.UpdateInfo;
import com.king.greenDAO.bean.GeoModel;
import com.king.greenDAO.bean.Position;
import com.king.greenDAO.service.DbService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.bugly.beta.Beta;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.king.geomodel.model.serializable.SUserInfo.getUserInfoInstance;

public class MainActivity extends BaseActivity implements View.OnClickListener, MatrixImageView.OnSingleTapListener, AppWakeUpListener {

    /**
     * 如果想更新so，可以将System.loadLibrary替换成Beta.loadLibrary
     */
//    static {
//        Beta.loadLibrary("libs/mylibs");
//    }

    private final String TAG = this.getClass().getName();
    private ActivityMainBinding mBinding;
    private DrawerLayout mDrawerLayout;
    private CircleImageView mMainAvatar;
    private ImageView mAvatar;

    View pagerContainer;//图片显示部分
    TextView mCountView;//大图数量提示

    private static final int TOADDPICTURE_CODE = 0;
    private List<GeoModel> models = new ArrayList<>();
    private List<Position> positions = new ArrayList<>();

    private TextView mPhoneNum;
    private boolean isLoadMore = true;
    private static int currentPage = 1;

    private MaterialRefreshLayout materialRefreshLayout;
    private View mEmptyView;
    private EmptyRecyclerView mRecyclerView;
    private AcademyAdapter adapter;

    //版本更新部分
    private UpdateInfo info;
    private int mCurrentVersionCode;


    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ((TextView) findViewById(R.id.tv_center)).setText("庵后");
//        loadPatch();//热修复
//        //版本更新
//        mCurrentVersionCode = DeviceInfo.getVersionCode(this);
//        RequestCenter.checkVersion(this);

        BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());

        OpenInstall.getWakeUp(getIntent(), this);

        EventBus.getDefault().register(this);
        initViews();
        initDrawer();
        initData();
        initRefresh();
        setListener();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //此处要调用，否则App在后台运行时，会无法截获
        OpenInstall.getWakeUp(intent, this);
    }

    @Override
    public void onWakeUpFinish(AppData appData, Error error) {
        if (error == null) {
            Log.d("MainActivity", "wakeup = " + appData.toString());
        } else {
            Log.d("MainActivity", "error : " + error.toString());
        }
    }

    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {
        @Override
        public void onCheckComplete() {
        }
    }

    /**
     * 初始化抽屉
     */
    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl);
    }

    private void initViews() {
        //头像加载
        mMainAvatar = (CircleImageView) findViewById(R.id.iv_icon);
        mAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mEmptyView = findViewById(R.id.id_empty_view);

        String headPath = SharedPreferencesUtil.getStringValue(this, CommonValues.URI_HEAD, "");
        if (!TextUtils.isEmpty(headPath)) {
            ImageLoader.getInstance().displayImage("file://" + SharedPreferencesUtil.getStringValue(this, CommonValues.URI_HEAD, ""), mMainAvatar, SampleApplicationLike.getInstance().getDisplayOptions());
            ImageLoader.getInstance().displayImage("file://" + SharedPreferencesUtil.getStringValue(this, CommonValues.URI_HEAD, ""), mAvatar, SampleApplicationLike.getInstance().getDisplayOptions());
        } else {
            ImageLoader.getInstance().displayImage("http://210.72.27.32:6081//head/head.png", mMainAvatar, SampleApplicationLike.getInstance().getDisplayOptions());
            ImageLoader.getInstance().displayImage("http://210.72.27.32:6081//head/head.png", mAvatar, SampleApplicationLike.getInstance().getDisplayOptions());
        }

        mRecyclerView = (EmptyRecyclerView) findViewById(R.id.lv_list);
        recyclerView();
        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        mPhoneNum = (TextView) findViewById(R.id.tv_phone);
        pagerContainer = findViewById(R.id.pagerview);
        mCountView = (TextView) findViewById(R.id.header_bar_photo_count);

    }

    private void recyclerView() {
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));//分割线
        adapter = new AcademyAdapter(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setEmptyView(mEmptyView);
    }

    private void initData() {
        DialogManager.getInstance().dissMissDialog();
        mPhoneNum.setText(SUserInfo.getUserInfoInstance().getPhone());
        models = DbService.getInstance(getApplicationContext()).getGaoModelByPageSize(getUserInfoInstance().getId(), 1, 5);
        refreshPosition();

        adapter.setList(models);
        adapter.notifyDataSetChanged();
    }

    /**
     * 加载热补丁插件
     */
    public void loadPatch() {
        Beta.applyTinkerPatch(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
    }

    public void refreshPosition() {
        positions.clear();
        for (int i = 0; i < models.size(); i++) {
            positions = DbService.getInstance(getApplicationContext()).getPositionByModelId(models.get(i).getId());
        }
    }

    /**
     * 初始化刷新
     */
    private void initRefresh() {
        materialRefreshLayout.setLoadMore(isLoadMore);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                //一般加载数据都是在子线程中，这里用到了handler
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.getInstance().toastInCenter(MainActivity.this, "没有新的数据了!");
                        materialRefreshLayout.finishRefresh();//刷新完成后调用此方法，要不然刷新效果不消失
                    }
                }, 1500);
            }

            @Override
            public void onRefreshLoadMore(final MaterialRefreshLayout materialRefreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoadMore = false;
                        List<GeoModel> loadModels = DbService.getInstance(getApplicationContext()).getGaoModelByPageSize(getUserInfoInstance().getId(), ++currentPage, 5);
                        if (loadModels.size() == 0) {
                            ToastUtil.getInstance().toastInCenter(MainActivity.this, "已经到底部啦!");
                        } else {
                            ToastUtil.getInstance().toastInCenter(MainActivity.this, "加载完成!");
                            for (int i = 0; i < loadModels.size(); i++) {
                                models.add(loadModels.get(i));
                                positions = DbService.getInstance(getApplicationContext()).getPositionByModelId(loadModels.get(i).getId());
                            }
                            adapter.notifyDataSetChanged();
                        }
                        materialRefreshLayout.finishRefreshLoadMore();
                    }
                }, 1500);
            }
        });
    }

    private void setListener() {
        mBinding.albumviewpager.setOnPageChangeListener(pageChangeListener);
        mBinding.albumviewpager.setOnSingleTapListener(this);
        mBinding.albumItemHeaderBar.setOnClickListener(this);
        findViewById(R.id.iv_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDrawer();
            }
        });
        mBinding.lvList.setOnCreateContextMenuListener(this);

        adapter.setOnItemClickListener(new AcademyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GeoModel model = models.get(position);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", model);
                intent.putExtras(bundle);
                startActivityForResult(intent, TOADDPICTURE_CODE);
            }

            @Override
            public void onContextMenuClick(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final int position = adapter.getPosition();
        final GeoModel model = models.get(position);
        switch (item.getItemId()) {
            case 1:
                new AlertDialog.Builder(this)
                        .setTitle("删除")
                        .setMessage("确定删除这条数据吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getGeoModelDao().deleteByKey(model.getId());
                                models.remove(position);
                                adapter.notifyDataSetChanged();
                                ToastUtil.getInstance().toastInCenter(MainActivity.this, "已删除");
                            }
                        })
                        .show();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    /**
     * 抽屉开关
     */
    private void switchDrawer() {
        if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_center:
                mBinding.lvList.smoothScrollToPosition(0);
                break;
            case R.id.header_bar_photo_back:
            case R.id.header_bar_photo_count:
                hideViewPager();
                break;
            case R.id.tv_add:
                //添加
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_person:
                //个人资料
                startActivity(new Intent(MainActivity.this, PersonalActivity.class));
                break;
            case R.id.tv_setting:
                //设置界面
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.tv_exit:
                //退出登录
                DbService.getInstance(this).delete(SUserInfo.getUserInfoInstance().getId());
                SharedPreferencesUtil.saveValue(this, CommonValues.USERINFO, "");
                finish();
                goToLoginActivity();
                break;
        }

    }

    @Override
    public boolean onSuccess(ZCResponse response, String method) {
        DialogManager.getInstance().dissMissDialog();
        //检测版本
        if (method.equals(RequestCenter.CHECKVERSION)) {
            info = MyJSON.parseObject(response.getMainData().getString("updateInfo"), UpdateInfo.class);
            if (!TextUtils.equals(info.getVersion(), mCurrentVersionCode + ".0")) {
                //版本不一致
                showUpdateDialog();
            }
        }
        return super.onSuccess(response, method);
    }

    //显示大图viewpager
    public void showViewPager(int index, List<LocalImageHelper.LocalFile> pictures) {
        pagerContainer.setVisibility(View.VISIBLE);
        mBinding.rvList.setVisibility(View.GONE);
        mBinding.albumviewpager.setAdapter(mBinding.albumviewpager.new LocalViewPagerAdapter(pictures));
        mBinding.albumviewpager.setCurrentItem(index);
        mCountView.setText((index + 1) + "/" + pictures.size());
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation((float) 0.9, 1, (float) 0.9, 1, pagerContainer.getWidth() / 2, pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }

    //关闭大图显示
    private void hideViewPager() {
        pagerContainer.setVisibility(View.GONE);
        mBinding.rvList.setVisibility(View.VISIBLE);
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, (float) 0.9, 1, (float) 0.9, pagerContainer.getWidth() / 2, pagerContainer.getHeight() / 2);
        scaleAnimation.setDuration(200);
        set.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(200);
        set.addAnimation(alphaAnimation);
        pagerContainer.startAnimation(set);
    }

    @Override
    public void onSingleTap() {
        hideViewPager();
    }


    @Override
    public void onBackPressed() {
        if (pagerContainer.getVisibility() != View.VISIBLE) {
            super.onBackPressed();
        } else {
            hideViewPager();
        }
    }

    /**
     * 图片滚动监听
     */
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (mBinding.albumviewpager.getAdapter() != null) {
                String text = (position + 1) + "/" + mBinding.albumviewpager.getAdapter().getCount();
                mCountView.setText(text);
            } else {
                mCountView.setText("0/0");
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Subscribe
    public void onEventMainThread(AnyEvent event) {
        currentPage = 1;
        models = DbService.getInstance(getApplicationContext()).getGaoModelByPageSize(getUserInfoInstance().getId(), 1, 5);
        refreshPosition();
        adapter.setList(models);
        adapter.notifyDataSetChanged();
    }


    @Subscribe
    public void onEventMainThread(HeadEvent event) {
        ImageLoader.getInstance().displayImage("file://" + event.getPath(), mAvatar, SampleApplicationLike.getInstance().getDisplayOptions());
        ImageLoader.getInstance().displayImage("file://" + event.getPath(), mMainAvatar, SampleApplicationLike.getInstance().getDisplayOptions());
    }


    /*
     * 弹出对话框通知用户更新程序
	 */
    protected void showUpdateDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_update, null);
        dialog.setContentView(view);
        TextView versionCode = (TextView) view.findViewById(R.id.tv_versionName);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        versionCode.setText(info.getVersion());
        content.setText(info.getDescription());
        Button cancel = (Button) view.findViewById(R.id.btn_cancel);
        Button down = (Button) view.findViewById(R.id.btn_down);

        /**
         * 不清空
         */
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        /**
         * 清空
         */
        down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                downLoadApk();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final CommonProgressDialog mDialog = new CommonProgressDialog(this);
        mDialog.setMessage("正在下载");
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDialog.show();
        mDialog.setMax(100 * 1024 * 1024);
        mDialog.setProgress(0);
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = DownLoadApkManager.getFileFromServer(info.getApk_url(), mDialog);
                    sleep(3000);
                    installApk(file);
                    mDialog.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (pagerContainer.getVisibility() == View.VISIBLE) {
                hideViewPager();
            } else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "MainActivity");
    }
}

