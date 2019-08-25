package com.example.david.tradingapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

//test url
//"https://query1.finance.yahoo.com/v8/finance/chart/BTC-USD?interval=1m&fbclid=IwAR2CsRao9BCIixPc2OYW-J7DsGbMhFHma7J3lAxRHvevCXHmcHmtBJjFrYI"

/**  Historical info call
    +currency/crypto/stock/commodity symbol

    calls the yahoo finance restricted api and grabs the historical information
    to the queried symbol and returns the list of prices
 */
public class GetHistoricalInfo extends AsyncTask <String, Void, LinkedHashMap<Integer, Double>> {

    /**this is using a key that i borrowed, please do not call this async in a loop otherwise it might get banned and then we'd be in the shit
     * https://query1.finance.yahoo.com/v8/finance/chart/ -- this portion is the URL
     * BTC-USD?interval=1m -- this portion is what tells the api what we want, its the symbol-currencyReturned then the interval.
     * accepted intervales are 1d, 5d, 1m, 3m, 6m, 1y, 2y, 5, ytd, max -- id advise against using anything greater than a year otherwise the call is slow
     * &fbclid=IwAR2CsRao9BCIixPc2OYW-J7DsGbMhFHma7J3lAxRHvevCXHmcHmtBJjFrYI -- this is the key, don't touch this.
     */
    private static String URL_start = "https://query1.finance.yahoo.com/v8/finance/chart/";
//    private static String URL_end = "-USD?interval=1m&fbclid=IwAR2CsRao9BCIixPc2OYW-J7DsGbMhFHma7J3lAxRHvevCXHmcHmtBJjFrYI"; // currently grabbing each value for the minutes passed in a day.... very slow
    private static String URL_end = "&fbclid=IwAR2CsRao9BCIixPc2OYW-J7DsGbMhFHma7J3lAxRHvevCXHmcHmtBJjFrYI";
    private String URL;

    public GetHistoricalInfo(String symbol, String range) {
        //taking in the currencyObjects symbol to creating the URL to grab historical info
        this.URL = URL_start + symbol + range + URL_end;
    }

    @Override
    protected LinkedHashMap<Integer, Double> doInBackground(String... str_url) {
        HttpHandler httpHandler = new HttpHandler();

        String jsonStr = httpHandler.makeServiceCall(URL);

        LinkedHashMap<Integer, Double> historicalInfo = new LinkedHashMap<>();

        //testing log
        //Log.i("JSON_result", "pulled historical data " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);

                //get the json object stuff here
                JSONObject historical_chart = jsonObject.getJSONObject("chart");
                JSONArray historical_result = historical_chart.getJSONArray("result");
                JSONObject historical_array = historical_result.getJSONObject(0);

                //getting the timestamp for each day
                JSONArray historical_timestamp = historical_array.getJSONArray("timestamp");

                JSONObject historical_indicators = historical_array.getJSONObject("indicators");
                JSONArray historical_quote = historical_indicators.getJSONArray("quote");

                // getting the historical low/high/close/volume/open for the given financial info
                JSONObject historical_data = historical_quote.getJSONObject(0);
//                JSONArray historical_low = historical_data.getJSONArray("low");
//                JSONArray historical_high = historical_data.getJSONArray("high");
                JSONArray historical_close = historical_data.getJSONArray("close");
//                JSONArray historical_volume = historical_data.getJSONArray("volume");
//                JSONArray historical_open = historical_data.getJSONArray("open");

                //putting the information into the hashmap to be returned
                for (int i = 0; i < historical_timestamp.length(); i++) {
                    //testing log
//                    Log.i("JSON_result_time", "time: " + historical_timestamp.get(i).toString() + ", closing: " + historical_close.get(i).toString());
                    if (!historical_close.get(i).equals(null)) {
                        historicalInfo.put(historical_timestamp.getInt(i), historical_close.getDouble(i));
                    } else {
//                        Log.d("debugMode", "null found: " + historical_timestamp.getInt(i) + ", " + historical_close.getDouble(i));
                        historicalInfo.put(historical_timestamp.getInt(i), 0.0);
                    }
                }
                return historicalInfo;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return historicalInfo;
    }

    @Override
    protected void onPostExecute(LinkedHashMap<Integer, Double> historicalInfo) {
        super.onPostExecute(historicalInfo);
    }
}
