package com.example.hongyu.jiangwen;

import android.content.Context;
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
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;
    private Uri[] mUriList;
    private int mCurUri = 0;
    private boolean mPaused = true;
    private String TAG = "JiangWen";
    private Context mContext;

    private ImageView mImageView;
    private int[] mImageRes;
    private int mCurImage = 0;

    private Handler mHandler = new Handler();
    private Runnable mRunable;
    private static final int IMAGE_TIME = 30000;

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

    public boolean onKeyDown (int keyCode, KeyEvent event) {
        Log.d(TAG, "Key detected: " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

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
        mImageRes = new int[3];
        mImageRes[0] = R.drawable.bg1;
        mImageRes[1] = R.drawable.bg2;
        mImageRes[2] = R.drawable.bg3;
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
