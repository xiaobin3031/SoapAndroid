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

/**
 * @Date: 2017/10/28
 @Author: XWB
 @Msg:  任务明细
 */
public class TasksDetailActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private Bundle bundle;
    private Handler.Callback handCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            dialog.dismiss();
            switch(msg.what){
                case Const.INSERT:
                    ContentValues contentValues = (ContentValues) msg.obj;
                    for(String key : contentValues.keySet()){
                        if(!bundle.containsKey(key) || !(contentValues.get(key) instanceof String)){
                            continue;
                        }
                        bundle.putString(key,contentValues.getAsString(key));
                    }
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
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
        setContentView(R.layout.activity_tasks_detail);

        bundle = getIntent().getExtras();

        Spinner taskTypeSpinner = (Spinner) findViewById(R.id.task_type);

        Code code = new Code();
        List<Code> list = code.putCodes(new String[]{"00","05","10","20","90"},new String[]{"bug","误报","代码","优化","重复提交"});

        CodeSpinnerAdapter arrayAdapter = new CodeSpinnerAdapter(this,android.R.layout.simple_spinner_item,list);
        taskTypeSpinner.setAdapter(arrayAdapter);

        ImageButton backButton = (ImageButton) findViewById(R.id.go_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton saveButton = (ImageButton) findViewById(R.id.tasksSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        if(bundle != null){
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_tasks_detail);

            if(relativeLayout != null){
                setData(relativeLayout,bundle);
            }
        }
    }

    private void saveData(){
        dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(R.string.textSaving);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                CharSequence title = ((TextView)findViewById(R.id.title)).getText();
                if(title == null || title.toString().trim().equals("")){
                    dialog.dismiss();
                    Toast.makeText(TasksDetailActivity.this, R.string.hintTitle, Toast.LENGTH_SHORT).show();
                    return;
                }
                CharSequence description = ((TextView)findViewById(R.id.task_info)).getText();
                if(description == null || description.toString().trim().equals("")){
                    description = title;
                }

                CheckBox isNext = (CheckBox) findViewById(R.id.is_next);
                Spinner taskTypeSpinner = (Spinner) findViewById(R.id.task_type);
                Code selectedType = (Code) taskTypeSpinner.getSelectedItem();
                TasksDbHelper dbHelper = new TasksDbHelper(TasksDetailActivity.this, Db.getInstance().getDbName(),null,TasksDbHelper.VERSION);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(getString(R.string.colTitle),title.toString());
                contentValues.put(getString(R.string.colTaskInfo),description.toString());
                contentValues.put(getString(R.string.colTaskType),selectedType == null ? getString(R.string.val00) : selectedType.getCode());
                contentValues.put(getString(R.string.colUpdateUser),getString(R.string.valSelfUUID));
                contentValues.put(getString(R.string.colUpdateTime),System.currentTimeMillis());
                contentValues.put(getString(R.string.colIsNext),isNext.isChecked() ? getString(R.string.val1) : getString(R.string.val0));

                int result = db.update(dbHelper.getTableName(),contentValues,getString(R.string.colTaskId) + " = ?",new String[]{bundle.getString(getString(R.string.colTaskId))});

                Message message = new Message();
                message.what = result == -1 ? Const.NONE : Const.INSERT;
                message.obj = contentValues;
                handCallback.handleMessage(message);

                Looper.loop();
            }
        }).start();
    }

    private void setData(RelativeLayout relativeLayout, Bundle bundle){
        View view;
        for(String key : bundle.keySet()){
            int viewId = Util.viewId(key);
            if(viewId == -1){
                continue;
            }
            view = relativeLayout.findViewById(viewId);
            if(view == null){
                continue;
            }
            if(view instanceof EditText){
                ((EditText) view).setText(bundle.getString(key));
            }else if(view instanceof CheckBox){
                ((CheckBox)view).setChecked(Util.isTrue(bundle.getString(key)));
            }
        }
    }

}
