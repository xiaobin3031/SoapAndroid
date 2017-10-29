package com.xiaobin.soapandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.xiaobin.soapandroid.model.Code;

import java.util.List;

/**
 * 自定义适配器
 * Created by XWB on 2017-10-29.
 */
public class CodeSpinnerAdapter extends BaseAdapter{
    private List<Code> data;
    private int resourceId;
    private Context context;

    public CodeSpinnerAdapter(Context context,int resourceId,List<Code> data){
        this.data = data;
        this.context = context;
        this.resourceId = resourceId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(resourceId,null);
            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setText(data.get(position).getName());
        }
        return convertView;
    }
}
