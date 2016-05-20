package com.example.bluetooth.le;

import android.content.Context;
import android.util.Log;

import com.welling.kinghacker.bean.BloodPressureBean;
import com.welling.kinghacker.tools.MTHttpManager;

import org.json.JSONObject;

import android.os.Handler;

/**
 * Created by zsw on 2016/5/16.
 */
public class UptoServer {
    private Context context=null;
    private JSONObject jsonObject;
    private int num,count;
    boolean state;
    private Handler mHandler;
    public UptoServer(Context context,Handler mHandler){
        this.context=context;
        this.mHandler=mHandler;
    }
    public void upToServer(){
        BloodPressureBean bpbean=new BloodPressureBean(this.context);
        jsonObject=bpbean.getNotUptoServer();
        MTHttpManager mtHttpManager=new MTHttpManager();
        try{
            count=(int)jsonObject.get("count");
            for(int i=0;i<count;i++){
                num=i;
                mtHttpManager.updateToCloud(context, ((JSONObject)jsonObject.get(i+"")).toString(), MTHttpManager.BP, 0);
                mtHttpManager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
                    @Override
                    public void onSuccess(int requestId, JSONObject JSONResponse) {
                        Log.i("database_network_succ", requestId + " " + JSONResponse.toString());
                        Log.i("database_network", "上传成功"+num);

                        if(num==count-1){
                            state=true;
                            try {
                                updatelocalindb(jsonObject);//更新本地数据库
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mHandler.sendEmptyMessage(SampleGattAttributes.SUCCESS);
                        }
                    }
                    @Override
                    public void onFailure(int requestId, int errorCode) {
                        Log.i("database_network_fail", requestId + " " + errorCode);
                        state=false;
                        mHandler.sendEmptyMessage(SampleGattAttributes.FAILED);
                    }
                });
            }
        }catch (Exception e){e.printStackTrace();}
    }
    void updatelocalindb(JSONObject jsonObject)throws Exception{
        for(int i=0;i<(int)jsonObject.get("count");i++) {
            String updatetime = (String)((JSONObject) jsonObject.get(i+"")).get("time");
            BloodPressureBean bpbean = new BloodPressureBean(this.context);
            String sql = "UPDATE " + bpbean.TABLENAME + " SET " + bpbean.ISUPDATE +
                    "='1' WHERE " + bpbean.UPDATETIME + "='" + updatetime + "'";
            bpbean.update_data(sql);
        }
    }
}
