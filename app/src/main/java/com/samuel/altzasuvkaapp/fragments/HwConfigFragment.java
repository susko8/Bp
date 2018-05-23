package com.samuel.altzasuvkaapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.samuel.altzasuvkaapp.Intervals;
import com.samuel.altzasuvkaapp.MainActivity;
import com.samuel.altzasuvkaapp.R;

public class HwConfigFragment extends Fragment implements View.OnClickListener {

    TextView instructions;
    SeekBar seeker;
    TextView seekerState;
    int defaultProgress = 5;
    Button blink;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //uloz stav fragmentu
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Nastavenia Zásuvky");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null) //ak je nieco v bundli
        {
            defaultProgress = savedInstanceState.getInt("progress"); //ulozena pozicia spinner, sup ju tam
        }
        View rootView = inflater.inflate(R.layout.fragment_hw_config, container, false);
        blink = (Button) rootView.findViewById(R.id.btn_blink);
        blink.setOnClickListener(this);
        instructions = (TextView) rootView.findViewById(R.id.text);
        instructions.setText("TIP: Pred odoslaním nastavení do zásuvky sa odporúča si ju najprv identifikovať. Po stlačení " +
                "tlačidla sa na zásuvke rozbliká sveteľná dióda.");
        seeker = (SeekBar) rootView.findViewById(R.id.seeker);
        seeker.incrementProgressBy(5);
        seeker.setMax(65);
        seeker.setProgress(defaultProgress);
        seekerState = (TextView) rootView.findViewById(R.id.seeker_label);
        seekerState.setText("Interval merania: " + defaultProgress + " min");
        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 5;
                progress = progress * 5;
                progress = progress + 5;
                defaultProgress = progress;
                seekerState.setText("Interval merania: " + progress + " min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekerState = (TextView) rootView.findViewById(R.id.seeker_label);
        return rootView;
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("progress",defaultProgress ); //uloz funguje pri otacani
    }

    @Override
    public void onClick(View v) {
        Activity activity = getActivity();
        MainActivity myactivity = (MainActivity) activity;
        if(v==blink)
        {
            Context context = getActivity().getApplicationContext();
            ((MainActivity) activity).blinker();
            Toast toast = Toast.makeText(context, "Blikám", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
