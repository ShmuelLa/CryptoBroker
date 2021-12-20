package com.example.cryptotrader;

import android.widget.Spinner;
import android.widget.TextView;

public class ctUtils {

    public static String getSpinnerChosenText(Spinner spinner) {
        TextView textView = (TextView)spinner.getSelectedView();
        String result = textView.getText().toString();
        return result;
    }
}