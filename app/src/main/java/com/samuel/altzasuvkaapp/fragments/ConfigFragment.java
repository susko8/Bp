package com.samuel.altzasuvkaapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.samuel.altzasuvkaapp.Intervals;
import com.samuel.altzasuvkaapp.R;

import java.nio.BufferUnderflowException;


public class ConfigFragment extends Fragment
{
    TextView instructions;
    SeekBar seeker;
    TextView seekerState;
    int defaultProgress=5;
    NumberPicker CheapFrom;
    NumberPicker CheapTo;
    NumberPicker ExpFrom;
    NumberPicker ExpTo;
    Intervals intervaly;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        Bundle arguments = getArguments();
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//uloz instanciu
        intervaly = (Intervals) arguments.getSerializable("Intervaly");
        RetrieveSettings();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        //Log.e("interval", String.valueOf(intervaly.getCheapFrom()));
        if (savedInstanceState != null) //ak je nieco v bundli
        {
            defaultProgress = savedInstanceState.getInt("progress"); //ulozena pozicia spinner, sup ju tam
        }
        View rootView=inflater.inflate(R.layout.config_fragment,container,false); //inflatni layout
        instructions=(TextView) rootView.findViewById(R.id.text);
        instructions.setText("TIP: Pred odoslaním nastavení do zásuvky sa odporúča si ju najprv identifikovať. Po stlačení " +
                "tlačidla sa na zásuvke rozbliká sveteľná dióda.");
        seeker = (SeekBar) rootView.findViewById(R.id.seeker);
        seeker.incrementProgressBy(5);
        seeker.setMax(65);
        seeker.setProgress(defaultProgress);
        seekerState=(TextView) rootView.findViewById(R.id.seeker_label);
        seekerState.setText("Intenzita merania: "+ defaultProgress+" min");
        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                progress = progress / 5;
                progress = progress * 5;
                progress=progress+5;
                defaultProgress=progress;
            seekerState.setText("Intenzita merania: "+ progress+" min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekerState=(TextView) rootView.findViewById(R.id.seeker_label);
        setPickers(rootView);
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("progress",defaultProgress ); //uloz funguje pri otacani
    }
    public void setPickers(View rootView)
    {
        CheapTo= (NumberPicker) rootView.findViewById(R.id.cheap_picker_to);
        CheapFrom=(NumberPicker) rootView.findViewById(R.id.cheap_picker_from);
        ExpTo=(NumberPicker) rootView.findViewById(R.id.exp_picker_to);
        ExpFrom=(NumberPicker) rootView.findViewById(R.id.exp_picker_from);
        CheapTo.setMaxValue(24);
        CheapFrom.setMaxValue(24);
        ExpTo.setMaxValue(24);
        ExpFrom.setMaxValue(24);
        CheapTo.setMinValue(0);
        CheapFrom.setMinValue(0);
        ExpTo.setMinValue(0);
        ExpFrom.setMinValue(0);
        CheapFrom.setWrapSelectorWheel(false);
        CheapTo.setWrapSelectorWheel(false);
        ExpFrom.setWrapSelectorWheel(false);
        ExpTo.setWrapSelectorWheel(false);
        CheapFrom.setValue(intervaly.getCheapFrom());
        CheapTo.setValue(intervaly.getCheapTo());
        ExpFrom.setValue(intervaly.getExpFrom());
        ExpTo.setValue(intervaly.getExpTo());
        CheapFrom.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        intervaly.setCheapFrom(newVal);
                saveSettings();
    }
    });
        CheapTo.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        intervaly.setCheapTo(newVal);
        saveSettings();
    }
});
        ExpFrom.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                intervaly.setExpFrom(newVal);
                saveSettings();
            }
        });
        ExpTo.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                intervaly.setExpTo(newVal);
                saveSettings();
            }
        });

    }
    public void saveSettings()
    {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(intervaly);
        editor.putString("Intervaly",json);
        editor.apply();
    }
    public void RetrieveSettings()
    {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_APPEND);
        if(sharedPref.contains("Intervaly"))
        {
            Gson gson = new Gson();
            String json = sharedPref.getString("Intervaly", "");
            intervaly = gson.fromJson(json, Intervals.class);
        }
    }

}
