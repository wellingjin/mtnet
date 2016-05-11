package com.welling.kinghacker.tools;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.creative.bluetooth.BluetoothOpertion;
import com.creative.bluetooth.IBluetoothCallBack;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 2015/12/1.
 *
 */
public class BluetoothConnectUtils {

    private static final String TAG = "BluetoothConnectUtils";
    final public static int SEARCHING = 0x006;  //  正在查找
    final public  static int SEARCH_COMPLETE = 0x001;  //  搜索完成
    final public static int SEARCH_FAILED = 0x002;  //  搜索异常
    final public static int CONNECTING = 0x003;  //  正在连接
    final public static int CONNECT_FAILED = 0x004;  //  连接失败
    final public static int CONNECTED = 0x005;  //  连接成功

    Context context;
    public BluetoothOpertion bluetoothOper;

    public BluetoothSocket socket;  //  建立的蓝牙连接
    BluetoothDevice device;  //  搜素到的蓝牙设备
    private Dialog dialog;

    private OnBluetoothConnectedListener listener;
    public static boolean isPairedConnected = false;
    private BlueToothManager blueToothManager;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i("tag", msg.what + "");
            listener.bluetoothConnectState(msg.what);
            switch (msg.what) {
                //  搜索完成
                case SEARCH_COMPLETE:
                    Log.i("tag", "搜索到设备准备连接");
                    break;
                //  搜索异常
                case SEARCH_FAILED:
                    Log.i("tag", msg.obj + "");
                    break;
                //  正在连接
                case CONNECTING:
                    Log.i("tag", "连接...");
                    break;
                //  连接失败
                case CONNECT_FAILED:
                    if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                    break;
                //  连接成功
                case CONNECTED:
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();

                    break;
                //  延时关闭窗口
                case 0x006:
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    break;
            }
        }
    };

    public BluetoothConnectUtils(Context context) {
        this.context = context;
        blueToothManager = new BlueToothManager(context);
        this.bluetoothOper = new BluetoothOpertion(context, new MyBluetoothCallback());
        connectParedDevice();

//        initDialog();
    }


    public void setOnBluetoothConnectedListener(OnBluetoothConnectedListener listener) {
        this.listener = listener;
    }

    public void closeSocket(){
        if (socket != null){
            bluetoothOper.disConnect(socket);
        }
    }

    private void connectParedDevice() {

        Set<BluetoothDevice> devices = blueToothManager.getBluetoothAdapter().getBondedDevices();
        for (BluetoothDevice device:devices){
            if (checkName(device.getName())){
                bluetoothOper.connect(device);
                isPairedConnected = true;
                return;
            }
        }

        bluetoothOper.discovery();
        Log.i("tag","开始查询");
        isPairedConnected = false;


    }


    /**
     * 初始化搜索进度窗口
     */
    private void initDialog() {
        if (dialog == null){
            dialog = new Dialog(context);
            dialog.setTitle("正在查找...");
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bluetoothOper.stopDiscovery();
            }
        });

    }

    /**
     * 检查搜索到的设备是否是目标设备型号，返回类型为是或否
     * @param deviceName name
     * @return true or false
     */
    public static boolean checkName(String deviceName) {
        return deviceName!=null && (deviceName.equals("PC80B")||deviceName.equals("PC-60NW-1"));
    }


    /**
     * 搜索设备回调接口
     */
    class MyBluetoothCallback implements IBluetoothCallBack {

        @Override
        public void OnFindDevice(BluetoothDevice bluetoothDevice) {
            if (bluetoothDevice != null) {
                device = bluetoothDevice;
                if(checkName(device.getName())) {
                    handler.sendEmptyMessage(SEARCH_COMPLETE);
                    bluetoothOper.stopDiscovery();
                    bluetoothOper.connect(bluetoothDevice);
                    Log.i("tag","找到目标设备");
                }else{
                    Log.i("tag","设备"+device.getName()+"不匹配");
                }
            }
        }

        @Override
        public void OnDiscoveryCompleted(List<BluetoothDevice> list) {
            Log.i(TAG, "搜索完成");
            if(list!=null){
                if (list.size() == 0) {
                    handler.sendEmptyMessageDelayed(0x006, 1000);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        BluetoothDevice device = list.get(i);
                        Log.i("tag","name: "+device.getName()+" , address: "+device.getAddress());
                        if (checkName(device.getName())) {
                            handler.sendEmptyMessage(CONNECTING);
                        }
                    }
                }
                Log.i("tag", "设备列表大小 " + list.size() + "");

            }else{
                Log.i("tag","没有搜到设备");
            }
        }

        @Override
        public void OnConnected(BluetoothSocket bluetoothSocket) {
            Log.i(TAG, "连接成功");
            handler.sendEmptyMessage(CONNECTED);
            handler.sendEmptyMessageDelayed(0x006, 1000);

            socket = bluetoothSocket;
            device=socket.getRemoteDevice();
            //    连接后要做的事情
            listener.setOnBluetoothConnected(bluetoothSocket);

        }

        @Override
        public void OnConnectFail(String s) {
            Log.i(TAG, "连接失败");
            if(isPairedConnected){
                bluetoothOper.discovery();
                isPairedConnected=false;
            }
            handler.sendEmptyMessage(CONNECT_FAILED);
            handler.sendEmptyMessageDelayed(0x006, 1000);

        }

        @Override
        public void OnException(int i) {
            Log.i(TAG, "产生异常" + i);
            Message msg = handler.obtainMessage(SEARCH_FAILED);
            switch (i) {
                case BluetoothOpertion.ExceptionCode.BLUETOOTHNOTOPEN:
                //  蓝牙未开启
                    msg.obj = "蓝牙未开启";
                    break;
                case BluetoothOpertion.ExceptionCode.DISCOVERYTIMEOUT:
                //  搜索超时
                    msg.obj = "搜索超时";
                    break;
                case BluetoothOpertion.ExceptionCode.NOBLUETOOTHADAPTER:
                //  无蓝牙
                    msg.obj = "手机未发现蓝牙";
                    break;
            }
            handler.sendMessage(msg);
            handler.sendEmptyMessageDelayed(0x006, 1000);

        }
    }
    public interface OnBluetoothConnectedListener{
        void setOnBluetoothConnected(BluetoothSocket bluetoothSocket);
        void bluetoothConnectState(int state);
    }

}
