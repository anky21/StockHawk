package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by anky_ on 18/12/2016.
 */

public class WidgetIntentService extends IntentService {
    private static final String[] QUOTE_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };
    private static final int INDEX_SYMBOL = 0;
    private static final int INDEX_PRICE = 1;
    private static final int INDEX_PERCENTAGE_CHANGE = 2;

    public WidgetIntentService(){
        super(WidgetIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WidgetProvider.class));

        String symbol = "FB";
        Uri uri = Contract.Quote.makeUriForStock(symbol);

        Cursor data = getContentResolver().query(uri,QUOTE_COLUMNS, null, null, null);
        if(data == null){
            return;
        }
        if(!data.moveToFirst()){
            data.close();
            return;
        }

        // Extract the data from the Cursor
        String stockSymbol = data.getString(INDEX_SYMBOL);
        String price = data.getString(INDEX_PRICE);
        String change = data.getString(INDEX_PERCENTAGE_CHANGE);
        String description = stockSymbol + " price is " + price;
        data.close();

        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds){
            int layoutId = R.layout.widget_detail_list_item;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
            views.setTextViewText(R.id.symbol, stockSymbol);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
                setRemoteContentDescription(views, description);
            }
            views.setTextViewText(R.id.price, price);
            views.setTextViewText(R.id.change,change);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0 );
//            views.setInt(R.id.widget, "setBackgroundColor", R.color.widget_background_color);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description){
        views.setContentDescription(R.id.symbol, description);
    }
}
