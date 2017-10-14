package com.xiaobin.soapandroid.util;

import android.content.Context;

import java.util.UUID;

/**
 * 工具类
 * Created by XWB on 2017-10-10.
 */
public class Util {

    public static Prop getProp(Context context, String name){
        return new Prop(context,name);
    }

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
