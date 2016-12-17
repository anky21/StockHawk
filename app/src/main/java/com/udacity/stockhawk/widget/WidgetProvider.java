package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by anky_ on 17/12/2016.
 */

public class WidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        String stockSymbol = "AAPL";
        String price = "$92.53";
        String percentageChange = "+1.13%";

        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds){
            int layoutId = R.layout.widget_static;
            RemoteViews views = new RemoteViews(context.getPackageName(),layoutId);

            // Add the data to the RemoteViews
            views.setTextViewText(R.id.symbol,stockSymbol);
            views.setTextViewText(R.id.price, price);
            views.setTextViewText(R.id.change, percentageChange);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0 );
            views.setInt(R.id.widget, "setBackgroundColor", R.color.widget_background_color);
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
