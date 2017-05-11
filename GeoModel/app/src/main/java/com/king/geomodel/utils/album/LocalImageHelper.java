package com.king.geomodel.utils.album;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.utils.StringUtil;
import com.king.geomodel.view.pojo.PositionEntity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by king on 2016/9/5.
 */
public class LocalImageHelper {
    private static final int CODE_FOR_READ_PERMISSION = 1;
    private static final int CODE_FOR_WRITE_PERMISSION = 2;
    private static LocalImageHelper instance;
    private final Activity context;
    final List<LocalFile> checkedItems = new ArrayList<>();

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    //当前选中的图片个数
    private int currentSize;

    public String getCameraImgPath() {
        return CameraImgPath;
    }

//    public String setCameraImgPath() {
//        String foloder = BaseApplication.getInstance().getCachePath()
//                + "/PostPicture/";
//        File savedir = new File(foloder);
//        if (!savedir.exists()) {
//            savedir.mkdirs();
//        }
//        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
//                .format(new Date());
//        // 照片命名
//        String picName = timeStamp + ".jpg";
//        //  裁剪头像的绝对路径
//        CameraImgPath = foloder + picName;
//        return CameraImgPath;
//    }


    public String setCameraImgPath() {
        String storagePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath();
        String rootPath = storagePath + "/cameraImg";
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());

        File fileRoot = new File(rootPath);
        if (!fileRoot.exists()) {
            fileRoot.mkdirs();
        }
        String picName = fileName + ".jpg";
        File file = new File(rootPath + picName);
        CameraImgPath = storagePath + picName;
        try {
            if (!file.createNewFile()) {
                Log.i("file", "This file is already exist: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CameraImgPath;
    }

    //拍照时指定保存图片的路径
    private String CameraImgPath;
    //大图遍历字段
    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION
    };
    //小图遍历字段
    private static final String[] THUMBNAIL_STORE_IMAGE = {
            MediaStore.Images.Thumbnails._ID,
            MediaStore.Images.Thumbnails.DATA
    };

    final List<LocalFile> paths = new ArrayList<>();

    final Map<String, List<LocalFile>> folders = new HashMap<>();

    private LocalImageHelper(Activity context) {
        this.context = context;
    }

    public Map<String, List<LocalFile>> getFolderMap() {
        return folders;
    }

    public static LocalImageHelper getInstance() {
        return instance;
    }

    public static void init(Activity context) {
        instance = new LocalImageHelper(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                instance.initImage();
            }
        }).start();
    }

    public boolean isInited() {
        return paths.size() > 0;
    }

    public List<LocalFile> getCheckedItems() {
        return checkedItems;
    }

    private boolean resultOk;

    public boolean isResultOk() {
        return resultOk;
    }

    public void setResultOk(boolean ok) {
        resultOk = ok;
    }

    private boolean isRunning = false;

    public synchronized void initImage() {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(SampleApplicationLike.getInstance().getApplication(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODE_FOR_WRITE_PERMISSION);
            return;
        }
        int hasWriteContactsPermission2 = ContextCompat.checkSelfPermission(SampleApplicationLike.getInstance().getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CODE_FOR_READ_PERMISSION);
            return;
        }
        if (isRunning)
            return;
        isRunning = true;
        if (isInited())
            return;

        //获取大图的游标
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,  // 大图URI
                STORE_IMAGES,   // 字段
                null,         // No where clause
                null,         // No where clause
                MediaStore.Images.Media.DATE_TAKEN + " DESC"); //根据时间升序
        if (cursor == null)
            return;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);//大图ID
            String path = cursor.getString(1);//大图路径
            File file = new File(path);
            //判断大图是否存在
            if (file.exists()) {
                //小图URI
                String thumbUri = getThumbnail(id, path);
                //获取大图URI
                String uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
                        appendPath(Integer.toString(id)).build().toString();
                if (StringUtil.isEmpty(uri))
                    continue;
                if (StringUtil.isEmpty(thumbUri))
                    thumbUri = uri;
                //获取目录名
                String folder = file.getParentFile().getName();
                long fileSize = file.length() / 1024 / 500;
                LocalFile localFile = new LocalFile();
                localFile.setOriginalUri(uri);
                localFile.setThumbnailUri(thumbUri);
                localFile.setFileSize(fileSize);
                localFile.setFileName(file.getName());
                int degree = cursor.getInt(2);
                if (degree != 0) {
                    degree = degree + 180;
                }
                localFile.setOrientation(360 - degree);

                paths.add(localFile);
                //判断文件夹是否已经存在
                if (folders.containsKey(folder)) {
                    folders.get(folder).add(localFile);
                } else {
                    List<LocalFile> files = new ArrayList<>();
                    files.add(localFile);
                    folders.put(folder, files);
                }
            }
        }
        folders.put("所有图片", paths);
        cursor.close();
        isRunning = false;
    }

    private String getThumbnail(int id, String path) {
        //获取大图的缩略图
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                THUMBNAIL_STORE_IMAGE,
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                new String[]{id + ""},
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int thumId = cursor.getInt(0);
            String uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().
                    appendPath(Integer.toString(thumId)).build().toString();
            cursor.close();
            return uri;
        }
        cursor.close();
        return null;
    }

    public List<LocalFile> getFolder(String folder) {
        return folders.get(folder);
    }

    public void clear() {
        checkedItems.clear();
        currentSize = (0);
        String foloder = SampleApplicationLike.getInstance().getCachePath()
                + "/PostPicture/";
        File savedir = new File(foloder);
        if (savedir.exists()) {
            deleteFile(savedir);
        }
    }

    public void deleteFile(File file) {

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
        } else {
        }
    }

    public static class LocalFile {
        private String fileName;
        private String originalUri;//原图URI
        private String thumbnailUri;//缩略图URI
        private int orientation;//图片旋转角度
        private long fileSize;

        private PositionEntity position;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public String getThumbnailUri() {
            return thumbnailUri;
        }

        public void setThumbnailUri(String thumbnailUri) {
            this.thumbnailUri = thumbnailUri;
        }

        public String getOriginalUri() {
            return originalUri;
        }

        public void setOriginalUri(String originalUri) {
            this.originalUri = originalUri;
        }


        public int getOrientation() {
            return orientation;
        }

        public void setOrientation(int exifOrientation) {
            orientation = exifOrientation;
        }

        public PositionEntity getPosition() {
            return position;
        }

        public void setPosition(PositionEntity position) {
            this.position = position;
        }
    }


}
