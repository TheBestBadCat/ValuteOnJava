package com.stanislavkorneev.valute;

import java.io.Serializable;

public class Valute implements Serializable {

    private String ID;
    private String NumCode;
    private String CharCode;
    private int Nominal;
    private String Name;
    private float Value;
    private float Previous;

    public Valute(String ID, String numCode, String charCode, int nominal, String name, float value, float previous) {
        this.ID = ID;
        NumCode = numCode;
        CharCode = charCode;
        Nominal = nominal;
        Name = name;
        Value = value;
        Previous = previous;
    }

    @Override
    public String toString() {
        String changePrefix;
        float changeOfValue = Value - Previous;
        if (changeOfValue > 0) {
            changePrefix = "+";
        } else {
            changePrefix = "-";
        }
        return NumCode + " " + CharCode + " " + Nominal + " " + Name + "\nКурс: " + Value + " (" + changePrefix + String.format("%.2f", changeOfValue) + ")";
    }
}
