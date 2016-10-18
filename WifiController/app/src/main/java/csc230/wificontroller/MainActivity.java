package csc230.wificontroller;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private boolean isBrake = true;
    Float azimut;
    TextView turnDebug;


    SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    float[] mGravity;
    float[] mGeomagnetic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        float pixelsHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 230, getResources().getDisplayMetrics());
        float pixelsWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());

        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        Switch fr = (Switch) findViewById(R.id.switch1);
        Switch brake = (Switch) findViewById(R.id.brake);

        brake.setChecked(true);
        fr.setChecked(true);

        seekBar.getLayoutParams().height = (int) pixelsHeight;
        seekBar.getLayoutParams().width = (int) pixelsWidth;
        seekBar.setMax(100);
        seekBar.setProgress(0);
        seekBar.setRotation(270);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

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


        brake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String brake = "";
                if (isChecked) {
                    brake = "Brake: On";
                    setBrake(true);
                } else {
                    brake = "Brake: Off";
                    setBrake(false);
                }
                stopDebug.setText(brake);
            }
        });


        fr.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String dir = "";
                if (getBrake()) {
                    if (isChecked) {
                        dir = "Direction: Forward";
                    } else {
                        dir = "Direction: Reverse";
                    }
                } else {
                    if (isChecked) {
                        dir = "Direction: Forward";
                    } else {
                        dir = "Direction: Reverse";
                    }
                    forwardDebug.setText(dir);
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int speed = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                speed = progress;
                String s = String.valueOf(speed);
                s = "Speed: " + s;
                speedDebug.setText(s);
                Log.d("Speed", s);

                if (getBrake()) {

                } else {

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

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

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
                     x = (int) (x * 10);
                    String s = "Turn: " + String.valueOf(x);
                    turnDebug.setText(s);

                }

            }
        }


    }

}
