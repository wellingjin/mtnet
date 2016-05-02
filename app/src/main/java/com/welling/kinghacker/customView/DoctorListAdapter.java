package com.welling.kinghacker.customView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.mtdata.AdapterStruct;
import com.welling.kinghacker.tools.SystemTool;

import java.util.List;

/**
 * Created by KingHacker on 3/22/2016.
 **/
public class DoctorListAdapter extends BaseAdapter{

    private List<AdapterStruct> dataaList;
    private Context context;

    public DoctorListAdapter(Context context, List<AdapterStruct> dataList){
        this.dataaList = dataList;
        this.context = context;
    }
    @Override
    public int getCount() {
        return dataaList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Composition composition;
        if (convertView == null){
            composition = new Composition();
            convertView = SystemTool.getSystem(context).getView(R.layout.doctor_info_list_layout);
            composition.profile = (ImageView)convertView.findViewById(R.id.profile);
            composition.nameText = (TextView)convertView.findViewById(R.id.nameText);
            composition.descText = (TextView)convertView.findViewById(R.id.descText);
            convertView.setTag(composition);
        }else {
            composition = (Composition)convertView.getTag();
        }
        composition.profile.setImageResource(dataaList.get(position).profile);
        composition.nameText.setText(String.format("%s  %s",dataaList.get(position).style, dataaList.get(position).name));
        composition.descText.setText(dataaList.get(position).description);
        return convertView;
    }


//
    class Composition{
        public ImageView profile;
        public TextView nameText,descText;
    }
}
