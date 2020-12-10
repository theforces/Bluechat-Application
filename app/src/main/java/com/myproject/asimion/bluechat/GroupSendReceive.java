package com.myproject.asimion.bluechat;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.provider.CalendarContract;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class GroupSendReceive extends Thread {
    //STATES for application (most client server) -- Handler
    static final int STATE_MESSAGE_RECEIVED = 5;

    private ArrayList<BluetoothSocket> btSockets;
    private ArrayList<InputStream> inputArray;
    private ArrayList<OutputStream> outputArray;
    private Handler currentHandler;
    //private ArrayList<Handler> handlers;

    public GroupSendReceive(ArrayList<BluetoothSocket> sockets, Handler currentHandler){
        btSockets = sockets;
        this.currentHandler = currentHandler;
        inputArray = new ArrayList<>();
        outputArray = new ArrayList<>();
        for(BluetoothSocket bs : btSockets){
            System.out.println(bs.toString());
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bs.getInputStream();
                tempOut = bs.getOutputStream();

            }catch (IOException e){
                e.printStackTrace();
            }

            inputArray.add( tempIn );
            outputArray.add( tempOut );
        }
    }

    public GroupSendReceive(){

    }

    public synchronized void run(){

        System.out.println( "input array:" + inputArray.size() );

        int bytes = 0;
        //receive message
        while(true){
            try{
                int counter = 0;
                for(InputStream is : inputArray) {
                    byte[]  buffer = new byte[1024];
                    bytes = is.read(buffer);
                    if(bytes <= 0){continue;}
                        sendToAll(buffer,outputArray);
                        currentHandler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1,buffer).sendToTarget();
                }

            //dispatch(buffer, outputArray, outputArray.get(counter));

            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }


    public void sendPrivateMessage(byte[] bytes, OutputStream outputStream){//private
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dispatch(byte[] bytes, ArrayList<OutputStream> outputArray, OutputStream outSender){
        for(OutputStream os : outputArray){
            if(!outSender.equals(os)){
                try {
                    System.out.println("DISPATCH SEND to :" + os);
                    os.write(bytes);
                } catch (IOException e) {
                    System.out.println(" -> DISPATCH --- ERROR");
                    e.printStackTrace();

                }
            }
        }
    }

    public void sendToAll(byte[] bytes, ArrayList<OutputStream> outputArray){
        for(OutputStream os : outputArray){

            try {
                    os.write(bytes);
                } catch (IOException e) {
                    System.out.println(" -> SEND TO ALL --- ERROR");
                    e.printStackTrace();

                }
        }
    }

    public ArrayList<OutputStream> getOutputArray(){ return outputArray; }

}
