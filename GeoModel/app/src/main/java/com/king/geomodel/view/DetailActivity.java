package com.king.geomodel.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.mobstat.StatService;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.databinding.ActivityModifyBinding;
import com.king.geomodel.eventbus.AnyEvent;
import com.king.geomodel.http.RequestCenter;
import com.king.geomodel.http.ZCResponse;
import com.king.geomodel.model.serializable.SUserInfo;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.utils.ActionSheet;
import com.king.geomodel.utils.DateUtils;
import com.king.geomodel.utils.ImageUtils;
import com.king.geomodel.utils.LogUtil;
import com.king.geomodel.utils.StringUtil;
import com.king.geomodel.utils.ToastUtil;
import com.king.geomodel.utils.album.FilterImageView;
import com.king.geomodel.utils.album.LocalImageHelper;
import com.king.geomodel.utils.album.MatrixImageView;
import com.king.geomodel.utils.dialogUtils.DialogManager;
import com.king.geomodel.view.pojo.PositionEntity;
import com.king.greenDAO.bean.GeoModel;
import com.king.greenDAO.bean.Position;
import com.king.greenDAO.service.DbService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends BaseActivity implements View.OnClickListener, MatrixImageView.OnSingleTapListener, AMapLocationListener {
    private ActivityModifyBinding mDataBinding;
    private InputMethodManager imm;//软键盘管理
    private List<LocalImageHelper.LocalFile> pictures = new ArrayList<>();//图片路径数组
    View pagerContainer;//图片显示部分
    //显示大图的viewpager 集成到了Actvity中 下面是和viewpager相关的控件
    ImageView delete;//删除按钮
    TextView mCountView;//大图数量提示


    int size;//小图大小
    int padding;//小图间距

    public List<String> picPathList = new ArrayList<>();//图片SD卡路径
    private String paths;
    private Cursor cursor;
    private GeoModel model;
    private String[] havePaths;

    private ActionSheet.Builder mBuilder;
    private String[] chooseItem = new String[]{"上传", "保存", "删除"};
    private static final int BACKADDPICTURE_CODE = 1;

    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    private PositionEntity mPosition = new PositionEntity();//当前定位实体
    public List<Position> mListPositions = new ArrayList<>();//地址信息，用于保存到数据库


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_modify);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        getTopbar().setLeftText("编辑");

        initViews();
        initData();
        setListener();

        initButton();
        Intent intent = this.getIntent();
        model = (GeoModel) intent.getSerializableExtra("model");
        mListPositions = DbService.getInstance(SampleApplicationLike.getInstance().getApplication()).getPositionByModelId(model.getId());
        mDataBinding.etTitle.setText(model.getTitle());
        mDataBinding.etDes.setText(model.getDes());
        havePaths = model.getPhotos().split(",");
        LocalImageHelper.getInstance().setCurrentSize(havePaths.length);
        for (int i = 0; i < havePaths.length; i++) {
            picPathList.add(havePaths[i]);
            LocalImageHelper.LocalFile localFile = new LocalImageHelper.LocalFile();
            String part = havePaths[i].substring(havePaths[i].lastIndexOf("/"));
            localFile.setFileName(part.substring(0, part.indexOf(".")));
            localFile.setThumbnailUri(havePaths[i]);
            pictures.add(localFile);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.rightMargin = padding;
            FilterImageView imageView = new FilterImageView(this);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImageLoader.getInstance().displayImage("file://" + havePaths[i], imageView);
            imageView.setOnClickListener(this);

            mDataBinding.postPicContainer.addView(imageView, mDataBinding.postPicContainer.getChildCount() - 1);
        }
    }


    public void initButton() {
        mDataBinding.btnFinish.setEnabled(true);
    }

    /**
     * @Description： 初始化Views
     */
    private void initViews() {
        mCountView = (TextView) findViewById(R.id.header_bar_photo_count);
        delete = (ImageView) findViewById(R.id.header_bar_photo_delete);
        pagerContainer = findViewById(R.id.pagerview);
        delete.setVisibility(View.VISIBLE);
    }

    public void setListener() {
        mDataBinding.postAddPic.setOnClickListener(this);
        mDataBinding.albumviewpager.setOnPageChangeListener(pageChangeListener);
        mDataBinding.albumviewpager.setOnSingleTapListener(this);
        mDataBinding.albumItemHeaderBar.setOnClickListener(this);

        delete.setOnClickListener(this);
    }

    private void initData() {
        size = (int) getResources().getDimension(R.dimen.size_100);
        padding = (int) getResources().getDimension(R.dimen.padding_10);

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setInterval(1000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        mLocationOption.setHttpTimeOut(20000);
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
            if (mDataBinding.albumviewpager.getAdapter() != null) {
                String text = (position + 1) + "/" + mDataBinding.albumviewpager.getAdapter().getCount();
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

    //显示大图viewpager
    private void showViewPager(int index) {
        pagerContainer.setVisibility(View.VISIBLE);
        mDataBinding.postEditContainer.setVisibility(View.GONE);
        mDataBinding.albumviewpager.setAdapter(mDataBinding.albumviewpager.new LocalViewPagerAdapter(pictures));
        mDataBinding.albumviewpager.setCurrentItem(index);
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
        mDataBinding.postEditContainer.setVisibility(View.VISIBLE);
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
    public void onClick(final View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.header_bar_photo_back:
            case R.id.header_bar_photo_count:
                hideViewPager();
                break;
            case R.id.header_bar_photo_delete:
                final int index = mDataBinding.albumviewpager.getCurrentItem();
                new AlertDialog.Builder(this)
                        .setTitle("  提示")
                        .setMessage("        要删除这张照片吗?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                pictures.remove(index);
                                picPathList.remove(index);
                                mListPositions.remove(index);
                                if (pictures.size() == 0) {
                                    hideViewPager();
                                    mDataBinding.btnFinish.setEnabled(false);
                                }
                                mDataBinding.postPicContainer.removeView(mDataBinding.postPicContainer.getChildAt(index));
                                mCountView.setText((mDataBinding.albumviewpager.getCurrentItem() + 1) + "/" + pictures.size());
                                mDataBinding.albumviewpager.getAdapter().notifyDataSetChanged();
                                LocalImageHelper.getInstance().setCurrentSize(pictures.size());
                            }

                        }).show();

                break;

            case R.id.btn_finish:
                mBuilder = ActionSheet.createBuilder(this, getSupportFragmentManager()).setOtherButtonTitles(chooseItem).setCancelButtonTitle(R.string.tv_cancel).setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0:
                                //上传
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                if (TextUtils.isEmpty(mDataBinding.etTitle.getText()) || TextUtils.isEmpty(mDataBinding.etDes.getText())) {
                                    ToastUtil.getInstance().toastInCenter(DetailActivity.this, "填写标题或描述");
                                    return;
                                }
                                if (!SampleApplicationLike.getInstance().isNetworkAvailable(DetailActivity.this)) {
                                    ToastUtil.getInstance().toastInCenter(DetailActivity.this, "网络连接失败");
                                    return;
                                }
                                Log.e("size", picPathList.size() + "");
                                DialogManager.getInstance().showProgressDialog(DetailActivity.this, "正在上传");
                                upload(picPathList);
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Log.e("list.zi", picPathList.size() + "");
//                                        ossUpload(picPathList);
//                                    }
//                                }).start();
                                break;
                            case 1:
                                //保存
                                if (TextUtils.isEmpty(mDataBinding.etTitle.getText()) || TextUtils.isEmpty(mDataBinding.etDes.getText())) {
                                    ToastUtil.getInstance().toastInCenter(DetailActivity.this, "填写标题或描述");
                                    return;
                                }
                                DialogManager.getInstance().showProgressDialog(DetailActivity.this, "正在保存");
                                updateList(getResources().getString(R.string.tv_haveSave));
                                DialogManager.getInstance().dissMissDialog();
                                setResult(BACKADDPICTURE_CODE);
                                finish();
                                ToastUtil.getInstance().toastInCenter(DetailActivity.this, "保存成功");
                                EventBus.getDefault().post(new AnyEvent());
                                break;

                            case 2:
                                //删除
                                getGeoModelDao().delete(model);
                                ToastUtil.getInstance().toastInCenter(DetailActivity.this, "删除成功");
                                setResult(BACKADDPICTURE_CODE);
                                finish();
                                EventBus.getDefault().post(new AnyEvent());
                                break;
                        }
                    }
                });
                mBuilder.show();
                break;
            case R.id.post_add_pic:
                //给定位客户端对象设置定位参数
                mLocationClient.setLocationOption(mLocationOption);
                //启动定位
                mLocationClient.startLocation();
                //去选择
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //  拍照后保存图片的绝对路径
                String cameraPath = LocalImageHelper.getInstance().setCameraImgPath();
                File file = new File(cameraPath);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent,
                        ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                break;
            default:
                //添加图片至Viewpager,显示大图
                if (view instanceof FilterImageView) {
                    for (int i = 0; i < mDataBinding.postPicContainer.getChildCount(); i++) {
                        if (view == mDataBinding.postPicContainer.getChildAt(i)) {
                            showViewPager(i);
                        }
                    }
                }
                break;
        }
    }

    /**
     * 上传
     *
     * @param urls
     */
    public void upload(List<String> urls) {
        Map<String, File> fileMaps = new HashMap<>();
        for (int i = 0; i < urls.size(); i++) {
            File file = new File(urls.get(i));
            fileMaps.put("photo" + i, file);
        }
        RequestCenter.upload(fileMaps, mListPositions, this);
    }

    @Override
    public boolean onSuccess(final ZCResponse response, String method) {
        DialogManager.getInstance().dissMissDialog();
        if (method.equals(RequestCenter.UPLOAD_PICTURES)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mResultUrl = response.getMainData().getString("glassVR");
                    updateList(getResources().getString(R.string.tv_haveUp));
                    ToastUtil.getInstance().toastInCenter(DetailActivity.this, "上传完成");
                    setResult(BACKADDPICTURE_CODE);
                    finish();
                    EventBus.getDefault().post(new AnyEvent());
                }
            });
        }
        return super.onSuccess(response, method);
    }

    /**
     * 更新
     */
    private void updateList(String status) {
        GeoModel geoModel;
        Position position;
        geoModel = new GeoModel(model.getId(), DateUtils.getCurrentFormateTime(), mDataBinding.etTitle.getText().toString(), mDataBinding.etDes.getText().toString(), status, StringUtil.listToString(picPathList), Long.parseLong(SUserInfo.getUserInfoInstance().getId() + ""));
        getGeoModelDao().update(geoModel);
        for (int i = 0; i < mListPositions.size(); i++) {
            if (mListPositions.get(i).getId() == null) {
                position = new Position(null, mListPositions.get(i).getLongitude(), mListPositions.get(i).getLatitude(), mListPositions.get(i).getAddress(), mListPositions.get(i).getTime(), Long.parseLong(model.getId() + ""));
                getPositionDao().insert(position);
            } else {
                position = new Position(mListPositions.get(i).getId(), mListPositions.get(i).getLongitude(), mListPositions.get(i).getLatitude(), mListPositions.get(i).getAddress(), mListPositions.get(i).getTime(), Long.parseLong(model.getId() + ""));
                getPositionDao().update(position);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                String cameraPath = LocalImageHelper.getInstance().getCameraImgPath();
                if (StringUtil.isEmpty(cameraPath)) {
                    Toast.makeText(this, "图片获取失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                File file = new File(cameraPath);
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    LocalImageHelper.LocalFile localFile = new LocalImageHelper.LocalFile();
                    localFile.setThumbnailUri(uri.toString());
                    localFile.setOriginalUri(uri.toString());
                    localFile.setFileSize(file.length() / 1024 / 100);
                    localFile.setFileName(file.getName());
                    localFile.setOrientation(ImageUtils.getBitmapDegree(cameraPath));
                    LocalImageHelper.getInstance().getCheckedItems().add(localFile);
                    LocalImageHelper.getInstance().setResultOk(true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                    //这里本来有个弹出progressDialog的，在拍照结束后关闭，但是要延迟1秒，原因是由于三星手机的相机会强制切换到横屏，
                    //此处必须等它切回竖屏了才能结束，否则会有异常
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (LocalImageHelper.getInstance().isResultOk()) {
                                //相册
                                LocalImageHelper.getInstance().setResultOk(true);
                                //获取选中的图片
                                Position positionEntity = new Position();
                                positionEntity.setTime(DateUtils.getCurrentFormateTime6());
                                positionEntity.setLongitude(mPosition.getLongitude());
                                positionEntity.setLatitude(mPosition.getLatitude());
                                positionEntity.setAddress(mPosition.getAddress());
                                mListPositions.add(positionEntity);

                                mDataBinding.btnFinish.setEnabled(true);
                                List<LocalImageHelper.LocalFile> files = LocalImageHelper.getInstance().getCheckedItems();
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                                params.rightMargin = padding;
                                FilterImageView imageView = new FilterImageView(DetailActivity.this);
                                imageView.setLayoutParams(params);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                Log.e("name", files.get(0).getFileName());
                                mPosition.setTime(DateUtils.getCurrentFormateTime6());
                                files.get(0).setPosition(mPosition);
                                LogUtil.e("当前的照片信息:", "拍照地址:" + files.get(0).getPosition().getAddress() + ",时间:" + mPosition.getTime());
                                ImageLoader.getInstance().displayImage(files.get(0).getThumbnailUri(), new ImageViewAware(imageView), SampleApplicationLike.getInstance().getDisplayOptions(),
                                        null, null, files.get(0).getOrientation());
                                imageView.setOnClickListener(DetailActivity.this);
                                pictures.add(files.get(0));
                                mDataBinding.postPicContainer.addView(imageView, mDataBinding.postPicContainer.getChildCount() - 1);
                                LocalImageHelper.getInstance().setCurrentSize(pictures.size());

                                Log.e("path" + 0, ImageUtils.getImagePath(Uri.parse(files.get(0).getThumbnailUri()), DetailActivity.this) + "共" + picPathList.size() + "张");
                                picPathList.add(ImageUtils.getImagePath(Uri.parse(files.get(0).getThumbnailUri()), DetailActivity.this));
                                //清空选中的图片
                                files.clear();
                                //设置当前选中的图片数量
                                LocalImageHelper.getInstance().setCurrentSize(pictures.size());
                                //延迟滑动至最右边
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        mDataBinding.postScrollview.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                                    }
                                }, 50L);
                            }
                        }
                    }, 1000);
                } else {
                    Toast.makeText(this, "取消拍照", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                mPosition = new PositionEntity();
                mPosition.setLongitude(aMapLocation.getLongitude());
                mPosition.setLatitude(aMapLocation.getLatitude());
                mPosition.setAddress(aMapLocation.getAddress());
                mPosition.setCountry(aMapLocation.getCountry());
                mPosition.setProvince(aMapLocation.getProvince());
                mPosition.setCity(aMapLocation.getCity());
                mPosition.setDistrict(aMapLocation.getDistrict());
                mPosition.setStreet(aMapLocation.getStreet());
                ToastUtil.getInstance().toastInCenter(this, "当前位置：" + aMapLocation.getAddress());
                LogUtil.e("当前位置信息:", "纬度:" + aMapLocation.getLatitude() + ",经度：" + aMapLocation.getLongitude() + ",地址:" + aMapLocation.getAddress());
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "DetailActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "DetailActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
