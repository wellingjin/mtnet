package com.example.bluetooth.le;

import android.content.ContentResolver;
import android.content.Context;

import com.welling.kinghacker.bean.BloodPressureBean;
import com.welling.kinghacker.tools.MTHttpManager;

import org.json.JSONObject;

/**
 * Created by zsw on 2016/5/16.
 */
public class UptoServer {
    private Context context=null;
    public UptoServer(Context context){
        this.context=context;
    }
    public boolean upToServer(){
        BloodPressureBean bpbean=new BloodPressureBean(this.context);
        JSONObject jsonObject=bpbean.getNotUptoServer();
        MTHttpManager mtHttpManager=new MTHttpManager();
        mtHttpManager.updateToCloud(context,jsonObject.toString(),MTHttpManager.BP,0);
        try{
            updatelocalindb(jsonObject);//更新本地数据库
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }
    void updatelocalindb(JSONObject jsonObject)throws Exception{
        for(int i=0;i<(int)jsonObject.get("count");i++){
            JSONObject item=(JSONObject)jsonObject.get(i+"");
            String updatetime=(String)item.get("UpdateTime");
            String sql="UPDATE "+BloodPressureBean.TABLENAME+" SET "+BloodPressureBean.ISUPDATE+
                    "='1' WHERE "+BloodPressureBean.UPDATETIME+"="+updatetime;
            BloodPressureBean bpbean=new BloodPressureBean(this.context);
            bpbean.update_data(sql);
        }
    }
}
