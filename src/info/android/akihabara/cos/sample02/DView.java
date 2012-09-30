package info.android.akihabara.cos.sample02;

//日本Androidの会 秋葉原支部 コスプレ理系女子普及部
//Android アプリ開発　勉強会 教材
//Programmed by Kazuyuki Eguchi 2012

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class DView extends View 
{
	// 定数の定義
	private String TAG = "test";
	
	// 変数の定義
	private Bitmap mBmp = null;
	private Bitmap mBackBmp = null;
	private Canvas mCanvas = null;
	private Paint mPaint = null;
	
	@SuppressWarnings("unused")
	private int mWidth = 0;
	@SuppressWarnings("unused")
	private int mHeight = 0;
	
	private int mColor = Color.BLACK;
	private float mStroke = 10.0f;
	
	private float[] mX = new float[10];
	private float[] mY = new float[10];
	
	public DView(Context context)
	{
		super(context);

		// 初期化の処理はこちらに書いてね！
		Log.d(TAG,"DView Create");
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		// 画面描画をしてください。
		Log.d(TAG,"onDraw");

		// キャンバスに画像を描画する
		canvas.drawBitmap(mBackBmp, 0, 0, null);
		canvas.drawBitmap(mBmp, 0, 0, null);
		
		super.onDraw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// 画面のサイズが変わりましたよ
		Log.d(TAG,"onSizeChanged x=" + w + ",h=" + h + ",oldw=" + oldw + ",oldh=" + oldh);
		
		mWidth = w;
		mHeight = h;

		// 新しいビットマップを作成
		mBackBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		
		// 新しいキャンバスを作成
		mCanvas = new Canvas(mBackBmp);
		mCanvas.drawColor(Color.WHITE);
		
		mCanvas = new Canvas(mBmp);
		mCanvas.drawColor(Color.TRANSPARENT);

		mPaint = new Paint();
		
		mPaint.setColor(this.mColor);
		mPaint.setStrokeWidth(this.mStroke);
		mPaint.setAntiAlias(true);
		
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// 画面に指が触れましたよ
		Log.d(TAG,"onTouchEvent " + (event.getAction() & MotionEvent.ACTION_MASK));
		
		// 何本の指を検出しているのか？
		int points = event.getPointerCount();
		
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				// 画面を押しましたよ。
				Log.d(TAG,"ACTION_DOWN");

				for(int i=0 ; i < mX.length ; i++)
				{
					mX[i] = -1;
				}

				for(int i=0 ; i < mY.length ; i++)
				{
					mY[i] = -1;
				}
				
				for(int i=0 ; i < points ; i++)
				{
					Log.d(TAG,"index=" + i + ",X=" + event.getX(i) + ",Y=" + event.getY(i));
					
					mX[i] = event.getX(i);
					mY[i] = event.getY(i);
				}
				break;
				
			case MotionEvent.ACTION_MOVE:
				// 画面を撫でていますよ
				Log.d(TAG,"ACTION_MOVE");

				for(int i=0 ; i < points ; i++)
				{
					Log.d(TAG,"index=" + i + ",X=" + event.getX(i) + ",Y=" + event.getY(i));
					
					// 線を描画する
					if(mX[i] != -1 && mY[i] != -1)
					{
						mCanvas.drawLine(mX[i], mY[i],event.getX(i), event.getY(i), mPaint);
					}
					
					mX[i] = event.getX(i);
					mY[i] = event.getY(i);
				}				
				
				// 画面の描画指示をする
				invalidate();
				break;
				
			case MotionEvent.ACTION_UP:
				// 画面からすべての指が離れましたよ。
				Log.d(TAG,"ACTION_UP");
				for(int i=0 ; i < points ; i++)
				{
					Log.d(TAG,"index=" + i + ",X=" + event.getX(i) + ",Y=" + event.getY(i));
				}
				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				Log.d(TAG,"ACTION_POINTER_DOWN");
				
				for(int i=0 ; i < points ; i++)
				{
					Log.d(TAG,"index=" + i + ",X=" + event.getX(i) + ",Y=" + event.getY(i));
					mX[i] = event.getX(i);
					mY[i] = event.getY(i);
				}
				break;

			case MotionEvent.ACTION_POINTER_UP:
				Log.d(TAG,"ACTION_POINTER_UP");
				for(int i=0 ; i < points ; i++)
				{
					Log.d(TAG,"index=" + i + ",X=" + event.getX(i) + ",Y=" + event.getY(i));
					
					mX[i] = -1;
					mY[i] = -1;
				}
				break;
		}
		return true;
	}
	
	// 描画の際の色を指定する
	public void setColor(int color)
	{
		this.mColor = color;
		
		if(color != Color.TRANSPARENT)
		{
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setColor(this.mColor);
			mPaint.setStrokeWidth(mStroke);
		}
	}
	
	// 下敷きを画像にする
	public void setPhoto(Uri uri)
	{
		try
		{
			Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
			c.moveToFirst();
			String filename = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
			
			Log.d(TAG,filename);

			File srcFile = new File(filename);
			FileInputStream fis = new FileInputStream(srcFile);
			Bitmap tBmp = BitmapFactory.decodeStream(fis);
			
			int w = tBmp.getWidth();
			int h = tBmp.getHeight();
			
			Log.d(TAG,"w=" + w + ",h=" + h);
			
			int nw = mBmp.getWidth();
			int nh = mBmp.getHeight();
			
			Log.d(TAG,"nw=" + nw + ",nh=" + nh);

			float scaleWidth = 1.0f;
	        float scaleHeight = 1.0f;

	        Matrix matrix = new Matrix();
			
			if(w > h)
			{
				scaleWidth = (float)nh / (float)w;
		        scaleHeight = (float)nw / (float)h;
		        matrix.postRotate(90);
			}
			else
			{
				scaleWidth = (float)nw / (float)w;
		        scaleHeight = (float)nh / (float)h;
			}
			
	        matrix.postScale(scaleWidth, scaleHeight);
	        
	        Log.d(TAG,"sw=" + scaleWidth + ",sh=" + scaleHeight);
	        
	        Bitmap rBmp = Bitmap.createBitmap(tBmp, 0, 0, w, h, matrix, true);

			this.mBackBmp = rBmp;
			invalidate();
		}
		catch(Exception ex)
		{
			Log.d(TAG,ex.toString());
			Toast.makeText(getContext(), "デコード失敗", Toast.LENGTH_LONG).show();
		}
	}

	// 印刷する
	public void Printout()
	{
		try
		{
			Bitmap tBmp = Bitmap.createBitmap(mBmp.getWidth(), mBmp.getHeight(), Config.ARGB_8888);
			Canvas tCanvas = new Canvas(tBmp);
			
			tCanvas.drawBitmap(mBackBmp, 0, 0, null);
			tCanvas.drawBitmap(mBmp, 0, 0, null);

		    String filename = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + System.currentTimeMillis() + ".jpg";
		    
		    Log.d(TAG,filename);
		    
			File file = new File(filename);
			FileOutputStream out = new FileOutputStream(file);
			tBmp.compress(CompressFormat.JPEG, 100, out);
			tBmp.recycle();
			out.close();
			
			// Bluetooth送信をする場合
			Uri uri = Uri.parse("file://" + filename);
			Log.d(TAG,uri.toString());
			
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/jpeg");
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			getContext().startActivity(intent);
		}
		catch(Exception ex)
		{
			Log.d(TAG,ex.toString());
		}
	}
	
	// 描画したものを消す
	public void clear()
	{
		if(mBmp.isRecycled() == false)
		{
			mBmp.recycle();
		}
		
		mBmp = Bitmap.createBitmap(mBackBmp.getWidth(), mBackBmp.getHeight(), Bitmap.Config.ARGB_8888);

		mCanvas = new Canvas(mBmp);
		mCanvas.drawColor(Color.TRANSPARENT);
		
		invalidate();
	}

}
