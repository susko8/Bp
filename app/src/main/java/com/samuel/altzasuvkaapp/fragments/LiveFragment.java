package com.samuel.altzasuvkaapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.samuel.altzasuvkaapp.Intervals;
import com.samuel.altzasuvkaapp.R;

import java.util.ArrayList;
import java.util.List;

public class LiveFragment extends Fragment
{
    TextView label; //nazov grafu podla spinnera
    Spinner spinner; //spinner
    int spinposition; //poz. spinnera
    Intervals intervaly;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
      /*  if (savedInstanceState != null) //ak je nieco v bundli
            spinposition = savedInstanceState.getInt("spinner"); //ulozena pozicia spinner, sup ju tam
        View rootView = inflater.inflate(R.layout.line_fragment, container, false); //inflate layout
        LineChart chart = (LineChart) rootView.findViewById(R.id.chart_live);
        List<Entry> entries = new ArrayList<Entry>();

        chart.invalidate();*/

        View rootView = inflater.inflate(R.layout.live_fragment, container, false);
        if (savedInstanceState != null) //ak je nieco v bundli
            spinposition = savedInstanceState.getInt("spinner");
        LineChart chart = (LineChart) rootView.findViewById(R.id.chart_live);
        List<Entry> entries = new ArrayList<Entry>();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Live Metering");
        setSpinner(rootView);
        return rootView;
    }
    public void setSpinner(View rootView)
    {
        spinner = (Spinner) rootView.findViewById(R.id.spinner_live); //najdi spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.spinnerlivearray, android.R.layout.simple_spinner_item); //daj mu array definovany v string.xml
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); //prirad adapter
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//listener a v nom logika
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id)
            {
                if(position == 0)
                {
                   //dosomething
                }
                if(position == 1)
                {
                    //dosomething
                }
                if(position == 2)
                {
                    //dosomething
                }
                if(position == 3)
                {
                    //dosomething
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                label.setText("Vyber jednu z možností"); //ak nic nevybrate
            }
        });
        spinner.setSelection(spinposition); //ak nieco v bundli tak (re)setni
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("spinner", spinner.getSelectedItemPosition()); //uloz poziciu spinnera
    }
}
