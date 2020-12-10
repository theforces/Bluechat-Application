package com.myproject.asimion.bluechat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class BTServer extends Thread {

    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;

    private static final String APP_NAME = "BlueChat";
    public static final int MAX_CONNECTIONS = 7;
    private ArrayList<ConnectionThread> connThread;
    private ArrayList<AcceptConnectionThread> acceptThread;
    private ArrayList<BluetoothServerSocket> bts;
    private ArrayList<BluetoothSocket> bs;
    private ArrayList<OutputStream> outputArray;
    private BluetoothAdapter myAdapter;
    private ArrayList<UUID> uuids;
    private GroupSendReceive controller;
    private Handler handler;

    //constructor
    public BTServer(Handler handler,BluetoothAdapter adapter, ArrayList<UUID> uuids){
        myAdapter = adapter;
        this.uuids = uuids;
        connThread = new ArrayList<>();
        acceptThread = new ArrayList<>();
        bts = new ArrayList<>();
        bs  = new ArrayList<>();
        //controller = null;
        this.handler = handler;
    }

    //method that create BluetoothServerSocket for every new connection of server that is expected
    private void connectionThreads() throws IOException, InterruptedException {
        //start the threads in parallel
         System.out.println("CONN THREADS:");
            for(UUID u : uuids){
                connThread.add(new ConnectionThread( u, myAdapter,APP_NAME));
                connThread.get(connThread.size()-1).start();
            }

            for(ConnectionThread ct : connThread){
                ct.join();
                bts.add(ct.getServerSocket());
                System.out.println(" ct is "+ct.getServerSocket());
            }


    }

    //fill the list bs with bluetoothsocket --> accept connextion on BluetoothServerSocket
    private void acceptThreads() throws IOException, InterruptedException {
        System.out.println("ACCEPT THREADS:");
        int  i = 0;
        for(BluetoothServerSocket ss : bts){

            Message message = Message.obtain();
            message.what = STATE_CONNECTING;
            handler.sendMessage(message);
            acceptThread.add(new AcceptConnectionThread(ss));
            acceptThread.get(acceptThread.size()-1).start();
            System.out.println("START ATTEMPT : ");
            //acceptThread.get(acceptThread.size() -1).join();
        }

        //startComm();


        for(AcceptConnectionThread act : acceptThread){
            act.join();
            bs.add(act.getConnSocket());
        }
        System.out.println("Final ACCEPT!!");
    }

    //start communication using GroupSendReceive thread
   private void startComm(){
        System.out.println("StartCOMM : ");
        controller = new GroupSendReceive(bs,handler);
        outputArray = controller.getOutputArray();
        System.out.println("OUTPUT ARRAY:");
        for(OutputStream o : outputArray){
            System.out.println(o.toString());
        }
        controller.start();
    }

    public void run(){
        try {
            connectionThreads();
            System.out.println("END connThreads!");
            acceptThreads();
            System.out.println("Start exec startComm : run() bt");
            startComm();
            System.out.println("END EXCEC run() -- btserver !!");

            Message message = Message.obtain();
            message.what = STATE_CONNECTED;
            handler.sendMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = STATE_CONNECTION_FAILED;
            handler.sendMessage(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = STATE_CONNECTION_FAILED;
            handler.sendMessage(message);
        }

    }

    public GroupSendReceive getController(){return controller;}
    public ArrayList<OutputStream> getOutputArray() {return outputArray;}
    public ArrayList<BluetoothSocket> getBs(){return  bs;}
}

