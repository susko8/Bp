package com.samuel.altzasuvkaapp.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//uloz instanciu
    }

    public void setConnectedStatus()
    {

            Id.setText("ID: XX:XX:XX:XX:XX ");
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //inicializacia
        View view = inflater.inflate(R.layout.main_fragment, container, false); //inflate layout fragmentom
        status = (TextView) view.findViewById(R.id.status); //najdi status
        Id = (TextView) view.findViewById(R.id.device_id); //najdi Id
        connectButton = (Button) view.findViewById(R.id.connect_button); //najdi button
        connectButton.setOnClickListener(this); //daj mu listener
        status.setText("Status: Nepripojené "); //daj mu default text
        btTrigger = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        btTrigger.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v)
    {
        Activity activity = getActivity();
        MainActivity myactivity = (MainActivity) activity;
        if (v == btTrigger) {
            myactivity.startBluetooth();
        }
        if (v == connectButton)
        {
            try {
                myactivity.openBluetoothMenu();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
