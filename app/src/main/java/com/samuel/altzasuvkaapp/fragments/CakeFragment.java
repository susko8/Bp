package com.samuel.altzasuvkaapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.samuel.altzasuvkaapp.R;

import java.util.ArrayList;
import java.util.List;

public class CakeFragment extends Fragment implements OnChartValueSelectedListener
{
    Spinner spinner;
    TextView spinnertext;
    TextView info;
    PieChart pieChart;
    int spinposition;
    String showData;
    List<PieEntry> entries;
    View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//uloz instanciu
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Graf spotreby v domácnosti");
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null) //ak je nieco v bundli
            spinposition = savedInstanceState.getInt("spinner");
        rootView=inflater.inflate(R.layout.cake_fragment,container,false); //inflate layout
        pieChart = (PieChart) rootView.findViewById(R.id.chartcake); //najdi graf v layoute
        info=(TextView)rootView.findViewById(R.id.cake_info);


        //info.setVisibility(View.INVISIBLE);

        setSpinner(rootView);
        entries=new ArrayList<>();
        showData="day";

        pieChart.setHardwareAccelerationEnabled(true);
        pieChart.animateY(1500);

        Description desc =  new Description();
        desc.setText("Spotreba jednotlivých miestností");
        //desc.setPosition(150,150);
        desc.setTextSize(18);
        pieChart.setDescription(desc);
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.invalidate();

        return rootView;
    }

    public void setData()
    {
        //graf data
        entries.clear();
        if(showData.equals("day")) {
            entries.add(new PieEntry(18.5f, "Obývačka"));
            entries.add(new PieEntry(26.7f, "Kuchyňa"));
            entries.add(new PieEntry(24.0f, "Spálňa"));
            entries.add(new PieEntry(30.8f, "Kúpeľňa"));
        }
        if(showData.equals("week"))
        {
            entries.add(new PieEntry(22f, "Obývačka"));
            entries.add(new PieEntry(45f, "Kuchyňa"));
            entries.add(new PieEntry(21f, "Spálňa"));
            entries.add(new PieEntry(12f, "Kúpeľňa"));
        }
        if(showData.equals("month"))
        {
            entries.add(new PieEntry(23f, "Obývačka"));
            entries.add(new PieEntry(40f, "Kuchyňa"));
            entries.add(new PieEntry(30f, "Spálňa"));
            entries.add(new PieEntry(7f, "Kúpeľňa"));
        }

        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.invalidate();
    }
    public void setSpinner(View rootView)
    {
        //spinnertext= (TextView) rootView.findViewById(R.id.cake_label); // najdi label
        //spinner pre výber
        spinner = (Spinner) rootView.findViewById(R.id.cake_spinner); //najdi spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.spinnerarray, android.R.layout.simple_spinner_item); //daj mu array definovany v string.xml
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); //prirad adapter
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//listener a v nom logika
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id)
            {
                if(position == 0)
                {
                    showData = "day";
                    setData();
                    info.setText("");
                }
                if(position == 1)
                {
                    showData = "week";
                    setData();
                    info.setText("");
                }
                if(position == 2)
                {
                    showData = "month";
                    setData();
                    info.setText("");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                spinnertext.setText("Vyber jednu z možností"); //ak nic nevybrate
            }
        });
        spinner.setSelection(spinposition); //ak nieco v bundli tak (re)setni
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("spinner", spinner.getSelectedItemPosition()); //uloz poziciu spinner
    }

    @Override
    public void onValueSelected(Entry e, Highlight h)
    {
        double val = e.getY()/100;
        String s="";
        if(showData.equals("day"))
        {
            val = 5600*val;
            s = "Spotreba miestnosti: "+ String.format("%.2f", val)+" Wh";
        }
        if(showData.equals("week"))
        {
            val = 39.2*val;
            s = "Spotreba miestnosti: "+ String.format("%.2f", val)+" kWh";
        }
        if(showData.equals("month"))
        {
            val = 156.8*val;
            s = "Spotreba miestnosti: "+ String.format("%.2f", val)+" kWh";
        }
        info.setText(s);
    }

    @Override
    public void onNothingSelected() {

    }
}
