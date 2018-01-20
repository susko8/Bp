package com.samuel.altzasuvkaapp;


import android.app.FragmentManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import com.samuel.altzasuvkaapp.fragments.CakeFragment;
import com.samuel.altzasuvkaapp.fragments.ConfigFragment;
import com.samuel.altzasuvkaapp.fragments.LineChartFragment;
import com.samuel.altzasuvkaapp.fragments.MainFragment;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BluetoothAdapter.LeScanCallback//implementacia bocneho menu
{
    boolean connectedStatus=false;
    public BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //deklaracia Bt adaptera telefonu
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int RESULT_OK = 2;
    private static final int RESULT_CANCELED=0; //musi byt 0
    public Intervals intervaly = new Intervals();
    MainFragment mainFragment = new MainFragment();
    private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
               mainFragment.setConnectedStatus();
                Toast toast = Toast.makeText(context,"Device Connected, You can fully use App now!", Toast.LENGTH_SHORT);
                connectedStatus=true;
                toast.show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                if(mBluetoothAdapter.isEnabled())
                {
                    mainFragment.setEnabledStatus();
                    connectedStatus = false;
                }
                else
                    mainFragment.setDisabledStatus();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if(null == savedInstanceState) //ak tam nebol fragment nahod defaultny
        {
            FragmentManager fm = getFragmentManager(); //android si posledny fragment automaticky uklada do Bundlu
            fm.beginTransaction().replace(R.id.content_frame,mainFragment).commit();
        }
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(BTReceiver, filter1);
        registerReceiver(BTReceiver, filter2);
        registerReceiver(BTReceiver, filter3);
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
        //todo if possibly connected cez socket ?
        if(connectedStatus)
            mainFragment.setConnectedStatus();
    }
    else
    {
        mainFragment.setDisabledStatus();
    }
}
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        FragmentManager fm = getFragmentManager(); //nutne pre nacitavanie fragmentov
        int id = item.getItemId();
        Bundle data = new Bundle();
        data.putSerializable("Intervaly", intervaly);
        switch (id)
        { //logika menu, podla vybrateho nacitaj fragment
            case R.id.nav_home:
                fm.beginTransaction().replace(R.id.content_frame,mainFragment).commit();
                break;
            case R.id.nav_config:

                ConfigFragment c= new ConfigFragment();
                c.setArguments(data);
             fm.beginTransaction().replace(R.id.content_frame,c).addToBackStack("main").commit();
                break;
            case R.id.nav_graph:
                LineChartFragment l = new LineChartFragment();
                l.setArguments(data);
                fm.beginTransaction().replace(R.id.content_frame,l).addToBackStack("line").commit();
                break;
            case R.id.nav_cake:
                fm.beginTransaction().replace(R.id.content_frame,new CakeFragment()).addToBackStack("cake").commit();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout); //najdi drawer
        drawer.closeDrawer(GravityCompat.START); // a zatvor drawer po vybere
        return true;
    }
    @Override
    public void onBackPressed()
    {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else
        {
            super.onBackPressed(); //kvoli lepsiemu lifecycle, do stacku su davane posledne nacitane fragmenty
        }
    }
    @Override
   protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean("connectedstatus", connectedStatus);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
    super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
    {

    }
    public void startBluetooth()
    {
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            onActivityResult(REQUEST_ENABLE_BT, RESULT_OK, enableBtIntent);
        }
    }
    public void openBluetoothMenu()
    {
        if(mBluetoothAdapter.isEnabled())
        {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings",
                    "com.android.settings.bluetooth.BluetoothSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
            mainFragment.makeDisabledToast();
    }
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
              mainFragment.setEnabledStatus();
            }
            if(resultCode==RESULT_CANCELED)
            {
                mainFragment.setDisabledStatus();
            }
        }
    }
}

