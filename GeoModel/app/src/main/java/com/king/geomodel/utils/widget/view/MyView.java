package com.king.geomodel.utils.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.king.geomodel.R;


/**
 * Created by king on 2016/9/18.
 */
public class MyView extends View {

    private Paint mPaint;

    public MyView(Context context) {
        super(context);
        mPaint = new Paint();

    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.myView);
        int textColor = a.getColor(R.styleable.myView_textColor, 0XFFFFFFFF);
        float textSize = a.getDimension(R.styleable.myView_textSize, 20);

        mPaint.setTextSize(textSize);
        mPaint.setColor(textColor);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //填充风格
        //画矩形
//        canvas.drawRect(50,50,200,200,mPaint);
        mPaint.setColor(Color.RED);
        canvas.drawCircle(20, 20, 15, mPaint);
//        mPaint.setUnderlineText(true);
        mPaint.setAntiAlias(true);
        //颜色
        mPaint.setColor(Color.WHITE);
        canvas.drawText("1", 15, 26, mPaint);
    }

    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 4;// listView.getNumColumns();
        int totalHeight = 0;
        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加
        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);
    }
}
