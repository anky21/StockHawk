package com.udacity.stockhawk;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import android.os.Handler;

/**
 * Created by anky_ on 30/11/2016.
 */

public class utilities {

    /**
     * Showing a toast message, using the Main thread
     */
    public static void showToast(final Context context, final String symbol, final int resourceId) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, symbol + context.getString(resourceId), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
