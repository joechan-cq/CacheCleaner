package com.joe.broom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.cachecleaner.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Created by chenqiao on 2015/12/2.
 */
public class AppCacheAdapter2 extends RecyclerView.Adapter<AppCacheAdapter2.MyViewHolder> {

    private List<AppInfo> datas;

    private Context mContext;

    private List<String> chosen;

    public AppCacheAdapter2(Context context, List<AppInfo> datas, ArrayList<String> chosen) {
        this.datas = datas;
        this.mContext = context;
        this.chosen = chosen;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item2, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.appName.setText(datas.get(position).getAppName());
        if (datas.get(position).getAppIcon() != null) {
            holder.appIcon.setImageDrawable(datas.get(position).getAppIcon());
        } else {
            holder.appIcon.setImageResource(R.mipmap.ic_launcher);
        }
        holder.appCacheSize.setText(String.valueOf(datas.get(position).getAppCacheSize()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!chosen.contains(datas.get(position).getPackageName())) {
                        chosen.add(datas.get(position).getPackageName());
                    }
                } else {
                    if (chosen.contains(datas.get(position).getPackageName())) {
                        chosen.remove(datas.get(position).getPackageName());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView appName, appCacheSize;
        private ImageView appIcon;
        private CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_choose);
            appName = (TextView) itemView.findViewById(R.id.tv_appName);
            appCacheSize = (TextView) itemView.findViewById(R.id.tv_appCacheSize);
            appIcon = (ImageView) itemView.findViewById(R.id.img_appIco);
        }
    }
}
