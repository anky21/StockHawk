package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.udacity.stockhawk.data.Contract.Quote;
import static com.udacity.stockhawk.data.Contract.Quote.makeUriForStock;

/**
 * Created by anky_ on 11/12/2016.
 */

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private LineChart chart;
    private Uri mUri;
    private String symbol;
    private final static int STOCK_LOADER = 23;

    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;
    private DecimalFormat percentageFormat;
    private NumberFormat formatter;

    private String volumeString;


    private Unbinder unbinder;
    @BindView(R.id.company_name_tv)
    TextView mCompanyName;
    @BindView(R.id.price)
    TextView mPrice;
    @BindView(R.id.dollar_change)
    TextView mDollarChange;
    @BindView(R.id.percentage_change)
    TextView mPercentageChange;
    @BindView(R.id.day_range_tv)
    TextView mDayRange;
    @BindView(R.id.year_range_tv)
    TextView mYearRange;
    @BindView(R.id.avg_price_tv)
    TextView mAvgPrice;
    @BindView(R.id.avg_volume_tv)
    TextView mAverageVolume;

    public DetailFragment() {
    }  // Required empty public constructor

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            symbol = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        // Build the URI for this stock with its symbol
        mUri = makeUriForStock(symbol);

        chart = (LineChart) rootView.findViewById(R.id.stock_linechart);

        // Initialise the cursor loader
        getLoaderManager().initLoader(STOCK_LOADER, null, this);

        // Formats for share price, dollar change and percentage change
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

        // Set fraction digits of float numbers
        formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return rootView;
    }

    // Method for plotting a graph
    private void plotStock(ArrayList<String> date, ArrayList<Float> price, String stockSymbol) {
        ArrayList<String> xAxis = date;
        ArrayList<Entry> yAxis = new ArrayList<>();
        int numDataPoints = date.size();
        for (int i = 0; i < numDataPoints; i++) {
            float singlePrice = price.get(i);
            yAxis.add(new Entry(singlePrice, i));
        }
        String[] xaxes = new String[xAxis.size()];
        for (int i = 0; i < xAxis.size(); i++) {
            xaxes[i] = xAxis.get(i).toString();
        }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataset = new LineDataSet(yAxis, stockSymbol);
        lineDataset.setDrawCircles(false);
        lineDataset.setColor(Color.BLUE);

        lineDataSets.add(lineDataset);

        applyChartSettings(chart); // Apply settings to the chart

        chart.setData(new LineData(xaxes, lineDataSets));
        chart.invalidate();
    }

    // Apply settings to the chart
    private void applyChartSettings(LineChart lineChart) {
        int textColor = getResources().getColor(R.color.general_text_color);

        // Left axis
        lineChart.getAxisLeft().setTextColor(textColor);

        // Right axis
        lineChart.getAxisRight().setTextColor(textColor);

        // X axis
        lineChart.getXAxis().setTextColor(textColor);

        // Hide Legend
        lineChart.getLegend().setEnabled(false);

        // Remove Description label
        lineChart.setDescription("");

        // Allow scaling in both X and Y directions simultaneously
        lineChart.setPinchZoom(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    Quote.QUOTE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            mCompanyName.setText(data.getString(Quote.POSITION_NAME));
            mPrice.setText(dollarFormat.format(data.getFloat(Quote.POSITION_PRICE)));
            mAvgPrice.setText(dollarFormat.format(data.getFloat(Quote.POSITION_AVERAGE_PRICE)));
            float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);

            mDollarChange.setText(dollarFormatWithPlus.format(rawAbsoluteChange));
            mPercentageChange.setText(percentageFormat.format(data.getFloat(Quote.POSITION_PERCENTAGE_CHANGE) / 100));

            if(rawAbsoluteChange > 0){
                mPercentageChange.setBackgroundResource(R.drawable.percent_change_pill_green);
            } else {
                mPercentageChange.setBackgroundResource(R.drawable.percent_change_pill_red);
            }

            // Daily price range
            float dayHigh = data.getFloat(Quote.POSITION_DAY_HIGH);
            float dayLow = data.getFloat(Quote.POSITION_DAY_LOW);

            String dayRange = String.format(getResources().getString(R.string.price_range),
                    formatter.format(dayLow), formatter.format(dayHigh));
            mDayRange.setText(dayRange);

            // Yearly price range
            float yearHigh = data.getFloat(Quote.POSITION_YEAR_HIGH);
            float yearLow = data.getFloat(Quote.POSITION_YEAR_LOW);
            String yearRange = String.format(getResources().getString(R.string.price_range),
                    formatter.format(yearLow), formatter.format(yearHigh));
            mYearRange.setText(yearRange);

            // Average Volume
            float volume = data.getFloat(Quote.POSITION_AVERAGE_VOLUME);
            float formattedVolume;
            if(volume > 1000000){ // When volume is bigger than 1 million
                formattedVolume = volume/1000000;
                volumeString = formatter.format(formattedVolume) + "m";
            } else if(volume > 1000) { // When volume is bigger than 1 thousand
                formattedVolume = volume/1000;
                volumeString = formatter.format(formattedVolume) + "k";
            } else { // When volume is lower than 1 thousand
                formattedVolume = Math.round(volume);
                volumeString = String.valueOf(formattedVolume);
            }
            mAverageVolume.setText(volumeString);

            String historicalDataRaw = data.getString(Quote.POSITION_HISTORY);
            ArrayList<String> historyDate = new ArrayList<>(); // Array data of historical dates
            ArrayList<String> formattedHistoryDate = new ArrayList<>(); // Array data of historical dates in a chronological order
            ArrayList<Float> historyPrice = new ArrayList<>(); // Array data of historical prices
            ArrayList<Float> formattedHistoryPrice = new ArrayList<>(); // Array data of historical prices in a chronological order
            if (null != historicalDataRaw) {
                String[] splitHistoricalData = historicalDataRaw.split("\\r?\\n"); // Split on new lines (.split("\\r\\n|\\n|\\r"))
                for (int i = 0; i < splitHistoricalData.length; i++) {
                    String[] a = splitHistoricalData[i].split(",");
                    int commaIndex = splitHistoricalData[i].indexOf(","); // Index of the comma
                    int endIndex = splitHistoricalData[i].length();
                    String dateString = splitHistoricalData[i].substring(0, commaIndex);
                    long dateLong = Long.parseLong(dateString);
                    // Change the date in milliseconds to a format of DD MMM YYYY
                    String formattedDate = utilities.formatDate(dateLong);

                    String priceString = splitHistoricalData[i].substring(commaIndex + 2, endIndex);
                    historyDate.add(formattedDate);
                    formattedHistoryDate = utilities.formatStringArrayList(historyDate);
                    historyPrice.add(Float.parseFloat(priceString));
                    formattedHistoryPrice = utilities.formatFloatArrayList(historyPrice);
                }
            }
            // Plot the graph
            plotStock(formattedHistoryDate, formattedHistoryPrice, symbol);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
