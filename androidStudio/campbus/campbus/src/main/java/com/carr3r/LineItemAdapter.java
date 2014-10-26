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
public class LineItemAdapter extends ArrayAdapter<LineItem> {

    private final Context context;
    private final ArrayList<LineItem> itemsArrayList;
    private int[] colors = new int[] {Color.rgb (175, 203, 219), Color.WHITE };

    public LineItemAdapter(Context context, ArrayList<LineItem> itemsArrayList) {

        super(context, R.layout.line_row, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.line_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView nameView = (TextView) rowView.findViewById(R.id.line_row_line_name);
        TextView numberView = (TextView) rowView.findViewById(R.id.line_row_line_number);
        TextView board1View = (TextView) rowView.findViewById(R.id.line_row_board1);
        TextView board2View = (TextView) rowView.findViewById(R.id.line_row_board2);

        nameView.setText(itemsArrayList.get(position).getTitle());
        numberView.setText(itemsArrayList.get(position).getNumber());
        board1View.setText(itemsArrayList.get(position).getBoards()[0]);
        board2View.setText(itemsArrayList.get(position).getBoards()[1]);

        int colorPos = 1;//position % colors.length;
        rowView.setBackgroundColor(colors[colorPos]);

        // 5. retrn rowView
        return rowView;
    }
}
