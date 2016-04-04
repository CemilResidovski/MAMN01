package lthstudents.mamn01app;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DisplaySensorValuesActivity extends AppCompatActivity implements SensorEventListener {

    // device sensor manager
    private SensorManager mSensorManager;

    private Sensor mAccelerometer;

    public TextView tvHeading;

    private float RTmp[] = new float[9];
    private float Rot[] = new float[9];
    private float I[] = new float[9];
    private float grav[] = new float[3];
    private float mag[] = new float[3];
    private float results[] = new float[3];

    static final float ALPHA = 0.8f;
    protected float[] gravSensorVals;
    protected float[] magSensorVals;

    private RelativeLayout layout;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sensorvalues);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layout = (RelativeLayout) findViewById(R.id.content);
        textView = new TextView(this);
        layout.addView(textView);

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) findViewById(R.id.tvHeading);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // initialize the accelerometer
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        textView.setText("");
        float[] linear_acceleration = new float[3];

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravSensorVals = lowPass(event.values.clone(), gravSensorVals);
        }
// else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            magSensorVals = lowPass(event.values.clone(), magSensorVals);
//        }

        linear_acceleration[0] = event.values[0] - gravSensorVals[0];
        linear_acceleration[1] = event.values[1] - gravSensorVals[1];
        linear_acceleration[2] = event.values[2] - gravSensorVals[2];

        String message = "x: " + Float.toString(linear_acceleration[0]) + "\ny: "
                + Float.toString(linear_acceleration[1]) + "\nz: " + Float.toString(linear_acceleration[2]);

        textView.setTextSize(40);

        textView.setText(message);
    }

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = ALPHA * output[i] + (1 - ALPHA) * input[i];
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}
