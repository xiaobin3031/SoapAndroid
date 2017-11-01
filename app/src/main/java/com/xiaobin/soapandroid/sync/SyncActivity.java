package com.xiaobin.soapandroid.sync;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.xiaobin.soapandroid.R;
import com.xiaobin.soapandroid.db.Db;
import com.xiaobin.soapandroid.db.SyncDbHelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Date: 2017/10/29
 @Author: XWB
 @Msg: 同步数据库
 */
public class SyncActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        sync();
    }

    private void sync(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.colUrl),MODE_PRIVATE);
                String urlStr = sharedPreferences.getString(getString(R.string.colSyncUrl),"");
//                if(Util.isEmpty(urlStr)){
                    urlStr = "http://11.25.105.25:8090/sync";
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString(getString(R.string.colSyncUrl),urlStr);
//                    editor.apply();
//                }
                URL url;
                HttpURLConnection httpURLConnection = null;
                DataOutputStream dos = null;
                BufferedReader bufferedReader = null;
                Cursor tablesCursor = null;
                SyncDbHelper dbHelper = new SyncDbHelper(getApplicationContext(), Db.getInstance().getDbName(),null,SyncDbHelper.VERSION);
                try {
                    url = new URL(urlStr);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    String param = getString(R.string.colSyncTables) + "=soap_task";
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestMethod("POST");

                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                    httpURLConnection.setRequestProperty("Charset", getString(R.string.charUTF8));

//                    httpURLConnection.connect();

                    dos = new DataOutputStream(httpURLConnection.getOutputStream());
                    dos.writeBytes(param);
                    dos.flush();
                    dos.close();
                    dos = null;

                    int resultCode = httpURLConnection.getResponseCode();
                    if(HttpURLConnection.HTTP_OK == resultCode){
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),getString(R.string.charUTF8)));
                        while((line = bufferedReader.readLine()) != null){
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        Log.i("debug",stringBuilder.toString());
                        bufferedReader = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    if(dos != null){
                        try {
                            dos.flush();
                            dos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(bufferedReader != null){
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(httpURLConnection != null){
                        httpURLConnection.disconnect();
                    }
                }

                Looper.loop();
            }
        }).start();
    }
}
