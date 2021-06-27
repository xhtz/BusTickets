package tz.co.xhcodes.com;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lvrenyang.myprinter.Global;
import com.lvrenyang.myprinter.WorkService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectBTPairedActivity extends AppCompatActivity implements OnItemClickListener {

	private ProgressDialog dialog;
	private static ListView listView;
	public static final String ICON = "ICON";
	public static final String PRINTERNAME = "PRINTERNAME";
	public static final String PRINTERMAC = "PRINTERMAC";
	private static List<Map<String, Object>> boundedPrinters;

	private static Handler mHandler = null;
	private static String TAG = "ConnectBTMacActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setLogo(R.mipmap.ic_launcher);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		setContentView(R.layout.activity_connectbtpaired);
		Settings.startBluetoth();
		dialog = new ProgressDialog(this);
		boundedPrinters = getBoundedPrinters();
		listView = (ListView) findViewById(R.id.listViewSettingConnect);
		listView.setAdapter(new SimpleAdapter(this, boundedPrinters, R.layout.list_item_printernameandmac, new String[] { ICON, PRINTERNAME, PRINTERMAC }, new int[] {R.id.btListItemPrinterIcon, R.id.tvListItemPrinterName, R.id.tvListItemPrinterMac }));
		listView.setOnItemClickListener(this);

		mHandler = new MHandler(this);
		WorkService.addHandler(mHandler);

        if (null == WorkService.workThread) {
            Intent intent = new Intent(this, WorkService.class);
            startService(intent);
        }
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.back_menu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//When Sync action button is clicked
		if (id == R.id.back_btn) {
            if(ChooseSeatActivity.choose_seat.equalsIgnoreCase("No")) {
                Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(objIntent);
            }
            else
            {
                Intent objIntent = new Intent(getApplicationContext(), ChooseSeatActivity.class);
                startActivity(objIntent);
            }
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onBackPressed() {
		if(ChooseSeatActivity.choose_seat.equalsIgnoreCase("No")) {
			Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(objIntent);
		}
		else
		{
            Intent objIntent = new Intent(getApplicationContext(), ChooseSeatActivity.class);
            startActivity(objIntent);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		WorkService.delHandler(mHandler);
		mHandler = null;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// TODO Auto-generated method stub

		String address = (String) boundedPrinters.get(position).get(PRINTERMAC);
		dialog.setMessage(Global.toast_connecting + " " + address);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.show();
		WorkService.workThread.connectBt(address);
	}

	private List<Map<String, Object>> getBoundedPrinters() {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null)
		{
			// Device does not support Bluetooth
			return list;
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(ICON, android.R.drawable.stat_sys_data_bluetooth);
				map.put(PRINTERNAME, device.getName());
				map.put(PRINTERMAC, device.getAddress());
				list.add(map);
			}
		}
		return list;
	}

	static class MHandler extends Handler {

		WeakReference<ConnectBTPairedActivity> mActivity;

		MHandler(ConnectBTPairedActivity activity) { mActivity = new WeakReference<ConnectBTPairedActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			ConnectBTPairedActivity theActivity = mActivity.get();
			switch (msg.what) {
			/**
			 * DrawerService 的 onStartCommand会发送这个消息
			 */
			case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {
                try {
                    int result = msg.arg1;
                    Toast.makeText(theActivity, (result == 1) ? Global.toast_success : Global.toast_fail, Toast.LENGTH_SHORT).show();
                    theActivity.dialog.cancel();
                    Log.v(TAG, "Connect Result: " + result);
                    break;
                }catch (Exception e){}
			}
			}
		}
	}

}
