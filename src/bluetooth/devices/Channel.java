package bluetooth.devices;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class Channel extends Thread {
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private Handler hButton, hMassage;
    
    Channel(Handler hMassage, Handler hButton){
    	this.hMassage = hMassage;
    	this.hButton = hButton;
    }
    
    public Boolean start(BluetoothSocket socket){
        mmSocket = socket;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
        	mmInStream = socket.getInputStream();
        	mmOutStream = socket.getOutputStream();
        } catch (IOException e) { 
        	Log.e("MyLog", e.getMessage());
        	return false;
        }
        hButton.sendEmptyMessage(1); // Activated "send" button
        super.start();
        return true;
    }
    
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                if(bytes != 0){        
                	hMassage.sendMessage(hMassage.obtainMessage(0, 
                			(new String(buffer).substring(0, bytes)).toCharArray()));
                }
            } catch (IOException e) {
            	hButton.sendEmptyMessage(0); // Disabled "Send" button
                break;
            }
        }
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
    	hButton.sendEmptyMessage(0); // Disabled "Send" button
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
