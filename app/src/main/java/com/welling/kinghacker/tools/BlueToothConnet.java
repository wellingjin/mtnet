package com.welling.kinghacker.tools;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by KingHacker on 3/28/2016.
 **/
public class BlueToothConnet {
    private BluetoothDevice remoteDevice;
    private int port = 1;
    private BluetoothSocket socket;
    private boolean isConneted = false;
    private int connetTime;
    final private int TIMEOUT = 10;

    public BlueToothConnet(String mac,BluetoothAdapter adapter){
        try {
            Log.i("TAG",mac);
            remoteDevice = adapter.getRemoteDevice(mac);
        }catch (IllegalArgumentException e){
            remoteDevice = null;
            Log.i("TAG","IllegalArgumentException");
        }

        connetTime = 0;

    }

    public void startConnet(){
        if (remoteDevice == null) return;
        startConnet(remoteDevice);
    }
    public void startConnet(final BluetoothDevice device){
        if (device == null) return;
        if (remoteDevice == null) remoteDevice = device;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (device.getBondState() == BluetoothDevice.BOND_NONE){
                    Method creMethod = null;
                    try {
                        creMethod = BluetoothDevice.class
                                .getMethod("createBond");
                        creMethod.invoke(remoteDevice);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    Log.e("TAG", "开始配对");
                }
                while(!isConneted){
                    if (socket == null){
                        initSocket();
                    }
//                    超时了断开连接
                    if (connetTime >= TIMEOUT) break;
                    try {
                        socket.connect();
                        isConneted = true;
                        Log.i("TAG","conneted");

                        InputStream inputStream = socket.getInputStream();
                        
                    } catch (IOException e) {
//                        出现异常，重连
                        isConneted = false;
                        connetTime++;
                        if (socket.isConnected()){
                            try {
                                socket.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void initSocket() {
        BluetoothSocket temp = null;
        try {
            Method m = remoteDevice.getClass().getMethod(
                    "createRfcommSocket", new Class[] { int.class });
            temp = (BluetoothSocket) m.invoke(remoteDevice,port);//这里端口为1
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        socket = temp;
    }

}
