package com.example.bluetooth.le;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.welling.kinghacker.activities.MTActivity;
import com.welling.kinghacker.activities.R;


/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothLeService}, which in
 * turn interacts with the Bluetooth LE API.
 */
@SuppressLint("NewApi")
public class DeviceControlActivity extends MTActivity {
	
	private Button start_to_measure=null;
	private TextView status_dev=null;
	private TextView high_blood=null;
	private TextView low_blood=null;
	private TextView heart_rate=null;
	private TextView heart_rate_pro=null;
	private ProgressBar pressure_bar=null;
    private MyHandler handler=new MyHandler();
    private final int UPDATE_PRESSURE=0,SHOWBLOODPRESSURE=1,SETBAR2ZERO=2;
	private final static String TAG = DeviceControlActivity.class
			.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private String mDeviceName;
	private String mDeviceAddress;
	private BluetoothLeService mBluetoothLeService;
	private boolean mConnected = false;


	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			mBluetoothLeService.connect(mDeviceAddress);
		}
		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				mConnected = true;
				updateConnectionState(R.string.connected);
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				mConnected = false;
				updateConnectionState(R.string.disconnected);
				clearUI();
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				Log.i("asdfg", "service found!");
				mBluetoothLeService.setNotifyFirst();							
				
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				
			} else if (BluetoothLeService.ACTION_UPDATE_DATA.equals(action)) {
				handler.sendEmptyMessage(intent.getIntExtra("value", 0));
			}
		}
	};

	private void clearUI() {
		pressure_bar.setProgress(0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blood_measure);
		setActionBarTitle(getResources().getString(R.string.measure_blood));
		start_to_measure=(Button)findViewById(R.id.start_to_measure);
		status_dev=(TextView)findViewById(R.id.status_dev);
		high_blood=(TextView)findViewById(R.id.high_blood);
		low_blood=(TextView)findViewById(R.id.low_blood);
		heart_rate=(TextView)findViewById(R.id.heart_rate);
		heart_rate_pro=(TextView)findViewById(R.id.heart_rate_pro);
		pressure_bar=(ProgressBar)findViewById(R.id.pressure_bar);
		heart_rate_pro.setVisibility(View.GONE);
		final Intent intent = getIntent();
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

		//getActionBar().setTitle(mDeviceName);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
		start_to_measure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mBluetoothLeService!=null){
					mBluetoothLeService.writecommand();
				}
				handler.sendEmptyMessage(SETBAR2ZERO);
			}
		});
		Log.i("asdfg", "DeviceControlActivity->oncreate");
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		Log.i("asdfg", "DeviceControlActivity->onresume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
		Log.i("asdfg", "DeviceControlActivity->onPause");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
		Log.i("asdfg", "DeviceControlActivity->ondestroy");
	}

	private void updateConnectionState(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				status_dev.setText(resourceId);
			}
		});
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_UPDATE_DATA);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
	class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case UPDATE_PRESSURE:
				int result=(int) (BluetoothLeService.pressure_value/300.*100);
				pressure_bar.setProgress(result);break;
			case SHOWBLOODPRESSURE:
				high_blood.setText(""+BluetoothLeService.high_blood);
				low_blood.setText(""+BluetoothLeService.low_blood);
				heart_rate.setText(""+BluetoothLeService.heart_rate);
				if(BluetoothLeService.heart_rate_problem)heart_rate_pro.setVisibility(View.VISIBLE);
				else heart_rate_pro.setVisibility(View.GONE);
				break;	
			case SETBAR2ZERO:
				pressure_bar.setProgress(0);
				heart_rate_pro.setVisibility(View.GONE);
				break;
			case BluetoothLeService.LOW_BATTERY:
				Toast.makeText(DeviceControlActivity.this, "电量过低，请更换电池或插上电源", Toast.LENGTH_LONG).show();;
				break;
			case BluetoothLeService.ERROR_1:
				Toast.makeText(DeviceControlActivity.this, "测量过程中没有侦测到脉搏信号", Toast.LENGTH_LONG).show();
				break;
			case BluetoothLeService.ERROR_2:
				Toast.makeText(DeviceControlActivity.this, "测量错误，测量过长中干扰过大", Toast.LENGTH_LONG).show();
				break;
			case BluetoothLeService.ERROR_3:
				Toast.makeText(DeviceControlActivity.this, "充气失败，充气时间过长或袖带漏气", Toast.LENGTH_LONG).show();
				break;
			case BluetoothLeService.ERROR_4:
				Toast.makeText(DeviceControlActivity.this, "测量过程中出现未知错误", Toast.LENGTH_LONG).show();
				break;
			case BluetoothLeService.ERROR_5:
				Toast.makeText(DeviceControlActivity.this, "测量失败，测量的结果高压与低压相差太大", Toast.LENGTH_LONG).show();
				break;
			}
		}		
	}
}
