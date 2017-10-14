package com.xiaobin.soapandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.xiaobin.soapandroid.util.Prop;
import com.xiaobin.soapandroid.util.Util;

/**
 * 任务操作数据库表
 * Created by XWB on 2017-10-10.
 */
public class TasksDbHelper extends SQLiteOpenHelper{

    public static final int VERSION = 1;
    private static String TAG = "TasksSqLite";
    private Context context;

    public String getTableName(){
        return "soap_task";
    }

    public TasksDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Prop prop = Util.getProp(context,"table.properties");
        String sql = prop.getString("tasks");
        if(sql == null){
            return;
        }
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
