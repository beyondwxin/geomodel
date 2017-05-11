package com.king.geomodel.utils.widget.view;

/**
 * Created by king on 2016/9/27.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class GridViewForListView extends GridView {
    private OnTouchInvalidPositionListener onTouchInvalidPositionListener;

    public GridViewForListView(Context context) {
        super(context);

    }

    public GridViewForListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (onTouchInvalidPositionListener == null) {
            return super.onTouchEvent(ev);
        }
        if (!isEnabled()) {
            return isClickable() || isLongClickable();
        }
        final int motionPosition = pointToPosition((int) ev.getX(), (int) ev.getY());
        if (motionPosition == INVALID_POSITION) {
            super.onTouchEvent(ev);
            return onTouchInvalidPositionListener.onTouchInvalidPosition(ev.getActionMasked());
        }
        return super.onTouchEvent(ev);
    }

    public void setOnTouchInvalidPositionListener(
            OnTouchInvalidPositionListener onTouchInvalidPositionListener) {
        this.onTouchInvalidPositionListener = onTouchInvalidPositionListener;
    }

    public interface OnTouchInvalidPositionListener {
        boolean onTouchInvalidPosition(int motionEvent);
    }

}