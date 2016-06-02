package com.welling.kinghacker.bean;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.creative.filemanage.ECGFile;
import com.welling.kinghacker.database.TableItem;
import com.welling.kinghacker.tools.PublicRes;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;

/**
 * Created by KingHacker on 5/3/2016.
 * 心电数据
 */
public class ELCBean  extends MTBean{

    static public String
            FILENAME = "filename",
            ISUPDATE = "isupdate",
            CREATETIME = "createTime";
    public String TABLENAME;
    public String fileName;
    public int isUpdate = NO;
    public String createTime;
    public ELCBean(Context context){
        super(context);
    }

    @Override
    public void init() {
        TABLENAME = "ECG"+SystemTool.getSystem(context).getStringValue(PublicRes.ACCOUNT);
    }

    public void update(){
        ContentValues cv = new ContentValues();
        isUpdate = YES;
        cv.put(ISUPDATE,isUpdate);
        manager.updateByFieldEqual(TABLENAME,FILENAME,fileName,cv);
    }


    @Override
    public void insert() {
        if (fileName == null) return;
        manager.deleteByFieldEqual(TABLENAME, FILENAME, fileName);
        ContentValues cv = new ContentValues();
        cv.put(FILENAME,fileName);
        cv.put(ISUPDATE,NO);
        cv.put(CREATETIME,createTime);
        manager.insert(TABLENAME,cv);
    }

    @Override
    protected void createTable() {
        TableItem item1 = new TableItem(FILENAME,TableItem.M_VARCHAR,50);
        TableItem item2 = new TableItem(ISUPDATE,TableItem.M_INTEGER,1);
        TableItem item3 = new TableItem(CREATETIME,TableItem.M_VARCHAR,30);
        ArrayList<TableItem> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        Log.i("data", item1.toString());
        manager.createTable(TABLENAME, items);
    }
}
