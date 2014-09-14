package bluetooth.devices;

import searchin.bluetooth.devices.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("HandlerLeak") public class MainActivity extends Activity {
	
	private ProgressBar progBar;
	private ListView list1;
	private boolean searching = false;
	private boolean isDiscoverability = false;
	private Button button, buttonDiscoverability, buttonSend;
	private BluetoothDevices blDevices;
	private TextView text, messages;
	private Channel channel;
	private ServerThread serverThread;
	private ClientThread cliantThread;
	private EditText edit;
	public final static String MY_UUID = "e91521df-92b9-47bf-96d5-c52ee838f6f6";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Delete title in program
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Use all screen
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
       //     WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        progBar = (ProgressBar) findViewById(R.id.progressBar1);
        progBar.setVisibility(ProgressBar.INVISIBLE);
        list1 = (ListView) findViewById(R.id.listView1);
        list1.setScrollbarFadingEnabled(false); 
        button = (Button) findViewById(R.id.button1);
        buttonDiscoverability = (Button) findViewById(R.id.button3);
        buttonSend = (Button) findViewById(R.id.button2);
        buttonSend.setEnabled(false);
        text = (TextView) findViewById(R.id.textView3);
        edit = (EditText) findViewById(R.id.editText1);
        // For control button @Send@ from other thread
        Handler hButton = new Handler(){        	
        	public void handleMessage(android.os.Message msg){
        		if(msg.what == 0)
        			buttonSend.setEnabled(false);
        		else buttonSend.setEnabled(true);
        	}
        };
        // For control edit from other thread
        messages = (TextView) findViewById(R.id.textView2);
        Handler hEdit = new Handler(){
        	public void handleMessage(android.os.Message msg){
        		messages.setText(String.valueOf((char[]) msg.obj));
	    	}
	    };
        blDevices = new BluetoothDevices(this, list1);
        channel = new Channel(hEdit, hButton);
        serverThread = new ServerThread(channel);
        serverThread.start();
        list1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				BluetoothDevice device = blDevices.getFindDvices().get(position);
				if(searching)
					onclick(null);
				cliantThread = new ClientThread(device, channel);
				cliantThread.start();
				if(device.getBondState() == BluetoothDevice.BOND_BONDED)
					text.setText(device.getAddress() + " Bonded State bonded");
				if(device.getBondState() == BluetoothDevice.BOND_BONDING)
					text.setText(device.getAddress() + " Bonded State bonding");
				if(device.getBondState() == BluetoothDevice.BOND_NONE)
					text.setText(device.getAddress() + " Bonded State none");
			}
		});
    }
      
    public void onSend(View v){
    	if(edit.getTextSize() != 0)
    		channel.write(edit.getText().toString().getBytes());
    }
    
    public void onclick(View v){
    	if(!searching){	
    		edit.setText("");
    		searching = true;
    		button.setText(R.string.bt_stop);
    		progBar.setVisibility(ProgressBar.VISIBLE);
	    	if(!blDevices.find()){
	    		
	    		edit.setText("Enable Bluetooth and try again");
	    		onclick(null);
	    	}
    	}else{
    		blDevices.stop();
    		searching = false;
    		progBar.setVisibility(ProgressBar.INVISIBLE);
    		button.setText(R.string.bt_start);
    	}
    }
    
    public void onClickDiscoverability(View v){
    	if(isDiscoverability){
    		//disable not works
    		Intent discoverableIntent = new
    				Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			//discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			//startActivity(discoverableIntent);
    		sendBroadcast(discoverableIntent);
    		isDiscoverability = false;
    		buttonDiscoverability.setText(R.string.bt_start_discoverability);
    	}else{
    		Intent discoverableIntent = new
    				Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
    		isDiscoverability = true;
    		buttonDiscoverability.setText(R.string.bt_stop_discoverability);
    	}
    }
}
