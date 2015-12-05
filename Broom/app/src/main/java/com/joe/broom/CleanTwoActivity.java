package com.joe.broom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.joe.cachecleaner.engine.AppCleanEngine;
import com.joe.cachecleaner.model.AppInfo;

import java.util.ArrayList;

/**
 * Description
 * Created by chenqiao on 2015/12/4.
 */
public class CleanTwoActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private AppCacheAdapter2 adapter;
    private AppCleanEngine engine;
    private ArrayList<AppInfo> caches;
    private ArrayList<String> chosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);
        chosen = new ArrayList<>();
        caches = new ArrayList<>();
        findViewById(R.id.btn_scan2).setOnClickListener(this);
        findViewById(R.id.btn_clean2).setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        engine = new AppCleanEngine();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan2:
                caches = engine.scanCacheFileByJsonFile(this);
                adapter = new AppCacheAdapter2(this, caches, chosen);
                for (AppInfo info : caches) {
                    Log.d("Demo", "appName:" + info.getAppName());
                    Log.d("Demo", "appCache size:" + info.getAppCacheSize());
                }
                recyclerView.setAdapter(adapter);
                adapter.notifyItemRangeChanged(0, caches.size());
                break;
            case R.id.btn_clean2:
                engine.deleteCacheFilesFromJsonFile(this, chosen);
                break;
        }
    }
}
