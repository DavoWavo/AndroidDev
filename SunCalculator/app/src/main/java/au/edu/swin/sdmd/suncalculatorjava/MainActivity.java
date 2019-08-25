package au.edu.swin.sdmd.suncalculatorjava;

import android.content.Intent;
import android.content.res.AssetManager;
import android.location.LocationListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    CustomPagerAdapter pagerAdapter;
    Location location;
    List<Location> locationList = new ArrayList<Location>();

    FragmentPagerAdapter adapterViewPager;

    LocationSunChangeListener activitySunCommander;
    LocationDateChangeListener activityDateCommander;

    public interface LocationSunChangeListener {
        public void changeSunLocation(Location location);
    }
    public interface LocationDateChangeListener {
        public void changeDateLocation(Location location);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("debugMode", "MainActivity onCreate launched");
        //need to set the location before creating the fragment
        createLocations(LocationInputStream());


        //creating the viewPager adapter and the fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapterViewPager = new CustomPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debugMode", "MainActivity onRestart launched");
        locationList.clear();
        createLocations(LocationInputStream());
    }

    public void passSunVal(LocationSunChangeListener activityCommander) {
        this.activitySunCommander = activityCommander;
    }

    public void passDateVal(LocationDateChangeListener activityCommander) {
        this.activityDateCommander = activityCommander;
    }

    //creates the action bar menu that contains the different locations
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //changing the location and checking for each press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_button:
                shareLocation();
                Toast.makeText(this, "Shared!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.select_location:
                //select location dialog fragment call here
                //pass in location list here
                showLocationSelector();
                break;
            case R.id.location_manager:
                final Intent launchLocationManager = new Intent(this, LocationManager.class);
                startActivity(launchLocationManager);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //setting the location to the option that was selected
    public void setLocation(Location location) {
        Log.v("MainActivity", "setLocation");
        this.location = location;
        Toast.makeText(this, this.location.getName() + " selected", Toast.LENGTH_SHORT).show();

        activitySunCommander.changeSunLocation(location);
        activityDateCommander.changeDateLocation(location);
    }

    public Location getLocation() {
        return location;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    //implement this into its own method so it can be used easy in other files later
    //reading the input stream and creating array of locations
    public ArrayList<String> LocationInputStream() {
        ArrayList<String> locations = new ArrayList<String>();
        AssetManager assetManager = getAssets();
        //if the file doesn't exist, make one with the contents of au_locations.txt from assets
        File file = new File(getFilesDir(), "Locations.txt");
        if (!file.exists()) {
            Log.i("locationFile", "Location.txt doesn't exist, creating file");
            try {
                //reading the resource file
                InputStream inputStream = getAssets().open("au_locations.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String location;

                //writing from resource file location to internal file location
                while ((location = bufferedReader.readLine()) != null) {
                    String line = "\n" + location;

                    byte[] bytes = line.getBytes();

                    FileOutputStream out = null;
                    try {
                        out = openFileOutput("Locations.txt", MODE_APPEND);
                        out.write(bytes);
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    locations.add(location.toString());
                }
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return locations;
        } else {
            //if the file does exist, don't make a new one. Read from existing
            try {
                Log.i("locationFile", "Location.txt already exists, reading file");
                InputStream inputStream = openFileInput("Locations.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String location;

                while ((location = bufferedReader.readLine()) != null) {
                    locations.add(location.toString());
                }
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } return locations;
        }
    }

    public void showLocationSelector() {
        LocationPickerFragment locationPickerFragment = new LocationPickerFragment();
        locationPickerFragment.show(getFragmentManager(), "location picker");
    }

    //Pass in the array of read locations here and process them
    private void createLocations(ArrayList<String> locations) {

        //locationList.add(new Location("Melbourne", -37.81, 144.96, ""));
        for (String line : locations) {
            String[] entry = line.split(",");
            for(int i = 0; i < entry.length; i++) {
                Log.i("ec", entry[i]);
            }
            Log.i("entry length", Integer.toString(entry.length));
            if (entry.length == 5) {
                locationList.add(new Location(entry[0], Double.parseDouble(entry[1]), Double.parseDouble(entry[2]), entry[3], entry[4]));
            }
        }
        location = locationList.get(0);
    }

    //for sharing information via text or email
    private void shareLocation() {
        startActivity(Intent.createChooser(share(), "Share options"));
    }

    //for sharing information via text or email
    private Intent share() {
        //grabbing the view elements from the sunFragment
        TextView sunriseTV_share = (TextView) findViewById(R.id.sunriseTimeTV);
        TextView sunsetTV_share = (TextView) findViewById(R.id.sunsetTimeTV);
        TextView locationTV_share = (TextView) findViewById(R.id.locationTV_sun);
        TextView dateTv_share = (TextView) findViewById(R.id.dateHolder_sun);

        String date_share = dateTv_share.getText().toString();
        String srise = sunriseTV_share.getText().toString();
        String sset = sunsetTV_share.getText().toString();
        String location = locationTV_share.getText().toString();

        String message = "In " + location + " the sun will rise at " + srise + " and set at " + sset + " on the " + date_share + ".";
        Intent result = new Intent();
        result.setAction(Intent.ACTION_SEND);
        result.setType("text/plain");
        result.putExtra(Intent.EXTRA_SUBJECT, "Sun Times");
        result.putExtra(Intent.EXTRA_TEXT, message);
        return result;
    }
}
