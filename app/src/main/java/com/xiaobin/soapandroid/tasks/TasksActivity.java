package com.xiaobin.soapandroid.tasks;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.xiaobin.soapandroid.R;
import com.xiaobin.soapandroid.db.Db;
import com.xiaobin.soapandroid.db.TasksDbHelper;
import com.xiaobin.soapandroid.util.Const;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private List<Map<String,Object>> data = new ArrayList<>();
    private SimpleAdapter simpleAdapter;
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };
    private Handler.Callback handlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            dialog.dismiss();
            switch(msg.what){
                case Const.FETCH:
                    simpleAdapter = new SimpleAdapter(getApplicationContext(), data,R.layout.list_tasks,new String[]{
                            getString(R.string.colTitle)
                            ,getString(R.string.colTaskInfo)
                    },new int[]{
                            R.id.listTasksTitle
                            ,R.id.listTasksDescription
                    });
                    ListView listView = (ListView) findViewById(R.id.taskListView);
                    listView.setAdapter(simpleAdapter);
                    listView.setOnItemClickListener(itemClickListener);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        ImageButton tasksAddButton = (ImageButton) findViewById(R.id.tasksAddButton);
        tasksAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TasksAddActivity.class);
                startActivityForResult(intent, RESULT_FIRST_USER);
            }
        });

        fetchList();
    }

    private void fetchList(){
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(getString(R.string.textFetching));
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                TasksDbHelper dbHelper = new TasksDbHelper(getApplicationContext(), Db.getInstance().getDbName(),null,TasksDbHelper.VERSION);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery("select * from " + dbHelper.getTableName() + " where 1 = 1 order by create_time desc",null);
                    if(cursor.moveToFirst()){
                        String[] columns = cursor.getColumnNames();
                        Map<String,Object> map;
                        do{
                            map = new HashMap<>();
                            for(String column : columns){
                                map.put(column,cursor.getString(cursor.getColumnIndex(column)));
                            }
                            data.add(map);
                        }while(cursor.moveToNext());
                    }

                }finally{
                    if(cursor != null){
                        cursor.close();
                    }
                }

                Message message = new Message();
                message.what = Const.FETCH;
                handlerCallback.handleMessage(message);

                Looper.loop();
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case RESULT_FIRST_USER:
                if(resultCode == RESULT_OK){
                    String[] columns = data.getStringArrayExtra(getString(R.string.columns));
                    Map<String,Object> map = new HashMap<>();
                    for(String column : columns){
                        map.put(column,data.getStringExtra(column));
                    }
                    this.data.add(0,map);
                    simpleAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
