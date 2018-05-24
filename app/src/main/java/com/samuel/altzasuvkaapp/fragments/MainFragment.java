package com.samuel.altzasuvkaapp.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.samuel.altzasuvkaapp.Intervals;
import com.samuel.altzasuvkaapp.MainActivity;
import com.samuel.altzasuvkaapp.R;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements View.OnClickListener {
    private Button connectButton; //button na pripojenie
    private Button setRoomButton;
    private FloatingActionButton btTrigger;
    private TextView Id;
    private TextView status;
    private TextView value1;
    private TextView value2;
    //premenne dialogu
    RoomListAdapter adapter = new RoomListAdapter();
    ListView listRoomView;
    Intervals intervaly;
    List<String> rooms;
    //multithreading



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle arguments = getArguments();
        intervaly = (Intervals) arguments.getSerializable("Intervaly");
        setRetainInstance(true);//uloz instanciu
        RetrieveSettings();
    }

    public void RetrieveSettings()
    {
        @SuppressLint("WrongConstant") SharedPreferences sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_APPEND);
        if(sharedPref.contains("Intervaly"))
        {
            Gson gson = new Gson();
            String json = sharedPref.getString("Intervaly", "");
            intervaly = gson.fromJson(json, Intervals.class);
            rooms = intervaly.getRooms();
        }
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
        RetrieveSettings();
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
        Toast toast = Toast.makeText(context, "Bluetooth vypnutý, nemôžem pristúpiť k aktivite", Toast.LENGTH_SHORT);
        toast.show();
    }
    public void setValue1(float temp)
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
        setRoomButton = (Button) view.findViewById(R.id.place_button);
        setRoomButton.setOnClickListener(this);
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
            BluetoothAdapter mBluetoothAdapter = manager.getAdapter();
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
        if(v == setRoomButton)
        {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_rooms, null);
            listRoomView = (ListView) mView.findViewById(R.id.room_list);
            listRoomView.setAdapter(adapter);
            mBuilder.setCancelable(true);
            mBuilder.setView(mView);
            mBuilder.setNegativeButton("Zavrieť", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            final AlertDialog dialog = mBuilder.create();
            listRoomView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Context context = getActivity().getApplicationContext();
                    Toast toast = Toast.makeText(context, "Zásuvka zaradená do miestnosti " + rooms.get(position), Toast.LENGTH_SHORT);
                    toast.show();
                    dialog.cancel();

                }
            });
            dialog.show();
        }
    }

    public class RoomListAdapter extends BaseAdapter
    {
        public RoomListAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return intervaly.getRooms().size();
        }

        @Override
        public Object getItem(int position) {
            return rooms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_room, null);
            TextView roomName = (TextView) convertView.findViewById(R.id.room_name);
            roomName.setText(rooms.get(position));
            return convertView;

        }
    }
}

