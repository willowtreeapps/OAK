package oak.demo.widget;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.widget.spreadsheetview.SpreadsheetRow;
import oak.widget.spreadsheetview.SpreadsheetView;


public class SpreadsheetViewActivity extends OakDemoActivity {


    SpreadsheetView table;
    final static int NUM_OBJECTS = 100;
    final static int NUM_VALUES = 100;

    final static float FOOTER_HEIGHT = 75;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sticky_spreadsheet_demo);


        table = (SpreadsheetView) findViewById(R.id.spreadsheet_table);


        //createData();
        setup();
        table.setNumberStickyColumns(1);





        Button incStickyButton = (Button) findViewById(R.id.spreadsheet_inc_sticky);
        incStickyButton.setText("+");
        Button decStickyButton = (Button) findViewById(R.id.spreadsheet_dec_sticky);
        decStickyButton.setText("-");



        incStickyButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                table.setNumberStickyColumns(table.getNumberStickyColumns() + 1);
            }
        });

        decStickyButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                table.setNumberStickyColumns(table.getNumberStickyColumns() - 1);
            }
        });


        Button hideFooterButton = (Button) findViewById(R.id.spreadsheet_hide_footer);

        hideFooterButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (table.getStickyFooterHeight()==0f){
                    table.setStickyFooterHeight(FOOTER_HEIGHT);
                    ((Button) v).setText("Hide Footer");
                } else{
                    table.setStickyFooterHeight(0f);
                    ((Button) v).setText("Show Footer");

                }
            }
        });


    }

    private void setup(){

            SpreadsheetRow[] objects = new SpreadsheetRow[NUM_OBJECTS];
            for (int i = 0; i < NUM_OBJECTS; i++){
                String[] values = new String[NUM_VALUES];
                values[0] = "object "+(i+1);
                for (int j = 1; j < NUM_VALUES; j++){
                    values[j] = "value "+j;
                }
                objects[i] = new SpreadsheetRow(values);
            }

            String[] headers = new String[NUM_VALUES];
            String[] footers = new String[NUM_VALUES];

            headers[0] = "Name";
            footers[0] = "";

            for (int j = 1; j <NUM_VALUES;j++){
                headers[j] = "header "+j;
                footers[j] = "footer "+j;
            }

            table.setData(objects, headers);
            table.setFooters(footers);


        table.setOnHeaderClickListener(new SpreadsheetView.OnHeaderClickListener(){
        @Override
        public void headerClick(int valueIndex) {
            if (valueIndex < table.getNumberStickyColumns()){
                Toast.makeText(table.getContext(), "Clicked Sticky Header " + table.getHeaderAt(valueIndex), Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(table.getContext(),"Clicked Header " + table.getHeaderAt(valueIndex),Toast.LENGTH_SHORT).show();
            }
        }

        });


        table.setOnFooterClickListener(new SpreadsheetView.OnFooterClickListener(){

            @Override
            public void footerClick(int valueIndex) {
                if (valueIndex < table.getNumberStickyColumns()){
                    Toast.makeText(table.getContext(),"Clicked Sticky Footer " + table.getFooterAt(valueIndex),Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(table.getContext(),"Clicked Footer " + table.getFooterAt(valueIndex),Toast.LENGTH_SHORT).show();
                }
            }
        });


        table.setOnCellClickListener(new SpreadsheetView.OnCellClickListener(){

            @Override
            public void cellClick(int objectIndex, int valueIndex) {
                if (valueIndex < table.getNumberStickyColumns()){
                    Toast.makeText(table.getContext(),"Clicked Sticky Cell " + table.getHeaderAt(valueIndex) + ": "
                            +table.getRowAt(objectIndex).getValueAt(valueIndex),Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(table.getContext(),"Clicked Cell " + table.getHeaderAt(valueIndex) + ": "
                            +table.getRowAt(objectIndex).getValueAt(valueIndex),Toast.LENGTH_SHORT).show();
                }

            }

        });


    }


}
