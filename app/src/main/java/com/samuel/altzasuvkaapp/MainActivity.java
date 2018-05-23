package com.samuel.altzasuvkaapp;


import android.Manifest;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.samuel.altzasuvkaapp.fragments.CakeFragment;
import com.samuel.altzasuvkaapp.fragments.ConfigFragment;
import com.samuel.altzasuvkaapp.fragments.HwConfigFragment;
import com.samuel.altzasuvkaapp.fragments.LineChartFragment;
import com.samuel.altzasuvkaapp.fragments.LiveFragment;
import com.samuel.altzasuvkaapp.fragments.MainFragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //premenne Bluetoothu
    private boolean connectedStatus = false;
    private boolean scanningState = true;
    private BluetoothAdapter mBluetoothAdapter; //deklaracia Bt adaptera telefonu
    private BluetoothDevice mBluetoothDevice;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int RESULT_OK = 2;
    private static final int RESULT_CANCELED = 0; //musi byt 0
    private static final long SCAN_PERIOD = 10000;
    //UUI pre pristup k servicom
    protected static final UUID UPDATE_NOTIFICATION_DESCRIPTOR= UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID ENVIRONMENT_SERVICE=UUID.fromString("EF680200-9B35-4933-9B10-52FFA9740042");
    private static final UUID TEMPERATURE=UUID.fromString("EF680201-9B35-4933-9B10-52FFA9740042");
    private static final UUID HUMIDITY=UUID.fromString("EF680203-9B35-4933-9B10-52FFA9740042");
    private static final UUID LED = UUID.fromString("EF680300-9B35-4933-9B10-52FFA9740042");
    private static final UUID BLINK = UUID.fromString("EF680301-9B35-4933-9B10-52FFA9740042");
    //premenne pre pristup k nameranym hodnotam
    public int value1;
    public int value2;
    //arraylist devicov
    ArrayList<BTDevice> devices = new ArrayList<>();
    //implementacia Bluetooth Callbacku
    BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            boolean add=true;
                            for(int i=0;i<devices.size();i++)
                            {
                                if(Objects.equals(device.getAddress(), devices.get(i).getAddress()))
                                {
                                    add=false;
                                }
                            }
                            if(add)
                            {
                                devices.add(new BTDevice(device));
                                adapter.notifyDataSetChanged();
                                DialogProgressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            };
    //premenne interfacu
    public Intervals intervaly = new Intervals();
    MainFragment mainFragment = new MainFragment();
    //premenne dialogu
    ListView listView;
    Button DialogRefresh;
    ProgressBar DialogProgressBar;
    protected boolean mLocationPermissionGranted;
    DeviceListAdapter adapter = new DeviceListAdapter();
    //multithreading premenne
    Handler mHandler;
    int mState=0; //state machine pre čitanie charakteristik rad za radom


    public MainFragment getMainFragment() {
        return mainFragment;
    }

    public void setMainFragment(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }
    public BluetoothDevice getmBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public int getValue1()
    {
        return value1;
    }

    public int getValue2()
    {
        return value2;
    }

    public boolean getisConnectedStatus() {
        return connectedStatus;
    }

    public void setisConnectedStatus(boolean connectedStatus) {
        this.connectedStatus = connectedStatus;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mLocationPermissionGranted = false;
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
                break;
        }
    }

    private void initPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestFineLocationPermission();
        } else {
            mLocationPermissionGranted = true;
        }
    }
    private void requestFineLocationPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                mainFragment.setConnectedStatus(mBluetoothDevice);
                Toast toast = Toast.makeText(context, "Device Connected, You can fully use App now!", Toast.LENGTH_SHORT);
                connectedStatus = true;
                toast.show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                 Log.e("GATT", "DisConnected!!!");
                connectedStatus = false;
                if (mBluetoothAdapter.isEnabled())
                {
                    mainFragment.setEnabledStatus();

                } else
                    mainFragment.setDisabledStatus();
            }
            else if(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action))
            {
                Log.e("GATT", "DisConnected requested!!!");
                connectedStatus = false;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inicializacia BT na interface
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) //check ci ma telefon BT LE
        {
            mainFragment.setNoBtStatus();
        }
        //deklaracia adaptera
        final BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();



        //setlayout a navdrawer
        setContentView(R.layout.activity_main); //layout do ktorehu su davane fragmenty a obsahuje navdrawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //setToolbaru s menom
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //nahodeny layout draweru
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view); //najdene ID navigacneho menu
        navigationView.setNavigationItemSelectedListener(this);
        if (null == savedInstanceState) //ak tam nebol fragment nahod defaultny
        {
            FragmentManager fm = getFragmentManager(); //android si posledny fragment automaticky uklada do Bundlu
            fm.beginTransaction().replace(R.id.content_frame, mainFragment).commit();
        }



        //inicializiacia receivera na BT akcie
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(BTReceiver, filter1);
        registerReceiver(BTReceiver, filter2);
        registerReceiver(BTReceiver, filter3);


        //Inicializacia multithreadingu
        mHandler=new Handler(Looper.getMainLooper());
        try {
            startBluetooth();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //kontrola povoleni pristupu k lokalite
        initPermission();


    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (mBluetoothAdapter == null) //ak nie je v telefone adapter
        {
            mainFragment.setNoBtStatus();
            return;
        }
        if (mBluetoothAdapter.isEnabled()) //ak je adapter zapnuty daj vediet
        {
            mainFragment.setEnabledStatus();
            if (connectedStatus)
                mainFragment.setConnectedStatus(mBluetoothDevice);
        } else {
            mainFragment.setDisabledStatus();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)  //Override metoda aby bolo jasny aky vysledok z nej bol
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // zbehol vobec request ?
            if (resultCode == RESULT_OK)
            {
                try {
                    TimeUnit.SECONDS.sleep(3); //po 3 sekundach napis zapnuty Bt, cisto esteticka zalezitost
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //mainFragment.setEnabledStatus();
                try {
                    openBluetoothMenu();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == RESULT_CANCELED)
            {
                mainFragment.setDisabledStatus();
            }
        }
    }

    public void notAllowedAlert()
    {
        Context context = MainActivity.this;
        Toast toast = Toast.makeText(context, "Not Allowed ! Device NOT Connected !", Toast.LENGTH_SHORT);
        toast.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fm = getFragmentManager(); //nutne pre nacitavanie fragmentov
        int id = item.getItemId();
        Bundle data = new Bundle();
        data.putSerializable("Intervaly", intervaly);
        switch (id) { //logika menu, podla vybrateho nacitaj fragment
            case R.id.nav_home:
                this.getSupportActionBar().setTitle("Inteligentná zásuvka");
                fm.beginTransaction().replace(R.id.content_frame, mainFragment)/*.addToBackStack("main")*/.commit();
                break;
            case R.id.nav_live:
                if(connectedStatus)
                fm.beginTransaction().replace(R.id.content_frame,new LiveFragment()).addToBackStack("live").commit();
                else
                   notAllowedAlert();
                break;
            case R.id.nav_config:
                if(connectedStatus) {
                    ConfigFragment c = new ConfigFragment();
                    c.setArguments(data);
                    fm.beginTransaction().replace(R.id.content_frame, c).addToBackStack("main").commit();
                                    }
                                    else
                    notAllowedAlert();
                break;
            case R.id.nav_graph:
                if(connectedStatus) {
                    LineChartFragment l = new LineChartFragment();
                    l.setArguments(data);
                    fm.beginTransaction().replace(R.id.content_frame, l).addToBackStack("line").commit();
                }
                else
                    notAllowedAlert();
                break;
            case R.id.nav_cake:
                if(connectedStatus)
                fm.beginTransaction().replace(R.id.content_frame, new CakeFragment()).addToBackStack("cake").commit();
                else
                    notAllowedAlert();
                break;
            case R.id.nav_config_hw:
                if(connectedStatus)
                fm.beginTransaction().replace(R.id.content_frame, new HwConfigFragment()).addToBackStack("hwset").commit();
                else
                    notAllowedAlert();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //najdi drawer
        drawer.closeDrawer(GravityCompat.START); // a zatvor drawer po vybere
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            mBluetoothAdapter.disable();
            finish();//kvoli lepsiemu lifecycle, do stacku su davane posledne nacitane fragmenty
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("connectedstatus", connectedStatus);
    }

    public void startBluetooth() throws InterruptedException
    {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            onActivityResult(REQUEST_ENABLE_BT, RESULT_OK, enableBtIntent);
        }
    }


    public void openBluetoothMenu() throws InterruptedException
    {
        if (mBluetoothAdapter.isEnabled()) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_devices, null);
            DialogProgressBar = (ProgressBar) mView.findViewById(R.id.dialog_progress_bar);
            DialogProgressBar.setVisibility(View.INVISIBLE);
            DialogRefresh = (Button) mView.findViewById(R.id.dialog_button_refresh);
            DialogRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == DialogRefresh)
                    {
                        if(scanningState)
                        {
                            DialogRefresh.setText("Stop");
                            scanningState=false;
                            scanLeDevice(false);
                        }
                        else
                        {
                            DialogRefresh.setText("Refresh");
                            scanningState=true;
                            scanLeDevice(true);
                        }
                    }
                }
            });
            listView = (ListView) mView.findViewById(R.id.device_list);
            listView.setAdapter(adapter);
            mBuilder.setCancelable(true);
            mBuilder.setView(mView);
            mBuilder.setNegativeButton("Zavrieť", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    devices.clear();
                }
            });
            final AlertDialog dialog = mBuilder.create();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Connecting to " + devices.get(position).getName(), Toast.LENGTH_SHORT);
                    mBluetoothDevice=devices.get(position).bluetoothDevice;
                    BTConnect();
                    dialog.cancel();
                    toast.show();
                }
            });
            dialog.show();
            scanLeDevice(true);
        } /*else
            mainFragment.makeDisabledToast();*/
    }

    private void scanLeDevice(final boolean enable)
    {
       if (enable)
        {            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanningState = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    DialogProgressBar.setVisibility(View.INVISIBLE);
                    DialogRefresh.setText("Refresh");
                }
            }, SCAN_PERIOD);
            scanningState = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            DialogRefresh.setText("Stop");
            DialogProgressBar.setVisibility(View.VISIBLE);
        } else {
           scanningState = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    BluetoothGatt bluetoothGatt;

    public void blinker()
    {
        Log.e("w!!!!", "called blinker ");
        BluetoothGattCharacteristic characteristic = bluetoothGatt.getService(LED).getCharacteristic(BLINK);
        blink(characteristic);

    }
    public void blink(BluetoothGattCharacteristic characteristic)
    {
        int data = 1; // whatever goes here
        if (mBluetoothAdapter == null || bluetoothGatt == null) {
            Log.e("w!!!!", "BluetoothAdapter not initialized");
            return;
        }

        Log.e("w!!!!", "characteristic " + characteristic.toString());

            Log.e("w!!!!", "data write ");

            characteristic.setValue(data,BluetoothGattCharacteristic.FORMAT_UINT8,0);

            bluetoothGatt.writeCharacteristic(characteristic);


    }

    private void BTConnect()
    {
        mBluetoothDevice.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
            {
                bluetoothGatt = gatt;
                super.onConnectionStateChange(gatt, status, newState);
                if(status==BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED)
                {
                   // Log.e("GATT", "Connected!!!");
                    //ked som pripojeny musim pristupit k servicom
                    gatt.discoverServices();
                }
                else if (status != BluetoothGatt.GATT_SUCCESS)
                {
                   // Log.e("GATT", "NOT Connected!!!");
                    //neuspesny pokus odpoj
                    gatt.disconnect();
                    connectedStatus=false;
                }

            }

            public boolean setCharacteristicNotification(BluetoothGatt gatt, UUID serviceUuid, UUID characteristicUuid,
                                                         boolean enable)
            {
                //inicializacia charakteristiky pomocou UID
                BluetoothGattCharacteristic characteristic = gatt.getService(serviceUuid).getCharacteristic(characteristicUuid);

                //upozorni ma ked sa charakteristika zmeni (user strana)
                gatt.setCharacteristicNotification(characteristic, enable);

                ////upozorni ma ked sa charakteristika zmeni (device strana)
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UPDATE_NOTIFICATION_DESCRIPTOR);
                descriptor.setValue(true ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : new byte[]{0x00, 0x00});

                //ak true tak success
                return gatt.writeDescriptor(descriptor);

            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (status == BluetoothGatt.GATT_SUCCESS)
                {
                    switch (mState)
                    {
                        case 0:

                    if(setCharacteristicNotification(gatt,ENVIRONMENT_SERVICE,TEMPERATURE,true)) {
                      //  Log.e("!!!E", "CHECK TEMP OK!!");
                    }
                    break;
                        case 1:
                    if(setCharacteristicNotification(gatt,ENVIRONMENT_SERVICE,HUMIDITY,true))
                    {
                      //  Log.e("!!!E","CHECK HUM OK!!");
                        //prepinac toto musi byt pri poslednej charakteristike aby fungoval state machine
                        reset();
                    }
                    break;
                    }
                }
            }
            public void reset()
            {
                mState=0;
            }
            public void switchToNextSensor()
            {
                mState++;
            }
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
            {
                super.onCharacteristicRead(gatt, characteristic, status);
              //  Log.e("!!!","READ CALLED!!!");

                switchToNextSensor();

                onServicesDiscovered(gatt,BluetoothGatt.GATT_SUCCESS);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
            {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic)
            {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.e("!!!","CHANGE CALLED!!!");
                Log.e("!!!",characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0).toString());
                onCharacteristicRead(gatt,characteristic,BluetoothGatt.GATT_SUCCESS);
                if(characteristic.getUuid().equals(TEMPERATURE))
                {
                    value1=characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mainFragment.setValue1(value1);
                        }
                    });
                }
                else if(characteristic.getUuid().equals(HUMIDITY))
                     {
                         value2=characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mainFragment.setValue2(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
                            }
                        });
                     }
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
            {
                super.onDescriptorRead(gatt, descriptor, status);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
            {
                super.onDescriptorWrite(gatt, descriptor, status);
                //kontrola spravneho spristupnenia charakteristiky v logu
               // Log.e("W","WROTE DESC!!!");
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
            }
        });
    }


    //adapter pre listview zobrazujuce najdene zariadenia
    public class DeviceListAdapter extends BaseAdapter
    {
        public DeviceListAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.listitem_device, null);

            TextView address = (TextView) convertView.findViewById(R.id.device_address);
            TextView name = (TextView) convertView.findViewById(R.id.device_name);
            address.setText(devices.get(position).getAddress());
            name.setText(devices.get(position).getName());
            return convertView;

        }
    }
    
}
