package info.android.akihabara.cos.sample02;

public interface ChangeAccelerometeCallback {

	public void onStart();

	public void onChangeY(float diffY);

	public void onChangeZ(float diffZ);

	public void onFinish();
}
