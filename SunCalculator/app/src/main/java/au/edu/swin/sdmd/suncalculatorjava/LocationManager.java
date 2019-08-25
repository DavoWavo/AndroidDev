package au.edu.swin.sdmd.suncalculatorjava;

import android.content.Intent;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LocationManager extends AppCompatActivity {

    public static final String LOCATION = "LOCATION_KEY";
    public static final int KEY = 0x1;

    Button newLocationButton;

    //list of all the locations that will be displayed in the recycler view
    private List<Location> locationList = new ArrayList<Location>();

    //recycler view
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    RecyclerView rvLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debugMode", "LocationManager onCreate Launched");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_manager);

        //clear this list everytime this activity is reloaded
        locationList.clear();
        //creating all the locations that are already in the file
        createLocations(LocationInputStream());

        //initializing RV
        RVManager(locationList);

        //initializing button
        newLocationButton = findViewById(R.id.create_location_button);
        newLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location tempNewLocation = new Location("", 0.0, 0.0, "", "true");
                Log.d("debugMode initial", tempNewLocation.getName() + ","
                        + Double.toString(tempNewLocation.getLatitude()) + ","
                        + Double.toString(tempNewLocation.getLongitude()) + ","
                        + tempNewLocation.getTimezone() + ","
                        + tempNewLocation.getCustom());
                showLocationDetail(tempNewLocation);
            }
        });
    }

    public void RVManager(List<Location> locationList) {
        rvLocations = (RecyclerView) findViewById(R.id.location_manager_rv);
        mLayoutManager = new LinearLayoutManager(this);
        rvLocations.setLayoutManager(mLayoutManager);
        mAdapter = new LocationManager.RVAdapter(locationList);
        rvLocations.setAdapter(mAdapter);
    }

    //reading the input file
    public ArrayList<String> LocationInputStream() {
        ArrayList<String> locations = new ArrayList<String>();
        AssetManager assetManager = getAssets();

        try {
//            InputStream inputStream = getAssets().open("au_locations.txt");
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

    //Pass in the array of read locations here and process them
    private void createLocations(ArrayList<String> locations) {
        for (String line : locations) {
            String[] entry = line.split(",");
            for(int i = 0; i < entry.length; i++) {
                Log.i("ec", entry[i]);
                Log.d("debugExtra", entry[i]);
            }
            //Log.i("entry length", Integer.toString(entry.length));
            if (entry.length == 5) {
                locationList.add(new Location(entry[0], Double.parseDouble(entry[1]), Double.parseDouble(entry[2]), entry[3], entry[4]));
            }
        }
    }

    //RV adapter for locations, don't touch
    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
        List<Location> mValues;
//        Button deleteButton;

        public RVAdapter(List<Location> mValues) {
            this.mValues = mValues;
        }

        public void AddLocation(Location newLocation) {
            mValues.add(newLocation);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.location_manager_row, parent, false);
            view.setOnClickListener(onClickListener);

            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        //replaces the contents of the view, invoked by the layout manager
        @Override
        public void onBindViewHolder(@NonNull RVAdapter.ViewHolder holder, final int position) {
            Log.d ("debugMode", "onBindHolder getting position in list....");
            Location location = mValues.get(position);
            Log.d ("debugMode", "onBindHolder beginning to assign values to holder....");
            holder.mLocation.setText(location.getName());

            if (location.getCustom().equals("false")) {
                holder.mDelete.setVisibility(View.INVISIBLE);
                holder.mDelete.setEnabled(false);
            } else {
                holder.mDelete.setVisibility(View.VISIBLE);
                holder.mDelete.setEnabled(true);
            }

            /*
             *when the user clicks delete on an item, the file of the contents is deleted
             *and rewritten with the previous items, minus the deleted item
             */
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("debugMode", "delete pressed");
                    mValues.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mValues.size());
                    removeContentsFromFile();
                    rewriteLocationList(mValues);
                }
            });
            Log.d ("debugMode", "onBindHolder succeeded in assigning to holder in adapter...");
        }

        //remove all the items from the file
        private void removeContentsFromFile() {
            Log.d("debugMode", "deleting entire file contents...");
            try {
                FileOutputStream openFileInput = openFileOutput("Locations.txt", MODE_PRIVATE);
                PrintWriter printWriter = new PrintWriter(openFileInput);
                printWriter.print("");
                printWriter.close();
                Log.d("debugMode", "deleting entire file contents successful");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        //rewrite everything to the file
        private void rewriteLocationList(List<Location> newLocationList) {
            Log.d("debugMode", "writing new contents");
            for (Location location : newLocationList) {
                String line = "\n" + location.returnFull();
                byte[] bytes = line.getBytes();

                FileOutputStream out;
                try {
                    Log.d("debugMode", location.returnFull());
                    out = openFileOutput("Locations.txt", MODE_APPEND);
                    out.write(bytes);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //return the number of items in the view
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        //creates the new views, invoked by the layout manager
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mLocation;
            public Button mDelete;

            public ViewHolder(View view) {
                super(view);
                mLocation = view.findViewById(R.id.location_manager_row_tv);
                mDelete = view.findViewById(R.id.location_manager_delete_button);
            }
        }
    }

    //setting the location within the mainActivity to the selected location
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("debugMode", "LocationManager onClick reached");
            int position = rvLocations.getChildLayoutPosition(view);
            //getting reference to main activity
            Location selectLocation = locationList.get(position);
            Log.d("debugMode", "RV onClick: " + selectLocation.returnFull());
            showLocationDetail(selectLocation);
        }
    };

    //this starts the location manager form activity with the information stored within the location
    private void showLocationDetail(Location selectedLocation) {
        Log.d("debugMode", "LocationMananager showLocationDetail Launched");
        final Intent formIntent = new Intent(this, LocationManagerForm.class);
        formIntent.putExtra(LOCATION, selectedLocation);
        startActivityForResult(formIntent, KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("debugMode", "LocationMananager onActivityResult Launched");
        if (requestCode == KEY) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Log.i("INTENT", "Intent empty");
                } else {
                    final Location location = intent.getParcelableExtra(LocationManagerForm.RETURN_LOCATION);
                    Log.d("debugMode", "RETURN_LOCATION REACHED");
                    saveLocation(location);
                    locationList.add(location);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                Log.i("INTENT", "Result not okay");
            }
        } else {
            Log.i("INTENT", "Key doesn't match");
        }
    }

    //inserts a new location into the file
    public void saveLocation(Location newLocation) {
        FileOutputStream outputStream = null;
        Log.d("debugMode", "writing new location to memory");
        byte[] writeLine = ("\n"+ newLocation.returnFull()).getBytes();
        try {
            outputStream = openFileOutput("Locations.txt", MODE_APPEND);
            outputStream.write(writeLine);
            outputStream.close();
            Log.d("debugMode", "New location saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
