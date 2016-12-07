package com.example.gruver.gps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDb;

    private static final int BUFFER_DIST = 5;   //dead zone radius in meters

    private Button button;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private TextView textView;
    private EditText editText;
    private LocationManager lm;
    private LocationListener locCircuit; //for timing a full sprint
    private LocationListener locNewCircuit; //for start and end locations
    private double startLong,startLat,endLong,endLat;
    private boolean foundStart = false;
    private boolean saveStart = true;
    private long timeS = 0; //start time
    private long timeT = 0; //total run time
    private int i = 0;

    private Location start = new Location("");
    private Location end = new Location("");
    private Location newStart = new Location("");
    private Location newEnd = new Location("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);


        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);

        /*startLong = -96.58575;
        startLat = 39.188436;
        endLong = -96.58575;
        endLat = 39.188429;*/

        startLong = startLat = endLong = endLat = 0;

        start.setLatitude(startLat);
        start.setLongitude(startLong);
        end.setLatitude(endLat);
        end.setLongitude(endLong);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        locCircuit = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //textView.clearComposingText();
                textView.setText(" "+location.getLatitude()+" "+location.getLongitude());

                if(foundStart == false){
                    if(location.distanceTo(start)<=BUFFER_DIST){
                        foundStart = true;
                        timeS = System.currentTimeMillis();//start timer

                        Toast.makeText(getApplicationContext(), "Found Start", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(location.distanceTo(end)<=BUFFER_DIST){
                        timeT = System.currentTimeMillis() - timeS;//stop timer
                        lm.removeUpdates(locCircuit);
                        foundStart = false;
                        Toast.makeText(getApplicationContext(), "Found End", Toast.LENGTH_SHORT).show();
                    }

                    //record speed
                    //check top speed
                    //update average
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        locNewCircuit = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(saveStart == true){
                    //average location over 10 checks
                    startLat += location.getLatitude();
                    startLong += location.getLongitude();
                }else{
                    //average end location
                    endLat += location.getLatitude();
                    endLong += location.getLongitude();
                }
                i++;
                if(i==10){
                    lm.removeUpdates(locNewCircuit);
                    i = 0;

                    if(saveStart == true){
                        startLat = startLat/10.0;
                        startLong = startLong/10.0;

                        newStart.setLatitude(startLat);
                        newStart.setLongitude(startLong);
                    }else {
                        endLat = endLat/10.0;
                        endLong = endLong/10.0;

                        newEnd.setLatitude(endLat);
                        newEnd.setLongitude(endLong);
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.INTERNET
                },10);
                return;
            }
        }else{
            configureButton();
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
        }
    }

    private void configureButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeS = timeT = 0;
                //check for selected circuit name
                    //if null toast message to select a circuit
                //get set startLoc and endLoc to locations from db

                lm.requestLocationUpdates("gps", 500, 0, locCircuit);
            }
        });

        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                lm.removeUpdates(locCircuit);
            }
        });

        //make a set start button
            //on click, saveStart == true
            //get 10 single updates
        button3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveStart = true;
                i = 0;
                startLat = 0;
                startLong = 0;

                lm.requestLocationUpdates("gps",500,0,locNewCircuit);
            }
        });

        //make a set end button
            //on click, saveStart == false
            //get 10 single updates
        button4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveStart = false;
                i = 0;
                endLat = 0;
                endLong = 0;

                lm.requestLocationUpdates("gps",500,0,locNewCircuit);
            }
        });

        //make save circuit button
            //save name, start, and end to db
            //check to find duplicate start and ends
        button5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(start.distanceTo(end)<50) {
                    Toast.makeText(getApplicationContext(), "Start and End locations are too close, must be greater than 50m", Toast.LENGTH_SHORT).show();
                }else if(editText.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Enter a circuit name", Toast.LENGTH_SHORT).show();
                }else{
                    myDb.insertCircuit(editText.getText().toString(), Double.toString(newStart.getLatitude()),Double.toString(newStart.getLongitude()),Double.toString(newEnd.getLatitude()),Double.toString(newEnd.getLongitude()));
                }
            }
        });
    }
}
