package com.example.david.favouritefood;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Picture;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String FOOD = "FOOD_KEY";
    public static final int KEY = 0;

    Food burger;
    Food steak;
    Food pasta;
    Food pizza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
    }

    //handling the clicking on an image and initialization of food date
    public void initializeUI() {
        CreateFoodData();
        SetFoodData();

        //setting the picture buttons
        ImageView burgerClick = findViewById(R.id.burger);
        ImageView steakClick = findViewById(R.id.steak);
        ImageView pastaClick = findViewById(R.id.pasta);
        ImageView pizzaClick = findViewById(R.id.pizza);
        //setting the onclick listeners on the pictures and running the procedure that launches the next activity
        burgerClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDetail(burger);
            }
        });
        steakClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDetail(steak);
            }
        });
        pastaClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDetail(pasta);
            }
        });
        pizzaClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showDetail(pizza);
            }
        });
    }

    //bundling the information and storing information in intent and starting another activity
    public void showDetail(Food food) {
        final Intent intent = new Intent(this, FoodActivity.class);
        intent.putExtra(FOOD, food);

        startActivityForResult(intent, KEY);
    }

    //creating the food data for each of the food objects
    public void CreateFoodData() {
        //creating the picture data
        burger = new Food("burger", "burger@mail.com", 1);
        steak = new Food("steak", "steak@mail.com", 2);
        pasta = new Food("pasta", "pasta@mail.com", 3);
        pizza = new Food("pizza", "pizza@mail.com", 4);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
        String date = dateFormat.format(new Date());

        burger.setDate(date);
        steak.setDate(date);
        pasta.setDate(date);
        pizza.setDate(date);
    }

    //setting up the information for the main activity
    public void SetFoodData() {
        TextView burgerTitle = findViewById(R.id.title_burger);
        TextView steakTitle = findViewById(R.id.title_steak);
        TextView pastaTitle = findViewById(R.id.title_pasta);
        TextView pizzaTitle = findViewById(R.id.title_pizza);

        TextView burgerDate = findViewById(R.id.date_burger);
        TextView steakDate = findViewById(R.id.date_steak);
        TextView pastaDate = findViewById(R.id.date_pasta);
        TextView pizzaDate = findViewById(R.id.date_pizza);

        if (burger != null) {
            burgerTitle.setText(burger.getName());
        }
        if (steak != null) {
            steakTitle.setText(steak.getName());
        }
        if (pasta != null) {
            pastaTitle.setText(pasta.getName());
        }
        if (pizza != null) {
            pizzaTitle.setText(pizza.getName());
        }

        if (!burger.getDate().isEmpty()) {
            burgerDate.setText(burger.getDate());
        }
        if (!steak.getDate().isEmpty()) {
            steakDate.setText(steak.getDate());
        }
        if (!pasta.getDate().isEmpty()) {
            pastaDate.setText(pasta.getDate());
        }
        if (!pizza.getDate().isEmpty()) {
            pizzaDate.setText(pizza.getDate());
        }
    }

    //this unbundles the changes that were made in the food activity and then writes them to the food objects
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == KEY) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Log.i("INTENT", "Intent empty");
                } else {
                    FoodIntent(intent); //getting what food item was changed
                    SetFoodData(); //setting that food data to the changed data
                }
            } else {
                Log.i("INTENT", "Result not okay");
            }
        } else {
            Log.i("INTENT", "Code does not match");
        }
    }

    //gets the returned food intent changes
    public void FoodIntent(Intent intent) {
        final Food food = intent.getParcelableExtra(FoodActivity.RETURN_FOOD);
        if (food != null) {
            switch(food.getID()) {
                case 1:
                    burger = food;
                    break;
                case 2:
                    steak = food;
                    break;
                case 3:
                    pasta = food;
                    break;
                case 4:
                    pizza = food;
                    break;
                default:
                    break;
            }
        } else {
            Log.e("Food", "NULL");
        }
    }
}
