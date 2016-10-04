package temp.helloworld;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

public class HelloWorld extends Activity {
    TextView xText, yText, zText;
    SensorManager mSensorManager;
    Sensor mGyro;
    Sensor mAccel;
    Sensor mMagno;
    float xPosition = 0;
    float yPosition = 0;
    double zPosition = 0;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagno = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        zText = (TextView) findViewById(R.id.zText);

    }

    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(gyroListener, mGyro, mSensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(accelListener, mAccel, mSensorManager.SENSOR_DELAY_NORMAL);
       // mSensorManager.registerListener(magnoListener, mMagno, mSensorManager.SENSOR_DELAY_NORMAL);

    }

    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(gyroListener);
       // mSensorManager.unregisterListener(accelListener);
       // mSensorManager.unregisterListener(magnoListener);

    }

    public SensorEventListener gyroListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xPosition = xPosition + x;
            yPosition = yPosition + y;
            zPosition = zPosition + (double)z - .000001;

            xText.setText("X : " + (int)x + " rad/s");
            yText.setText("Y : " + (int)y + " rad/s");
            zText.setText("Z : " + (int)z + " rad/s");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}
