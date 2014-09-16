package bluetooth.devices;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ServerThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    private Channel channel;
    public ServerThread(Channel channel) {
    	this.channel = channel;
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = BluetoothAdapter.getDefaultAdapter().
            		listenUsingRfcommWithServiceRecord("Bluetooth Comunicator",
            				UUID.fromString(MainActivity.MY_UUID));
        } catch (IOException e) {
        	Log.d("MyLog", e.getLocalizedMessage());
        }
        mmServerSocket = tmp;
    }
 
    @SuppressWarnings("finally")
	public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
            	if(channel.isAlive())
            		channel.cancel();
                channel.start(socket);
                try {
					mmServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				} finally{
					break;
				}
            }
        }
    }
 
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}