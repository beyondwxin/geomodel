package com.king.geomodel.view.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.geomodel.R;
import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.greenDAO.bean.Position;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

//import com.king.geomodel.view.MainActivity;

/**
 * Created by king on 2016/9/21.
 */
public class PicAdapter extends BaseAdapter {

    String[] arrPath;
    private Activity con;
    private LayoutInflater inflater;
    private List<Position> mList;

    public PicAdapter(Activity context, String[] arrPath, List mList) {
        this.con = context;
        this.arrPath = arrPath;
        this.mList = mList;
        inflater = LayoutInflater.from(con);
    }


    @Override
    public int getCount() {
        return arrPath.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_picture, null);
            holder.image = (ImageView) convertView.findViewById(R.id.pic);
            holder.position = (TextView) convertView.findViewById(R.id.tv_position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage("file://" + arrPath[position], holder.image, SampleApplicationLike.getInstance().getDisplayOptions());
//        holder.position.setText("经度:"+mList.get(position).getLongitude()+"\n"+"纬度:"+mList.get(position).getLatitude());
        holder.position.setText("当前位置:"+mList.get(position).getAddress());
        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
        public TextView position;

    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

}
