package com.xiaobin.soapandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 操作数据库
 * Created by XWB on 2017-10-10.
 */
public class Db {

    public String getDbName(){
        return "soap.db";
    }

    private static Db instance;
    private Db(){

    }

    public static Db getInstance(){
        if(instance != null){
            return instance;
        }
        instance = new Db();
        return instance;
    }

    public void init(){

    }

    public SQLiteDatabase getDb(Context context){
        String path = context.getFilesDir().getPath();
        if(!path.endsWith("/")){
            path += "/";
        }
        path += "data/com.xiaobin.db/" + getDbName();
        return SQLiteDatabase.openOrCreateDatabase(path,null);
    }

    public void createTable(){

    }
}
