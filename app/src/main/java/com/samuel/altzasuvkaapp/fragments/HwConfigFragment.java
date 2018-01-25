package com.samuel.altzasuvkaapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.samuel.altzasuvkaapp.Intervals;
import com.samuel.altzasuvkaapp.R;

public class HwConfigFragment extends Fragment {

    TextView instructions;
    SeekBar seeker;
    TextView seekerState;
    int defaultProgress = 5;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //uloz stav fragmentu
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        if (savedInstanceState != null) //ak je nieco v bundli
        {
            defaultProgress = savedInstanceState.getInt("progress"); //ulozena pozicia spinner, sup ju tam
        }
        View rootView = inflater.inflate(R.layout.fragment_hw_config, container, false);
        instructions = (TextView) rootView.findViewById(R.id.text);
        instructions.setText("TIP: Pred odoslaním nastavení do zásuvky sa odporúča si ju najprv identifikovať. Po stlačení " +
                "tlačidla sa na zásuvke rozbliká sveteľná dióda.");
        seeker = (SeekBar) rootView.findViewById(R.id.seeker);
        seeker.incrementProgressBy(5);
        seeker.setMax(65);
        seeker.setProgress(defaultProgress);
        seekerState = (TextView) rootView.findViewById(R.id.seeker_label);
        seekerState.setText("Intenzita merania: " + defaultProgress + " min");
        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 5;
                progress = progress * 5;
                progress = progress + 5;
                defaultProgress = progress;
                seekerState.setText("Intenzita merania: " + progress + " min");
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

}
