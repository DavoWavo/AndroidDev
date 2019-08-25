package com.example.david.tradingapplication;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ReadWriteCurrencies {
    private Context context;
    public ReadWriteCurrencies(Context context) {
        this.context = context;
    }

    public ArrayList<String> ReadFile() {
        Log.i("ReadWrite read", "Reading from file...");
        ArrayList<String> currencies = new ArrayList<String>();

        try {
            FileInputStream inputStream = context.openFileInput("Currencies.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String currency;
            //reading all the results and the putting them into the array
            while((currency = bufferedReader.readLine()) != null) {
//                Log.i("ReadWrite read", "R: " + currency);
                currencies.add(currency);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currencies;
    }

    public void WriteListToFile(List<CurrencyObject> currencyList) {
        Log.i("ReadWrite write", "Writing to file...");
        for (int i = 0 ; i < currencyList.size(); i++) {
            String line = "\n" + currencyList.get(i).ReturnFull();
//            Log.i("ReadWrite read", "W: " + currencyList.get(i).ReturnFull());
            byte[] bytes = line.getBytes();

            FileOutputStream out;

            try {
                out = context.openFileOutput("Currencies.txt", Context.MODE_APPEND);
                out.write(bytes);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ClearFile() {
        Log.i("ReadWrite clear", "Clearing file...");
        FileOutputStream openFileInput = null;
        try {
            openFileInput = context.openFileOutput("Currencies.txt", context.MODE_PRIVATE);
            PrintWriter printWriter = new PrintWriter(openFileInput);
            printWriter.print("");
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
