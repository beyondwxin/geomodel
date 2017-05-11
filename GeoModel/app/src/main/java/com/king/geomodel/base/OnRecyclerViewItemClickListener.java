package com.king.geomodel.base;

import android.view.View;

/**
 * Created by king on 2016/9/23.
 */
public interface OnRecyclerViewItemClickListener<T> {
    void onItemClick(View view, T data);
}
