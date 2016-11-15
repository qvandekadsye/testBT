package org.legtux.qvandekadsye.testbluetooth.bluetoothThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by quentinvdk on 13/11/16.
 * Cette classe représente le client blutooth
 */

public class BluetoothClientThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private  BluetoothAdapter mBluetoothAdapter;
    private Handler handler;

    private static final UUID MY_UUID = UUID.fromString("5e5a9edb-2055-462e-88b2-5302258a505a");
    public BluetoothClientThread(BluetoothDevice device, BluetoothAdapter mBluetoothAdapter, Handler handler) {
        this.mBluetoothAdapter = mBluetoothAdapter;

        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            this.handler=handler;
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        ManageConnectionThread conecThread = new ManageConnectionThread(mmSocket,this.handler);
        conecThread.run();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

}
