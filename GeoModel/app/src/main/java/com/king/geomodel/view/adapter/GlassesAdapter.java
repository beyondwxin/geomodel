package com.king.geomodel.view.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.king.geomodel.R;
import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.utils.LogUtil;
import com.king.geomodel.view.pojo.Glasses;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Vector;


public class GlassesAdapter extends ListAdapter {

    private Activity activity;
    private List<Glasses> models;
    private int lastPosition = -1;                                //记录上一次选中的图片位置，-1表示未选中
    private Vector<Boolean> vector = new Vector<Boolean>();       // 定义一个向量作为选中与否容器

    public GlassesAdapter(Activity activity, List<Glasses> models) {
        super(activity);
        this.activity = activity;
        this.models = models;
        for (int i = 0; i < models.size(); i++) {
            vector.add(false);
        }
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected ViewHolder createViewHolder(View root) {
        Holder hold = new Holder();
        hold.iv_glasses = (ImageView) root.findViewById(R.id.iv_glasses);
        hold.iv_selected = (ImageView) root.findViewById(R.id.iv_selected);
        return hold;
    }

    @Override
    protected void fillView(View root, Object item, ViewHolder holder,
                            int position) {
        final Holder hold = (Holder) holder;
        hold.glasses = models.get(position);
        if (!"".equals(hold.glasses.getUrl())) {
            ImageLoader.getInstance().displayImage(models.get(position).getUrl(), hold.iv_glasses, SampleApplicationLike.getInstance().getDisplayOptions());
        }
        if (vector.elementAt(position) == true) {
            hold.iv_selected.setVisibility(View.VISIBLE);
        } else {
            hold.iv_selected.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getItemViewId() {
        return R.layout.item_chooseglasses;
    }

    private class Holder extends ViewHolder {
        private ImageView iv_glasses;
        private ImageView iv_selected;
        private Glasses glasses;
    }

    /**
     * 修改选中时的状态
     *
     * @param position
     */
    public void changeState(int position) {
        if (lastPosition != -1)
            vector.setElementAt(false, lastPosition);                   //取消上一次的选中状态
        vector.setElementAt(!vector.elementAt(position), position);     //直接取反即可
        lastPosition = position;                                        //记录本次选中的位置
        notifyDataSetChanged();                                         //通知适配器进行更新
    }
}
