package com.bhaskarn.automated_rcs_setup;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.List;


public class NotificationService extends Service {

    String TAG = "DebugTest";

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {

            Log.d(TAG, "onConfigurationChanged: nothing much");

        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {

            Log.d(TAG, "onConfigurationChanged: nothing much");


        }

        //A hardware keyboard is being disconnected
        Toast.makeText(this, "Touchview mode",
                Toast.LENGTH_LONG).show();
        super.onConfigurationChanged(newConfig);
    }
}
