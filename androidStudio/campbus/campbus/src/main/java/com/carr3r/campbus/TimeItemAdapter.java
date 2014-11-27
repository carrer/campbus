package com.carr3r.campbus;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import com.carr3r.campbus.R;

/**
 * Created by carrer on 10/23/14.
 * Extensor do ArrayAdapter para customização de GridViews (layout definido em time.xml)
 */
public class TimeItemAdapter extends ArrayAdapter<TimeItem> {

    private final Context context;
    private final ArrayList<TimeItem> itemsArrayList;
    private final int highlight = Color.rgb(232, 7, 71);
    private int dayOfWeek = 0;
    public static final int WEEKDAY = 1;
    public static final int SATURDAY = 2;
    public static final int SUNDAY = 3;


    private static boolean nextTime = false;
    private static int now;
    public static int highlightNext = Utils.dayOfWeek() >= Calendar.MONDAY && Utils.dayOfWeek() <= Calendar.FRIDAY ? WEEKDAY : ( Utils.dayOfWeek() == Calendar.SATURDAY ? SATURDAY : SUNDAY );

    public static void resetCounter()
    {
        nextTime = false;
        Date dNow = new Date( );
        now = dNow.getHours()*60 + dNow.getMinutes();
    }

    public TimeItemAdapter(Context context, ArrayList<TimeItem> itemsArrayList) {

        super(context, R.layout.line_row, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // expande o contexto pai para caber a view descrita no layout time.xml
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.time, parent, false);

        TextView titleView = (TextView) rowView.findViewById(R.id.time_time);
        String time = itemsArrayList.get(position).getTitle();
        titleView.setText(time);

        int thisTime;
        try
        {
            thisTime =  Integer.valueOf(time.substring(0, time.indexOf(":")))*60 +
                    Integer.valueOf(time.substring(time.indexOf(":")+1));
        } catch(Exception e)
        {
            thisTime = 0;
        }

        if (highlightNext == dayOfWeek && thisTime > now && !TimeItemAdapter.nextTime)
        {
            TimeItemAdapter.nextTime = true;
            titleView.setBackgroundColor(highlight);
            titleView.setTextColor(Color.WHITE);
        }

        return rowView;
    }

    public void setDayOfWeek(int i)
    {
        dayOfWeek = i;
    }
}
