package com.example.cryptotrader;

import android.widget.Spinner;
import android.widget.TextView;

/**
 * CryptoTrader Utils class will be used generally for code conventions, preproccessing and
 * data generation with general project methods.
 * Methods here are used across all classes and are no subject unique
 */
public class ctUtils {

    /**
     * Returns the current item choosed in a spinner as String
     * @param spinner Spinner object
     * @return String result of the current chosen object on spinner
     */
    public static String getSpinnerChosenText(Spinner spinner) {
        TextView textView = (TextView)spinner.getSelectedView();
        String result = textView.getText().toString();
        return result;
    }
}