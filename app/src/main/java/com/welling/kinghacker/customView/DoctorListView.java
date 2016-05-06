package com.welling.kinghacker.customView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.welling.kinghacker.activities.DoctorDetailActivity;
import com.welling.kinghacker.bean.DoctorInfoBean;
import com.welling.kinghacker.database.DatabaseManager;
import com.welling.kinghacker.mtdata.AdapterStruct;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 5/1/2016.
 * 医生列表
 */
public class DoctorListView extends ListView{
    private DoctorListAdapter docAdaptor;
    final private String Tag ="DoctorListView";
    public List<AdapterStruct> data = new ArrayList<>();
    public DoctorListView(final Context context){
        super(context);
        docAdaptor = new DoctorListAdapter(context,data);
        setAdapter(docAdaptor);
        setDocList();
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, DoctorDetailActivity.class);
                intent.putExtra("doctorID", data.get(position).doctorID);
                Log.i(Tag,data.get(position).doctorID+"id");
                context.startActivity(intent);
            }
        });
    }


    public void update(){
        Log.i(Tag,"count:"+docAdaptor.getCount());
       docAdaptor.notifyDataSetChanged();
    }
    void setDocList(){
        DatabaseManager manager = new DatabaseManager(getContext());
        JSONObject jsonDocList = manager.getMultiRaw(DoctorInfoBean.TABLENAME, null, null, null);
        DoctorInfoBean doctorInfoBean = new DoctorInfoBean(getContext());
        data.clear();
        try {
            int count = jsonDocList.getInt("count");
            for (int i=0;i<count;i++){
                JSONObject object = jsonDocList.getJSONObject(""+i);
                doctorInfoBean.doctorID = Integer.valueOf(object.getString(DoctorInfoBean.DOCTORID));
                doctorInfoBean.name = object.getString(DoctorInfoBean.USERNAME);
                doctorInfoBean.hospital = object.getString(DoctorInfoBean.HOSPITAL);
                doctorInfoBean.perfession = object.getString(DoctorInfoBean.PERFESSION);
                data.add(new AdapterStruct(doctorInfoBean));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        update();
    }

}
