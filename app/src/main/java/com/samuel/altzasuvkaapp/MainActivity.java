package com.samuel.altzasuvkaapp;

import android.app.Fragment;
import android.app.FragmentManager;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.samuel.altzasuvkaapp.fragments.CakeFragment;
import com.samuel.altzasuvkaapp.fragments.ConfigFragment;
import com.samuel.altzasuvkaapp.fragments.LineChartFragment;
import com.samuel.altzasuvkaapp.fragments.MainFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    int trigger=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame,new MainFragment()).commit();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        FragmentManager fm = getFragmentManager();
        int id = item.getItemId();

        switch (id){
            case R.id.nav_home:
                fm.beginTransaction().replace(R.id.content_frame,new MainFragment()).commit();
                trigger=0;
                break;
            case R.id.nav_config:
             fm.beginTransaction().replace(R.id.content_frame,new ConfigFragment()).addToBackStack("main").commit();
                trigger=1;
                break;
            case R.id.nav_graph:
                fm.beginTransaction().replace(R.id.content_frame,new LineChartFragment()).addToBackStack("line").commit();
                trigger=2;
                break;
            case R.id.nav_cake:
                fm.beginTransaction().replace(R.id.content_frame,new CakeFragment()).addToBackStack("cake").commit();
                trigger=3;
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void ForRotate()
    {
        FragmentManager fm = getFragmentManager();
        switch (trigger)
        {
            case 0:
                fm.beginTransaction().replace(R.id.content_frame,new MainFragment()).commit();
                break;
            case 1:
                fm.beginTransaction().replace(R.id.content_frame,new ConfigFragment()).addToBackStack("main").commit();
                break;
            case 2:
                fm.beginTransaction().replace(R.id.content_frame,new LineChartFragment()).addToBackStack("line").commit();
                break;
            case 3:
                fm.beginTransaction().replace(R.id.content_frame,new CakeFragment()).addToBackStack("cake").commit();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onBackPressed()
    {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else
        {
            super.onBackPressed();
        }
    }
    @Override
   protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("trigger",trigger);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
    super.onRestoreInstanceState(savedInstanceState);
        trigger=savedInstanceState.getInt("trigger");
        ForRotate();
    }
}

