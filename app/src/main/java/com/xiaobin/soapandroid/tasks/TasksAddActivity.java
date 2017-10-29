package com.xiaobin.soapandroid.tasks;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.xiaobin.soapandroid.R;
import com.xiaobin.soapandroid.adapter.CodeSpinnerAdapter;
import com.xiaobin.soapandroid.db.Db;
import com.xiaobin.soapandroid.db.TasksDbHelper;
import com.xiaobin.soapandroid.model.Code;
import com.xiaobin.soapandroid.util.Const;
import com.xiaobin.soapandroid.util.Util;

import java.util.List;
import java.util.Set;

/**
 * @Date: 2017/10/10
 @Author: XWB
 @Msg:  添加任务
 */
public class TasksAddActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private Handler.Callback hanCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            dialog.dismiss();
            switch(msg.what){
                case Const.INSERT:
                    ContentValues contentValues = (ContentValues) msg.obj;
                    Intent intent = new Intent();
                    Set<String> keys = contentValues.keySet();
                    String[] columns = new String[keys.size()];
                    int i=0;
                    for(String key : keys){
                        intent.putExtra(key,contentValues.getAsString(key));
                        columns[i] = key;
                        i++;
                    }
                    intent.putExtra(getString(R.string.columns),columns);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
            }
            return true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_add);

        Spinner taskTypeSpinner = (Spinner) findViewById(R.id.tasksType);

        Code code = new Code();
        List<Code> list = code.putCodes(new String[]{"00","05","10","20","90"},new String[]{"bug","误报","代码","优化","重复提交"});

        CodeSpinnerAdapter arrayAdapter = new CodeSpinnerAdapter(this,android.R.layout.simple_spinner_item,list);
        taskTypeSpinner.setAdapter(arrayAdapter);

        ImageButton tasksSaveButton = (ImageButton) findViewById(R.id.tasksSaveButton);
        tasksSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.go_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @SuppressWarnings("unchecked")
    private void saveData(){
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(getString(R.string.textSaving));
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                CharSequence title = ((TextView)findViewById(R.id.tasksTitle)).getText();
                if(title == null || title.toString().trim().equals("")){
                    dialog.dismiss();
                    Toast.makeText(TasksAddActivity.this, R.string.hintTitle, Toast.LENGTH_SHORT).show();
                    return;
                }
                CharSequence description = ((TextView)findViewById(R.id.tasksDescription)).getText();
                if(description == null || description.toString().trim().equals("")){
                    description = title;
                }

                CheckBox isNext = (CheckBox) findViewById(R.id.tasksNextPublish);
                Spinner taskTypeSpinner = (Spinner) findViewById(R.id.tasksType);
                Code selectedType = (Code) taskTypeSpinner.getSelectedItem();
                TasksDbHelper dbHelper = new TasksDbHelper(TasksAddActivity.this, Db.getInstance().getDbName(),null,TasksDbHelper.VERSION);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(getString(R.string.colTaskId), Util.uuid());
                contentValues.put(getString(R.string.colTitle),title.toString());
                contentValues.put(getString(R.string.colTaskInfo),description.toString());
                contentValues.put(getString(R.string.colTaskType),selectedType == null ? getString(R.string.val00) : selectedType.getCode());
                contentValues.put(getString(R.string.colUserId),getString(R.string.valSelfUUID));
                contentValues.put(getString(R.string.colCreateUser),getString(R.string.valSelfUUID));
                contentValues.put(getString(R.string.colCreateTime),System.currentTimeMillis());
                contentValues.put(getString(R.string.colIsNext),isNext.isChecked() ? getString(R.string.val1) : getString(R.string.val0));
                long result = db.insert(dbHelper.getTableName(),null,contentValues);

                Message message = new Message();
                message.what = result == -1 ? Const.NONE : Const.INSERT;
                message.obj = contentValues;
                hanCallback.handleMessage(message);

                Looper.loop();
            }
        }).start();
    }
}
