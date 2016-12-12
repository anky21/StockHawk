package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;

import java.util.ArrayList;

/**
 * Created by anky_ on 11/12/2016.
 */

public class DetailFragment extends Fragment {
    private LineChart chart;

    public DetailFragment() {}  // Required empty public constructor

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            String symbol = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        chart = (LineChart)rootview.findViewById(R.id.stock_linechart);
        // Plot the graph
        plotStock();

        return rootview;
    }

    // Method for plotting a graph
    private void plotStock(){
        ArrayList<String> xAXES = new ArrayList<>();
        ArrayList<Entry> yAXEScos = new ArrayList<>();
        double x = 0 ;
        int numDataPoints = 1000;
        for(int i=0;i<numDataPoints;i++){
            float cosFunction = Float.parseFloat(String.valueOf(Math.cos(x)));
            x = x + 0.1;
            yAXEScos.add(new Entry(cosFunction,i));
            xAXES.add(i, String.valueOf(x));
        }
        String[] xaxes = new String[xAXES.size()];
        for(int i=0; i<xAXES.size();i++){
            xaxes[i] = xAXES.get(i).toString();
        }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        LineDataSet lineDataSet1 = new LineDataSet(yAXEScos,"cos");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.BLUE);

        lineDataSets.add(lineDataSet1);

        chart.setData(new LineData(xaxes,lineDataSets));

        chart.setVisibleXRangeMaximum(1000f);
    }
}
