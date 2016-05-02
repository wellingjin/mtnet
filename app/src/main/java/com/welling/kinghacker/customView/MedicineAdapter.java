package com.welling.kinghacker.customView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.mtdata.AdapterStruct;
import com.welling.kinghacker.mtdata.MedicineAdapterStruct;
import com.welling.kinghacker.tools.SystemTool;

import java.util.List;

/**
 * Created by KingHacker on 3/22/2016.
 **/
public class MedicineAdapter extends BaseAdapter{

    private List<MedicineAdapterStruct> dataaList;
    private Context context;

    public MedicineAdapter(Context context, List<MedicineAdapterStruct> dataList){
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
            convertView = new FrameLayout(context);
            View childView1= SystemTool.getSystem(context).getView(R.layout.layout_medicine_list) ;
            View childView2 =SystemTool.getSystem(context).getView(R.layout.layout_list_header);
            if (dataaList.get(position).isSeccsion){

                ((FrameLayout)convertView).addView(childView2);
            }else {
                ((FrameLayout)convertView).addView(childView1);

            }
            composition.timeText = (TextView)childView2.findViewById(R.id.listHeader);
            composition.nameText = (TextView)childView1.findViewById(R.id.medicineNameText);
            composition.countText = (TextView)childView1.findViewById(R.id.countText);
            composition.wayText = (TextView)childView1.findViewById(R.id.medicineWay);
            convertView.setTag(composition);

        }else {
            composition = (Composition)convertView.getTag();
        }
        if (dataaList.get(position).isSeccsion){
            composition.timeText.setText( dataaList.get(position).eatTime);
        }else {
            composition.countText.setText(dataaList.get(position).count);
            composition.wayText.setText(dataaList.get(position).way);
            composition.nameText.setText(dataaList.get(position).medicineName);
        }

        return convertView;
    }


    //
    class Composition{

        public TextView nameText,wayText,timeText,countText;
    }
}
