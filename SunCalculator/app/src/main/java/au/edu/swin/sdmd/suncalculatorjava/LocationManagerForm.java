package au.edu.swin.sdmd.suncalculatorjava;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class LocationManagerForm extends AppCompatActivity {

    public static final String RETURN_LOCATION = "RETURN_KEY";

    Location location;
    EditText locationNameET;
    EditText longitudeET;
    EditText latitudeET;
    EditText timezoneET;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_manager_form);

        initializeUI();
    }

    private void initializeUI() {
        Intent intent = getIntent();
        location = intent.getParcelableExtra(LocationManager.LOCATION);

        Log.d("debugMode RecieveIntent", location.getName() + ","
                + Double.toString(location.getLatitude()) + ","
                + Double.toString(location.getLongitude()) + ","
                + location.getTimezone() + ","
                + location.getCustom());

        getViewValues();
        postValues();
        //perform the button click action here
        formValidationProcessing();
    }

    //posting back to location manager to save to memory, performing form validation to ensure valid information
    private void formValidationProcessing() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("debugMode", "Form LocationManager button clicked");
                String msg = "";
                if(!locationNameET.getText().toString().equals("")) {
                    if (locationNameET.getText().toString().matches("^[\\p{L} ]+$")) {
                        if (longitudeET.getText().toString().matches("^[\\-]{0,1}[0-9]+[\\.][0-9]+$")) {
                            if (latitudeET.getText().toString().matches("^[\\-]{0,1}[0-9]+[\\.][0-9]+$")) {
                                if (timezoneET.getText().toString().equals("")) {
                                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
                                    Date currentLocalTime = calendar.getTime();
                                    DateFormat dateFormat = new SimpleDateFormat("z", Locale.getDefault());
                                    Log.d("debugMode", dateFormat.format(currentLocalTime));

                                    getChanges(dateFormat.format(currentLocalTime));
                                    returnChanges();
                                } else {
                                    String findTZ = findTimeZone(timezoneET.getText().toString());
                                    if (findTZ.equals("no result")) {
                                        msg = "Error: Invalid time zone, leave empty for GMT";
                                    } else {
                                        getChanges(findTZ);
                                        returnChanges();
                                    }
                                }
                            } else {
                                msg = "Error: Latitude must be a number";
                            }
                        } else {
                            msg = "Error: Longitude must be a number";
                        }
                    } else {
                        msg = "Error: Location name can only contain letters";
                    }
                } else {
                    msg = "Error: location name field is empty";
                }
                if (msg != "") {
                    ToastMaker(msg);
                }
            }
        });
    }

    private void ToastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String findTimeZone(String timeZoneQuery) {
        String[] timeZoneAvailableIDs = TimeZone.getAvailableIDs();
        for (int i = 0; i < timeZoneAvailableIDs.length; i++) {
            if (timeZoneAvailableIDs[i].matches(timeZoneQuery))
            {
                return timeZoneAvailableIDs[i];
            }
        }
        return "no result";
    }

    //get the values from the parcelable
    private void getViewValues() {
        locationNameET = findViewById(R.id.location_form_title_et);
        longitudeET = findViewById(R.id.location_form_longitude_et);
        latitudeET = findViewById(R.id.location_form_latitude_et);
        //disable this field permanently and only make visible when not new
        timezoneET = findViewById(R.id.location_form_timezone_et);
        saveButton = findViewById(R.id.save_location_form);

        //checking if the location is a new location and disabling fields if not
        if (!location.getName().equals("")) {
            disableFields();
        }
    }

    //disabling the editText and save button field to ensure user doesn't ruin provided data
    private void disableFields() {
        locationNameET.setEnabled(false);
        longitudeET.setEnabled(false);
        latitudeET.setEnabled(false);
        timezoneET.setEnabled(false);
        saveButton.setEnabled(false);
        saveButton.setVisibility(View.INVISIBLE);
    }

    //posts the values from the passed location to the view
    private void postValues() {
        locationNameET.setText(location.getName());
        longitudeET.setText(Double.toString(location.getLongitude()));
        latitudeET.setText(Double.toString(location.getLatitude()));
        timezoneET.setText(location.getTimezone());
    }

    private void getChanges(String timeZone) {
        Log.d("debugMode", "Form LocationManager getChanges launched");
        location.setName(locationNameET.getText().toString());
        location.setLatitude(Double.parseDouble(latitudeET.getText().toString()));
        location.setLongitude(Double.parseDouble(longitudeET.getText().toString()));
        location.setTimezone(timeZone);
    }

    public void returnChanges() {
        Log.d("debugMode", "Form LocationManager returnChanges launched");
        final Intent intent = new Intent();
        intent.putExtra(RETURN_LOCATION, location);
        setResult(Activity.RESULT_OK, intent);
        Log.d("debugMode", location.returnFull());
        finish();
    }
}
