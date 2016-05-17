package com.example.bluetooth.le;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.welling.kinghacker.activities.MTActivity;
import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.bean.BloodPressureBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
@SuppressLint("NewApi")
public class DeviceScanActivity extends MTActivity{
	
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private ListView device_list;
    private ProgressBar scan;
    private Button bar_status;
    private TextView showresult,blood_device,handin,showtimeinhandin;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    private ViewFlipper content;
    private final int CONTENT_CHILD_0=0;
    private final int CONTENT_CHILD_1=1;
    private EditText handin_highblood,handin_lowblood,handin_heartrate;
    private CheckBox handin_heartproblem;
    public BloodPressureBean bpbean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devicelist_layout);
        setActionBarTitle(getResources().getString(R.string.title_devices));
        device_list=(ListView)findViewById(R.id.device_list);
        scan=(ProgressBar)findViewById(R.id.scan);
        showresult=(TextView)findViewById(R.id.showresult);
        blood_device=(TextView)findViewById(R.id.id_blood_device);
        handin=(TextView)findViewById(R.id.id_handin);
        showtimeinhandin=(TextView)findViewById(R.id.showtimeinhandin);
        bar_status=(Button)findViewById(R.id.bar_status);
        content=(ViewFlipper)findViewById(R.id.optionstochoose);
        handin_highblood=(EditText)findViewById(R.id.handin_highblood);
        handin_lowblood=(EditText)findViewById(R.id.handin_lowblood);
        handin_heartrate=(EditText)findViewById(R.id.handin_heartrate);
        handin_heartproblem=(CheckBox)findViewById(R.id.handin_heartproblem);
        mHandler = new MyHandler();
        mHandler.sendEmptyMessage(CONTENT_CHILD_0);

        //if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
        //    Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        //    finish();
        //}

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.i("qwert","dsa_onCreate");
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("qwert", "dsa_onResume");

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        device_list.setAdapter(mLeDeviceListAdapter);
        device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("==position==" + position);
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null) return;
                final Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                if (mScanning) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            scanLeDevice(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    scan.setVisibility(View.GONE);
                    bar_status.setText(R.string.scan);
                    if(device_list.getCount()<=0){
                        showresult.setVisibility(View.VISIBLE);
                    }
                }
            }, SCAN_PERIOD);
            scan.setVisibility(View.VISIBLE);
            bar_status.setText(R.string.stop);
            showresult.setVisibility(View.GONE);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            scan.setVisibility(View.GONE);
            bar_status.setText(R.string.scan);
            if(device_list.getCount()<=0){
                showresult.setVisibility(View.VISIBLE);
            }
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
    public void scanorstop(View v){
        if(mScanning==true){
            mScanning=false;
            scanLeDevice(false);
        }else{
            mScanning=true;
            scanLeDevice(true);
        }
    }
    public void blood_device_click(View view){
        if(content.getDisplayedChild()==1)mHandler.sendEmptyMessage(CONTENT_CHILD_0);
    }
    public void handin_click(View view){
        if(content.getDisplayedChild()==0)mHandler.sendEmptyMessage(CONTENT_CHILD_1);
    }
    public void confirmandsave(View v){
        String highbloodstr=handin_highblood.getText().toString();
        String lowbloodstr=handin_lowblood.getText().toString();
        String heartratestr=handin_heartrate.getText().toString();
        if(highbloodstr.equals("")||lowbloodstr.equals("")||heartratestr.equals("")){
            Toast.makeText(DeviceScanActivity.this,"输入值不能为空",Toast.LENGTH_SHORT).show();
        }else{
            int highblood=Integer.parseInt(highbloodstr);
            int lowblood=Integer.parseInt(lowbloodstr);
            int heartrate=Integer.parseInt(heartratestr);
            int heartproblem=handin_heartproblem.isChecked()?1:0;
            if(bpbean==null)bpbean=new BloodPressureBean(this);
            bpbean.setData(highblood, lowblood, heartrate, heartproblem);
            bpbean.insert();
            UptoServer uptoServer=new UptoServer(this);
            uptoServer.upToServer();
            Toast.makeText(DeviceScanActivity.this,"成功上传",Toast.LENGTH_SHORT).show();
        }
    }
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CONTENT_CHILD_0:
                    content.setDisplayedChild(0);
                    blood_device.setTextColor(getResources().getColor(R.color.colorWhite));
                    blood_device.setBackgroundColor(getResources().getColor(R.color.highred));
                    handin.setTextColor(getResources().getColor(R.color.gray));
                    handin.setBackgroundColor(getResources().getColor(R.color.near_white));
                    break;
                case CONTENT_CHILD_1:
                    content.setDisplayedChild(1);
                    handin.setTextColor(getResources().getColor(R.color.colorWhite));
                    handin.setBackgroundColor(getResources().getColor(R.color.highred));
                    blood_device.setTextColor(getResources().getColor(R.color.gray));
                    blood_device.setBackgroundColor(getResources().getColor(R.color.near_white));
                    showtimeinhandin.setText(new SimpleDateFormat("yyyy.MM.dd").format(new Date()));
                    break;
            }
        }
    }
}










