package com.samuel.altzasuvkaapp.fragments;


import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.samuel.altzasuvkaapp.R;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainFragment extends Fragment implements View.OnClickListener {
    Button connectButton; //button na pripojenie
    FloatingActionButton btTrigger;
    TextView Id;
    TextView status;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //deklaracia Bt adaptera telefonu
    public ArrayList<BluetoothDevice> mBTDevices=new ArrayList<>();
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int RESULT_OK = 2;
    private static final int RESULT_CANCELED=0; //musi byt 0
    private boolean connectedstatus=false;
    //pidy na ktorych bezi BT, budu pribudat
    private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                connectedstatus=true;
                setConnectedStatus(connectedstatus);
                Toast toast = Toast.makeText(context,"Device Connected, You can fully use App now!", Toast.LENGTH_SHORT);
                toast.show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
            connectedstatus=false;
              setConnectedStatus(connectedstatus);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//uloz instanciu
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.getActivity().registerReceiver(BTReceiver, filter1);
        this.getActivity().registerReceiver(BTReceiver, filter2);
        this.getActivity().registerReceiver(BTReceiver, filter3);
    }
    @Override
    public void onStart()
    {
        super.onStart();
       if (mBluetoothAdapter.isEnabled()) //ak je adapter zapnuty daj vediet
        {
            String text = "Status: <font color='#43A047'>Bluetooth turned ON</font>";
            status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            Id.setText("ID: Nepripojená žiadna zásuvka");
            if(connectedstatus)
            {
                setConnectedStatus(connectedstatus);
            }
        }
        else
        {
            status.setText("Status: Nepripojené"); //ak nie je tiez daj vediet
            Id.setText("ID:");
        }
    }
    public void setConnectedStatus(boolean connectedstatus)
    {
        if(connectedstatus)
        {
            Id.setText("ID: XX:XX:XX:XX:XX ");
            String text = "Status: <font color='#43A047'>Connected</font>";
            status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        }
        else if (mBluetoothAdapter.isEnabled()) //ak je adapter zapnuty daj vediet
        {
            String text = "Status: <font color='#43A047'>Bluetooth turned ON</font>";
            status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            Id.setText("ID: Nepripojená žiadna zásuvka");
        }
        else
        {
            status.setText("Status: Nepripojené"); //ak nie je tiez daj vediet
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
        status.setText("Status: Nepripojené "); //daj mu default text
        btTrigger = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        btTrigger.setOnClickListener(this);
        if (savedInstanceState != null) //ak je nieco v bundli
        {
            connectedstatus = savedInstanceState.getBoolean("connectedstatus");
            setConnectedStatus(connectedstatus);
        }
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
            Id.setText("ID: Nepripojená žiadna zásuvka");
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
           if(mBluetoothAdapter.isEnabled())
           {
               Intent intent = new Intent (Intent.ACTION_MAIN,null);
               intent.addCategory(Intent.CATEGORY_LAUNCHER);
               ComponentName cn = new ComponentName("com.android.settings",
                       "com.android.settings.bluetooth.BluetoothSettings");
               intent.setComponent(cn);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent);
           }
           else
           {
               Context context = this.getActivity().getApplicationContext();
               Toast toast = Toast.makeText(context,"Bluetooth Disabled, cannot access activity", Toast.LENGTH_SHORT);
               toast.show();
           }


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
                String text = "Status: <font color='#43A047'>Bluetooth je zapnutý</font>"; //daj mi vediet ze bol
                status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                Id.setText("ID: Nepripojená žiadna zásuvka");
                IntentFilter filter = new IntentFilter(); //todo intent pre hladanie realnych deviceov
            }
            if(resultCode==RESULT_CANCELED)
            {
                status.setText("Status: Disconnected"); //ak clovek BT nezapol
                Id.setText("ID: ");
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("connectedstatus", connectedstatus); //uloz poziciu spinner
    }
}
