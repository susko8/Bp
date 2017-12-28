package com.samuel.altzasuvkaapp;


import android.app.FragmentManager;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.samuel.altzasuvkaapp.fragments.CakeFragment;
import com.samuel.altzasuvkaapp.fragments.ConfigFragment;
import com.samuel.altzasuvkaapp.fragments.LineChartFragment;
import com.samuel.altzasuvkaapp.fragments.MainFragment;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener //implementacia bocneho menu
{
    public Intervals intervaly = new Intervals();
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
            fm.beginTransaction().replace(R.id.content_frame,new MainFragment()).commit();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        FragmentManager fm = getFragmentManager(); //nutne pre nacitavanie fragmentov
        Bundle data = new Bundle();
        data.putSerializable("Intervaly", intervaly);
        int id = item.getItemId();
        switch (id)
        { //logika menu, podla vybrateho nacitaj fragment
            case R.id.nav_home:
                fm.beginTransaction().replace(R.id.content_frame,new MainFragment()).commit();
                break;
            case R.id.nav_config:

                ConfigFragment c= new ConfigFragment();
                c.setArguments(data);
             fm.beginTransaction().replace(R.id.content_frame,c).addToBackStack("main").commit();
                break;
            case R.id.nav_graph:
                Bundle dataTwo = new Bundle();
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
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
    super.onRestoreInstanceState(savedInstanceState);
    }
}

