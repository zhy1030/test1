package com.example.hongyu.jiangwen;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;
    private Uri[] mUriList;
    private int mCurUri = 0;
    private boolean mPaused = true;
    private String TAG = "JiangWen";
    private Context mContext;
    private Activity mActivity;

    private ImageView mImageView;
    private int[] mImageRes;
    private int mCurImage = 0;

    private Handler mHandler = new Handler();
    private Runnable mRunable;
    private static final int IMAGE_TIME = 10000;

    private ImageButton btn_tv;
    private ImageButton btn_music;
    private ImageButton btn_video;

    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "onCompletion called, mCurUri = " + mCurUri);
            if ( mPaused ) {
                return;
            }
            playNext();
            // setNextImage();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        setContentView(R.layout.activity_main);
        setUriList();
        setDefaultMediaPlayer();
        setDefaultImage();

        // set Timer
        mRunable = new Runnable( ) {
            public void run ( ) {
                setNextImage();
                mHandler.postDelayed(this,IMAGE_TIME);
            }
        };

        // set button listeners
        btn_tv = (ImageButton) findViewById(R.id.btn_tv);
        btn_music = (ImageButton) findViewById(R.id.btn_music);
        btn_video = (ImageButton) findViewById(R.id.btn_video);

        // send back key twice
        btn_tv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    public void run() {
                        try{
                            Instrumentation inst = new Instrumentation();
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                            Log.d(TAG, "Send backkey 1");
                        }
                        catch (Exception e) {
                            Log.e("Exception when onBack", e.toString());
                        }
                    }
                }.start();

                new Thread(){
                    public void run() {
                        try{
                            Instrumentation inst = new Instrumentation();
                            sleep(500);
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                            Log.d(TAG, "Send backkey 2");
                        }
                        catch (Exception e) {
                            Log.e("Exception when onBack", e.toString());
                        }
                    }
                }.start();
            }
        });

        // start music
        btn_music.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_MUSIC);
                startActivity(intent);
            }
        });

        // Send Home
        btn_video.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                mContext.startActivity(intent);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        mPaused = false;
        mMediaPlayer.start();
        // start timer to update the image
        mHandler.postDelayed(mRunable,IMAGE_TIME);
    }

    protected void onPause(){
        super.onPause();
        mPaused = true;
        mMediaPlayer.pause();
        // stop Timer to update the image
        mHandler.removeCallbacks(mRunable);
    }

    protected void onDestory() {
        super.onDestroy();
        mMediaPlayer.release();
        mMediaPlayer = null;
        mUriList = null;
        mContext = null;
    }

//    public boolean onKeyDown (int keyCode, KeyEvent event) {
//        Log.d(TAG, "Key detected: " + keyCode);
//        return super.onKeyDown(keyCode, event);
//    }

    private void setUriList() {
        mUriList = new Uri[5];
        mUriList[0] = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.everything_okay);
        mUriList[1] = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.the_show);
        mUriList[2] = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cocoon);
        mUriList[3] = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.unique);
        mUriList[4] = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.i_am_you);
    }

    private void setDefaultMediaPlayer () {
        mMediaPlayer = new MediaPlayer();
        Log.i(TAG, "setDataSource failed URI=" +  mUriList[mCurUri].toString());
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(this, mUriList[mCurUri]);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            Log.e(TAG, "setDataSource or prepare failed", e);
        }
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
    }

    private void playNext () {
        mCurUri++;
        if (mCurUri >= mUriList.length) {
            mCurUri = 0;
        }
        Log.d(TAG, "play next, mCurUri = " + mCurUri);
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mContext, mUriList[mCurUri]);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            Log.e(TAG, "setDataSource or prepare failed", e);
        }
        mMediaPlayer.start();
    }

    private void setDefaultImage() {
        mImageView = (ImageView)findViewById(R.id.bgImage);
        mImageRes = new int[10];
        mImageRes[0] = R.drawable.bg1;
        mImageRes[1] = R.drawable.bg2;
        mImageRes[2] = R.drawable.bg3;
        mImageRes[3] = R.drawable.bg4;
        mImageRes[4] = R.drawable.bg5;
        mImageRes[5] = R.drawable.bg6;
        mImageRes[6] = R.drawable.bg7;
        mImageRes[7] = R.drawable.bg8;
        mImageRes[8] = R.drawable.bg9;
        mImageRes[9] = R.drawable.bg10;
        mImageView.setImageResource(mImageRes[mCurImage]);
    }

    private void setNextImage () {
        mCurImage++;
        if ( mCurImage >= mImageRes.length ) {
            mCurImage = 0;
        }
        mImageView.setImageResource(mImageRes[mCurImage]);
    }
}
