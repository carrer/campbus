package com.carr3r.campbus;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.carr3r.campbus.R;

/**
 * Created by carrer on 10/23/14.
 * Extensor do ArrayAdapter para customização de ListViews (layout definido em line_row.xml)
 */
public class LineItemAdapter extends ArrayAdapter<LineItem> {

    private final Context context;
    private final ArrayList<LineItem> itemsArrayList;
    /*
        Cores de fundo (linha par utiliza colors[0] de fundo; linha impar colors[1];)
     */
    private int[] colors = new int[] {Color.rgb (175, 203, 219), Color.WHITE };

    public LineItemAdapter(Context context, ArrayList<LineItem> itemsArrayList) {

        super(context, R.layout.line_row, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // expande o contexto pai para caber a view descrita no layout line_row.xml
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.line_row, parent, false);

        // identifica os objetos gráficos do layout referente aos mapeados na classe LineInfo
        TextView nameView = (TextView) rowView.findViewById(R.id.line_row_line_name);
        TextView numberView = (TextView) rowView.findViewById(R.id.line_row_line_number);
        TextView board1View = (TextView) rowView.findViewById(R.id.line_row_board1);
        TextView board2View = (TextView) rowView.findViewById(R.id.line_row_board2);
        TextView direction1View = (TextView) rowView.findViewById(R.id.line_row_direction1);
        TextView direction2View = (TextView) rowView.findViewById(R.id.line_row_direction2);

        nameView.setText(itemsArrayList.get(position).getTitle());
        numberView.setText(itemsArrayList.get(position).getNumber());
        direction1View.setText(itemsArrayList.get(position).getDirections()[0]);
        direction2View.setText(itemsArrayList.get(position).getDirections()[1]);
        board1View.setText(itemsArrayList.get(position).getBoards()[0]);
        board2View.setText(itemsArrayList.get(position).getBoards()[1]);

        int colorPos = 1;//position % colors.length;
        rowView.setBackgroundColor(colors[colorPos]);

        return rowView;
    }
}
