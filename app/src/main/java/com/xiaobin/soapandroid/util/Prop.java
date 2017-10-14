package com.xiaobin.soapandroid.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置类
 * Created by XWB on 2017-10-10.
 */
public class Prop {

    private Properties properties;

    Prop(Context context, String name){
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(name);
            properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getString(String key){
        return properties.getProperty(key);
    }
    public String getString(String key,String defaultValue){
        return properties.getProperty(key,defaultValue);
    }

    public Properties getProperties(){
        return properties;
    }
}
