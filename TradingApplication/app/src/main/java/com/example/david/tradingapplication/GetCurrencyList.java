package com.example.david.tradingapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//this is used to grab the list of currencies from the currency converter api exclusively
public class GetCurrencyList extends AsyncTask <String,Void,List<Map<String, String>>> {             // Parameters - Progress unit - Result
    @Override
    protected List<Map<String, String>> doInBackground(String... str_url) {
        //TODO 5: add a delay to ensure that the full api pull is completed
        //TODO 6: add a progress dial to show that the information is being pulled
        HttpHandler httpHandler = new HttpHandler();

        String jsonStr = httpHandler.makeServiceCall(str_url[0]);
        int indexKey = 0;

        HashMap<String, String> currency;
        List<Map<String, String>> currencyList = new ArrayList<Map<String, String>>();

        Log.i("JSON_result", "pulled json_data " + jsonStr);
        if (jsonStr != null) {
            try {
                //creating new json object, this will be pulled apart for its values
                JSONObject jsonObject = new JSONObject(jsonStr);

                // Getting JSON object node
                JSONObject currency_finance = jsonObject.getJSONObject("results");

                //iterating through all of the objects within the json file
                Iterator<String> iter = currency_finance.keys();
                while(iter.hasNext()) {
                    //getting the id key, which contains all the objects
                    String key = (String) iter.next();
                    JSONObject jsonCryptoObject = currency_finance.getJSONObject(key);
                    //pulling apart each object for the given key
                    String name = jsonCryptoObject.getString("currencyName");
                    String symbol = jsonCryptoObject.getString("id");
                    Log.d("testingIterator", "name: " + name + ", symbol: " + symbol);
                    //creating a new hashmap to put the contents of the pulled apart json
                    currency = new HashMap<>();
                    currency.put("id", "null");
                    currency.put("name", name);
                    currency.put("symbol", symbol);
                    currency.put("last_updated", "0");
                    currency.put("price", "0.0");
                    //adding the hashmap to the list of hashmaps with the index
                    currencyList.add(indexKey, currency);
                    //increment the number of items and index within the hashmap
                    indexKey++;
                }
                //send back the hashmap
                return currencyList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return currencyList;
    }

    @Override
    protected void onPostExecute(List<Map<String, String>> currencyList){
        super.onPostExecute(currencyList);
    }
}