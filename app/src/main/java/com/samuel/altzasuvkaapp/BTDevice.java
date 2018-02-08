package com.samuel.altzasuvkaapp;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

public class BTDevice
{

    public BluetoothDevice bluetoothDevice;
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
