package com.carr3r;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by carrer on 10/23/14.
 * Extensor do ArrayAdapter para customização de ListViews (layout definido em street_row.xml)
 */
public class StreetItemAdapter extends ArrayAdapter<StreetItem> {

    private final Context context;
    private final ArrayList<StreetItem> itemsArrayList;

    /*
        Para coloração diferenciada de linhas ímpares e pares
     */
    private int[] BackgroundColors = new int[] {Color.rgb (33, 70, 92), Color.WHITE };
    private int[] ForegroundColors = new int[] {Color.WHITE, Color.BLACK };

    public StreetItemAdapter(Context context, ArrayList<StreetItem> itemsArrayList) {

        super(context, R.layout.street_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.street_row, parent, false);

        TextView labelView = (TextView) rowView.findViewById(R.id.street_row_street_name);
        labelView.setText(itemsArrayList.get(position).getTitle());

        int colorPos = position % 2;
        rowView.setBackgroundColor(BackgroundColors[colorPos]);
        labelView.setTextColor(ForegroundColors[colorPos]);

        return rowView;
    }
}
