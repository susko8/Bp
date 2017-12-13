package com.samuel.altzasuvkaapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.samuel.altzasuvkaapp.R;

import org.w3c.dom.Text;

public class MainFragment extends Fragment implements View.OnClickListener
{
    Button connectButton;
    TextView Id;
    TextView status;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.main_fragment,container,false);
        status = (TextView) view.findViewById(R.id.status);
        Id = (TextView) view.findViewById(R.id.device_id);
        connectButton = (Button) view.findViewById(R.id.connect_button);
        connectButton.setOnClickListener(this);
        status.setText("Status: Disconnected");
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v==connectButton)
        {
            /*todo logika statusu*/
            String text = "Status: <font color='#43A047'>OK</font>";
            status.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        }
    }
}
