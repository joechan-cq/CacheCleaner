package com.joe.broom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.cachecleaner.model.AppInfo;

import java.util.List;

/**
 * Description
 * Created by chenqiao on 2015/12/2.
 */
public class AppCacheAdapter extends RecyclerView.Adapter<AppCacheAdapter.MyViewHolder> {

    private List<AppInfo> datas;

    private Context mContext;

    public AppCacheAdapter(Context context, List<AppInfo> datas) {
        this.datas = datas;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.appName.setText(datas.get(position).getAppName());
        holder.appIcon.setImageDrawable(datas.get(position).getAppIcon());
        holder.appCacheSize.setText(String.valueOf(datas.get(position).getAppCacheSize()));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView appName, appCacheSize;
        private ImageView appIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            appName = (TextView) itemView.findViewById(R.id.tv_appName);
            appCacheSize = (TextView) itemView.findViewById(R.id.tv_appCacheSize);
            appIcon = (ImageView) itemView.findViewById(R.id.img_appIco);
        }
    }
}
