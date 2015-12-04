package com.joe.broom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.joe.cachecleaner.engine.AppCleanEngine;
import com.joe.cachecleaner.model.AppInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private AppCacheAdapter adapter;

    private ArrayList<AppInfo> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        results = new ArrayList<>();
        findViewById(R.id.btn_scan).setOnClickListener(this);
        findViewById(R.id.btn_clean).setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppCacheAdapter(this, results);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                ScanAsyncTask task = new ScanAsyncTask();
                task.execute();
                break;
            case R.id.btn_clean:
                break;
        }
    }

    class ScanAsyncTask extends AsyncTask<Void, Void, Void> {

        AppCleanEngine engine = new AppCleanEngine();

        @Override
        protected Void doInBackground(Void... params) {
            results = engine.scanAppCache(MainActivity.this);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Demo", "adapter notifydatachanged:" + results.toString());
                    adapter.notifyDataSetChanged();
                }
            });
            return null;
        }
    }
}