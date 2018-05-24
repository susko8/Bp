package com.samuel.altzasuvkaapp;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Intervals implements Serializable {
    private int CheapFrom;
    private int CheapTo;
    private int ExpFrom;
    private int ExpTo;
    private double price;
    private List<String> rooms;



    Intervals()
    {
        CheapFrom=7;
        CheapTo=9;

        ExpFrom=21;
        ExpTo=23;
        price = 0.0502;
        rooms = new ArrayList<>();
        rooms.add("Obývačka");
        rooms.add("Kuchyňa");
        rooms.add("Spálňa");
        rooms.add("Kúpeľňa");
    }

    public List<String> getRooms() {
        return rooms;
    }

    public void setRooms(List<String> rooms) {
        this.rooms = rooms;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
