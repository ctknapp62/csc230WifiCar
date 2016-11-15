package csc230.wificontroller;

//Importing required libraries
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    //Defining constants used in class
    private boolean isBrake = true;
    Float azimut;
    TextView turnDebug;
    //Starting speed set to zero
    int speed = 0;
    //Starting direction set to Forward
    String direction = "Forward";


    // Sensor Objects
    SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    float[] mGravity;
    float[] mGeomagnetic;

    // IP:port of Car
    //******************************************************
    //This part changes! Need to ifconfig -a and look at En0
    //******************************************************
    String url = "http://172.20.10.4:2300";

    //Class to get HTTP requests off of main thread
    private class HttpBuilder extends AsyncTask<String, Integer, Long> {

        protected Long doInBackground(String... urls)  {

            //Making sure we can connect to the specified URL, If connection fails look in console log
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                int responseCode = con.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                //print result
                System.out.println(response.toString());
            }
            catch (Exception e){
                System.out.println(e);
            }
            return 0L;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting the dynamic screen size
        float pixelsHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230, getResources().getDisplayMetrics());
        float pixelsWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());

        //Decleration of screen elements
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        Switch fr = (Switch) findViewById(R.id.switch1);
        Switch brake = (Switch) findViewById(R.id.brake);

        //Initializing variable values
        brake.setChecked(true);
        fr.setChecked(true);
        seekBar.getLayoutParams().height = (int) pixelsHeight;
        seekBar.getLayoutParams().width = (int) pixelsWidth;
        seekBar.setMax(100);
        seekBar.setProgress(0);
        seekBar.setRotation(270);

        //Initializing sensors for use of gyroscope
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //Linking TextView to TextView elements on screen (Used for debugging)
        final TextView speedDebug = (TextView) findViewById(R.id.sDebug);
        final TextView forwardDebug = (TextView) findViewById(R.id.frDebug);
        final TextView stopDebug = (TextView) findViewById(R.id.stopDebug);
        turnDebug = (TextView) findViewById(R.id.turnDebug);


        String s = "Speed: 0";
        speedDebug.setText(s);


        s = "Direction: Forward";
        forwardDebug.setText(s);

        s = "Brake: On";
        stopDebug.setText(s);

        //Checking to see if the brake switch is on. If on do nothing if off receive controls
        brake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String brake = "";
                if (isChecked) {
                    brake = "Brake: On";
                    setBrake(true);
                    speed = 0;
                    new HttpBuilder().execute(url+"/steer/Center");
                    new HttpBuilder().execute(url+"/drive/Stop");
                } else {
                    brake = "Brake: Off";
                    setBrake(false);
                }
                stopDebug.setText(brake);
            }
        });


        //Checking to see if the Forward/Reverse is on. If on Go forward, if off go reverse
        fr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String dir = "";
                if (getBrake()) {
                    if (isChecked) {
                        dir = "Direction: Forward";
                        direction = "Forward";
                    } else {
                        dir = "Direction: Reverse";
                        direction = "Reverse";
                    }
                } else {
                    if (isChecked) {
                        dir = "Direction: Forward";
                        direction = "Forward";
                    } else {
                        dir = "Direction: Reverse";
                        direction = "Reverse";
                    }
                    forwardDebug.setText(dir);
                }
            }
        });

        //Checking the SeekBars value: Value represents % of speed. 0% is not moving 100% is full speed
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = progress;
                String s = String.valueOf(speed);
                s = "Speed: " + s;
                speedDebug.setText(s);
                Log.d("Speed", s);

                if (getBrake()) {
                    speed = 0;
                    seekBar.setProgress(0);
                } else {
                    new HttpBuilder().execute(url+"/drive/"+direction+"/"+speed);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //Handling the Android Life Cycle
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    //Handling the Android Life Cycle
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }



    public boolean getBrake() {
        return isBrake;
    }

    public void setBrake(Boolean bool) {
        if (bool) {
            isBrake = true;
        } else {
            isBrake = false;
        }
    }

    // @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //Setting up the Gyroscope controller. Takes into account changes in the Y plane using Magnometer and Accelerometer
    public void onSensorChanged(SensorEvent event) {
        if (!getBrake()) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                    float x = orientation[1];
                    String dir = "Right";
                    x = (int) (x * 10);
                    if(x>0){
                        dir = "Left";
                    }
                    else if(x == 0){
                        dir = "Center";
                    }
                    String s = "Turn: " + String.valueOf(x) + " "+ dir;
                    new HttpBuilder().execute(url+"/steer/"+dir);
                    turnDebug.setText(s);

                }

            }
        }


    }

}
