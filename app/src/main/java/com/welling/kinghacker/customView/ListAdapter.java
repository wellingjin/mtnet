package com.welling.kinghacker.customView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.mtdata.AdapterStruct;

import java.util.List;

/**
 * Created by KingHacker on 3/22/2016.
 **/
public class ListAdapter extends BaseAdapter{

    private List<AdapterStruct> dataaList;
    private LayoutInflater layoutInflater;

    public ListAdapter(Context context,List<AdapterStruct> dataList){
        this.dataaList = dataList;
        layoutInflater = LayoutInflater.from(context);
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
        Composition composition = null;
        if (convertView == null){
            composition = new Composition();
            convertView = layoutInflater.inflate(R.layout.doctor_info_list_layout,null);
            composition.profile = (ImageView)convertView.findViewById(R.id.profile);
            composition.nameText = (TextView)convertView.findViewById(R.id.nameText);
            composition.descText = (TextView)convertView.findViewById(R.id.descText);
            convertView.setTag(composition);
        }else {
            composition = (Composition)convertView.getTag();
        }
        composition.profile.setImageResource(dataaList.get(position).profile);
        composition.nameText.setText(dataaList.get(position).style + "  " + dataaList.get(position).name);
        composition.descText.setText(dataaList.get(position).description);
        return convertView;
    }


//
    class Composition{
        public ImageView profile;
        public TextView nameText,descText;
    }
}
