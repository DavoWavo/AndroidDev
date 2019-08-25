package com.example.david.breakingrules;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView status;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.time);
        button = findViewById(R.id.button);
        //pre asyncTask implementation, didn't update the view
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    for (int i = 3; i >= 0; i--) {
//                        Thread.sleep(1000);
//                        Log.d("debugMode", Integer.toString(i));
//                        status.setText(Integer.toString(i));
//                    }
//                } catch (InterruptedException ie) {
//                    ie.printStackTrace();
//                }
//            }
//        });

        //calling the asyncCount to create another thread to do the work
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncCount().execute("");
            }
        });
    }

    //the asyncCount thread, used to handle the delay, counting and updating
    private class AsyncCount extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            int count = params.length;
            for (int i = 3; i >= 0; i--) {
                try {
                    Thread.sleep(1000);
                    publishProgress(Integer.toString(i));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return Integer.toString(count);
        }

        @Override
        protected void onProgressUpdate(String... text) {
            status.setText(text[0]);
        }
    }
}
