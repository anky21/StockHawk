package com.udacity.stockhawk.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    public static final String AUTHORITY = "com.udacity.stockhawk";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_QUOTE = "quote";
    public static final String PATH_QUOTE_WITH_SYMBOL = "quote/*";

    private Contract(){}

    public static final class Quote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_QUOTE).build();

        public static final String TABLE_NAME = "quotes";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_ABSOLUTE_CHANGE = "absolute_change";
        public static final String COLUMN_PERCENTAGE_CHANGE = "percentage_change";
        public static final String COLUMN_HISTORY = "history";
        public static final String COLUMN_AVERAGE_VOLUME = "average_volume";
        public static final String COLUMN_DAY_HIGH = "day_high";
        public static final String COLUMN_DAY_LOW = "day_low";
        public static final String COLUMN_AVERAGE_PRICE = "avg_price";
        public static final String COLUMN_YEAR_HIGH = "year_high";
        public static final String COLUMN_YEAR_LOW = "year_low";
        public static final String COLUMN_COMPANY_NAME = "name";

        public static final int POSITION_ID = 0;
        public static final int POSITION_SYMBOL = 1;
        public static final int POSITION_PRICE = 2;
        public static final int POSITION_ABSOLUTE_CHANGE = 3;
        public static final int POSITION_PERCENTAGE_CHANGE = 4;
        public static final int POSITION_HISTORY = 5;
        public static final int POSITION_AVERAGE_VOLUME = 6;
        public static final int POSITION_DAY_HIGH = 7;
        public static final int POSITION_DAY_LOW = 8;
        public static final int POSITION_AVERAGE_PRICE = 9;
        public static final int POSITION_YEAR_HIGH = 10;
        public static final int POSITION_YEAR_LOW = 11;
        public static final int POSITION_NAME = 12;

        public static final String[] QUOTE_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_PRICE,
                COLUMN_ABSOLUTE_CHANGE,
                COLUMN_PERCENTAGE_CHANGE,
                COLUMN_HISTORY,
                COLUMN_AVERAGE_VOLUME,
                COLUMN_DAY_HIGH,
                COLUMN_DAY_LOW,
                COLUMN_AVERAGE_PRICE,
                COLUMN_YEAR_HIGH,
                COLUMN_YEAR_LOW,
                COLUMN_COMPANY_NAME
        };

        public static Uri makeUriForStock(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getStockFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }


    }

}
