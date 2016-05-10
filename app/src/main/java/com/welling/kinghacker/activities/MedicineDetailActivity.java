package com.welling.kinghacker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.welling.kinghacker.bean.MedicineBean;
import com.welling.kinghacker.tools.MTHttpManager;
import com.welling.kinghacker.tools.PublicRes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KingHacker on 5/1/2016.
 * 药品详细
 */
public class MedicineDetailActivity extends MTActivity{
    TextView name,way,number,count,time,element,indication,reaction,taboo,attention,direction;
    private MedicineBean medicineBean;

    @Override
    protected void onCreate(Bundle instance) {
        super.onCreate(instance);
        setActionBarTitle("详细信息");
        setContentView(R.layout.layout_medicine_detail);
        medicineBean = new MedicineBean(this);
        initView();
        Intent intent = getIntent();
        //medicineID = intent.getIntExtra("medicineID",0);
        String medicine = intent.getStringExtra("medicine");
        Log.i("medicine",medicine);
        try {
            JSONObject object = new JSONObject(medicine);
            medicineBean.medicineID = object.getInt("medicineID");
            medicineBean.way = object.getString("way");
            medicineBean.unit = object.getString("unit");
            medicineBean.number = Float.valueOf(object.getString("number"));
            medicineBean.time = object.getString("time");
            medicineBean.count = object.getString("count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setDetail(medicineBean.medicineID);

    }

    private void initView() {
        name = (TextView)findViewById(R.id.medicineDetailName);
        way = (TextView)findViewById(R.id.medicineDetailWay);
        number = (TextView)findViewById(R.id.medicineDetailNumber);
        count = (TextView)findViewById(R.id.medicineDetailCount);
        time = (TextView)findViewById(R.id.medicineDetailTime);
        element = (TextView)findViewById(R.id.medicineDetailElement);
        indication = (TextView)findViewById(R.id.medicineDetailIndication);
        reaction = (TextView)findViewById(R.id.medicineDetailReaction);
        taboo = (TextView)findViewById(R.id.medicineDetailTaboo);
        direction = (TextView)findViewById(R.id.medicineDetailDirection);
        attention = (TextView)findViewById(R.id.medicineDetailAttention);
    }
    void setDetailView(MedicineBean bean){
        name.setText(bean.medicineName);
        way.setText(bean.way);
        number.setText(String.format("%s%s",bean.number,bean.unit));
        count.setText(String.format("1天%s次",bean.count));
        time.setText(bean.time);
        element.setText(bean.constituent);
        indication.setText(bean.adaptation_disease);
        reaction.setText(bean.adverse_reaction);
        taboo.setText(bean.taboo);
        attention.setText(bean.attentions);
        direction.setText(bean.direction);
    }

    private void setDetail(int id){
        Log.i("Medicine","id:"+id);
        if (id <= 0) {
            makeToast("id error");
            return;
        }
        getLocalMedicineDetail(id);
        MTHttpManager manager = new MTHttpManager();
        manager.setHttpResponseListener(new MTHttpManager.HttpResponseListener() {
            @Override
            public void onSuccess(int requestId, JSONObject JSONResponse) {
                setDetail(JSONResponse);
            }

            @Override
            public void onFailure(int requestId, int errorCode) {

            }
        });
        RequestParams params = new RequestParams();
        params.put("medicineId", id);
        manager.post(params, manager.getRequestID(), "getMedicineById.do");
    }

    private void getLocalMedicineDetail(int id) {
        setDetailView(new MedicineBean(this, id));
    }
    private void setDetail(JSONObject object){
        int state = 0;
        String excption;

        try {
            state = object.getInt(PublicRes.STATE);
            excption = object.getString(PublicRes.EXCEPTION);
            JSONObject medicine = object.getJSONObject("medicineDetail");

            medicineBean.adaptation_disease = medicine.getString("adaptation_disease");
            medicineBean.adverse_reaction = medicine.getString("adverse_reaction");
            medicineBean.attentions = medicine.getString("attentions");
            medicineBean.constituent = medicine.getString("constituent");
            medicineBean.direction = medicine.getString("direction");
            medicineBean.medicineName = medicine.getString("medicineName");
            medicineBean.taboo = medicine.getString("taboo");


        } catch (JSONException e) {
            excption = "格式解析错误";
            e.printStackTrace();
        }
        if (state == 0){
            makeToast(excption);
        }else {
            medicineBean.update();
            setDetailView(medicineBean);
        }
    }
}
