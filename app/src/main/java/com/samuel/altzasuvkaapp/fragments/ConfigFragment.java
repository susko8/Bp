package com.samuel.altzasuvkaapp.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.samuel.altzasuvkaapp.Intervals;
import com.samuel.altzasuvkaapp.R;


public class ConfigFragment extends Fragment
{
    NumberPicker CheapFrom;
    NumberPicker CheapTo;
    NumberPicker ExpFrom;
    NumberPicker ExpTo;
    EditText price;
    Intervals intervaly;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        Bundle arguments = getArguments();
        super.onCreate(savedInstanceState);
        setRetainInstance(true);//uloz instanciu
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Nastavenia Aplikácie");
        intervaly = (Intervals) arguments.getSerializable("Intervaly");
        RetrieveSettings();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView=inflater.inflate(R.layout.config_fragment,container,false); //inflatni layout
        setPickers(rootView);
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        saveSettings(); super.onSaveInstanceState(outState);
    }
    public void setPickers(View rootView)
    {
        Context context = getActivity().getApplicationContext();
        final Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        CheapTo= (NumberPicker) rootView.findViewById(R.id.cheap_picker_to);
        CheapFrom=(NumberPicker) rootView.findViewById(R.id.cheap_picker_from);
        ExpTo=(NumberPicker) rootView.findViewById(R.id.exp_picker_to);
        ExpFrom=(NumberPicker) rootView.findViewById(R.id.exp_picker_from);
        price=(EditText) rootView.findViewById(R.id.prizeInput);
        price.setText(String.valueOf(intervaly.getPrice()));
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
        public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                if(newVal < intervaly.getCheapTo())
                {
                    intervaly.setCheapFrom(newVal);
                    saveSettings();
                    toast.setText("Hodnota nastavená");
                    toast.show();
                }
                else
                {
                    toast.setText("Nemôžem nastaviť");
                    toast.show();
                }
    }
    });
        CheapTo.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
    {
        if(newVal > intervaly.getCheapFrom()) {
            intervaly.setCheapTo(newVal);
            saveSettings();
            toast.setText("Hodnota nastavená");
            toast.show();
        }
        else
        {
           toast.setText("Nemôžem nastaviť");
           toast.show();

        }
    }
});
        ExpFrom.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                if(newVal < intervaly.getExpTo())
                {
                    intervaly.setExpFrom(newVal);
                    saveSettings();
                    toast.setText("Hodnota nastavená");
                    toast.show();
                }
                else
                    {
                        toast.setText("Nemôžem nastaviť");
                        toast.show();
                }
            }
        });
        ExpTo.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal > intervaly.getExpFrom())
                {
                    intervaly.setExpTo(newVal);
                    saveSettings();
                    toast.setText("Hodnota nastavená");
                    toast.show();
                }
                else
                {
                    toast.setText("Nemôžem nastaviť");
                    toast.show();
                }
            }
        });
        price.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty() || s.length()>0 || count !=0) {
                    intervaly.setPrice(Double.parseDouble(s.toString()));
                    toast.setText("Cena bola nastavená");
                    toast.show();
                    saveSettings();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Place the logic here for your output edittext
            }
        });

    }
    public void saveSettings()
    {
        @SuppressLint("WrongConstant") SharedPreferences sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(intervaly);
        editor.putString("Intervaly",json);
        editor.apply();
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


}
