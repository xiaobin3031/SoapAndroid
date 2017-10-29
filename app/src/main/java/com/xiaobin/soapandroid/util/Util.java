package com.xiaobin.soapandroid.util;

import android.content.Context;
import com.xiaobin.soapandroid.R;

import java.lang.reflect.Field;
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

    public static boolean isEmpty(Object obj) {
        return obj == null || obj instanceof String && obj.toString().trim().equals("");
    }

    public static int viewId(String name){
        try {
            Field field = R.id.class.getField(name);
            return field.getInt(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isTrue(String string){
        return !isEmpty(string) && ("t".equalsIgnoreCase(string) || "true".equalsIgnoreCase(string) || "1".equals(string));
    }
}
