package com.myproject.asimion.bluechat;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
public class Client extends  Thread {

    //STATES for application client -- Handler

    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;

    private UUID currentUUID;
    private BluetoothDevice mBTDevice;
    private BluetoothSocket mBTSocket;
    private SendReceive sendReceive;
    private Handler handler;

    public Client(BluetoothDevice device,UUID uuids, Handler handler){
        mBTDevice = device;
        this.handler = handler;
        currentUUID = uuids;

    }


    public void run(){
        try{

                 mBTSocket = mBTDevice.createInsecureRfcommSocketToServiceRecord(currentUUID);
                 mBTSocket.connect();

            Message message = Message.obtain();
            message.what = STATE_CONNECTED;
            handler.sendMessage(message);

            sendReceive = new SendReceive(mBTSocket, handler);
            sendReceive.start();

        }catch (IOException e){
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = STATE_CONNECTION_FAILED;
            handler.sendMessage(message);
        }
    }

    public BluetoothSocket getmBTSocket(){
        return mBTSocket;
    }

}
