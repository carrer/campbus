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
 */
public class StreetItemAdapter extends ArrayAdapter<StreetItem> {

    private final Context context;
    private final ArrayList<StreetItem> itemsArrayList;
    private int[] colors = new int[] {Color.rgb (175, 203, 219), Color.WHITE };

    public StreetItemAdapter(Context context, ArrayList<StreetItem> itemsArrayList) {

        super(context, R.layout.street_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.street_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = (TextView) rowView.findViewById(R.id.label);

        // 4. Set the text for textView
        labelView.setText(itemsArrayList.get(position).getTitle());

        int colorPos = position % colors.length;
        rowView.setBackgroundColor(colors[colorPos]);

        // 5. retrn rowView
        return rowView;
    }
}
