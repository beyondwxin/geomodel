package com.king.geomodel.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.flyco.labelview.LabelView;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.utils.album.LocalImageHelper;
import com.king.geomodel.utils.widget.EmptyRecyclerView;
import com.king.geomodel.utils.widget.view.GridViewForListView;
import com.king.geomodel.view.adapter.PicAdapter;
import com.king.greenDAO.bean.GeoModel;
import com.king.greenDAO.bean.Position;
import com.king.greenDAO.service.DbService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by king on 2016/9/25.
 */
public class AcademyAdapter extends RecyclerView.Adapter<AcademyAdapter.MainListHolder> {
    private Activity activity;
    private List<GeoModel> mList;
    List<LocalImageHelper.LocalFile> pictures = new ArrayList<>();
    private List<Position> positions = new ArrayList<>();//位置信息列表

    public AcademyAdapter(Activity activity) {
        this.activity = activity;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
    }

    public void setList(List<GeoModel> mList) {
        this.mList = mList;
    }

    /**
     * 添加一条到顶部
     * <p>
     * //     * @param lists
     */
    public void addLists(List<GeoModel> mGeoList) {
        addLists(0, mGeoList);
    }

    public void addLists(int position, List<GeoModel> mList) {
        if (this.mList != null) {
            if (mList != null && mList.size() > 0) {
                this.mList.addAll(mList);
                notifyDataSetChanged();
            }
        }
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onContextMenuClick(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo);
    }


    private AcademyAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        } else {
            return mList.size();
        }
    }

    public class MainListHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        TextView time;
        TextView title;
        TextView des;
        LabelView status;
        GridViewForListView photos;

        public MainListHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            des = (TextView) itemView.findViewById(R.id.tv_des);
            status = (LabelView) itemView.findViewById(R.id.tv_status);
            photos = (GridViewForListView) itemView.findViewById(R.id.photos);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    }

    @Override
    public MainListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_main, parent, false);
        MainListHolder holder = new MainListHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MainListHolder holder, int position) {
        final GeoModel geoModel = mList.get(position);
        positions = DbService.getInstance(SampleApplicationLike.getInstance().getApplication()).getPositionByModelId(geoModel.getId());
        holder.title.setText(geoModel.getTitle());
        holder.des.setText(geoModel.getDes());
        holder.time.setText(geoModel.getTime());

        if (TextUtils.equals(geoModel.getStatus(), "已上传")) {
            holder.status.setBgColor(activity.getResources().getColor(R.color.col_bgup));
        } else {
            holder.status.setBgColor(activity.getResources().getColor(R.color.col_bgsave));
        }
        holder.status.setText(geoModel.getStatus());

        final String[] arrPath = geoModel.getPhotos().split(",");

        final PicAdapter adapter = new PicAdapter(activity, arrPath, positions);
        holder.photos.setAdapter(adapter);

        holder.photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pictures.clear();
                int line = holder.getLayoutPosition();
                GeoModel show = mList.get(line);
                String[] url = show.getPhotos().split(",");
                for (int i = 0; i < url.length; i++) {
                    LocalImageHelper.LocalFile localFile = new LocalImageHelper.LocalFile();
                    String name = url[i].substring(url[i].lastIndexOf("/"));
                    localFile.setFileName(name.substring(0, name.indexOf(".")));
                    localFile.setThumbnailUri(url[i]);
                    pictures.add(localFile);
                }
                ((MainActivity) activity).showViewPager(position, pictures);
            }
        });

        holder.photos.setOnTouchInvalidPositionListener(new GridViewForListView.OnTouchInvalidPositionListener() {
            @Override
            public boolean onTouchInvalidPosition(int motionEvent) {
                return false;
            }
        });


        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        }
        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 1, Menu.NONE, R.string.tv_delete);
                setPosition(holder.getPosition());
                mOnItemClickListener.onContextMenuClick(menu, v, menuInfo);
            }
        });

    }
}


