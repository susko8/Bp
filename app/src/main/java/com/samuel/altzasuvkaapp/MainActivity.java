package com.samuel.altzasuvkaapp;


import android.Manifest;
import android.app.Dialog;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.samuel.altzasuvkaapp.fragments.MainFragment;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //premenne BLuetoothu
    private boolean connectedStatus = false;
    private boolean scanningState = true;
    private BluetoothAdapter mBluetoothAdapter; //= BluetoothAdapter.getDefaultAdapter(); //deklaracia Bt adaptera telefonu
    private BluetoothGatt mBluetoothGatt;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int RESULT_OK = 2;
    private static final int RESULT_CANCELED = 0; //musi byt 0
    private static final long SCAN_PERIOD = 10000;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    ArrayList<BTDevice> devices = new ArrayList<>();
    //premenne interfacu
    public Intervals intervaly = new Intervals();
    MainFragment mainFragment = new MainFragment();
    //premenne dialogu
    ListView listView;
    Button DialogRefresh;
    ProgressBar DialogProgressBar;
    DeviceListAdapter adapter = new DeviceListAdapter();
    //multithreading premenne



    private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                mainFragment.setConnectedStatus();
                Toast toast = Toast.makeText(context, "Device Connected, You can fully use App now!", Toast.LENGTH_SHORT);
                connectedStatus = true;

                toast.show();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                if (mBluetoothAdapter.isEnabled()) {
                    mainFragment.setEnabledStatus();
                    connectedStatus = false;
                } else
                    mainFragment.setDisabledStatus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) //check ci ma telefon BT LE
        {
            mainFragment.setNoBtStatus();
        }
        //deklaracia adaptera
        final BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        ;
        mBluetoothAdapter = manager.getAdapter();
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
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(BTReceiver, filter1);
        registerReceiver(BTReceiver, filter2);
        registerReceiver(BTReceiver, filter3);
        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
    }

    @Override
    public void onStart() {
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
                mainFragment.setConnectedStatus();
        } else {
            mainFragment.setDisabledStatus();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)  //Override metoda aby bolo jasny aky vysledok z nej bol
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // zbehol vobec request ?
            if (resultCode == RESULT_OK) {
                try {
                    TimeUnit.SECONDS.sleep(3); //po 3 sekundach napis zapnuty Bt, cisto esteticka zalezitost
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mainFragment.setEnabledStatus();
            }
            if (resultCode == RESULT_CANCELED) {
                mainFragment.setDisabledStatus();
            }
        }
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
                fm.beginTransaction().replace(R.id.content_frame, mainFragment).commit();
                break;
            case R.id.nav_config:

                ConfigFragment c = new ConfigFragment();
                c.setArguments(data);
                fm.beginTransaction().replace(R.id.content_frame, c).addToBackStack("main").commit();
                break;
            case R.id.nav_graph:
                LineChartFragment l = new LineChartFragment();
                l.setArguments(data);
                fm.beginTransaction().replace(R.id.content_frame, l).addToBackStack("line").commit();
                break;
            case R.id.nav_cake:
                fm.beginTransaction().replace(R.id.content_frame, new CakeFragment()).addToBackStack("cake").commit();
                break;
            case R.id.nav_config_hw:
                fm.beginTransaction().replace(R.id.content_frame, new HwConfigFragment()).addToBackStack("hwset").commit();
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
        } else {
            super.onBackPressed(); //kvoli lepsiemu lifecycle, do stacku su davane posledne nacitane fragmenty
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("connectedstatus", connectedStatus);
    }

    public void startBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            onActivityResult(REQUEST_ENABLE_BT, RESULT_OK, enableBtIntent);
        }
    }

    BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            devices.add(new BTDevice(device));
                            adapter.notifyDataSetChanged();
                            DialogProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            };
    public void openBluetoothMenu() throws InterruptedException {
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
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "Connecting to " + devices.get(position).getName(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
            AlertDialog dialog = mBuilder.create();
            dialog.show();
            scanLeDevice(true);
        } else
            mainFragment.makeDisabledToast();
    }

    private void scanLeDevice(final boolean enable) {
       if (enable)
        {
            Handler mHandler=new Handler();
            mHandler.postDelayed(new Runnable() {
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
