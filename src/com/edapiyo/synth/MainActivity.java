package com.edapiyo.synth;

import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;


public class MainActivity extends Activity {
	String TAG = "SYNTH";
	private static final int MENU_CLEAR = 0;
	//private static final int MENU_SAVE = 1;
	DrawNoteView view;
	private AudioManager mAudioManager = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		//Log.v(TAG,"onCreate START");
       // mAudioManager = (AudioManager)getSystemService(this.AUDIO_SERVICE);
        view = new DrawNoteView(getApplication());
        //Log.v(TAG,"onCreate END");
    }

    @Override
    public void onStart(){
		//Log.v(TAG,"onStart START");
		super.onStart();
		setContentView(view);
    	//Log.v(TAG,"onStart END");
    }

    @Override
    public void onResume(){
		//Log.v(TAG,"onResume START");
		super.onResume();
    	//Log.v(TAG,"onResume END");
    }



    /** メニューの生成イベント */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	menu.add(0, MENU_CLEAR, 0, "Clear");
    	//menu.add(0, MENU_SAVE, 0, "Save");
    	return true;
    }
    /** メニューがクリックされた時のイベント */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch ( item.getItemId() ) {
    		case MENU_CLEAR:
    			view.clearDrawList(); break;
    		//case MENU_SAVE:
    		//	break;
    	}
    	return true;
    }


    @Override
    public void onDestroy(){
		//Log.v(TAG,"onDestroy START");
		super.onDestroy();
    	//Log.v(TAG,"onDestroy END");
    }

}


class DrawNoteView extends android.view.View {
	Bitmap bmp = null;
	Canvas bmpCanvas = null;
	Point oldpos = new Point(-1,-1);
	String TAG = "SYNTH";
	Context context;
	public SoundPool mSoundPool = null;
	int index = 0;
	private int[] soundResouces = {
			R.raw.bashi2,
			R.raw.bogo,
			R.raw.bokaan,
			R.raw.dogu,
			R.raw.fin,
			R.raw.kaze,
			R.raw.muchi,
			R.raw.pachi,
			R.raw.pikopiko,
			R.raw.zasi,
			R.raw.k
	};
	private int soundIds[];
	private int mWidth;
	private int mHeight;
	private int mRingVol;


	public DrawNoteView(Context c) {
		super(c);
        //Log.v(TAG,"DrawNoteView START");
        context = c;
        setFocusable(true);
        //Log.v(TAG,"DrawNoteView END");
        AudioManager audio = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        mRingVol = audio.getStreamVolume(AudioManager.STREAM_RING);

        mSoundPool = new SoundPool(10, AudioManager.STREAM_RING, 0);

        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				// TODO 自動生成されたメソッド・スタブ
				if (0 == status) {
					//Log.v(TAG,"mSoundPool.setOnLoadCompleteListener LoadComplete" + sampleId);
		            //Toast.makeText(context, "LoadComplete", Toast.LENGTH_LONG).show();
		        }
			}
        });
		soundIds = new int[soundResouces.length];
		for (int i=0; i<soundResouces.length; i++) {
			soundIds[i] = mSoundPool.load(context, soundResouces[i], 1);
		}
	}

	public void clearDrawList() {
		bmpCanvas.drawColor(Color.BLACK);
		mSoundPool.play(soundIds[1], 100, 100, 1, 0, 1);
		invalidate();
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
		mHeight = h;
		//Log.v("DrawNoteK", "view.onSizeChanged");
		super.onSizeChanged(w,h,oldw,oldh);
		if (bmp == null) {
			bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
		}
		bmpCanvas = new Canvas(bmp);
		bmpCanvas.drawColor(Color.BLACK);



	}

	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bmp, 0, 0, null);
	}

	public boolean onTouchEvent(MotionEvent event) {

		Point cur = new Point((int)event.getX(), (int)event.getY());
		//mSoundPool.play(soundIds[10], 0.05f, 0.05f, 1, 0, 0.5f);
		mSoundPool.play(soundIds[10], mRingVol,mRingVol, 1, 0, 1.76f-(float)cur.y*1.32f/(float)mHeight);
		//Log.v(TAG,(1.76f-(float)cur.y*1.32f/(float)mHeight) + "kHz" + " Y:" + cur.y + " Y:" + mHeight);
		if (oldpos.x < 0) {
			oldpos = cur;
		}
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(5);
		paint.setAntiAlias(true);
		// 線を描画
		//bmpCanvas.drawCircle(cur.x, cur.y, 1.0f, paint);
		bmpCanvas.drawLine(oldpos.x, oldpos.y, cur.x, cur.y, paint);
		oldpos = cur;
		// 指を持ち上げたら座標をリセット
		if (event.getAction() == MotionEvent.ACTION_UP) {
			oldpos = new Point(-1, -1);
			//Log.v(TAG,"ACTION UP");
			mSoundPool.stop(soundIds[10]);
			if (index >= soundIds.length) {
				index = 0;
			}
			//mSoundPool.stop(soundIds[9]);
			//Log.v(TAG, "index = " + index);
			//mSoundPool.play(soundIds[index++], 100, 100, 1, 0, 1);
			//Log.v(TAG,"ACTION UP END");
		}
		invalidate();
		return true;
	}

}