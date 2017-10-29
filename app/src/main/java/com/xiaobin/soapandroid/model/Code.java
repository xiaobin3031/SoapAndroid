package com.xiaobin.soapandroid.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉值
 * Created by XWB on 2017-10-10.
 */
public class Code {

    private String code;
    private String name;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Code> putCodes(String[] codes,String[] names){
        List<Code> list = new ArrayList<>();
        Code code;
        for(int i=0;i<codes.length;i++){
            code = new Code();
            code.setCode(codes[i]);
            code.setName(names[i]);
            list.add(code);
        }
        return list;
    }
}
