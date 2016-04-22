package com.welling.kinghacker.tools;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.welling.kinghacker.mtdata.BlueToothStruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by KingHacker on 3/25/2016.
 **/
public class BlueToothDiscover {
    private BroadcastReceiver receiver;
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private List<BlueToothStruct> boundList;
    private List<BlueToothStruct> unBondedList;
    private SearchBlueToothState searchBlueToothState;

    public BlueToothDiscover(Context context,BluetoothAdapter bluetoothAdapter){
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        init();
    }
    private void init(){
        boundList = new ArrayList<>();
        unBondedList = new ArrayList<>();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // 获得已经搜索到的蓝牙设备
                if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // 搜索到的不是已经绑定的蓝牙设备
                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                        BlueToothStruct struct = new BlueToothStruct(device.getName(), device.getAddress());
                        unBondedList.add(struct);
                        searchBlueToothState.actionFound(struct);

                    }
                    // 搜索完成
                } else if (action
                        .equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    searchBlueToothState.searchFinish();
                }
            }
        };

    }

    public void startSearch(){
        // 获取所有已经绑定的蓝牙设备
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : devices) {
                boundList.add(new BlueToothStruct(bluetoothDevice.getName(),bluetoothDevice.getAddress()));
            }
        }
        // 注册用以接收到已搜索到的蓝牙设备的receiver
        IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(receiver, mFilter);
        // 注册搜索完时的receiver
        mFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(receiver, mFilter);

        // 如果正在搜索，就先取消搜索
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        // 开始搜索蓝牙设备,搜索到的蓝牙设备通过广播返回
        bluetoothAdapter.startDiscovery();
    }

    public List<BlueToothStruct> getBoundList() {
        return boundList;
    }

    public List<BlueToothStruct> getUnBondedList() {
        return unBondedList;
    }

    public void setSearchBlueToothState(SearchBlueToothState searchBlueToothState) {
        this.searchBlueToothState = searchBlueToothState;
    }

    public interface SearchBlueToothState{
        void actionFound(BlueToothStruct device);
        void searchFinish();
    }
}
