package com.samuel.altzasuvkaapp.fragments;


import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
    Button connectButton; //button na pripojenie
    FloatingActionButton btTrigger;
    TextView Id;
    TextView status;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //deklaracia Bt adaptera telefonu
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int RESULT_OK = 2;
    private static final int RESULT_CANCELED=0; //musi byt 0
    //pidy na ktorych bezi BT, budu pribudat

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //uloz stav fragmentu do pamate
    }
    @Override
    public void onStart()
    {
        super.onStart();
        if (mBluetoothAdapter.isEnabled()) //ak je adapter zapnuty daj vediet
        {
            String text = "Status: <font color='#43A047'>Bluetooth turned ON</font>";
            status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            Id.setText("ID: Device not connected yet");
        }
        else
        {
            status.setText("Status: Disconnected"); //ak nie je tiez daj vediet
            Id.setText("ID:");
        }
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
        status.setText("Status: Disconnected"); //daj mu default text
        btTrigger = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        btTrigger.setOnClickListener(this);
        //BT Check
        if (mBluetoothAdapter == null) //ak nie je v telefone adapter
        {
            status.setText("Your device doesn´t support Bluetooth");
            Id.setText("You can´t use this application");
        }
        if (mBluetoothAdapter.isEnabled())  //ak je BT zapnuty opat daj vediet, toto je potrebne ak bola app minimalizovana
        {
            String text = "Status: <font color='#43A047'>Bluetooth turned ON</font>";
            status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            Id.setText("ID: Device not connected yet");
        }
        return view;
    }

    @Override
    public void onClick(View v) {

        if (v == btTrigger) {
            if (!mBluetoothAdapter.isEnabled())
            { //po stlaceni buttonu BT magia
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                onActivityResult(REQUEST_ENABLE_BT, RESULT_OK, enableBtIntent);
            }
        }
        if (v == connectButton)
        {
            //todo
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  //Override metoda aby bolo jasny aky vysledok z nej bol
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT)
            {
            // zbehol vobec request ?
            if (resultCode == RESULT_OK)
            {
                try {
                    TimeUnit.SECONDS.sleep(3); //po 3 sekundach napis zapnuty Bt, cisto esteticka zalezitost
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String text = "Status: <font color='#43A047'>Bluetooth turned ON</font>"; //daj mi vediet ze bol
                status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                Id.setText("ID: Device not connected yet");
                IntentFilter filter = new IntentFilter(); //todo intent pre hladanie realnych deviceov
                /*filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);*/
            }
            if(resultCode==RESULT_CANCELED)
            {
                status.setText("Status: Disconnected"); //ak clovek BT nezapol
                Id.setText("ID: ");
            }
        }
    }
}
