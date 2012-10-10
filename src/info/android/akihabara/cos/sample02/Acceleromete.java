package info.android.akihabara.cos.sample02;

import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class Acceleromete implements SensorEventListener {
	private SensorManager mManager;
	private Activity mActivity;
	private ChangeAccelerometeCallback mChangeAccelerometeCallback;

	static private float ENABLE_DIFF_Y = 10;
	static private float ENABLE_DIFF_Z = 10;

	// 変更前の各軸加速度
	private float oldx = 0;
	private float oldy = 0;
	private float oldz = 0;

	// 各軸加速度増減
	private float dx = 0;
	private float dy = 0;
	private float dz = 0;

	public Acceleromete() {

	}

	public Acceleromete setActivity(Activity activity) {
		mActivity = activity;
		return this;
	}

	public Acceleromete setOnChangeAcceleromete(
			ChangeAccelerometeCallback changeAcceleromete) {
		mChangeAccelerometeCallback = changeAcceleromete;
		return this;
	}

	public void init() {
		mManager = (SensorManager) mActivity
				.getSystemService(Activity.SENSOR_SERVICE);
	}

	public void registerListener() {
		List<Sensor> sensors = mManager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			Sensor s = sensors.get(0);
			mManager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
		}
	}

	public void clear() {
		mManager.unregisterListener(this);

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	private boolean isEnbleDiffY(float y) {
		return y > ENABLE_DIFF_Y;
	}

	private boolean isEnbleDiffZ(float z) {
		return z > ENABLE_DIFF_Z;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		String str = "";
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	        if (mChangeAccelerometeCallback != null) {
	            mChangeAccelerometeCallback.onStart();
	        }
			// 加速度センサー値変更時で前回との差分計算
			dx = event.values[0] - oldx;
			dy = event.values[1] - oldy;
			dz = event.values[2] - oldz;
			oldx = event.values[0];
			oldy = event.values[1];
			oldz = event.values[2];

			if (isEnbleDiffZ(dz)) {
				Log.d("test", "##z:true");
				if (mChangeAccelerometeCallback != null) {
					mChangeAccelerometeCallback.onChangeZ(dz - ENABLE_DIFF_Z);
				}
			}

			if (isEnbleDiffY(dy)) {
				if (mChangeAccelerometeCallback != null) {
					mChangeAccelerometeCallback.onChangeY(dy - ENABLE_DIFF_Y);
				}
				Log.d("test", "##y:true");
			}

			// str = "----差分:" + dx + "Y軸:" + dy + "Z軸:" + dz;
			// str = "----差分:z軸:" + dz;
			// Log.d("test", str);

			// boolean y = dy > 10;
			// if(y){
			// Log.d("test","##y:"+y);
			// }

			//
			// str = "####old:X軸:" + oldx + "Y軸:" + oldy + "Z軸:" + oldz;
			// Log.d("test", str);
			//
			// str = "####now:X軸:" + event.values[0] + "Y軸:" + event.values[1]
			// + "Z軸:" + event.values[2];
			// Log.d("test", str);
	        if (mChangeAccelerometeCallback != null) {
	            mChangeAccelerometeCallback.onFinish();
	        }
		}


	}
}
