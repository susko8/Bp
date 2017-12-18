package com.samuel.altzasuvkaapp.fragments;


import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.samuel.altzasuvkaapp.R;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class MainFragment extends Fragment implements View.OnClickListener {
    Button connectButton;
    TextView Id;
    TextView status;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int RESULT_OK = 2;
    private static final int RESULT_CANCELED=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onStart()
    {
        super.onStart();
        if (mBluetoothAdapter.isEnabled())
        {
            String text = "Status: <font color='#43A047'>Bluetooth turned ON</font>";
            status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            Id.setText("ID: Device not connected yet");
        }
        else
        {
            status.setText("Status: Disconnected");
            Id.setText("ID:");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //inicializacia
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        status = (TextView) view.findViewById(R.id.status);
        Id = (TextView) view.findViewById(R.id.device_id);
        connectButton = (Button) view.findViewById(R.id.connect_button);
        connectButton.setOnClickListener(this);
        status.setText("Status: Disconnected");
        //BT Check

        if (mBluetoothAdapter == null) {
            status.setText("Your device doesn´t support Bluetooth");
            Id.setText("You can´t use this application");
        }
        if (mBluetoothAdapter.isEnabled()) {
            String text = "Status: <font color='#43A047'>Bluetooth turned ON</font>";
            status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            Id.setText("ID: Device not connected yet");
        }
        return view;
    }

    @Override
    public void onClick(View v) {

        if (v == connectButton) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                onActivityResult(REQUEST_ENABLE_BT, RESULT_OK, enableBtIntent);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK)
            {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String text = "Status: <font color='#43A047'>Bluetooth turned ON</font>";
                status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                Id.setText("ID: Device not connected yet");
                IntentFilter filter = new IntentFilter();
                /*filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);*/
            }
            if(resultCode==RESULT_CANCELED)
            {
                status.setText("Status: Disconnected");
                Id.setText("ID: ");
            }
        }
    }
}
