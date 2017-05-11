package com.king.geomodel.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by wx on 2016/7/7.
 */
public class CommonAdapter<T> extends BaseAdapter {
    private Context mContext;
    private List<T> mList;
    private int mLayoutId;
    private int mVariableId;

    public CommonAdapter(Context mContext, int mVariableId, int mLayoutId, List<T> mList) {
        this.mContext = mContext;
        this.mVariableId = mVariableId;
        this.mLayoutId = mLayoutId;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewDataBinding viewDataBinding=null;
        if(convertView==null){
            viewDataBinding= DataBindingUtil.inflate(LayoutInflater.from(mContext),mLayoutId,parent,false);
        }else{
            viewDataBinding= DataBindingUtil.getBinding(convertView);
        }
        viewDataBinding.setVariable(mVariableId,mList.get(position));
        return viewDataBinding.getRoot();
    }
}
