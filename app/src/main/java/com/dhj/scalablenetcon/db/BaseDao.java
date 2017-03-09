package com.dhj.scalablenetcon.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.volleydongnao.db.annotion.DbFiled;
import com.example.administrator.volleydongnao.db.annotion.DbTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/1/12.
 */

public abstract class BaseDao<T> implements IBaseDao<T> {
    private boolean isInit = false;
    protected SQLiteDatabase sqLiteDatabase;
    private Class<T> entityClass;
    private String tableName;
    private Map<String, Field> cacheMap;

    public String getTableName() {
        return tableName;
    }

    @Override
    public Long insert(T entity) {
        ContentValues contentValues = getContentValues(entity);
        long result = sqLiteDatabase.insert(tableName, null, contentValues);
        return result;
    }

    private ContentValues getContentValues(T entity) {
        ContentValues contentValues = new ContentValues();
        try {
            for (Map.Entry<String,Field> me:cacheMap.entrySet()){
                if(me.getValue().get(entity)==null){
                    continue;
                }
                contentValues.put(me.getKey(),me.getValue().get(entity).toString());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return contentValues;
    }

    @Override
    public int update(T entity, T where) {
        ContentValues contentValues = getContentValues(entity);
        Condition condition = new Condition(getContentValues(where));
        int update = sqLiteDatabase.update(tableName, contentValues, condition.whereClause, condition.whereArgs);
        return update;
    }

    @Override
    public int delete(T where) {
        Condition condition = new Condition(getContentValues(where));
        int delete = sqLiteDatabase.delete(tableName, condition.whereClause, condition.whereArgs);
        return delete;
    }

    @Override
    public List<T> query(T where) {
        return query(where,null,null,null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        String limitString = null;
        if(startIndex!=null && limit!=null){
            limitString = startIndex+","+limit;
        }
        Condition condition = new Condition(getContentValues(where));
        Cursor cursor = null;
        List<T> result =new ArrayList<>();
        try
        {
            cursor = sqLiteDatabase.query(tableName, null,condition.getWhereClause(),condition.whereArgs,null,null,orderBy,limitString);
            result=getResult(cursor,where);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return result;
    }

    protected List<T> getResult(Cursor cursor, T where) {
        ArrayList list = new ArrayList();
        Object item;
        while(cursor.moveToNext()){
            try {
                item = where.getClass().newInstance();
                Iterator<Map.Entry<String, Field>> iterator = cacheMap.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<String, Field> entry = iterator.next();
                    String colmunName = entry.getKey();
                    Field field = entry.getValue();
                    int columnIndex = cursor.getColumnIndex(colmunName);
                    Class type = field.getType();
                    if(columnIndex!=-1){
                        if(type==String.class){
                            field.set(item,cursor.getString(columnIndex));
                        }else if(type==Double.class){
                            field.set(item,cursor.getDouble(columnIndex));
                        }else if(type== Integer.class){
                            int value =cursor.getInt(columnIndex);
                            Log.i("dongnao","value="+value);
                            field.set(item,cursor.getInt(columnIndex));
                        }else if(type == Long.class){
                            field.set(item,cursor.getLong(columnIndex));
                        }else if(type == byte[].class){
                            field.set(item,cursor.getBlob(columnIndex));
                        }else{
                            /**
                             * 不支持的类型
                             */
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    protected synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if (!isInit) {
            this.sqLiteDatabase = sqLiteDatabase;
            this.entityClass = entity;
            if (entity.getAnnotation(DbTable.class) == null) {
                tableName = entity.getClass().getSimpleName();
            } else {
                tableName = entity.getAnnotation(DbTable.class).value();
            }
            if (!sqLiteDatabase.isOpen()) {
                return false;
            }

            if (!TextUtils.isEmpty(createTable())) {
                sqLiteDatabase.execSQL(createTable());
            }
            cacheMap = new HashMap<>();
            initCatchMap();
            isInit = true;
        }
        return isInit;
    }

    /**
     * 维护映射关系
     */
    private void initCatchMap() {
        String sql = "select * from " + this.tableName + " limit 1,0";
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql, null);
            /**
             * 表的列名数组
             */
            String[] columnNames = cursor.getColumnNames();
            /**
             * 拿到Filed数组
             */
            Field[] columnFields = entityClass.getFields();
            for (Field filed : columnFields) {
                filed.setAccessible(true);

                Field columnFiled = null;
                String colmunName = null;
                /**
                 * 开始找对应关系
                 */
                for (String cn : columnNames) {
                    String filedName = null;
                    if(filed.getAnnotation(DbFiled.class)!=null){
                        filedName = filed.getAnnotation(DbFiled.class).value();
                    }else {
                        filedName = filed.getName();
                    }
                    if(cn.equals(filedName)){
                        columnFiled = filed;
                        colmunName = cn;
                        break;
                    }
                }
                //找到了对应关系
                if(columnFiled!=null){
                    cacheMap.put(colmunName,columnFiled);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }


    public abstract String createTable();




    class Condition{
        /**
         * 查询条件
         * name=? && password =?
         */
        private String whereClause;

        private  String[] whereArgs;
        public Condition(ContentValues whereClause) {
            ArrayList list=new ArrayList();
            StringBuilder stringBuilder=new StringBuilder();

            stringBuilder.append(" 1=1 ");

            Set keys=whereClause.keySet();
            Iterator iterator=keys.iterator();
            while (iterator.hasNext())
            {
                String key= (String) iterator.next();
                String value= (String) whereClause.get(key);

                if (value!=null)
                {
                    /*
                    拼接条件查询语句
                    1=1 and name =? and password=?
                     */
                    stringBuilder.append(" and "+key+" =?");
                    /**
                     * ？----》value
                     */
                    list.add(value);
                }
            }
            this.whereClause=stringBuilder.toString();
            this.whereArgs= (String[]) list.toArray(new String[list.size()]);

        }

        public String[] getWhereArgs() {
            return whereArgs;
        }

        public String getWhereClause() {
            return whereClause;
        }
    }

}
