package org.legtux.qvandekadsye.testbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.legtux.qvandekadsye.testbluetooth.bluetoothThread.BluetoothClientThread;
import org.legtux.qvandekadsye.testbluetooth.bluetoothThread.BluetoothServerThread;
import org.legtux.qvandekadsye.testbluetooth.bluetoothThread.ManageConnectionThread;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final int DATA_RECEIVED = 3;
    public static final int SOCKET_CONNECTED = 4;
    public static final int MESSAGE_READ=200;
    public boolean mServerMode;

    private ManageConnectionThread bcManager;

    private TextView tv;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tvdata);
        //On vérifie si l'appareil dispose du bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this.getApplicationContext(), "Votre appareil n'a pas le bluetooth", Toast.LENGTH_LONG).show();
        } else {
            //On vérifie si le bluetooth est activé, sinon on demande l'activé
            if (!mBluetoothAdapter.isEnabled()) {
                Intent askBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(askBluetoothIntent, 1);
            }
            //On va lister les péripheriques appareillés. Ca va servir a rien pour nous mais ça peut être utile.
            //System.out.println("RECUP");
            Set<BluetoothDevice> appareilsLies = mBluetoothAdapter.getBondedDevices();
            if (appareilsLies.size() <= 0) {
                Toast.makeText(this.getApplicationContext(), "0", Toast.LENGTH_SHORT).show();
            }
            //System.out.println("=================APPAREILS====================");
            for (BluetoothDevice b : appareilsLies) {
                Toast.makeText(this.getApplicationContext(), b.getName(), Toast.LENGTH_SHORT).show();
            }
             handler = new Handler() {
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case SOCKET_CONNECTED: {
                            bcManager = (ManageConnectionThread) msg.obj;

                            if (!mServerMode)
                                bcManager.write("this is a message".getBytes());
                            break;
                        }
                        case DATA_RECEIVED: {
                            String data = (String) msg.obj;
                            tv.setText(data);
                            if (mServerMode)
                                bcManager.write(data.getBytes());
                        }
                        case MESSAGE_READ:

                            break;
                    }

                }
            };
        }
    }

    public void receive(View view) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
         boolean res =mBluetoothAdapter.startDiscovery();
        Toast.makeText(view.getContext(),String.valueOf(res),Toast.LENGTH_SHORT).show();
        Set<BluetoothDevice> appareilsLies = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice device = getDeviceB(appareilsLies,"A0:32:99:19:E1:89");
        {
            if (device !=null)
            {
                BluetoothClientThread client = new BluetoothClientThread(device,mBluetoothAdapter,this.handler);
                client.start();
                Toast.makeText(view.getContext(),"toto",Toast.LENGTH_SHORT).show();
            }
        }


        //BluetoothClientThread client = new BluetoothClientThread(null,mBluetoothAdapter);
        //client.run();
    }

    public void sencd(View view) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerThread serveur= new BluetoothServerThread(mBluetoothAdapter,this.handler);
        serveur.run();

    }

    private BluetoothDevice getDeviceB(Set<BluetoothDevice> devices, String macAdress)
    {
        for(BluetoothDevice b: devices)
        {
            if (b.getAddress().equals("A0:32:99:19:E1:89"))
                return b;
            return null;

        }
        return null;
    }
}
