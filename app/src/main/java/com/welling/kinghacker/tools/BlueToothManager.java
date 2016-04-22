package com.welling.kinghacker.tools;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import com.welling.kinghacker.mtdata.BlueToothStruct;

import java.util.List;

/**
 * Created by KingHacker on 3/25/2016.
 **/
public class BlueToothManager {
    private BluetoothAdapter bluetoothAdapter;
    private BlueToothDiscover blueToothDiscover;

    private BluetoothSocket socket;

    private Context context;

    public BlueToothManager(Context context){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }
//公用方法
    public boolean isBlueToothEnable(){
        return bluetoothAdapter.isEnabled();
    }

    public boolean isHaveBlueTooth(){
        return bluetoothAdapter != null;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    //搜索蓝牙相关方法
    public boolean startSearch(){
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
            return false;
        }
        blueToothDiscover = new BlueToothDiscover(context,bluetoothAdapter);
        blueToothDiscover.startSearch();
        return true;
    }

    public void setBlueToothDiscoverStaic(BlueToothDiscover.SearchBlueToothState searchBlueToothState){
        blueToothDiscover.setSearchBlueToothState(searchBlueToothState);
    }

    public List<BlueToothStruct> getBoundList() {
        return blueToothDiscover.getBoundList();
    }

    public List<BlueToothStruct> getUnBondedList() {
        return blueToothDiscover.getUnBondedList();
    }

//连接蓝牙相关方法
    public void connet(String macAddress){
        BlueToothConnet connet = new BlueToothConnet(macAddress,bluetoothAdapter);
        connet.startConnet();
    }
}
