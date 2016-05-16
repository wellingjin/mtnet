package com.welling.kinghacker.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.welling.kinghacker.bean.SugerBean;
import com.welling.kinghacker.customView.BloodSugerView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import cn.novacomm.ble.iGate;
import cn.novacomm.ble.iGateCallBacks;

public class IGateActivity extends MTActivity implements iGateCallBacks {
    private static final int ENABLE_BT_REQUEST_ID = 1;

    private BloodSugerView singleBloodSugerView;
    private Intent intentdata;
    private boolean dataRevice = false;
    private TextView mIGateState;
    private TextView mReceivedData;
    private Button mButtonSend;
    private Button mDataUp;
    private Button mButtonFinish;
    private Button mButtonCleanData;
    private ArrayList<String> mConnectedBluetoothDevicesAddress=new ArrayList<String>();
    private int sendIndex=0;
    public SugerBean sugerBean = null;
    BloodSugerActivity sugerAct;


    //private String mConnectedBluetoothAddress=null;
    private BroadcastReceiver mPairIntentReceiver=new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction()))
            {
                int prevBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                BluetoothDevice aDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(bondState==BluetoothDevice.BOND_BONDED && prevBondState == BluetoothDevice.BOND_BONDING){
                    mIgate.iGateDeviceSetPairState(aDevice.getAddress(), true);
                }
                Log.i("---- pair changed",aDevice.getAddress()+String.format("-%d", bondState));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_igate);

        intentdata = new Intent();
        mIGateState=(TextView) findViewById(R.id.textViewState);
        mReceivedData=(TextView) findViewById(R.id.textViewRxData);
        mButtonFinish = (Button) findViewById(R.id.buttonFinish);
        mButtonCleanData = (Button) findViewById(R.id.cleandatabutton);
        mDataUp = (Button) findViewById(R.id.dataUp);

        mButtonFinish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                IGateActivity.this.finish();
            }
        });
        mButtonSend = (Button) findViewById(R.id.buttonSend);
        mButtonSend.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                String message;
                message = "&N0 41854";
                if(sendIndex<mConnectedBluetoothDevicesAddress.size()){
                    mIgate.iGateDeviceSendData(mConnectedBluetoothDevicesAddress.get(sendIndex), message.getBytes());
                }
                else{
                    sendIndex=0;
                    if(mConnectedBluetoothDevicesAddress.size()>0){
                        mIgate.iGateDeviceSendData(mConnectedBluetoothDevicesAddress.get(sendIndex), message.getBytes());
                        Log.i("------",String.format("iGate State %d",mIgate.iGateDeviceGetState(mConnectedBluetoothDevicesAddress.get(sendIndex)).ordinal()));
                    }
                }
                sendIndex++;
                if(sendIndex==mConnectedBluetoothDevicesAddress.size()){
                    sendIndex=0;
                }
            }
        });
        // DE3A0001-7100-57EF-9190-F1BE84232730
        // C14D2C0A-401F-B7A9-841F-E2E93B80F631
        mIgate = new iGate(this, UUID.fromString("C14D2C0A-401F-B7A9-841F-E2E93B80F631") ,this);
        //mIgate.initialize(true);
        setRightButtonHidden();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mIgate.initialize(false);  // true
        IntentFilter bluetoothPairFilter = new IntentFilter();
        bluetoothPairFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mPairIntentReceiver, bluetoothPairFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mPairIntentReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        setActionBarTitle(getResources().getString(R.string.title_devices));

        if (mIgate.getIGateState()== iGateHostState.iGateHostStateSearching) {
            menu.findItem(R.id.scanning_start).setVisible(false);
            menu.findItem(R.id.scanning_stop).setVisible(true);
            menu.findItem(R.id.scanning_indicator)
                    .setActionView(R.layout.progress_indicator);

        } else {
            menu.findItem(R.id.scanning_start).setVisible(true);
            menu.findItem(R.id.scanning_stop).setVisible(false);
            menu.findItem(R.id.scanning_indicator).setActionView(null);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanning_start:
                mIgate.startScanning(false);
                break;
            case R.id.scanning_stop:
                mIgate.stopScanning();
                break;
        }

        invalidateOptionsMenu();
        return true;
    }

    @Override
    public void onBackPressed(){
        for(int i=0;i<mConnectedBluetoothDevicesAddress.size();i++){
            Log.i("------", String.format("iGate disconnect %s", mConnectedBluetoothDevicesAddress.get(i)));
            mIgate.iGateDeviceDisconnect(mConnectedBluetoothDevicesAddress.get(i));
        }
        if(mIgate.getIGateState()== iGateCallBacks.iGateHostState.iGateHostStateSearching){
            mIgate.stopScanning();
        }
        super.onBackPressed();
    }

    public void iGateHostDidUpdateState(final iGateHostState state){
        Log.i("------", String.format("iGate State Change %d", state.ordinal()));
        switch(state){
            case iGateHostStatePoweredOff:
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
                //mIgate.startScanning(true);
            case iGateHostStateIdle:
            case iGateHostStateSearching:
                // start automatically search when get idle,
                // mIgate.startScanning(true);
                invalidateOptionsMenu();
                break;

            default:
                break;
        }

        // adding to the UI have to happen in UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch(mIgate.getIGateState()){
                    case iGateHostStateIdle:
                        if(mConnectedBluetoothDevicesAddress.size()==0){
                            mIGateState.setText(R.string.str_idle);
                        }
                        else{
                            listConnectedDevices();
                        }
                        break;
                    case iGateHostStateSearching:
                        mIGateState.setText(R.string.str_scanning);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void iGateDeviceFound(final String bluetoothAddress, final int rssi, final byte[] record){
        Log.i("------", String.format("iGate found device %s", bluetoothAddress));
        // If iGate is initialized to automatically connect to device found, there's no need to stop scan and make connection manually
        // mIgate.stopScanning();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIGateState.setText(getString(R.string.str_found) + bluetoothAddress);
                mIgate.stopScanning();
                mIgate.iGateDeviceConnect(bluetoothAddress);
            }
        });
        //Log.i("------", String.format("start connect to %s", bluetoothAddress));
        //mIgate.iGateDeviceConnect(bluetoothAddress);
    }

    public void iGateDeviceConnected(final String bluetoothAddress){
        Log.i("------", String.format("iGate connected %s", bluetoothAddress));
        if(!mConnectedBluetoothDevicesAddress.contains(bluetoothAddress)){
            mConnectedBluetoothDevicesAddress.add(bluetoothAddress);
        }
        //mIgate.iGateDeviceBondService(bluetoothAddress);
        // adding to the UI have to happen in UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //listConnectedDevices();
                Log.i("------", String.format("iGate start bond service to %s", bluetoothAddress));
                mIgate.iGateDeviceBondService(bluetoothAddress);
            }
        });
    }

    public void iGateDeviceServiceBonding(final String bluetoothAddress){
        // adding to the UI have to happen in UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //listConnectedDevices();
                Log.i("------", String.format("iGate bonding service to %s", bluetoothAddress));
                mIgate.iGateDeviceBondServiceContinue(bluetoothAddress);
            }
        });
    }

    public void iGateDeviceServiceBonded(final String bluetoothAddress){
        Log.i("------", String.format("iGate bonded %s", bluetoothAddress));
        if(!mConnectedBluetoothDevicesAddress.contains(bluetoothAddress)){
            mConnectedBluetoothDevicesAddress.add(bluetoothAddress);
        }
        // adding to the UI have to happen in UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listConnectedDevices();
            }
        });
    }

    public void iGateDeviceDisConnected(final String bluetoothAddress){
        Log.i("------", String.format("disconnected %s", bluetoothAddress));
        mConnectedBluetoothDevicesAddress.remove(bluetoothAddress);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mConnectedBluetoothDevicesAddress.size()>0){
                    String allConnectedDevices=new String();
                    for(int i=0;i<mConnectedBluetoothDevicesAddress.size();i++){
                        allConnectedDevices+=" "+mConnectedBluetoothDevicesAddress.get(i);
                    }
                    mIGateState.setText(getString(R.string.str_connected) + allConnectedDevices);
                }
                else{
                    mIGateState.setText(getString(R.string.str_idle));
                }

            }
        });
    }

    public void iGateDeviceReceivedData(final String bluetoothAddress, final byte[] data){
        try {
            final String tmpValue = new String(data,"UTF-8");
            Log.i("------ RX data: ",tmpValue);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mReceivedData.append(tmpValue);
                    String[] measureData = tmpValue.split(" ");
                    String tempData;
                    for(int i = 0;i<measureData.length;i++){
                        tempData = measureData[i];
                        if(tempData.length() == 3 && tempData.startsWith("&N")){
                            mReceivedData.append(tempData+","+measureData[i+1]);
                            String crc_data = tempData + " ";
                            int crc_code = com.welling.kinghacker.tools.FormCRC.get_crc16(crc_data.getBytes(),
                                    crc_data.getBytes().length, new byte[2]);
                            if(!(String.valueOf(crc_code).equals(measureData[i+1]))){
                                mReceivedData.append("\n蓝牙通讯信道受干扰，请重新接受数据！\n");
                                return;
                            }
                        }
                        if(tempData.length() == 12 && dataRevice == false){
                            if(tempData.startsWith("&N")){
                                dataRevice = true;
                                float bloodGlusoce = Float.valueOf(measureData[i+1]);
                                bloodGlusoce = bloodGlusoce / 18;
                                mReceivedData.append("\n接收到数据为："+String.format("%.1f",bloodGlusoce));
                                mReceivedData.append("mmol/L");
                                mReceivedData.append(",数据类型为：血糖。\n");
                                if(measureData[i+2].equals("0")||measureData[i+2].startsWith("0")){
                                    intentdata.putExtra("type", "1");
                                    intentdata.putExtra("bloodGlusoce", String.format("%.1f",bloodGlusoce));
                                    mButtonFinish.setVisibility(View.VISIBLE);
                                    mButtonCleanData.setVisibility(View.VISIBLE);
                                    mDataUp.setVisibility(View.VISIBLE);
                                    mButtonSend.setVisibility(View.INVISIBLE);

                                    final float data = bloodGlusoce;
                                    mDataUp.setOnClickListener(new OnClickListener() {
                                        public void onClick(View v) {
                                            //将测量结果保存
                                            sugerBean = new SugerBean(IGateActivity.this,data);
                                            //创建表  当然有分析 如果表存在就不创建
                                            sugerBean.createTable();
                                            //将信息插入
                                            sugerBean.insert();
                                            sugerAct.currentSugerValue = data;
                                            sugerAct.myHandler.sendEmptyMessage(1);
                                            AlertDialog.Builder builder  = new AlertDialog.Builder(IGateActivity.this);
                                            builder.setTitle("提示" ) ;
                                            builder.setMessage("数据上传成功" ) ;
                                            builder.setPositiveButton("确定" ,  null );
                                            builder.show();
                                            mDataUp.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                                break;
                            }
                        }
                        if(tempData.startsWith("&J1")){
                            mReceivedData.append("血糖仪数据清空完毕!\n");
                        }
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //mIgate.iGateDeviceSendData(bluetoothAddress, data);
    }

    public void iGateDeviceReceivedCtr(BluetoothDevice bluetoothDevice, byte[] data){

    }

    public void iGateDeviceUpdateRssi(final String bluetoothAddress, final int rssi){
        Log.i("------", String.format("Rssi report %s,%d", mIgate.iGateDeviceGetName(bluetoothAddress),rssi));
    }

    public void iGateDeviceLinkLossAlertLevelReport(final String bluetoothAddress, byte alertLevel){
        Log.i("------", String.format("Link loss alert level %s,%d", bluetoothAddress,alertLevel));
    }

    public void iGateDeviceTxPowerReport(final String bluetoothAddress, byte txPower){
        Log.i("------", String.format("Tx Power %s,%d", bluetoothAddress,txPower));
    }

    private void listConnectedDevices(){
        String allConnectedDevices=new String();
        for(int i=0;i<mConnectedBluetoothDevicesAddress.size();i++){
            allConnectedDevices+=" "+mConnectedBluetoothDevicesAddress.get(i);
        }
        mIGateState.setText(getString(R.string.str_connected) + allConnectedDevices);
    }

    public void cleanData(View v){
        // Send a message using content of the edit text widget
        String message;
        message = "&J 45990";

        if(sendIndex<mConnectedBluetoothDevicesAddress.size()){
            mIgate.iGateDeviceSendData(mConnectedBluetoothDevicesAddress.get(sendIndex), message.getBytes());
        }
        else{
            sendIndex=0;
            if(mConnectedBluetoothDevicesAddress.size()>0){
                mIgate.iGateDeviceSendData(mConnectedBluetoothDevicesAddress.get(sendIndex), message.getBytes());
                Log.i("------",String.format("iGate State %d",mIgate.iGateDeviceGetState(mConnectedBluetoothDevicesAddress.get(sendIndex)).ordinal()));
            }
        }
        sendIndex++;
        if(sendIndex==mConnectedBluetoothDevicesAddress.size()){
            sendIndex=0;
        }
    }

    private iGate mIgate=null;

}
