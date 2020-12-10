package com.myproject.asimion.bluechat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class SecondActivity extends AppCompatActivity {
    //STATES for application (most client server) -- Handler
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    //public static final int IMAGE_GALLERY_REQUEST = 20;
    //widget
    ImageView paired;
    ImageView groupButton;
    //Button listenButton;
    TextView textView;
    ImageView sendButton;
    EditText input;
    ListView pairedDevicesList;
    TextView statusArea;
    ListView permissionList;

    public BluetoothAdapter myBluetoothAdapter;//bt adapter refference
    BluetoothDevice[] btDevices;//array of ref bt device
    BluetoothDevice[] chooseList;//bt devices list to choose permissions
    BTServer server;
    SendReceive sendReceive;
    GroupSendReceive gSendReceive;
    static final ArrayList<UUID> APP_UUID = new ArrayList<>();
    Boolean clicked = false;
    boolean flagGroupMessage;//flag for (false)1:1 or (true)1:many connection
    boolean type;//true server , client false

    boolean connAux;
    static final int REQUEST_ENABLE_CODE = 1;
    static final String APP_NAME = "BlueChat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //activate bluetooth --> give permission to use bluetooth module
        if(!myBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_CODE);
        }
        flagGroupMessage = false;
        connAux = false;
        type = false;
        setUpWidget();//setup layout
        permissionList.setVisibility(View.INVISIBLE);
        //display paired Devices
        paired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btDevices = ServerConnector.getListener(pairedDevicesList, myBluetoothAdapter, v.getContext());
                clicked = !clicked;

                if (clicked) {
                    pairedDevicesList.setVisibility(View.VISIBLE);
                } else {
                    pairedDevicesList.setVisibility(View.INVISIBLE);
                }

            }
        });


        clientConnection();//setup for client version
        setAPP_UUID();//fill the APP_UUID ArrayList
        startServer();//setup and start server
        sendMessage();

    }

/*
    public void onDestroy() {

        super.onDestroy();

        try {
            if(flagGroupMessage){
                gSendReceive.join();
            }else{
                sendReceive.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
*/
    public void setUpWidget(){
        paired = (ImageView) findViewById(R.id.paired);
        pairedDevicesList = (ListView) findViewById(R.id.pairedDevicesList);
        permissionList = (ListView) findViewById(R.id.permission);
        statusArea = (TextView)findViewById(R.id.status);
        groupButton = (ImageView) findViewById(R.id.group);
        groupButton.setImageResource(R.drawable.start_button_off);
        textView = (TextView)findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        sendButton = (ImageView) findViewById(R.id.send);
        input = (EditText)findViewById(R.id.textAreaInput);
    }


    public void startServer(){
       groupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = !clicked;
                if(clicked) {
                    permissionList.setVisibility(View.VISIBLE);

                    Set<BluetoothDevice> Devices = myBluetoothAdapter.getBondedDevices();//getBondedDevices retun Set<BluetoothDevice>
                    String[] devices = new String[Devices.size()];
                    //BluetoothDevice[] btDevices = new BluetoothDevice[Devices.size()];
                    chooseList = new BluetoothDevice[Devices.size()];
                    int index = 0;
                    if (Devices.size() >= 1) {
                        for (BluetoothDevice device : Devices) {
                            devices[index] = device.getName();
                            chooseList[index] = device;
                            index++;
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, devices);
                        permissionList.setAdapter(arrayAdapter);
                    }

                }else {
                    System.out.println("MAIN ACTIVITY SERVER APP_UUID :");
                    for(UUID u : APP_UUID){
                        System.out.println(u);
                    }
                    permissionList.setVisibility(View.INVISIBLE);

                    if(APP_UUID.size() >= 1){
                        flagGroupMessage = true;
                        groupButton.setImageResource(R.drawable.start_button_on);
                        server = new BTServer(handler, myBluetoothAdapter, APP_UUID);
                        type = true;
                        server.start();
                        gSendReceive = new GroupSendReceive();
                    }/*else if (APP_UUID.size() == 1){
                        type=true;
                        groupButton.setImageResource(R.drawable.start_button_on);
                        server = new BTServer(handler, myBluetoothAdapter, APP_UUID);
                        server.start();
                        gSendReceive = new GroupSendReceive();
                    }*/
                }
                //gSendReceive = groupServer.getController();
            }
        });
    }


    private void sendMessage(){
        sendButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String sender = myBluetoothAdapter.getName();
                String text = String.valueOf(input.getText());
                String message = sender+":"+text;
                if(connAux) {
                    if (!flagGroupMessage) {
                        sendReceive.writeMessage(message.getBytes());
                    } else {
                        gSendReceive.sendToAll(message.getBytes(), server.getOutputArray());
                    }
                    //System.out.println(fAux);
                    if(type) textView.append(message + "\n");
                }
            }
        });
    }



    private void clientConnection(){
        pairedDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothSocket socket = null;

                UUID uuidClient =  UUID.fromString("00001101-0000-1000-8000-" + ServerConnector.makeAddress(myBluetoothAdapter.getName()).toLowerCase());
                System.out.println("<MAIN ACTIVITY> CLIENT NAME ADAPTER :" + myBluetoothAdapter.getName());
                System.out.println("<MAIN ACTIVITY> CLIENT UUID :" + uuidClient.toString());
                Client client = new Client(btDevices[position], uuidClient, handler);
                client.start();
                while(socket == null){
                    socket = client.getmBTSocket();
                }

                sendReceive = new SendReceive(client.getmBTSocket(), handler);

                statusArea.setText("Connecting");
            }
        });
    }


    public void setAPP_UUID(){

        permissionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(chooseList[position].getName());
                if(APP_UUID.size() <= 7){
                    APP_UUID.add( UUID.fromString( "00001101-0000-1000-8000-" +  ServerConnector.makeAddress(chooseList[position].getName()).toLowerCase()));
                    System.out.println( APP_UUID.get(APP_UUID.size() - 1) );
                }else{
                    APP_UUID.remove(BTServer.MAX_CONNECTIONS);
                    permissionList.setVisibility(View.GONE);
                }
            }
        });
    }

    //Create Handler
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what){
                case STATE_LISTENING:{ statusArea.setText("Listening");break; }
                case STATE_CONNECTING:{  statusArea.setText("Connecting");break; }
                case STATE_CONNECTED:{ statusArea.setText("Connected");connAux = true;break; }
                case STATE_CONNECTION_FAILED:{statusArea.setText("Failed");break;}
                case STATE_MESSAGE_RECEIVED:{
                    byte[] readBuffer = (byte[]) msg.obj;
                    String receiveMsg = new String(readBuffer,0,msg.arg1);
                    textView.append(receiveMsg + "\n");
                    break;
                }

            }

            return false;
        }
    });

}
