
package info.android.akihabara.cos.sample02;

// 日本Androidの会 秋葉原支部 コスプレ理系女子普及部
// Android アプリ開発　勉強会 教材
// Programmed by Kazuyuki Eguchi 2012

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import info.android.akihabara.cos.sample02.ColorPickerDialog.OnColorChangedListener;

import java.util.ArrayList;

public class MainActivity extends Activity
{
    // 定数の定義
    private String TAG = "test";

    private final int RESULT_VOICE = 1;
    private final int RESULT_CAMERA = 2;

    private Uri mImageUri = null;

    private static final int MENU_ID_MENU1 = 1;
    private static final int MENU_ID_CAM = 2;
    private static final int MENU_ID_COLOR = 3;
    private static final int MENU_ID_FACE = 4;

    // 変数の定義
    private DView view = null;

    private Acceleromete mAcceleromete;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        view = new DView(this);
        this.setContentView(view);
        mAcceleromete = new Acceleromete().setActivity(this).setOnChangeAcceleromete(
                new ChangeAccelerometeCallback() {

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onChangeZ(float diffZ) {
                        view.shake(diffZ, DView.AXIS_Z);
                    }

                    @Override
                    public void onChangeY(float diffY) {
                        view.shake(diffY, DView.AXIS_Y);
                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub

                    }
                });
        mAcceleromete.init();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mAcceleromete.registerListener();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        mAcceleromete.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // メニューを呼び出された時（初回のみ）
        Log.d(TAG, "onCreateOpetionsMenu");
        menu.add(Menu.NONE, MENU_ID_MENU1, Menu.NONE, "音声で指示する");
        menu.add(Menu.NONE, MENU_ID_CAM, Menu.NONE, "cam");
        menu.add(Menu.NONE, MENU_ID_COLOR, Menu.NONE, "色を選ぶ");
        menu.add(Menu.NONE, MENU_ID_FACE, Menu.NONE, "ふくわらい");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // メニューが呼び出される度に呼ばれる
        Log.d(TAG, "onPrepareOptionsMenu");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // メニューの内容を選択した時
        Log.d(TAG, "onOptionsItemSelected");

        switch (item.getItemId())
        {
        case MENU_ID_MENU1:
            call_voice();
            return true;
        case MENU_ID_CAM:
            call_camera();
            return true;
        case MENU_ID_COLOR:
            showColorPicker();
            return true;
        case MENU_ID_FACE:
            startFukuwarai();
        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RESULT_VOICE)
        {
            if (resultCode == RESULT_OK)
            {
                ArrayList<String> results = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                for (int i = 0; i < results.size(); i++)
                {
                    String res = results.get(i);

                    Log.d(TAG, res);

                    if (res.compareTo("赤色") == 0)
                    {
                        view.setColor(Color.RED);
                        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if (res.compareTo("青色") == 0)
                    {
                        view.setColor(Color.BLUE);
                        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if (res.compareTo("カメラ") == 0)
                    {
                        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
                        call_camera();
                        return;
                    }
                    else if (res.compareTo("お楽しみ") == 0)
                    {
                        view.Printout();
                        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if (res.compareTo("クリア") == 0)
                    {
                        view.clear();
                        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                Toast.makeText(this, "『" + results.get(0) + "』は、理解できませんでした", Toast.LENGTH_LONG)
                        .show();
            }
        }
        else if (requestCode == RESULT_CAMERA)
        {
            if (resultCode == RESULT_OK)
            {
                Log.d(TAG, "RESULT_OK");
                view.setPhoto(mImageUri);
            }
            else
            {
                Log.d(TAG, "RESULT_NG");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // 音声認識アプリを呼び出す。
    void call_voice()
    {
        try
        {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "ご主人様ご命令を！");
            startActivityForResult(intent, RESULT_VOICE);
        } catch (Exception ex)
        {
            Log.d(TAG, ex.toString());
            Toast.makeText(this, "音声入力呼び出しできませんでした", Toast.LENGTH_LONG).show();
        }
    }

    // カメラを呼び出す。
    void call_camera()
    {
        try
        {
            String filename = System.currentTimeMillis() + ".jpg";

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, filename);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);

            // Log.d(TAG,mImageUri.toString());

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, RESULT_CAMERA);
        } catch (Exception ex)
        {
            Log.d(TAG, ex.toString());
            Toast.makeText(this, "カメラの呼び出しに失敗しました", Toast.LENGTH_LONG).show();
        }
    }

    private void showColorPicker() {
        new ColorPickerDialog(this, new OnColorChangedListener() {
            @Override
            public void colorChanged(int color) {
                view.setColor(color);
            }
        }, view.getColor()).show();
    }

    private void startFukuwarai() {
        view.detect();
    }
}
