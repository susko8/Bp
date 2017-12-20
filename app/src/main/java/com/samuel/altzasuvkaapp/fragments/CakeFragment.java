package com.samuel.altzasuvkaapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView=inflater.inflate(R.layout.cake_fragment,container,false);
        PieChart pieChart = (PieChart) rootView.findViewById(R.id.chartcake);
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(18.5f, "prva"));
        entries.add(new PieEntry(26.7f, "druha"));
        entries.add(new PieEntry(24.0f, "tretia"));
        entries.add(new PieEntry(30.8f, "stvrta"));
        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateY(1500);
        Description desc =  new Description();
        desc.setText("Popis tohoto grafu");
        //desc.setPosition(150,150);
        desc.setTextSize(18);
        PieData data = new PieData(set);
        pieChart.setDescription(desc);
        pieChart.setData(data);
        pieChart.invalidate(); //
        return rootView;
    }

}
