package com.myproject.asimion.bluechat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;

import java.io.IOException;
import java.util.UUID;

public class ConnectionThread extends Thread {

    private BluetoothServerSocket serverSocket;
    private BluetoothAdapter myAdapter;
    private UUID currentUUID = null;
    private  String APP_NAME ;

    public ConnectionThread(UUID uuid, BluetoothAdapter adapter, String appname)  {
        myAdapter = adapter;
        currentUUID = uuid;
        APP_NAME  = appname;
    }

    public void run(){

        try {
            serverSocket = myAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME, currentUUID);
        } catch (IOException e) {
           // e.printStackTrace();
            System.out.println("ERROARE LA SERVERSOCKET ");
        }
    }


    public  BluetoothServerSocket getServerSocket(){return serverSocket;}

}
