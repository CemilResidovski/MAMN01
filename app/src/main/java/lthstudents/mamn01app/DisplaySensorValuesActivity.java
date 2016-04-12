package lthstudents.mamn01app;

import android.content.Context;
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
    private SensorManager mSensorMng;
    private Sensor mAccelerometer;
    private static final int FORCE_THRESHOLD = 350;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    private SensorManager mSensorMgr;
    private float[] mLast;
    private long mLastTime;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;
    public TextView tvHeading;

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

        mLast = new float[3];

        mShakeCount = 0;

        mLast[0] = -1f;
        mLast[1] = -1f;
        mLast[2] = -1f;

        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) findViewById(R.id.tvHeading);

        // initialize your android device sensor capabilities
        mSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

        // initialize the accelerometer
        mAccelerometer = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onShake() {
        textView.setText("");
        String message = "Shake Count: " + mShakeCount;
        textView.setTextSize(40);
        textView.setText(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorMgr.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorMgr.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values.clone();

            long now = System.currentTimeMillis();

            if ((now - mLastForce) > SHAKE_TIMEOUT) {
                //mShakeCount = 0;
                mShakeCount++;
            }

            if ((now - mLastTime) > TIME_THRESHOLD) {
                long diff = now - mLastTime;
                float speed = Math.abs(values[0] + values[1] +
                        values[2] - mLast[0] - mLast[1] - mLast[2]) / diff * 10000;

                if (speed > FORCE_THRESHOLD) {
                    if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                        mLastShake = now;
                        //mShakeCount = 0;
                        mShakeCount++;
                        onShake();
                    }
                    mLastForce = now;
                }
                mLastTime = now;
                mLast = values;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}
