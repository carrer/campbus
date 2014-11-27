package com.carr3r.campbus.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carr3r.campbus.Definitions;
import com.carr3r.campbus.LineItem;
import com.carr3r.campbus.LineItemAdapter;
import com.carr3r.campbus.R;
import com.carr3r.campbus.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by carrer on 10/23/14.
 * Activity que exibe todas as linhas que passam por uma localidade.
 * Esta activity deve receber como parâmetro o nome da localidade via "extras", mapeada com a string "STREET",
 * assim como as LINHAS que atendem a localidade em "LINES"
 */
public class StreetInfo extends Activity {

    protected ListView listLines;
    private AlertDialog dialog;
    private Activity activity;
    private int locationY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
            Remove a barra de título e expanse o activity em fullscreen
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.street_info);

        activity = this;

        // Nome da rua
        ((TextView) findViewById(R.id.street_info_street_name)).setText(getIntent().getExtras().getString("STREET"));

        LineItemAdapter adapter = new LineItemAdapter(getApplicationContext(), new ArrayList<LineItem>());
        listLines = (ListView) findViewById(R.id.street_info_list_lines);

        listLines.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                locationY = (int) motionEvent.getRawY();
                return false;
            }
        });

        listLines.setAdapter(adapter);

        /*
            Define o evento de onClick do ListView onde são listadas as linhas que atendem a localidade
         */
        listLines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                int[] tmpCoordinate = new int[2];

                int[] withoutDirection = new int[2];
                int[] directionOne = new int[2];
                int[] directionTwo = new int[2];

                // Title panel
                view.findViewById(R.id.line_row_line_top_view).getLocationOnScreen(tmpCoordinate);
                withoutDirection[0] = tmpCoordinate[1];
                view.findViewById(R.id.line_row_way1).getLocationOnScreen(tmpCoordinate);
                withoutDirection[1] = tmpCoordinate[1];

                // Way 1 panel
                view.findViewById(R.id.line_row_way1).getLocationOnScreen(tmpCoordinate);
                directionOne[0] = tmpCoordinate[1];
                view.findViewById(R.id.line_row_way2).getLocationOnScreen(tmpCoordinate);
                directionOne[1] = tmpCoordinate[1];

                // Way 2 panel
                view.findViewById(R.id.line_row_way2).getLocationOnScreen(tmpCoordinate);
                directionTwo[0] = tmpCoordinate[1];
                directionTwo[1] = directionTwo[0] + view.findViewById(R.id.line_row_way2).getHeight();

                final String lineNumber = ((TextView) view.findViewById(R.id.line_row_line_number)).getText().toString();

                if (locationY >= withoutDirection[0] && locationY <= withoutDirection[1]) {
                    LineItem clickedItem = (LineItem) parent.getAdapter().getItem(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    LayoutInflater li = getLayoutInflater();
                    View dialogView = li.inflate(R.layout.way_dialog, null);

                    TextView way1 = (TextView) dialogView.findViewById(R.id.way_dialog_direction1);
                    TextView way2 = (TextView) dialogView.findViewById(R.id.way_dialog_direction2);
                    TextView board1 = (TextView) dialogView.findViewById(R.id.way_dialog_board1);
                    TextView board2 = (TextView) dialogView.findViewById(R.id.way_dialog_board2);

                    LinearLayout grp1 = (LinearLayout) dialogView.findViewById(R.id.way_dialog_way1);
                    RelativeLayout grp2 = (RelativeLayout) dialogView.findViewById(R.id.way_dialog_way2);

                    way1.setText(clickedItem.getDirections()[0]);
                    way2.setText(clickedItem.getDirections()[1]);
                    board1.setText(clickedItem.getBoards()[0]);
                    board2.setText(clickedItem.getBoards()[1]);

                    grp1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View arg0) {

                            Intent lineInfo = new Intent(StreetInfo.this, LineInfo.class);
                            lineInfo.putExtra("NUMBER", lineNumber);
                            lineInfo.putExtra("WAY", "1");
                            startActivity(lineInfo);
                            overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);

                            dialog.dismiss();
                        }
                    });

                    grp2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View arg0) {

                            Intent lineInfo = new Intent(StreetInfo.this, LineInfo.class);
                            lineInfo.putExtra("NUMBER", lineNumber);
                            lineInfo.putExtra("WAY", "2");
                            startActivity(lineInfo);
                            overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);

                            dialog.dismiss();
                        }
                    });

                    builder.setView(dialogView);
                    dialog = builder.create();
                    dialog.show();
                } else if (locationY >= directionOne[0] && locationY <= directionOne[1]) {
                    Intent lineInfo = new Intent(StreetInfo.this, LineInfo.class);
                    lineInfo.putExtra("NUMBER", lineNumber);
                    lineInfo.putExtra("WAY", "1");
                    startActivity(lineInfo);
                    overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);
                } else if (locationY >= directionTwo[0] && locationY <= directionTwo[1]) {
                    Intent lineInfo = new Intent(StreetInfo.this, LineInfo.class);
                    lineInfo.putExtra("NUMBER", lineNumber);
                    lineInfo.putExtra("WAY", "2");
                    startActivity(lineInfo);
                    overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);

                }

            }
        });

        /*
            Recebe a lista de linhas que atendem a localidade por parâmetro (uma vez que estas informações
            já foram carregadas na memória pela Activity anterior)
         */
        JSONArray lines;
        try {
            lines = new JSONArray(getIntent().getExtras().getString("LINES"));
        } catch (JSONException e) {
            lines = new JSONArray();
        }

        for (int i = 0; i < lines.length(); i++) {
            String lineNumber;
            JSONObject obj;
            String[] directions = new String[2];
            String[] boards = new String[2];
            String line;

            try {
                lineNumber = lines.getString(i);

                if (lineNumber.length()==3)
                    lineNumber += "-0";

                obj = new JSONObject(Utils.loadJSONFromAsset(this, lineNumber + ".json"));
                line = obj.getString("linha");
                line = line.substring(line.lastIndexOf("-") + 1).trim();
                directions[0] = obj.getJSONObject("ida").getString("sentido");
                directions[1] = obj.getJSONObject("volta").getString("sentido");
                boards[0] = obj.getJSONObject("ida").getString("letreiro");
                boards[1] = obj.getJSONObject("volta").getString("letreiro");
            } catch (JSONException e) {
                // Melhorar isso com Locale
                lineNumber = "???";
                line = "Desconhecido";
                boards[0] = boards[1] = "Desconhecido";
                directions[0] = directions[1] = "Desconhecido";
            }

            adapter.add(new LineItem(lineNumber, line, directions, boards));
        }


        // advertising pra pagar minha cerveja ;)
        AdView adView = (AdView) this.findViewById(R.id.street_info_adview);
        AdRequest adRequest;
        if (Definitions.DEBUG)
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                    .addTestDevice("2EAB96D84FE62876379A9C030AA6A0AC") // Nexus 5
                    .build();
        else
            adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void onBackPressed() {
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.right_slide_out);
    }


}
