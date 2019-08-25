package com.example.david.favouritefood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class FoodActivity extends AppCompatActivity {

    public static final String RETURN_FOOD = "RETURN_KEY";

    Food food;
    int picture;
    EditText name;
    EditText url;
    EditText keywords;
    EditText date;
    ToggleButton share;
    EditText email;
    RatingBar rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        initializeUI();
    }

    private void initializeUI() {
        //unbundling the intent stored information
        Intent intent = getIntent();
        food = intent.getParcelableExtra(MainActivity.FOOD);
        getValue();
        postValues();
        Button save = findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Context context = getApplicationContext();
                String msg;
                if (name.getText().toString().isEmpty()) {
                    msg = "Error: food name field empty";
                    Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                    toast.show();
                }   else if (email.getText().toString().isEmpty()) {
                    msg = "Error: email field empty";
                    Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        msg = "Error: email pattern doesn't match";
                        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.i("FIELD", "email doesn't match pattern");
                    } else {
                        getChanges();
                        returnChanges();
                    }
                }
            }
        });
    }

    //gets the values from the parcelable and assigns them to values
    public void getValue() {
        name = findViewById(R.id.title_edit);
        url = findViewById(R.id.url_edit);
        keywords = findViewById(R.id.keywords_edit);
        date = findViewById(R.id.date_edit);
        share = findViewById(R.id.share_toggle);
        email = findViewById(R.id.email_edit);
        rating = findViewById(R.id.rating_edit);
    }

    //posts the values that were assigned to the variables
    public void postValues() {
        name.setText(food.getName());
        url.setText(food.getUrl());
        keywords.setText(food.getKeywords());
        date.setText(food.getDate());
        share.setChecked(food.getShare());
        email.setText(food.getEmail());
        rating.setRating(food.getRating());
    }

    //collects the changes that were made to the form
    public void getChanges() {
        food.setName(name.getText().toString());
        food.setUrl(url.getText().toString());
        food.setKeywords(keywords.getText().toString());
        food.setDate(date.getText().toString());
        food.setShare(share.isChecked());
        food.setEmail(email.getText().toString());
        food.setRating((rating.getRating()));
    }

    //returns the bundled changes back to the mainActivity
    public void returnChanges() {
        Intent intent = new Intent();
        intent.putExtra(RETURN_FOOD, food);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnChanges();
        super.onBackPressed();
    }
}
