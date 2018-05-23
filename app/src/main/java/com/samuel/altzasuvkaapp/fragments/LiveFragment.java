package com.samuel.altzasuvkaapp.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.samuel.altzasuvkaapp.Intervals;
import com.samuel.altzasuvkaapp.MainActivity;
import com.samuel.altzasuvkaapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LiveFragment extends Fragment implements OnChartValueSelectedListener {
    TextView label; //nazov grafu podla spinnera
    Spinner spinner; //spinner
    int spinposition; //poz. spinnera
    LineChart chart;
    private Thread thread;
    private boolean doRun =true;
    private String toDraw = "temp";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
      /*  if (savedInstanceState != null) //ak je nieco v bundli
            spinposition = savedInstanceState.getInt("spinner"); //ulozena pozicia spinner, sup ju tam
        View rootView = inflater.inflate(R.layout.line_fragment, container, false); //inflate layout
        LineChart chart = (LineChart) rootView.findViewById(R.id.chart_live);
        List<Entry> entries = new ArrayList<Entry>();

        chart.invalidate();*/

        View rootView = inflater.inflate(R.layout.live_fragment, container, false);
        if (savedInstanceState != null) //ak je nieco v bundli
            spinposition = savedInstanceState.getInt("spinner");


        //set up the chart
        chart = (LineChart) rootView.findViewById(R.id.chart_live);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);






        XAxis xl =chart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
//        leftAxis.setAxisMaximum(35f);
//        leftAxis.setAxisMinimum(15f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        Description desc =  new Description();
        desc.setText("Aktuálne hodnoty veličiny");
        //desc.setPosition(150,150);
        desc.setTextSize(18);
        chart.setDescription(desc);
        chart.setOnChartValueSelectedListener(this);




        chart.setData(new LineData());
        ListenToData();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Live Metering");
        setSpinner(rootView);

        return rootView;
    }

    public void setSpinner(View rootView) {
        spinner = (Spinner) rootView.findViewById(R.id.spinner_live); //najdi spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.spinnerlivearray, android.R.layout.simple_spinner_item); //daj mu array definovany v string.xml
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); //prirad adapter
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//listener a v nom logika
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position == 0) {
                    toDraw = "temp";
                    refreshChart();
                }
                if (position == 1) {
                    toDraw="hum";
                    refreshChart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                label.setText("Vyber jednu z možností"); //ak nic nevybrate
            }
        });
        spinner.setSelection(spinposition); //ak nieco v bundli tak (re)setni
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("spinner", spinner.getSelectedItemPosition()); //uloz poziciu spinnera
    }

    public void refreshChart()
    {
        chart.getLineData().clearValues();
    }

    private void ListenToData()
    {
        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry();
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run()
            {
                final boolean status= ((MainActivity)getActivity()).getisConnectedStatus();
                for (int i = 0; i < 100; i++)
                {
                    if(!doRun)
                        break;
                    if(!status)
                    {
                        doRun=false;
                        closeFragment();
                    }
                    getActivity().runOnUiThread(runnable);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    private void closeFragment()
    {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        Context context = ((MainActivity)getActivity());
        Toast toast = Toast.makeText(context, "Zariadenie odpojené, Live Metering ukončený", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void addEntry() {
        float val;
        LineData data = chart.getData();
        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            if(toDraw.equals("temp")) {
               val = (float)((MainActivity) getActivity()).getValue1();
            }
            else
            {
                val = (float)((MainActivity) getActivity()).getValue2();
            }
            Log.e("!!!",Float.valueOf(val).toString());
            Entry entry= new Entry(data.getEntryCount(),val);
            data.addEntry((entry),0);
            data.notifyDataChanged();

            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(12);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());


            chart.invalidate();
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "DataSet");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.BLUE);
        set.setLineWidth(5f);
        set.setCircleRadius(6f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }



    @Override
    public void onPause()
    {
        super.onPause();
        doRun=false;
//        if (thread != null) {
//            thread.interrupt();
//        }

    }
    @Override
    public void onStop()
    {
        super.onStop();
    }
    @Override
    public void onDestroy()
    {
        Log.e("!!!","DESTROY CALLED!!!");
        super.onDestroy();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast toast;
        Context context = this.getActivity().getApplicationContext();
            toast = Toast.makeText(context, Float.valueOf(e.getY()).toString(), Toast.LENGTH_SHORT);
            toast.show();
    }

    @Override
    public void onNothingSelected() {

    }
}


