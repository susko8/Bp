package com.samuel.altzasuvkaapp.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.samuel.altzasuvkaapp.Intervals;
import com.samuel.altzasuvkaapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class LineChartFragment extends Fragment implements OnChartValueSelectedListener
{
    TextView label; //nazov grafu podla spinnera
    Spinner spinner; //spinner
    int spinposition; //poz. spinnera
    Intervals intervaly;
    List<Entry> entries;
    LineChart chart;
    String drawingChart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //uloz stav fragmentu
        Bundle arguments = getArguments();
        intervaly = (Intervals) arguments.getSerializable("Intervaly");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Graf spotreby");
        RetrieveSettings();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null) //ak je nieco v bundli
            spinposition = savedInstanceState.getInt("spinner"); //ulozena pozicia spinner, sup ju tam
        View rootView = inflater.inflate(R.layout.line_fragment, container, false); //inflate layout
        setSpinner(rootView); //nastav spinner
        chart = (LineChart) rootView.findViewById(R.id.chart);
        entries=new ArrayList<>();
        drawingChart="day";
        addDataToChart(entries,drawingChart);
        styleChart(chart);
        chart.setHardwareAccelerationEnabled(true);
        chart.setOnChartValueSelectedListener(this);
        chart.animateX(500, Easing.EasingOption.EaseInOutSine);
        chart.invalidate();
        return rootView;
    }
    public void setSpinner(View rootView)
    {
        //label = (TextView) rootView.findViewById(R.id.label); // najdi label
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
                    drawingChart = "day";
                   addDataToChart(entries,drawingChart);
                }
                if(position == 1)
                {
                    drawingChart = "week";
                    addDataToChart(entries,drawingChart);
                }
                if(position == 2)
                {
                    drawingChart = "month";
                    addDataToChart(entries,drawingChart);
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
    public void styleChart(LineChart chart)
    {
        Description desc = new Description();
        desc.setText("");
        //desc.setPosition(150,150);
        desc.setTextSize(18);
        //desc.setTextColor(Co);
        chart.setNoDataText("Zatiaľ nenamerané hodnoty");
        chart.setBackgroundColor(Color.WHITE);
        chart.setBorderColor(Color.GREEN);
        chart.setGridBackgroundColor(Color.GREEN);
        chart.setDescription(desc);
        chart.setKeepPositionOnRotation(true);
    }
    public void addDataToChart(List<Entry> entries,String type)
    {
        Random randomGenerator = new Random();
        //tu cyklus naplniť, pozor treba naplnat asi v poradi
        entries.clear();
        if(type.equals("day"))
        {
            //demo dataset
            //nie v cykle random ciel bol reprezentovat data co najrealnejsie
            entries.add(new Entry(0, 80));
            entries.add(new Entry(1, 83.5f));
            entries.add(new Entry(2, 80));
            entries.add(new Entry(3, 81.5f));
            entries.add(new Entry(4, 95f));
            entries.add(new Entry(5, 90.1f));
            entries.add(new Entry(6, 150f));
            entries.add(new Entry(7, 400f));
            entries.add(new Entry(8, 300f));
            entries.add(new Entry(9, 250f));
            entries.add(new Entry(10, 150f));
            entries.add(new Entry(11, 150f));
            entries.add(new Entry(12, 130f));
            entries.add(new Entry(13, 120f));
            entries.add(new Entry(14, 200f));
            entries.add(new Entry(15, 250f));
            entries.add(new Entry(16, 400f));
            entries.add(new Entry(17, 350f));
            entries.add(new Entry(18, 450f));
            entries.add(new Entry(19, 500f));
            entries.add(new Entry(20, 550f));
            entries.add(new Entry(21, 400f));
            entries.add(new Entry(22, 200f));
            entries.add(new Entry(23, 125.5f));
            addLimitLines();
        }
        if(type.equals("week"))
        {
            //demo dataset
            //nie v cykle random ciel bol reprezentovat data co najrealnejsie
            entries.add(new Entry(1, 3.8f));
            entries.add(new Entry(2, 6.2f));
            entries.add(new Entry(3, 6.5f));
            entries.add(new Entry(4, 5.0f));
            entries.add(new Entry(5, 4.8f));
            entries.add(new Entry(6, 4.8f));
            entries.add(new Entry(7, 5.8f));
            removeLimitLines();
        }
        if(type.equals("month"))
        {
            //demo dataset
            //nie v cykle random ciel bol reprezentovat data co najrealnejsie
            entries.add(new Entry(1, 3.8f));
            entries.add(new Entry(2, 6.2f));
            entries.add(new Entry(3, 6.5f));
            entries.add(new Entry(4, 5.0f));
            entries.add(new Entry(5, 4.8f));
            entries.add(new Entry(6, 4.9f));
            entries.add(new Entry(7, 5.5f));
            entries.add(new Entry(8, 4.3f));
            entries.add(new Entry(9, 3.5f));
            entries.add(new Entry(10, 6.2f));
            entries.add(new Entry(11, 5.1f));
            entries.add(new Entry(12, 5.0f));
            entries.add(new Entry(13, 4.8f));
            entries.add(new Entry(14, 4.13f));
            entries.add(new Entry(15, 5.0f));
            entries.add(new Entry(16, 4.0f));
            entries.add(new Entry(17, 3.8f));
            entries.add(new Entry(18, 6.2f));
            entries.add(new Entry(19, 4.9f));
            entries.add(new Entry(20, 2.21f));
            entries.add(new Entry(21, 4.43f));
            entries.add(new Entry(22, 4.6f));
            entries.add(new Entry(23, 5.4f));
            entries.add(new Entry(24, 4.8f));
            entries.add(new Entry(25, 4.8f));
            entries.add(new Entry(26, 5.0f));
            entries.add(new Entry(27, 4.0f));
            entries.add(new Entry(28, 4.2f));
            entries.add(new Entry(29, 6.2f));
            entries.add(new Entry(30, 5.75f));
            removeLimitLines();
        }
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
        chart.invalidate();
    }
    public void removeLimitLines()
    {
        XAxis xAxis = chart.getXAxis();
        xAxis.removeAllLimitLines();
    }
    public void addLimitLines()
    {
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);
        YAxis yAxisLeft = chart.getAxisLeft();
        YAxis yAxisRight = chart.getAxisRight();
        yAxisLeft.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);
        //Limity pre vyznačenie cien prúdov
        //lacny dolny
        LimitLine ll1 = new LimitLine(intervaly.getCheapFrom(), "Lacný prúd");
        ll1.setLineColor(Color.BLUE);
        ll1.setLineWidth(3f);
        ll1.setTextColor(Color.BLACK);
        ll1.setTextSize(12f);
        ll1.setTextColor(Color.BLUE);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        xAxis.addLimitLine(ll1);
        //lacny horny
        LimitLine ll2 = new LimitLine(intervaly.getCheapTo(), "");
        ll2.setLineColor(Color.BLUE);
        ll2.setLineWidth(3f);
        ll2.setTextColor(Color.BLACK);
        ll2.setTextSize(5f);
        xAxis.addLimitLine(ll2);
        //drahy dolny
        LimitLine ll3 = new LimitLine(intervaly.getExpFrom(), "Drahý prúd");
        ll3.setLineColor(Color.RED);
        ll3.setLineWidth(3f);
        ll3.setTextColor(Color.BLACK);
        ll3.setTextSize(5f);
        xAxis.addLimitLine(ll3);
        ll3.setTextSize(12f);
        ll3.setTextColor(Color.RED);
        ll3.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        //drahy horny
        LimitLine ll4 = new LimitLine(intervaly.getExpTo(), "");
        ll4.setLineColor(Color.RED);
        ll4.setLineWidth(3f);
        ll4.setTextColor(Color.BLACK);
        ll4.setTextSize(5f);
        xAxis.addLimitLine(ll4);
    }
    public void RetrieveSettings()
    {
        @SuppressLint("WrongConstant") SharedPreferences sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_APPEND);
        if(sharedPref.contains("Intervaly"))
        {
            Gson gson = new Gson();
            String json = sharedPref.getString("Intervaly", "");
            intervaly = gson.fromJson(json, Intervals.class);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h)
    {
        Toast toast;
        double cena = intervaly.getPrice();
        Context context = this.getActivity().getApplicationContext();
        if(drawingChart.equals("day")) {
            toast = Toast.makeText(context, "Spotreba o " + (int) e.getX() + "h : " + e.getY() + " Wh, Jednotková cena: "
                    + String.format("%.2f", e.getY() * (cena/1000)) + " €", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime ( date ); // convert your date to Calendar object
            int daysToDecrement = - (int) e.getX();
            cal.add(Calendar.DATE, daysToDecrement);
            date = cal.getTime(); // again get back your date object
            String dat = df.format(date);
            toast = Toast.makeText(context, "Spotreba "+ dat +" " + e.getY()
                    + " KWh, Jednotková cena: " + String.format("%.2f", e.getY() * cena) + " €", Toast.LENGTH_SHORT);
            toast.show();

        }

    }

    @Override
    public void onNothingSelected() {

    }
}

