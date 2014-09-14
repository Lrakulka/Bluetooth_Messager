package bluetooth.devices;

import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ListView;
// I have no low energy bluetooth device that is why I can't test this class.
// I will finish this part when I get device with low energy bluetooth
public class BluetoothLowlevelDevices {
	private ArrayList<BluetoothDevice> devices;
	private ArrayList<String> devicesNames;
	private BluetoothAdapter bluetoothAdapter;
	private MainActivity mainActivity;
	private ListView list;
	
	public BluetoothLowlevelDevices(MainActivity mainActivity, ListView list){
		this.mainActivity = mainActivity;
		this.list = list;
	}
	
	public boolean find(){
		devices = new ArrayList<BluetoothDevice>();
		devicesNames = new ArrayList<String>();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(bluetoothAdapter == null)
			return false;
		if (!bluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    mainActivity.startActivityForResult(enableBtIntent, 2);
		    return false;
		}else{
			//---------- to be continue
		}
		return true;
	}
}
