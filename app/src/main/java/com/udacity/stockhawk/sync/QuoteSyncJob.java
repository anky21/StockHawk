package com.udacity.stockhawk.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;

public final class QuoteSyncJob {

    private static final int ONE_OFF_ID = 2;
    public static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    private static final int PERIOD = 300000;
    private static final int INITIAL_BACKOFF = 10000;
    private static final int PERIODIC_ID = 1;
    private static final int YEARS_OF_HISTORY = 2;

    private QuoteSyncJob() {
    }

    static void getQuotes(final Context context) {

        Timber.d("Running sync job");

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.add(Calendar.YEAR, -YEARS_OF_HISTORY);

        try {
            Set<String> stockPref = PrefUtils.getStocks(context);
            Set<String> stockCopy = new HashSet<>();
            stockCopy.addAll(stockPref);
            String[] stockArray = stockPref.toArray(new String[stockPref.size()]);

            Timber.d(stockCopy.toString());

            if (stockArray.length == 0) {
                return;
            }

            Map<String, Stock> quotes = YahooFinance.get(stockArray);
            Iterator<String> iterator = stockCopy.iterator();

            Timber.d(quotes.toString());

            ArrayList<ContentValues> quoteCVs = new ArrayList<>();

            while (iterator.hasNext()) {
                String symbol = iterator.next();


                Stock stock = quotes.get(symbol);
                if (null == stock) {
                    stockPref.remove(symbol);
                    utilities.showToast(context, symbol.toUpperCase(), R.string.nonexistent_or_suspended_stock_msg);
                } else {
                    StockQuote quote = stock.getQuote();

                    if (null == quote.getPrice() && null == quote.getPreviousClose()) {
                        stockPref.remove(symbol);
                        utilities.showToast(context, symbol.toUpperCase(), R.string.nonexistent_or_suspended_stock_msg);
                    } else {
                        float price = quote.getPrice().floatValue();
                        float change = quote.getChange().floatValue();
                        float percentChange = quote.getChangeInPercent().floatValue();
                        Long averageVolume = quote.getAvgVolume().longValue();

                        String dayHigh;
                        if (null != quote.getDayHigh()) {
                            dayHigh = quote.getDayHigh().toString();
                        } else {
                            dayHigh = " ";
                        }

                        String dayLow;
                        if (null != quote.getDayLow()) {
                            dayLow = quote.getDayLow().toString();
                        } else {
                            dayLow = " ";
                        }

                        float averagePrice = quote.getPriceAvg50().floatValue();
                        float yearHigh = quote.getYearHigh().floatValue();
                        float yearLow = quote.getYearLow().floatValue();
                        String name = stock.getName();

                        // WARNING! Don't request historical data for a stock that doesn't exist!
                        // The request will hang forever X_x
                        List<HistoricalQuote> history = stock.getHistory(from, to, Interval.DAILY);

                        StringBuilder historyBuilder = new StringBuilder();

                        for (HistoricalQuote it : history) {
                            historyBuilder.append(it.getDate().getTimeInMillis());
                            historyBuilder.append(", ");
                            historyBuilder.append(it.getClose());
                            historyBuilder.append("\n");
                        }

                        ContentValues quoteCV = new ContentValues();
                        quoteCV.put(Contract.Quote.COLUMN_SYMBOL, symbol);
                        quoteCV.put(Contract.Quote.COLUMN_PRICE, price);
                        quoteCV.put(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, percentChange);
                        quoteCV.put(Contract.Quote.COLUMN_ABSOLUTE_CHANGE, change);
                        quoteCV.put(Contract.Quote.COLUMN_HISTORY, historyBuilder.toString());
                        quoteCV.put(Contract.Quote.COLUMN_AVERAGE_VOLUME, averageVolume);
                        quoteCV.put(Contract.Quote.COLUMN_DAY_HIGH, dayHigh);
                        quoteCV.put(Contract.Quote.COLUMN_DAY_LOW, dayLow);
                        quoteCV.put(Contract.Quote.COLUMN_AVERAGE_PRICE, averagePrice);
                        quoteCV.put(Contract.Quote.COLUMN_YEAR_HIGH, yearHigh);
                        quoteCV.put(Contract.Quote.COLUMN_YEAR_LOW, yearLow);
                        quoteCV.put(Contract.Quote.COLUMN_COMPANY_NAME, name);

                        quoteCVs.add(quoteCV);
                    }
                }
            }

            context.getContentResolver()
                    .bulkInsert(
                            Contract.Quote.URI,
                            quoteCVs.toArray(new ContentValues[quoteCVs.size()]));

        } catch (IOException exception) {
            Timber.e(exception, "Error fetching stock quotes");
        }
    }


    private static void schedulePeriodic(Context context) {
        Timber.d("Scheduling a periodic task");

        JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_ID, new ComponentName(context, QuoteJobService.class));


        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(PERIOD)
                .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        scheduler.schedule(builder.build());
    }

    synchronized public static void initialize(final Context context) {
        schedulePeriodic(context);
        syncImmediately(context);
    }

    synchronized public static void syncImmediately(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Intent nowIntent = new Intent(context, QuoteIntentService.class);
            context.startService(nowIntent);
        } else {

            JobInfo.Builder builder = new JobInfo.Builder(ONE_OFF_ID, new ComponentName(context, QuoteJobService.class));


            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setBackoffCriteria(INITIAL_BACKOFF, JobInfo.BACKOFF_POLICY_EXPONENTIAL);


            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

            scheduler.schedule(builder.build());
        }
    }
}
