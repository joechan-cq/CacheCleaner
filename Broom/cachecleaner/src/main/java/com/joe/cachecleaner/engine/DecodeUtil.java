package com.joe.cachecleaner.engine;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.joe.cachecleaner.model.CacheFileList;

import java.io.IOException;
import java.io.InputStream;

/**
 * Description 解析json文件获取清理的文件列表
 * Created by chenqiao on 2015/12/2.
 */
public class DecodeUtil {

    public static CacheFileList decodeListsJson(Context context) {
        try {
            InputStream is = context.getAssets().open("cache_list.json");
            int length = is.available();
            byte[] data = new byte[length];
            is.read(data);
            String jsonStr = new String(data);
            Gson gson = new Gson();
            CacheFileList result = gson.fromJson(jsonStr, CacheFileList.class);
            return result;
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}