package com.joe.cachecleaner.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import com.joe.cachecleaner.model.AppInfo;
import com.joe.cachecleaner.model.CacheFileList;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Description  缓存清理引擎
 * 需要添加
 * Created by chenqiao on 2015/12/1.
 */
public class AppCleanEngine {

    private PackageManager packageManager;

    private Context mContext;

    private final Object lockObj = new Object();

    private long oneAppCacheSize;

    /**
     * 扫描非系统应用缓存，会阻塞线程，需要放在异步中。
     */
    public ArrayList<AppInfo> scanAppCache(Context context) {
        mContext = context;
        ArrayList<AppInfo> results = new ArrayList<>();
        if (packageManager == null) {
            packageManager = context.getPackageManager();
        }
        List<ApplicationInfo> appinfos = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
        for (ApplicationInfo info : appinfos) {
            AppInfo item = new AppInfo();
            item.setAppName(String.valueOf(info.loadLabel(packageManager)));
            item.setAppIcon(info.loadIcon(packageManager));
            item.setPackageName(info.packageName);
            synchronized (lockObj) {
                queryAppCacheSize(info.packageName);
                try {
                    lockObj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            item.setAppCacheSize(oneAppCacheSize);
            oneAppCacheSize = 0;
            results.add(item);
        }
        return results;
    }

    private void queryAppCacheSize(String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            if (packageManager == null) {
                packageManager = mContext.getPackageManager();
            }
            String strGetPackageSizeInfo = "getPackageSizeInfo";
            try {
                Method getPackageSizeInfo = packageManager.getClass().getDeclaredMethod(strGetPackageSizeInfo, String.class, int.class, IPackageStatsObserver.class);
                getPackageSizeInfo.invoke(packageManager, packageName, android.os.Process.myUid() / 100000, mStatsObserver);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            synchronized (lockObj) {
                if (succeeded) {
                    oneAppCacheSize = pStats.cacheSize;
                } else {
                    oneAppCacheSize = 0;
                }
                lockObj.notify();
            }
        }
    };

    /**
     * 清理非系统应用缓存,需要系统root权限
     */
    public void cleanAppCache(List<String> list) {
        for (String info : list) {
            String packageName = info;
            String strDeleteApplicationCacheFiles = "deleteApplicationCacheFiles";
            try {
                Method deleteApplicationCacheFiles = packageManager.getClass().getDeclaredMethod(strDeleteApplicationCacheFiles, String.class, IPackageDataObserver.class);
                Log.i("Z-AppCleanEngine", "cleanAppCache: packageName:" + packageName);
                deleteApplicationCacheFiles.invoke(packageManager, packageName, new IPackageDataObserver() {
                    @Override
                    public IBinder asBinder() {
                        return null;
                    }

                    @Override
                    public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

                    }
                });
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private IPackageDataObserver.Stub mDataObserver = new IPackageDataObserver.Stub() {
        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            Log.d("AppCleanEngine", "clean " + packageName + " " + succeeded);
        }
    };

    public void setDataObserver(IPackageDataObserver.Stub dataObserver) {
        this.mDataObserver = dataObserver;
    }

    /**
     * 清空所有缓存，无法指定应用
     */
    public void cleanAllCache() {
        try {
            if (packageManager == null) {
                packageManager = mContext.getPackageManager();
            }
            Method localMethod = packageManager.getClass().getMethod("freeStorageAndNotify", Long.TYPE,
                    IPackageDataObserver.class);
            Long localLong = getEnvironmentSize() - 1L;
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = localLong;
            localMethod.invoke(packageManager, localLong, mDataObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getEnvironmentSize() {
        File localFile = Environment.getDataDirectory();
        long l1;
        if (localFile == null) {
            return 0L;
        }
        while (true) {
            String str = localFile.getPath();
            StatFs localStatFs = new StatFs(str);
            long l2 = localStatFs.getBlockSize();
            l1 = localStatFs.getBlockCount() * l2;
            return l1;
        }
    }

    /**
     * 扫描json文件提供的文件目录，不会阻塞线程，但是也推荐放在异步中
     */
    public ArrayList<AppInfo> scanCacheFileByJsonFile(Context context) {
        mContext = context;
        ArrayList<AppInfo> results = new ArrayList<>();
        CacheFileList fileList = DecodeUtil.decodeListsJson(mContext);
        if (fileList == null) {
            return null;
        }
        String rootPath = getRootPath();

        for (CacheFileList.DatasEntity entity : fileList.getDatas()) {
            AppInfo info = new AppInfo();
            String packageName = entity.getPackageName();
            String appName = entity.getAppName();
            long size = 0;
            for (CacheFileList.DatasEntity.FileDirsEntity fileDirsEntity : entity.getFileDirs()) {
                String dir = rootPath + fileDirsEntity.getDir();
                Log.d("Engine", "file dir:" + dir);
                long tmp = calculateDirSize(new File(dir));
                size += tmp;
                Log.d("Engine", "file size:" + tmp);
            }
            info.setPackageName(packageName);
            info.setAppCacheSize(size);
            info.setAppName(appName);
            results.add(info);
        }
        return results;
    }

    /**
     * 根据包名来删除Json文件中罗列的文件夹,配合{@link #scanCacheFileByJsonFile(Context)}使用
     */
    public void deleteCacheFilesFromJsonFile(Context context, List<String> packageNames) {
        mContext = context;
        CacheFileList fileList = DecodeUtil.decodeListsJson(mContext);
        if (fileList == null) {
            return;
        }

        String rootPath = getRootPath();
        for (CacheFileList.DatasEntity entity : fileList.getDatas()) {
            String packageName = entity.getPackageName();
            Log.d("Engine", "need to clear app packagename:" + packageName);
            if (packageNames.contains(packageName)) {
                for (CacheFileList.DatasEntity.FileDirsEntity fileDirsEntity : entity.getFileDirs()) {
                    String dir = rootPath + fileDirsEntity.getDir();
                    Log.d("Engine", "delete file:" + dir);
                    deleteFile(dir);
                }
            }
        }
    }

    /**
     * 删除文件（文件夹）
     */
    private boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    /**
     * 获取根目录
     */
    private String getRootPath() {
        String rootPath = Environment.getDataDirectory().getAbsolutePath() + File.separator;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator; // 取得sdcard文件路径
        }
        return rootPath;
    }

    /**
     * 计算文件（文件夹）的大小
     */
    private long calculateDirSize(File file) {
        long size = 0;
        if (!file.exists()) {
            return size;
        }
        if (file.isFile()) {
            size += file.length();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File item : files) {
                    size += calculateDirSize(item);
                }
            }
        }
        return size;
    }
}