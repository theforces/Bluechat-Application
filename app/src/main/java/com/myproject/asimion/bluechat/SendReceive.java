package com.myproject.asimion.bluechat;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.os.Handler;

public class SendReceive extends  Thread {

    //STATES for application (most client server) -- Handler
    static final int STATE_MESSAGE_RECEIVED = 5;

    private final BluetoothSocket btSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private Handler handler;

    public SendReceive(BluetoothSocket socket, Handler handler){
        btSocket = socket;
        this.handler = handler;
        InputStream tempIn = null;
        OutputStream tempOut = null;

        try {
            tempIn = btSocket.getInputStream();
            tempOut = btSocket.getOutputStream();
        }catch (IOException e){
            e.printStackTrace();
        }

        inputStream = tempIn;
        outputStream = tempOut;
    }

    public synchronized void run(){
        byte[]  buffer = new byte[1024];
        int bytes;
        //receive message
        while(true){
            try{
                bytes = inputStream.read(buffer);

                handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();

            }catch (IOException e){
                e.printStackTrace();
            }

        }

    }

    public void writeMessage(byte[] bytes){//write message on outpustream
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
