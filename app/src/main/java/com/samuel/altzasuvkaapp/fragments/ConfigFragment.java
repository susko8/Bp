package com.samuel.altzasuvkaapp.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.samuel.altzasuvkaapp.Intervals;
import com.samuel.altzasuvkaapp.R;

import java.util.List;


public class ConfigFragment extends Fragment implements View.OnClickListener
{
    Button addRoomButton;
    Button removeRoomButton;
    List<String> rooms;
    TextView roomInput;
    RoomListAdapter adapter = new RoomListAdapter();
    ListView listRoomView;
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
        rooms = intervaly.getRooms();
        RetrieveSettings();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View rootView=inflater.inflate(R.layout.config_fragment,container,false); //inflatni layout
        addRoomButton = (Button) rootView.findViewById(R.id.add_room);
        addRoomButton.setOnClickListener(this);
        removeRoomButton = (Button) rootView.findViewById(R.id.remove_room);
        removeRoomButton.setOnClickListener(this);
        roomInput = (TextView) rootView.findViewById(R.id.roomInput);
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

    @Override
    public void onClick(View v)
    {
        if(v==addRoomButton)
        {
            if(!roomInput.getText().toString().matches("")) {
                intervaly.getRooms().add(roomInput.getText().toString());
                saveSettings();
                RetrieveSettings();
                Context context = getActivity().getApplicationContext();
                Toast toast = Toast.makeText(context, "Pridaná "+ roomInput.getText().toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
            else
            {
                Context context = getActivity().getApplicationContext();
                Toast toast = Toast.makeText(context, "Musíte zadať meno miestnosti", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        if(v==removeRoomButton)
        {
            rooms = intervaly.getRooms();
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_rooms, null);
            listRoomView = (ListView) mView.findViewById(R.id.room_list);
            listRoomView.setAdapter(adapter);
            mBuilder.setCancelable(true);
            mBuilder.setView(mView);
            mBuilder.setNegativeButton("Zavrieť", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            final AlertDialog dialog = mBuilder.create();
            listRoomView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Context context = getActivity().getApplicationContext();
                    Toast toast = Toast.makeText(context, "Miestnosť " + rooms.get(position) + " bola vymazaná", Toast.LENGTH_SHORT);
                    intervaly.getRooms().remove(position);
                    saveSettings();
                    RetrieveSettings();
                    toast.show();
                    dialog.cancel();

                }
            });
           dialog.show();
        }
    }

    public class RoomListAdapter extends BaseAdapter
    {
        public RoomListAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return intervaly.getRooms().size();
        }

        @Override
        public Object getItem(int position) {
            return rooms.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_room, null);
            TextView roomName = (TextView) convertView.findViewById(R.id.room_name);
            roomName.setText(rooms.get(position));
            return convertView;

        }
    }

}
