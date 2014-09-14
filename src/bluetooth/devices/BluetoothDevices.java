package bluetooth.devices;

import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
// Search for classic bluetooth devices
public class BluetoothDevices{
	private ArrayList<BluetoothDevice> devices; // BluetoothDevice
	private BluetoothAdapter bluetoothAdapter;
	private MainActivity mainActivity;
	private ArrayList<String> devicesNames;
	private ArrayAdapter<String> adapter;
	
	// Receiver for hook of founded devices
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	        	BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	        	if(!devices.contains(device)){
		        	devicesNames.add(device.getName());
		        	adapter.notifyDataSetChanged();		        	
		            devices.add(device);
	        	}
	        }
	    }
	};
	
	// Receiver finished searching after closing searching
	private BroadcastReceiver discoveryFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainActivity.onclick(null);
            
        }
    };

	BluetoothDevices(MainActivity mainActivity, ListView list){
		this.mainActivity = mainActivity;
		devices = new ArrayList<BluetoothDevice>();
		devicesNames = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(mainActivity,
    			android.R.layout.simple_list_item_1, devicesNames);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();		
    	list.setAdapter(adapter);
    	
	}
	
	public boolean find(){
		devices.clear();
		devicesNames.clear();
		adapter.notifyDataSetChanged();
		if(bluetoothAdapter == null)
			return false;
		if (!bluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    mainActivity.startActivityForResult(enableBtIntent, 2);
		    return false;
		}else{
			// Register receivers
			mainActivity.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
			mainActivity.registerReceiver(discoveryFinishedReceiver, 
					new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
			// Don't forget to unregister during onDestroy
			bluetoothAdapter.startDiscovery();
		}
		return true;
	}
	
	public ArrayList<BluetoothDevice> getFindDvices(){
		return devices;
	}
	
	public boolean stop(){
		mainActivity.unregisterReceiver(mReceiver);
        mainActivity.unregisterReceiver(discoveryFinishedReceiver);
		return bluetoothAdapter.cancelDiscovery();
	}
}
