package com.samuel.altzasuvkaapp.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.samuel.altzasuvkaapp.MainActivity;
import com.samuel.altzasuvkaapp.R;

public class MainFragment extends Fragment implements View.OnClickListener {
    private Button connectButton; //button na pripojenie
    private FloatingActionButton btTrigger;
    private TextView Id;
    private TextView status;
    private TextView value1;
    private TextView value2;
    private BluetoothAdapter mBluetoothAdapter;
    //multithreading



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//uloz instanciu
    }

    @Override
    public void onStart()
    {
        super.onStart();
        boolean status =((MainActivity)getActivity()).getisConnectedStatus();
        if(status)
        {
            this.setConnectedStatus(((MainActivity) getActivity()).getmBluetoothDevice());
           // Log.e("!!!","SET CALLED FROM FRAGMENT START!!!");
        }
    }

    public void setConnectedStatus(BluetoothDevice mBluetoothDevice)
    {
            Log.e("!!!","SETTING CALLED!!!");
            String IdText="ID: "+mBluetoothDevice.getName().toString()+", "+mBluetoothDevice.getAddress().toString();
            Id.setText(IdText);
            String text = "Status: <font color='#43A047'>Connected</font>";
            status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
    }
    public void setEnabledStatus()
    {
        String text = "Status: <font color='#43A047'>Bluetooth turned ON</font>";
        status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        Id.setText("ID: Nepripojená žiadna zásuvka");
    }
    public void setDisabledStatus()
    {
        status.setText("Status: Nepripojené "); //daj mu default text
        Id.setText("ID:");
    }
    public void setId(String string)
    {
        Id.setText(string);
    }
    public void setNoBtStatus()
    {
        status.setText("Your device doesn´t support Bluetooth");
        Id.setText("You can´t use this application");
    }
    public void makeDisabledToast()
    {
        Context context = this.getActivity().getApplicationContext();
        Toast toast = Toast.makeText(context, "Bluetooth Disabled, cannot access activity", Toast.LENGTH_SHORT);
        toast.show();
    }
    public void setValue1(int temp)
    {
      //  Log.e("!!!","SET VAL1 CALLED");
        value1.setText(temp+"°C");
    }
    public void setValue2(int hum)
    {
      //  Log.e("!!!","SET VAL2 CALLED");
        value2.setText(hum+"%");
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
              //inicializacia
        View view = inflater.inflate(R.layout.main_fragment, container, false); //inflate layout fragmentom
        status = (TextView) view.findViewById(R.id.status); //najdi status
        Id = (TextView) view.findViewById(R.id.device_id); //najdi Id
        value1=(TextView) view.findViewById(R.id.val1);
        value2=(TextView) view.findViewById(R.id.val2);
        connectButton = (Button) view.findViewById(R.id.connect_button); //najdi button
        connectButton.setOnClickListener(this); //daj mu listener
        status.setText("Status: Nepripojené "); //daj mu default text
        return view;
    }

    @Override
    public void onClick(View v)
    {
        Activity activity = getActivity();
        MainActivity myactivity = (MainActivity) activity;
        if (v == connectButton)
        {
            final BluetoothManager manager = (BluetoothManager) myactivity.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = manager.getAdapter();
            if(!mBluetoothAdapter.isEnabled())
            {
                try {
                    myactivity.startBluetooth();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                myactivity.openBluetoothMenu();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    }
