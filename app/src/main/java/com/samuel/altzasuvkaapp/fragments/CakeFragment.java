package com.samuel.altzasuvkaapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.samuel.altzasuvkaapp.R;

import java.util.ArrayList;
import java.util.List;

public class CakeFragment extends Fragment
{
    Spinner spinner;
    TextView spinnertext;
    int spinposition;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//uloz instanciu
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null) //ak je nieco v bundli
            spinposition = savedInstanceState.getInt("spinner");
        View rootView=inflater.inflate(R.layout.cake_fragment,container,false); //inflate layout
        PieChart pieChart = (PieChart) rootView.findViewById(R.id.chartcake); //najdi graf v layoute
        setSpinner(rootView);
        List<PieEntry> entries = new ArrayList<>(); //graf data
        //ina grafova magia podla dokumentacie
        entries.add(new PieEntry(18.5f, "Obývačka"));
        entries.add(new PieEntry(26.7f, "Kuchyňa"));
        entries.add(new PieEntry(24.0f, "Spálňa"));
        entries.add(new PieEntry(30.8f, "Kúpeľňa"));
        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setHardwareAccelerationEnabled(true);
        pieChart.animateY(1500);
        Description desc =  new Description();
        desc.setText("Spotreba jednotlivých miestností");
        //desc.setPosition(150,150);
        desc.setTextSize(18);
        PieData data = new PieData(set);
        pieChart.setDescription(desc);
        pieChart.setData(data);
        pieChart.invalidate(); //
        return rootView;
    }
    public void setSpinner(View rootView)
    {
        spinnertext= (TextView) rootView.findViewById(R.id.cake_label); // najdi label
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
                    spinnertext.setText("Spotreba za posledných 24 hodín");
                }
                if(position == 1)
                {
                    spinnertext.setText("Spotreba za poslednú hodinu");
                }
                if(position == 2)
                {
                    spinnertext.setText("Spotreba za posledný týždeň");
                }
                if(position == 3)
                {
                    spinnertext.setText("Spotreba za posledný mesiac");
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

}
