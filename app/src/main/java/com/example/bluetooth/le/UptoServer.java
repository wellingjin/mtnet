package com.example.bluetooth.le;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

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
        mtHttpManager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                Log.i("database_network_succ", requestId + " " + JSONResponse.toString());
            }

            @Override
            public void onFailure(int requestId, int errorCode) {
                Log.i("database_network_fail", requestId + " " + errorCode);
            }
        });
        Log.i("database_network", "更新成功");
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

            BloodPressureBean bpbean=new BloodPressureBean(this.context);
            String sql="UPDATE "+bpbean.TABLENAME+" SET "+bpbean.ISUPDATE+
                    "='1' WHERE "+bpbean.UPDATETIME+"='"+updatetime+"'";
            bpbean.update_data(sql);
        }
    }
}
