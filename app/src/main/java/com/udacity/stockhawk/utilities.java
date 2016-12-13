package com.udacity.stockhawk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    public static String formatDate(long dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL yyyy");
        return dateFormat.format(dateObject);
    }

    // Reverse the order of an ArrayList<String>
    public static ArrayList<String> formatStringArrayList(ArrayList<String> arrayList){
        ArrayList<String> newArrayList = new ArrayList<>();
        int numDataPoints = arrayList.size();
        for(int i=0; i<numDataPoints;i++){
            String singleItem = arrayList.get(numDataPoints - i - 1);
            newArrayList.add(new String(singleItem));
        }
        return newArrayList;
    }

    // Reverse the order of an ArrayList<String>
    public static ArrayList<Float> formatFloatArrayList(ArrayList<Float> arrayList){
        ArrayList<Float> newArrayList = new ArrayList<>();
        int numDataPoints = arrayList.size();
        for(int i=0; i<numDataPoints;i++){
            float singleItem = arrayList.get(numDataPoints - i - 1);
            newArrayList.add(new Float(singleItem));
        }
        return newArrayList;
    }
}
