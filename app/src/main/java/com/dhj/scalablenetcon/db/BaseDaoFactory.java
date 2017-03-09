package com.dhj.scalablenetcon.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * Created by duanhuangjun on 17/3/9.
 */

public class BaseDaoFactory {
    private String sqliteDatabasePath;

    private SQLiteDatabase sqLiteDatabase;

    private static  BaseDaoFactory instance=new BaseDaoFactory();
    private BaseDaoFactory()
    {
        sqliteDatabasePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/teacher.db";
        openDatabase();
    }
    public  synchronized  <T extends  BaseDao<M>,M> T
        getDataHelper(Class<T> clazz,Class<M> entityClass)
        {
            BaseDao baseDao=null;
            try {
                baseDao=clazz.newInstance();
                baseDao.init(entityClass,sqLiteDatabase);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            return (T) baseDao;
        }
    private void openDatabase() {
        this.sqLiteDatabase=SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath,null);
    }

    public  static  BaseDaoFactory getInstance()
    {
        return instance;
    }
}
