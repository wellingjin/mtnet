package com.welling.kinghacker.database;

/**
 * Created by KingHacker on 4/29/2016.
 * 表的字段及属性
 */
public class TableItem {

    static public String
                            M_TIME = "time",// 包含了 小时、分钟、秒。
                            M_DATA = "data",//包含了 年份、月份、日期
                            M_BLOB = "blob",//值是BLOB数据块，以输入的数据格式进行存储。如何输入就如何存储,不改  变格式。
                            M_TEXT = "text",//值为文本字符串,使用数据库编码存储(TUTF-8, UTF-16BE or UTF-16-LE).
                            M_REAL = "real",
                            M_INTEGER = "integer",
                            M_VARCHAR = "varchar",//长度不固定且其最大长度为 n 的字串，n不能超过 4000
                            M_CHAR = "char";//长度固定为n的字串，n不能超过 254。
    public String fieldName;
    public String fieldType;
    public int length;
    public String attribute;
    public TableItem(String field){
        this(field,M_VARCHAR,4000);
    }
    public TableItem(String field,String fieldType){
        this(field,fieldType,0);
    }
    public TableItem(String field,String fieldType,int len){
        this(field,fieldType,len,null);
    }
    public TableItem(String field,String fieldType,int len,String attribute){
        this.fieldName = field;
        this.fieldType = fieldType;
        this.length = len;
        this.attribute = attribute;
    }
    @Override
    public String toString(){
        return "name:"+fieldName+" type:"+fieldType+" length:"+length+" attribute:"+attribute;
    }


}
