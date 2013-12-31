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

    SpreadsheetView mSpreadsheetView;

    final static int NUM_OBJECTS = 100;
    final static int NUM_VALUES = 100;
    private boolean[] headerSelected;

    final static float FOOTER_HEIGHT = 75;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sticky_spreadsheet_demo);

        headerSelected = new boolean[NUM_VALUES];
        mSpreadsheetView = (SpreadsheetView) findViewById(R.id.spreadsheet_table);

        //createData();
        setup();
        mSpreadsheetView.setNumberStickyColumns(1);

        Button incStickyButton = (Button) findViewById(R.id.spreadsheet_inc_sticky);
        incStickyButton.setText("+");
        Button decStickyButton = (Button) findViewById(R.id.spreadsheet_dec_sticky);
        decStickyButton.setText("-");

        incStickyButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSpreadsheetView.setNumberStickyColumns(mSpreadsheetView.getNumberStickyColumns() + 1);
            }
        });

        decStickyButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSpreadsheetView.setNumberStickyColumns(mSpreadsheetView.getNumberStickyColumns() - 1);
            }
        });

        Button hideFooterButton = (Button) findViewById(R.id.spreadsheet_hide_footer);

        hideFooterButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSpreadsheetView.getStickyFooterHeight() == 0f) {
                    mSpreadsheetView.setStickyFooterHeight(FOOTER_HEIGHT);
                    ((Button) v).setText("Hide Footer");
                } else {
                    mSpreadsheetView.setStickyFooterHeight(0f);
                    ((Button) v).setText("Show Footer");

                }
            }
        });
    }

    private void setup() {
        SpreadsheetRow[] objects = new SpreadsheetRow[NUM_OBJECTS];
        for (int i = 0; i < NUM_OBJECTS; i++) {
            String[] values = new String[NUM_VALUES];
            values[0] = "object " + (i + 1);
            for (int j = 1; j < NUM_VALUES; j++) {
                values[j] = "value " + j;
            }
            objects[i] = new SpreadsheetRow(values);
        }

        String[] headers = new String[NUM_VALUES];
        String[] footers = new String[NUM_VALUES];

        headers[0] = "Name";
        footers[0] = "";

        for (int j = 1; j < NUM_VALUES; j++) {
            headers[j] = "header " + j;
            footers[j] = "footer " + j;
        }

        mSpreadsheetView.setData(objects, headers);
        mSpreadsheetView.setFooters(footers);

        mSpreadsheetView.setOnHeaderClickListener(new SpreadsheetView.OnHeaderClickListener() {
            @Override
            public void headerClick(int valueIndex) {

                mSpreadsheetView.selectColumn(valueIndex, !headerSelected[valueIndex]);
                headerSelected[valueIndex] = !headerSelected[valueIndex];

                if (valueIndex < mSpreadsheetView.getNumberStickyColumns()) {
                    Toast.makeText(mSpreadsheetView.getContext(),
                            "Clicked Sticky Header " + mSpreadsheetView.getHeaderAt(valueIndex),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mSpreadsheetView.getContext(),
                            "Clicked Header " + mSpreadsheetView.getHeaderAt(valueIndex),
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

        mSpreadsheetView.setOnFooterClickListener(new SpreadsheetView.OnFooterClickListener() {
            @Override
            public void footerClick(int valueIndex) {
                if (valueIndex < mSpreadsheetView.getNumberStickyColumns()) {
                    Toast.makeText(mSpreadsheetView.getContext(),
                            "Clicked Sticky Footer " + mSpreadsheetView.getFooterAt(valueIndex),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mSpreadsheetView.getContext(),
                            "Clicked Footer " + mSpreadsheetView.getFooterAt(valueIndex),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSpreadsheetView.setOnCellClickListener(new SpreadsheetView.OnCellClickListener() {
            @Override
            public void cellClick(int objectIndex, int valueIndex) {

                if (valueIndex<mSpreadsheetView.getNumberStickyColumns()){
                    mSpreadsheetView.selectRow(objectIndex, !mSpreadsheetView.isSelected(objectIndex, valueIndex));
                }

                if (valueIndex < mSpreadsheetView.getNumberStickyColumns()) {
                    Toast.makeText(mSpreadsheetView.getContext(),
                            "Clicked Sticky Cell " + mSpreadsheetView.getHeaderAt(valueIndex) + ": "
                                    + mSpreadsheetView.getRowAt(objectIndex).getValueAt(valueIndex), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(
                            mSpreadsheetView.getContext(),
                            "Clicked Cell " + mSpreadsheetView.getHeaderAt(valueIndex) + ": "
                                    + mSpreadsheetView.getRowAt(objectIndex).getValueAt(valueIndex), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}
