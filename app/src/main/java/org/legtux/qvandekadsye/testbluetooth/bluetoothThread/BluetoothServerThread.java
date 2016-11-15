package org.legtux.qvandekadsye.testbluetooth.bluetoothThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import org.legtux.qvandekadsye.testbluetooth.MainActivity;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by quentinvdk on 13/11/16.
 * Cette classe represente le serveur Blutooth
 */
public class BluetoothServerThread extends Thread{

    private final BluetoothServerSocket serveurSocket;
    private static final String NAME = "MON BLEU EST IL TON BLEU";
    private static final UUID MY_UUID = UUID.fromString("5e5a9edb-2055-462e-88b2-5302258a505a");
    private Handler handler;

    public BluetoothServerThread(BluetoothAdapter mBluetoothAdapter, Handler handler)
    {
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        serveurSocket = tmp;

        this.handler = handler;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                System.out.println("a");
                socket = serveurSocket.accept();
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    serveurSocket.close();
                    break;
                }
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted

        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        ManageConnectionThread connecTread = new ManageConnectionThread(socket,this.handler);
        this.handler.obtainMessage(MainActivity.SOCKET_CONNECTED,connecTread).sendToTarget();
        connecTread.run();
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            serveurSocket.close();
        } catch (IOException e) { }
    }


}
