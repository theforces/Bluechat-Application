package com.myproject.asimion.bluechat;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.ArrayList;

public class AcceptConnectionThread extends  Thread {
    private  BluetoothServerSocket serverSocket;
    private  BluetoothSocket connSocket;

    public AcceptConnectionThread(BluetoothServerSocket serverSocket){
        this.serverSocket = serverSocket;
        connSocket = null;

    }

    public void run() {
        BluetoothSocket socket = null;
        System.out.println("ATTEMPT in thread accept " +  socket);
        while(socket == null){
            try {
                System.out.println("TRY! on server socket : " + serverSocket.toString());
                socket = serverSocket.accept();

            } catch (IOException e) {
                System.out.println("EROARE LA BLUETOOTH SOCKET : ACT class ");
                e.printStackTrace();
            }
            if(socket != null){
                connSocket = socket;
                System.out.println("ACCEPT " + connSocket.toString());
               break;
            }
        }
    }

    public  BluetoothSocket getConnSocket(){ return connSocket;}

}
