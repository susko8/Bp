package com.samuel.altzasuvkaapp;


import java.io.Serializable;

public class Intervals implements Serializable {
    private int CheapFrom;
    private int CheapTo;
    private int ExpFrom;
    private int ExpTo;

    Intervals()
    {
        CheapFrom=7;
        CheapTo=9;
        ExpFrom=21;
        ExpTo=23;
    }
    public int getCheapFrom() {
        return CheapFrom;
    }

    public int getCheapTo() {
        return CheapTo;
    }

    public int getExpFrom() {
        return ExpFrom;
    }

    public int getExpTo() {
        return ExpTo;
    }

    public void setCheapFrom(int cheapFrom) {
        CheapFrom = cheapFrom;
    }

    public void setCheapTo(int cheapTo) {
        CheapTo = cheapTo;
    }

    public void setExpFrom(int expFrom) {
        ExpFrom = expFrom;
    }

    public void setExpTo(int expTo) {
        ExpTo = expTo;
    }
}
