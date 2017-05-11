package com.king.geomodel.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * Created by king on 2016/9/18.
 */
public class CircleImageView extends ImageView {
    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
   Paint p=new Paint();
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable= getDrawable();
        //判断是否是BitmapDrawble
        if(drawable!=null&&drawable instanceof BitmapDrawable){
            Bitmap bmp=((BitmapDrawable) drawable).getBitmap();
            if(bmp!=null){

                int radius=getWidth()>getHeight()?getHeight()/2:getWidth()/2;
                //得到bnp和得到ImageView的宽度
                bmp=getCircleBitmap(bmp,radius);
                canvas.drawBitmap(bmp,getWidth()/2-radius,getHeight()/2-radius,p);
                }

        }else{
            super.onDraw(canvas);
        }
    }
    public Bitmap getCircleBitmap(Bitmap bmp, int radius){

        Bitmap output= Bitmap.createBitmap(radius*2,radius*2, Bitmap.Config.ARGB_8888);

        Canvas canvas=new Canvas(output);

        Paint paint=new Paint();
        //paint Color画笔颜色白色
        paint.setARGB(255,255,255,255);
        paint.setAntiAlias(true);
        //画笔的样式
        paint.setStyle(Paint.Style.FILL);
        //画板设置透明色
        canvas.drawARGB(0, 0, 0, 0);
        //创建个圆
        canvas.drawCircle(radius,radius,radius,paint);
        //设置Xfermode SrcIn取两层绘制交集。显示后绘制的
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        Rect src=new Rect(0,0,bmp.getWidth(),bmp.getHeight());

        Rect dst=new Rect(0,0,radius*2,radius*2);

        //在指定范围dst内绘制指定范围src的图片
        canvas.drawBitmap(bmp,src,dst,paint);

        return output;
    }
}
