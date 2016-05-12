package com.example.hongyu.jiangwen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by hzhan72 on 2016/4/28.
 */
public class BootBroadCastReceiver extends BroadcastReceiver {
    private final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // start new activity
            // context.startActivity(intent2);
            Log.d("JiangWen", "BOOT_COMPLETE message received");
        }
    }
}
