package com.ibg.receipt.job.encrypt;

import com.alibaba.fastjson.JSONArray;
import com.ibg.receipt.util.StringUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class EncryptRequestVo {
    //执行表名字
    private String tableName;
    //执行字段名
    private List<String> fieldName;
    //开始id(可选)
    private Long startId;
    //手动执行条数(可选)
    private Integer execSize;

    public boolean check(){
        return StringUtils.isNotBlank(tableName) && fieldName != null && fieldName.size() > 0 ;
    }


    public static void main(String[] args) {
        List<String> a = new ArrayList<>();
        a.add("123");
        a.add("123=");
        System.out.println(JSONArray.toJSON(a));
    }




}
