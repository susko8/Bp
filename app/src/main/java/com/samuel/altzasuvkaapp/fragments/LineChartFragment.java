package com.samuel.altzasuvkaapp.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.samuel.altzasuvkaapp.Intervals;
import com.samuel.altzasuvkaapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LineChartFragment extends Fragment
{
    TextView label; //nazov grafu podla spinnera
    Spinner spinner; //spinner
    int spinposition; //poz. spinnera
    Intervals intervaly;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //uloz stav fragmentu
        Bundle arguments = getArguments();
        intervaly = (Intervals) arguments.getSerializable("Intervaly");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null) //ak je nieco v bundli
        {
            spinposition = savedInstanceState.getInt("spinner"); //ulozena pozicia spinner, sup ju tam
        }
        View rootView = inflater.inflate(R.layout.line_fragment, container, false); //inflate layout
       setSpinner(rootView); //nastav spinner
        //tu dáta
        //todo rozumny objekt pre setgrafu a dat
        LineChart chart = (LineChart) rootView.findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();
        Description desc = new Description();
        desc.setText("");
        //desc.setPosition(150,150);
        desc.setTextSize(18);
        //desc.setTextColor(Co);
        //tu cyklus naplniť, pozor treba naplnat asi v poradi
        Random randomGenerator = new Random();
        for (int i = 0; i <= 24; i += 2) {
            int randomInt = randomGenerator.nextInt(100);
            entries.add(new Entry(i, randomInt));
        }
        //todo week and month podla list view
        LineDataSet dataSet = new LineDataSet(entries, "Test Dataset");
        dataSet.setColor(Color.BLACK);
        dataSet.setCubicIntensity(0.5f);
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#43a047"));
        dataSet.setLineWidth(1f);
        dataSet.setCircleRadius(2.5f);
        dataSet.setCircleColor(Color.BLACK);
        dataSet.setDrawCircleHole(false);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.setNoDataText("Zatiaľ nenamerané hodnoty");
        chart.setBackgroundColor(Color.WHITE);
        chart.setBorderColor(Color.GREEN);
        chart.setGridBackgroundColor(Color.GREEN);
        chart.setDescription(desc);
        chart.setKeepPositionOnRotation(true);
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        YAxis yAxisLeft = chart.getAxisLeft();
        YAxis yAxisRight = chart.getAxisRight();
        yAxisLeft.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);
        //Limity pre vyznačenie cien prúdov
        //lacny todo objekt pre drahy a lacny, bude to settovatelne
        LimitLine ll1 = new LimitLine(intervaly.getCheapFrom(), "Lacný prúd");
        ll1.setLineColor(Color.BLUE);
        ll1.setLineWidth(1f);
        ll1.setTextColor(Color.BLACK);
        ll1.setTextSize(5f);
        xAxis.addLimitLine(ll1);
        LimitLine ll2 = new LimitLine(intervaly.getCheapTo(), "");
        ll2.setLineColor(Color.BLUE);
        ll2.setLineWidth(1f);
        ll2.setTextColor(Color.BLACK);
        ll2.setTextSize(5f);
        xAxis.addLimitLine(ll2);
        //drahy
        LimitLine ll3 = new LimitLine(intervaly.getExpFrom(), "Drahý prúd");
        ll3.setLineColor(Color.RED);
        ll3.setLineWidth(1f);
        ll3.setTextColor(Color.BLACK);
        ll3.setTextSize(5f);
        xAxis.addLimitLine(ll3);
        LimitLine ll4 = new LimitLine(intervaly.getExpTo(), "");
        ll4.setLineColor(Color.RED);
        ll4.setLineWidth(1f);
        ll4.setTextColor(Color.BLACK);
        ll4.setTextSize(5f);
        xAxis.addLimitLine(ll4);
        chart.invalidate();
        return rootView;
    }
    public void setSpinner(View rootView)
    {
        label = (TextView) rootView.findViewById(R.id.label); // najdi label
        //spinner pre výber
        spinner = (Spinner) rootView.findViewById(R.id.spinner); //najdi spinner
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
                    label.setText("Spotreba za posledných 24 hodín");
                }
                if(position == 1)
                {
                    label.setText("Spotreba za poslednú hodinu");
                }
                if(position == 2)
                {
                label.setText("Spotreba za posledný týždeň");
                }
            if(position == 3)
                {
                label.setText("Spotreba za posledný mesiac");
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
        outState.putInt("spinner", spinner.getSelectedItemPosition()); //uloz poziciu spinner
    }
}

