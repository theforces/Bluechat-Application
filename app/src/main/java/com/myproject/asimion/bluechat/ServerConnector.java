package com.myproject.asimion.bluechat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;


public class ServerConnector {


    //get paired devices
    public static BluetoothDevice[] getListener(ListView listPairedDevice, BluetoothAdapter myBluetoothAdapter, Context context){
        Set<BluetoothDevice> Devices = myBluetoothAdapter.getBondedDevices();//getBondedDevices retun Set<BluetoothDevice>
        String[] devices = new String[Devices.size()];
       BluetoothDevice[]  btDevices = new BluetoothDevice[Devices.size()];
        int index = 0;
        if(Devices.size() >= 1){
            for(BluetoothDevice device : Devices){
                devices[index] = device.getName();
                btDevices[index] = device;
                index++;
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context.getApplicationContext(), android.R.layout.simple_list_item_1,devices){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    // Get the Item from ListView
                    View view = super.getView(position, convertView, parent);

                    // Initialize a TextView for ListView each Item
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);

                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.WHITE);

                    // Generate ListView Item using TextView
                    return view;
                }
            };

            listPairedDevice.setAdapter(arrayAdapter);
        }
        return btDevices;
    }


    public static BluetoothDevice[] serverStart(Context context,BluetoothAdapter myBluetoothAdapter,ListView list, ArrayList<UUID> APP_UUID, BluetoothDevice[] permissionDevices, BTServer server){

            list.setVisibility(View.VISIBLE);

            Set<BluetoothDevice> Devices = myBluetoothAdapter.getBondedDevices();//getBondedDevices retun Set<BluetoothDevice>
            String[] devices = new String[Devices.size()];
            //BluetoothDevice[] btDevices = new BluetoothDevice[Devices.size()];
            permissionDevices = new BluetoothDevice[Devices.size()];
            int index = 0;
            if (Devices.size() >= 1) {
                for (BluetoothDevice device : Devices) {
                    devices[index] = device.getName();
                    permissionDevices[index] = device;
                    index++;
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context.getApplicationContext(), android.R.layout.simple_list_item_1, devices);
               list.setAdapter(arrayAdapter);
            }




    return permissionDevices;
    }

    //return first to charcter between [0-f] in String name
    private static String getFirst2(String name){
        String first2 = "";
        int max = 2;
        char []arr = name.toLowerCase().trim().toCharArray();

        for(int i = 0; i < arr.length; i++){
            if(arr[i] >='0' && arr[i] <= 'f'){
                first2 = first2 + arr[i];
                max--;
            }
            if(max == 0){
                break;
            }
        }
        return first2;
    }

    public static String makeAddress(String device){// BLUETOOTH ADDRESS(MAC) maker
        String address = "";
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String dateToString = format.format(today);
        String []date = dateToString.split(" ");
        String []mid =  date[1].split(":");
        String  end= date[0].replaceAll("-","");
        address =(!getFirst2(device).equals(""))? getFirst2(device) + mid[0] + end : mid[1] + mid[0] + end;

        return address;

    }
}
