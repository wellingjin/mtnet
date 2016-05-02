package com.welling.kinghacker.customView;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.welling.kinghacker.mtdata.AdapterStruct;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KingHacker on 5/1/2016.
 * 医生列表
 */
public class DoctorListView extends ListView{


    public DoctorListView(Context context){
        super(context);
    }

    public void setData(List<AdapterStruct> data) {
        DoctorListAdapter docAdaptor = new DoctorListAdapter(getContext(),data);
        setAdapter(docAdaptor);
    }

}
