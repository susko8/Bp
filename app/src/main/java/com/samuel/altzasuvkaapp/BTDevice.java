package com.samuel.altzasuvkaapp;


import android.bluetooth.BluetoothDevice;

public class BTDevice
{

    private BluetoothDevice bluetoothDevice;
    private int rssi;
    public BTDevice(BluetoothDevice btdevice)
    {
        this.bluetoothDevice=btdevice;
    }
    public String getAddress(){
        return bluetoothDevice.getAddress();
    }
    public String getName()
    {
        return bluetoothDevice.getName();
    }
    public void setRssi(int rssi)
    {
        this.rssi=rssi;
    }
    public int getRssi(int rssi)
    {
        return rssi;
    }
}
