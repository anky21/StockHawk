package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String stockSymbol = intent.getStringExtra(Intent.EXTRA_TEXT);
            QuoteSyncJob.getQuotesForNewStock(getApplicationContext(), stockSymbol);
        } else {
            QuoteSyncJob.getQuotes(getApplicationContext());
        }
    }
}
