package com.joe.cachecleaner.model;

import java.util.List;

/**
 * Description
 * Created by chenqiao on 2015/12/2.
 */
public class CacheFileList {

    /**
     * packageName : com.joe.test
     * fileDirs : [{"dir":"MyLog"},{"dir":"MyCache"}]
     */

    private List<DatasEntity> datas;

    public void setDatas(List<DatasEntity> datas) {
        this.datas = datas;
    }

    public List<DatasEntity> getDatas() {
        return datas;
    }

    public static class DatasEntity {
        private String packageName;
        /**
         * dir : MyLog      相对于存储根目录的相对地址
         */

        private List<FileDirsEntity> fileDirs;

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public void setFileDirs(List<FileDirsEntity> fileDirs) {
            this.fileDirs = fileDirs;
        }

        public String getPackageName() {
            return packageName;
        }

        public List<FileDirsEntity> getFileDirs() {
            return fileDirs;
        }

        public static class FileDirsEntity {
            private String dir;

            public void setDir(String dir) {
                this.dir = dir;
            }

            public String getDir() {
                return dir;
            }
        }
    }
}
