package oak.widget.spreadsheetview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import oak.OAK;
import oak.util.OakUtils;

import java.util.Arrays;
import java.util.Comparator;


public class SpreadsheetView extends View implements GestureDetector.OnGestureListener {


    private GestureDetector gestureDetector;

    private Scroller scroller;

    private OnHeaderClickListener headerListener;

    private OnFooterClickListener footerListener;

    private OnCellClickListener cellListener;

    private OnFooterChangedListener footerChangedListener;



    private final static int SCALE = -2;

    private float stickyTableWidth;
    private float stickyTableHeight;

    private float stickyFooterHeight;
    private float stickyHeaderHeight;
    private float stickyColumnWidth;
    private float dataCellWidth;
    private float dataCellHeight;

    private boolean dataSet;

    private final float DEFAULT_CELL_WIDTH = 150;
    private final float DEFAULT_CELL_HEIGHT = 150;
    private final float DEFAULT_STICKY_FOOTER_HEIGHT = 0;
    private final float DEFAULT_STICKY_HEADER_HEIGHT= 100;
    private final float DEFAULT_STICKY_COLUMN_WIDTH =150;
    private final float DEFAULT_HORIZONTAL_BORDER_WIDTH = 2;
    private final int DEFAULT_TEXT_SIZE = 32;

    private float activeDataWindowWidth;
    private float activeDataWindowHeight;

    private float windowScrollX;
    private float windowScrollY;

    private float maxWindowScrollX;
    private float maxWindowScrollY;

    private int dataSizeObjects;
    private int dataSizeValues;

    protected SpreadsheetRow[] objectData;
    protected String[] headers;
    private boolean[] headerSelected;
    private boolean[] footerSelected;
    protected int[] sorted;
    public final static int SORTED_UNSORTED = 0;
    public final static int SORTED_ASCENDING = 1;
    public final static int SORTED_DESCENDING = 2;
    protected String[] footers;

    private float[] stickyColumnWidths;
    private int numberStickyColumns;
    private int targetNumberStickyColumns;

    private Paint vertDividingPaint;
    private Paint horzDividingPaint;

    private SpreadsheetCell dataCell;
    private SpreadsheetCell stickyHeaderCell;
    private SpreadsheetCell stickyFooterCell;
    private SpreadsheetCell stickyColumnCell;

    private SpreadsheetCell leftFootCornerCell;
    private SpreadsheetCell leftHeadCornerCell;





    public SpreadsheetView(Context context){
        super(context);
        init(context);
        defaultCells();
        defaultDimensions();
    }


    public SpreadsheetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        defaultCells();
        defaultDimensions();
        parseAttributes(attrs);
    }

    public SpreadsheetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        defaultCells();
        defaultDimensions();
        parseAttributes(attrs);

    }

    private void parseAttributes(AttributeSet attrs){
        if (attrs!=null){
            int dataCellDrawable = attrs.getAttributeResourceValue(OAK.XMLNS,"dataCellDrawable",0);
            if (dataCellDrawable!=0){
                dataCell.setDrawable(getContext().getResources().getDrawable(dataCellDrawable));
            }
            int dataCellSelectedDrawable = attrs.getAttributeResourceValue(OAK.XMLNS,"dataCellSelectedDrawable",0);
            if (dataCellSelectedDrawable!=0){
                dataCell.setSelectedDrawable(getContext().getResources().getDrawable(dataCellSelectedDrawable));
            }

            int stickyColumnCellDrawable = attrs.getAttributeResourceValue(OAK.XMLNS,"stickyColumnCellDrawable",0);
            if (stickyColumnCellDrawable!=0){
                stickyColumnCell.setDrawable(getContext().getResources().getDrawable(stickyColumnCellDrawable));
            }
            int stickyColumnCellSelectedDrawable =
                    attrs.getAttributeResourceValue(OAK.XMLNS,"stickyColumnCellSelectedDrawable",0);
            if (stickyColumnCellSelectedDrawable!=0){
                stickyColumnCell.setSelectedDrawable(getContext().getResources().getDrawable(stickyColumnCellSelectedDrawable));
            }

            int stickyHeaderCellDrawable = attrs.getAttributeResourceValue(OAK.XMLNS,"stickyHeaderCellDrawable",0);
            if (stickyHeaderCellDrawable!=0){
                stickyHeaderCell.setDrawable(getContext().getResources().getDrawable(stickyHeaderCellDrawable));
               leftHeadCornerCell.setDrawable(getContext().getResources().getDrawable(stickyHeaderCellDrawable));

            }
            int stickyHeaderCellSelectedDrawable =
                    attrs.getAttributeResourceValue(OAK.XMLNS,"stickyHeaderCellSelectedDrawable",0);
            if (stickyHeaderCellSelectedDrawable!=0){
                stickyHeaderCell.setSelectedDrawable(getContext().getResources().getDrawable(stickyHeaderCellSelectedDrawable));
                leftHeadCornerCell.setSelectedDrawable(getContext().getResources().getDrawable(stickyHeaderCellSelectedDrawable));

            }

            int stickyFooterCellDrawable = attrs.getAttributeResourceValue(OAK.XMLNS,"stickyFooterCellDrawable",0);
            if (stickyFooterCellDrawable!=0){
                stickyFooterCell.setDrawable(getContext().getResources().getDrawable(stickyFooterCellDrawable));
                leftFootCornerCell.setDrawable(getContext().getResources().getDrawable(stickyFooterCellDrawable));

            }
            int stickyFooterCellSelectedDrawable =
                    attrs.getAttributeResourceValue(OAK.XMLNS,"stickyFooterCellSelectedDrawable",0);
            if (stickyFooterCellSelectedDrawable!=0){
                stickyFooterCell.setSelectedDrawable(getContext().getResources().getDrawable(stickyFooterCellSelectedDrawable));
                leftFootCornerCell.setSelectedDrawable(getContext().getResources().getDrawable(stickyFooterCellSelectedDrawable));
            }

            int leftHeaderCornerCellDrawable =
                    attrs.getAttributeResourceValue(OAK.XMLNS,"leftHeaderCornerCellDrawable",0);
            if (leftHeaderCornerCellDrawable!=0){
                leftHeadCornerCell.setDrawable(getContext().getResources().getDrawable(leftHeaderCornerCellDrawable));
            }
            int leftHeaderCornerCellSelectedDrawable =
                    attrs.getAttributeResourceValue(OAK.XMLNS,"leftHeaderCornerCellSelectedDrawable",0);
            if (leftHeaderCornerCellSelectedDrawable!=0){
                leftHeadCornerCell.setSelectedDrawable(getContext().getResources().getDrawable(leftHeaderCornerCellSelectedDrawable));
            }

            int leftFooterCornerCellDrawable = attrs.getAttributeResourceValue(OAK.XMLNS,"leftFooterCornerCellDrawable",0);
            if (leftFooterCornerCellDrawable!=0){
                leftFootCornerCell.setDrawable(getContext().getResources().getDrawable(leftFooterCornerCellDrawable));
            }
            int leftFooterCornerCellSelectedDrawable =
                    attrs.getAttributeResourceValue(OAK.XMLNS,"leftFooterCornerCellSelectedDrawable",0);
            if (leftFooterCornerCellSelectedDrawable!=0){
                leftFootCornerCell.setSelectedDrawable(getContext().getResources().getDrawable(leftFooterCornerCellSelectedDrawable));
            }

            int allTextColor = attrs.getAttributeIntValue(OAK.XMLNS,"cellTextColor",-1);
            if (allTextColor!=-1){
                setAllCellsTextColor(allTextColor);
            }

            int allTextColorRes = attrs.getAttributeResourceValue(OAK.XMLNS,"cellTextColor",-1);
            if(allTextColorRes!=-1){
                setAllCellsTextColor(getContext().getResources().getColor(allTextColorRes));
            }

            int allTextSelectedColor = attrs.getAttributeIntValue(OAK.XMLNS,"cellSelectedTextColor",-1);
            if (allTextSelectedColor!=-1){
                setAllCellsSelectedTextColor(allTextSelectedColor);
            }

            int allTextSelectedColorRes = attrs.getAttributeResourceValue(OAK.XMLNS, "cellSelectedTextColor", -1);
            if (allTextSelectedColorRes!=-1){
                setAllCellsSelectedTextColor(getContext().getResources().getColor(allTextSelectedColorRes));
            }

            int dataCellTextColor = attrs.getAttributeIntValue(OAK.XMLNS,"dataCellTextColor",-1);
            if (dataCellTextColor!=-1){
                dataCell.setTextColor(dataCellTextColor);
            }
            int dataCellTextColorRes = attrs.getAttributeResourceValue(OAK.XMLNS,"dataCellTextColor",-1);
            if (dataCellTextColorRes!=-1){
                dataCell.setTextColor(getContext().getResources().getColor(dataCellTextColorRes));
            }
            int dataCellSelectedTextColor = attrs.getAttributeIntValue(OAK.XMLNS,"dataCellSelectedTextColor",-1);
            if (dataCellSelectedTextColor!=-1){
                dataCell.setSelectedTextColor(dataCellSelectedTextColor);
            }
            int dataCellSelectedTextColorRes = attrs.getAttributeResourceValue(OAK.XMLNS,"dataCellSelectedTextColor",-1);
            if (dataCellSelectedTextColorRes!=-1){
                dataCell.setSelectedTextColor(getContext().getResources().getColor(dataCellSelectedTextColorRes));
            }

            int stickyColumnCellTextColor = attrs.getAttributeIntValue(OAK.XMLNS,"stickyColumnCellTextColor",-1);
            if (stickyColumnCellTextColor!=-1){
                stickyColumnCell.setTextColor(stickyColumnCellTextColor);
            }
            int stickyColumnCellTextColorRes = attrs.getAttributeResourceValue(OAK.XMLNS,"stickyColumnCellTextColor",-1);
            if (stickyColumnCellTextColorRes!=-1){
                stickyColumnCell.setTextColor(getContext().getResources().getColor(stickyColumnCellTextColorRes));
            }
            int stickyColumnCellSelectedTextColor = attrs.getAttributeIntValue(OAK.XMLNS,"stickyColumnCellSelectedTextColor",-1);
            if (stickyColumnCellSelectedTextColor!=-1){
                stickyColumnCell.setSelectedTextColor(stickyColumnCellSelectedTextColor);
            }
            int stickyColumnCellSelectedTextColorRes = attrs.getAttributeResourceValue(OAK.XMLNS,"stickyColumnCellSelectedTextColor",-1);
            if (stickyColumnCellSelectedTextColorRes!=-1){
                stickyColumnCell.setSelectedTextColor(getContext().getResources().getColor(stickyColumnCellSelectedTextColorRes));
            }

            int stickyHeaderCellTextColor = attrs.getAttributeIntValue(OAK.XMLNS,"stickyHeaderCellTextColor",-1);
            if (stickyHeaderCellTextColor!=-1){
                stickyHeaderCell.setTextColor(stickyHeaderCellTextColor);
                leftHeadCornerCell.setTextColor(stickyHeaderCellTextColor);

            }
            int stickyHeaderCellTextColorRes = attrs.getAttributeResourceValue(OAK.XMLNS,"stickyHeaderCellTextColor",-1);
            if (stickyHeaderCellTextColorRes!=-1){
                stickyHeaderCell.setTextColor(getContext().getResources().getColor(stickyHeaderCellTextColorRes));
                leftHeadCornerCell.setTextColor(getContext().getResources().getColor(stickyHeaderCellTextColorRes));

            }
            int stickyHeaderCellSelectedTextColor = attrs.getAttributeIntValue(OAK.XMLNS,"stickyHeaderCellSelectedTextColor",-1);
            if (stickyHeaderCellSelectedTextColor!=-1){
                stickyHeaderCell.setSelectedTextColor(stickyHeaderCellSelectedTextColor);
                leftHeadCornerCell.setSelectedTextColor(stickyHeaderCellSelectedTextColor);

            }
            int stickyHeaderCellSelectedTextColorRes = attrs.getAttributeResourceValue(OAK.XMLNS,"stickyHeaderCellSelectedTextColor",-1);
            if (stickyHeaderCellSelectedTextColorRes!=-1){
                stickyHeaderCell.setSelectedTextColor(getContext().getResources().getColor(stickyHeaderCellSelectedTextColorRes));
                leftHeadCornerCell.setSelectedTextColor(getContext().getResources().getColor(stickyHeaderCellSelectedTextColorRes));

            }

            int stickyFooterCellTextColor = attrs.getAttributeIntValue(OAK.XMLNS,"stickyFooterCellTextColor",-1);
            if (stickyFooterCellTextColor!=-1){
                stickyFooterCell.setTextColor(stickyFooterCellTextColor);
                leftFootCornerCell.setTextColor(stickyFooterCellTextColor);
            }
            int stickyFooterCellTextColorRes = attrs.getAttributeResourceValue(OAK.XMLNS,"stickyFooterCellTextColor",-1);
            if (stickyFooterCellTextColorRes!=-1){
                stickyFooterCell.setTextColor(getContext().getResources().getColor(stickyFooterCellTextColorRes));
                leftFootCornerCell.setTextColor(stickyFooterCellTextColor);
            }
            int stickyFooterCellSelectedTextColor = attrs.getAttributeIntValue(OAK.XMLNS,"stickyFooterCellSelectedTextColor",-1);
            if (stickyFooterCellSelectedTextColor!=-1){
                stickyFooterCell.setSelectedTextColor(stickyFooterCellSelectedTextColor);
                leftFootCornerCell.setSelectedTextColor(stickyFooterCellSelectedTextColor);
            }
            int stickyFooterCellSelectedTextColorRes = attrs.getAttributeResourceValue(OAK.XMLNS,"stickyFooterCellSelectedTextColor",-1);
            if (stickyFooterCellSelectedTextColorRes!=-1){
                stickyFooterCell.setSelectedTextColor(getContext().getResources().getColor(stickyFooterCellSelectedTextColorRes));
                leftFootCornerCell.setSelectedTextColor(getContext().getResources().getColor(stickyFooterCellSelectedTextColorRes));
            }

            int allTextSize = attrs.getAttributeIntValue(OAK.XMLNS,"cellTextSize",-1);
            if (allTextSize!=-1){
                setAllCellsTextSize(allTextSize);
            }

            String fontName = attrs.getAttributeValue(OAK.XMLNS, "cellFont");
            if (fontName != null) {
                setAllCellsTypeface(OakUtils.getStaticTypeFace(getContext(), fontName));
            }

            int verticalDividingLineWidth = attrs.getAttributeIntValue(OAK.XMLNS,"verticalDividingLineWidth",-1);
            if (verticalDividingLineWidth!=-1){
                getVerticalDividingPaint().setStrokeWidth(verticalDividingLineWidth);
            }

            int verticalDividingLineColor = attrs.getAttributeIntValue(OAK.XMLNS, "verticalDividingLineColor", -1);
            if (verticalDividingLineColor!=-1){
                getVerticalDividingPaint().setColor(verticalDividingLineColor);
            }

            int horizontalDividingLineWidth = attrs.getAttributeIntValue(OAK.XMLNS,"horizontalDividingLineWidth",-1);
            if (horizontalDividingLineWidth!=-1){
                getHorizontalDividingPaint().setStrokeWidth(horizontalDividingLineWidth);
            }
            int horizontalDividingLineColor = attrs.getAttributeIntValue(OAK.XMLNS,"horizontalDividingLineColor",-1);
            if (horizontalDividingLineColor!=-1){
                getHorizontalDividingPaint().setColor(horizontalDividingLineColor);
            }


            boolean showFooter = attrs.getAttributeBooleanValue(OAK.XMLNS, "showFooter", true);
            int footerHeight = attrs.getAttributeIntValue(OAK.XMLNS, "footerHeight", -1);
            if (showFooter==false){
                setStickyFooterHeight(0);
            } else if (footerHeight!=-1){
                setStickyFooterHeight(footerHeight);
           }

            int headerHeight = attrs.getAttributeIntValue(OAK.XMLNS,"headerHeight",-1);
            if (headerHeight!=-1){
                setStickyHeaderHeight(headerHeight);
            }


            float dataCellWidth = attrs.getAttributeIntValue(OAK.XMLNS,"cellWidth",-1);
            if (dataCellWidth==-1){
                dataCellWidth = DEFAULT_CELL_WIDTH;
            }

            float dataCellHeight = attrs.getAttributeIntValue(OAK.XMLNS,"cellHeight",-1);
            if (dataCellHeight==-1){
                dataCellHeight = DEFAULT_CELL_HEIGHT;
            }

            setDataCellDimensions(dataCellWidth,dataCellHeight);

            int numStickyCol = attrs.getAttributeIntValue(OAK.XMLNS,"stickyColumns",-1);
            if (numStickyCol!=-1){
                setNumberStickyColumns(numStickyCol);
            }

        }
    }

    private void defaultCells(){
        Paint stickyColumnPaint = new Paint();
        stickyColumnPaint.setColor(Color.DKGRAY);
        stickyColumnPaint.setStyle(Paint.Style.FILL);
        Paint stickyColumnTextPaint = new Paint();
        stickyColumnTextPaint.setTextAlign(Paint.Align.CENTER);
        stickyColumnTextPaint.setStyle(Paint.Style.FILL);
        stickyColumnTextPaint.setColor(Color.WHITE);
        Paint stickyColumnBorderPaint = new Paint();
        stickyColumnBorderPaint.setStyle(Paint.Style.FILL);
        stickyColumnBorderPaint.setColor(Color.BLACK);
        Paint stickyHeaderPaint = new Paint(stickyColumnPaint);
        Paint stickyHeaderTextPaint = new Paint(stickyColumnTextPaint);
        Paint stickyHeaderBorderPaint = new Paint(stickyColumnBorderPaint);
        Paint stickyFooterPaint = new Paint(stickyColumnPaint);
        Paint stickyFooterTextPaint = new Paint(stickyColumnTextPaint);
        Paint stickyFooterBorderPaint = new Paint(stickyColumnBorderPaint);
        Paint leftHeadCornerPaint = new Paint(stickyColumnPaint);
        Paint leftHeadCornerBorderPaint = new Paint(stickyColumnBorderPaint);
        Paint leftHeadCornerTextPaint = new Paint(stickyColumnTextPaint);
        Paint leftFootCornerPaint = new Paint(leftHeadCornerPaint);
        Paint leftFootCornerBorderPaint = new Paint(leftHeadCornerBorderPaint);
        Paint leftFootCornerTextPaint = new Paint(leftHeadCornerTextPaint);
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.FILL);
        vertDividingPaint = new Paint(borderPaint);
        horzDividingPaint = new Paint(borderPaint);
        vertDividingPaint.setStyle(Paint.Style.STROKE);
        vertDividingPaint.setStrokeWidth(DEFAULT_HORIZONTAL_BORDER_WIDTH * 2);
        horzDividingPaint.setStyle(Paint.Style.STROKE);
        horzDividingPaint.setStrokeWidth(DEFAULT_HORIZONTAL_BORDER_WIDTH * 2);
        Paint cellPaint = new Paint();
        cellPaint.setColor(Color.LTGRAY);
        cellPaint.setStyle(Paint.Style.FILL);
        Paint cellTextPaint = new Paint();
        cellTextPaint.setColor(Color.BLUE);
        cellTextPaint.setStyle(Paint.Style.FILL);
        Paint cellBorderPaint = new Paint(stickyColumnBorderPaint);

        SpreadsheetCell stickyColumnCell = new SpreadsheetCell(this,stickyColumnPaint,stickyColumnTextPaint,
                stickyColumnBorderPaint,DEFAULT_HORIZONTAL_BORDER_WIDTH, DEFAULT_HORIZONTAL_BORDER_WIDTH);
        stickyColumnCell.getSelectedTextPaint().setFakeBoldText(true);

        SpreadsheetCell stickyHeaderCell = new SpreadsheetCell(this,stickyHeaderPaint,stickyHeaderTextPaint,
                stickyHeaderBorderPaint,DEFAULT_HORIZONTAL_BORDER_WIDTH, DEFAULT_HORIZONTAL_BORDER_WIDTH);
        stickyHeaderCell.getSelectedTextPaint().setFakeBoldText(true);

        SpreadsheetCell stickyFooterCell = new SpreadsheetCell(this,stickyFooterPaint,stickyFooterTextPaint,
                stickyFooterBorderPaint,DEFAULT_HORIZONTAL_BORDER_WIDTH, DEFAULT_HORIZONTAL_BORDER_WIDTH);
        stickyFooterCell.getSelectedTextPaint().setFakeBoldText(true);

        SpreadsheetCell dataCell = new SpreadsheetCell(this,cellPaint,cellTextPaint,cellBorderPaint,DEFAULT_HORIZONTAL_BORDER_WIDTH,
                DEFAULT_HORIZONTAL_BORDER_WIDTH);
        dataCell.getSelectedCellPaint().setColor(Color.YELLOW);
        dataCell.getSelectedTextPaint().setFakeBoldText(true);


        SpreadsheetCell leftHeadCornerCell = new SpreadsheetCell(this,leftHeadCornerPaint,leftHeadCornerTextPaint,
                leftHeadCornerBorderPaint,DEFAULT_HORIZONTAL_BORDER_WIDTH, DEFAULT_HORIZONTAL_BORDER_WIDTH);
        leftHeadCornerCell.getSelectedTextPaint().setFakeBoldText(true);

        SpreadsheetCell leftFootCornerCell = new SpreadsheetCell(this,leftFootCornerPaint,leftFootCornerTextPaint,
                leftFootCornerBorderPaint,DEFAULT_HORIZONTAL_BORDER_WIDTH, DEFAULT_HORIZONTAL_BORDER_WIDTH);
        leftFootCornerCell.getSelectedTextPaint().setFakeBoldText(true);

        setStickyHeaderCell(stickyHeaderCell);
        setStickyFooterCell(stickyFooterCell);
        setStickyColumnCell(stickyColumnCell);
        setLeftFootCornerCell(leftFootCornerCell);
        setLeftHeadCornerCell(leftHeadCornerCell);
        setDataCell(dataCell);


    }

    private void defaultDimensions(){
        stickyColumnWidths = new float[1];
        setNumberStickyColumns(1);
        headers = new String[1];
        headers[0] = "EMPTY";
        headerSelected = new boolean[1];
        footerSelected = new boolean[1];
        footers = new String[1];
        footers[0] = "EMPTY";
        dataSizeValues =1;
        dataSizeObjects = 0;
        sorted = new int[1];
        sorted[0] = SORTED_UNSORTED;
        setStickyColumnWidth(0, DEFAULT_STICKY_COLUMN_WIDTH);
        setDataCellDimensions(DEFAULT_CELL_WIDTH, DEFAULT_CELL_HEIGHT);
        setStickyHeaderHeight(DEFAULT_STICKY_HEADER_HEIGHT);
        setStickyFooterHeight(DEFAULT_STICKY_FOOTER_HEIGHT);
        setAllCellsTextSize(DEFAULT_TEXT_SIZE);

    }


    private void init(Context context){

        gestureDetector = new GestureDetector(context, this);
        scroller = new Scroller(context);
        windowScrollX = 0f;
        windowScrollY = 0f;
        dataSet = false;
    }



    @Override
    protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
        this.stickyTableWidth = width;
        this.stickyTableHeight = height;
        adjustTable();
        super.onSizeChanged(width, height, oldwidth, oldheight);
    }

    private void adjustTable(){
        this.activeDataWindowHeight = this.stickyTableHeight-this.stickyHeaderHeight-this.stickyFooterHeight;
        this.activeDataWindowWidth = this.stickyTableWidth-this.stickyColumnWidth;
        fitCellWidthToFill();
        this.maxWindowScrollX = (dataSizeValues - numberStickyColumns) * dataCellWidth - activeDataWindowWidth;
        if (this.maxWindowScrollX < 0){
            this.maxWindowScrollX = 0;
        }

        this.maxWindowScrollY = dataSizeObjects * dataCellHeight - activeDataWindowHeight;
        if (this.maxWindowScrollY < 0){
            this.maxWindowScrollY = 0;
        }
        scroll(0, 0); //to invalidate and make sure clamp bounds
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        super.onTouchEvent(event);
        return true;

    }

    private void processClick(float x, float y){


        if (y <stickyHeaderHeight){
            if (headerListener!=null){
                headerListener.headerClick(getValueDataCellIndex(x));
                invalidate();
            }
        }

        else if (y < stickyHeaderHeight + activeDataWindowHeight){
            if (cellListener!=null){
                cellListener.cellClick(getObjectDataCellIndex(y), getValueDataCellIndex(x));
                invalidate();
            }
        }


        else if(x <stickyTableWidth && y <  stickyTableHeight){
            if (footerListener!=null){
                footerListener.footerClick(getValueDataCellIndex(x));
                invalidate();
            }
        }
    }

    public interface OnFooterChangedListener{
        public void updateFooter(int valueIndex);
    }

    public void setFooterChangedListener(OnFooterChangedListener listener){
        this.footerChangedListener = listener;
    }

    public interface OnHeaderClickListener{
        public void headerClick(int valueIndex);
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener){
        this.headerListener = listener;
    }

    public interface OnFooterClickListener{
        public void footerClick(int valueIndex);
    }

    public void setOnFooterClickListener(OnFooterClickListener listener){
        this.footerListener = listener;
    }

    public interface OnCellClickListener{
        public void cellClick(int objectIndex, int valueIndex);
    }

    public void setOnCellClickListener(OnCellClickListener listener){
        this.cellListener = listener;
    }


    public void sortDataAscBy(final int valueIndex, Comparator<SpreadsheetRow> comparator){
        Arrays.sort(objectData, comparator);
        for (int i = 0; i < dataSizeValues;i++){
            sorted[i] = SORTED_UNSORTED;
        }
        sorted[valueIndex]=SORTED_ASCENDING;
    }

    public void sortDataDescBy(final int valueIndex, Comparator<SpreadsheetRow> comparator){
        Arrays.sort(objectData,comparator);
        for (int i = 0; i < dataSizeValues;i++){
            sorted[i] = SORTED_UNSORTED;
        }
        sorted[valueIndex]=SORTED_DESCENDING;
    }


    public void setData(SpreadsheetRow[] data, String[] headers){

        this.objectData = data;
        this.headers = headers;
        this.footers = new String[headers.length];
        this.sorted = new int[headers.length];
        this.headerSelected = new boolean[headers.length];
        this.footerSelected = new boolean[headers.length];
        if (headers.length > stickyColumnWidths.length){
            float[] tempArray = new float[headers.length];
            for (int i = 0; i < headers.length; i++){
                if (i < stickyColumnWidths.length){
                    tempArray[i] = stickyColumnWidths[i];
                } else{
                    tempArray[i] = dataCellWidth;
                }
            }
            stickyColumnWidths = tempArray;
            dataSet = true;
        }

        dataSizeObjects = data.length;
        dataSizeValues = headers.length;

        for (int i = 0; i < dataSizeValues; i ++){
            footers[i] = "";
            updateFooter(i);
            sorted[i] = SORTED_UNSORTED;
        }

        setNumberStickyColumns(targetNumberStickyColumns);
    }

    private int getValueDataCellIndex(float x){


        if ((x)<stickyColumnWidth){
            float widthSearched = 0f;
            for (int i = 0; i < numberStickyColumns; i ++){
                widthSearched+=stickyColumnWidths[i];
                if ((x)<widthSearched){
                    return i;
                }
            }
        }
        int startValuesIndex;

        if (windowScrollX == 0f){
            startValuesIndex = numberStickyColumns;
        } else{
            startValuesIndex = (int) (numberStickyColumns+windowScrollX/dataCellWidth);
        }

        float xOffset = windowScrollX%dataCellWidth;

        float xInto = x-stickyColumnWidth;
        int indexOffsetX = (int) ((xInto+xOffset)/dataCellWidth);
        return indexOffsetX+startValuesIndex;

    }

    private int getObjectDataCellIndex(float y){


        int startObjectsIndex;

        if (windowScrollY == 0f){
            startObjectsIndex = 0;
        } else{
            startObjectsIndex = (int) (windowScrollY/dataCellHeight);
        }

        float yOffset = windowScrollY%dataCellHeight;

        float yInto = y-stickyHeaderHeight;
        int indexOffsetY = (int) ((yInto+yOffset)/dataCellHeight);

        return indexOffsetY+startObjectsIndex;

    }



    private void updateFooter(int valueIndex){
        if (footerChangedListener!=null){
            updateFooter(valueIndex);
        }
    }


    public void scroll(float x, float y){
        windowScrollX = windowScrollX+ x;
        if (windowScrollX < 0){
            windowScrollX =0;
        }
        if (windowScrollX >= maxWindowScrollX){
            windowScrollX = maxWindowScrollX;
        }

        windowScrollY = windowScrollY + y;
        if (windowScrollY <0){
            windowScrollY = 0;
        }
        if (windowScrollY >= maxWindowScrollY){
            windowScrollY = maxWindowScrollY;
        }

        invalidate();
    }



    private void fitCellWidthToFill(){
        int numDataCells = getNumberNonStickyColumns();
        if (numDataCells == 0){
            return;
        }
        if (getTotalStickyColumnsWidth()+numDataCells*getDataCellWidth() < getStickyTableWidth()){
            float dataWindowWidth = getStickyTableWidth() - getTotalStickyColumnsWidth();
            setDataCellDimensions(dataWindowWidth / numDataCells, getDataCellHeight());
        }
    }

    public void selectRow(int objectIndex, boolean select){
        objectData[objectIndex].selectRow(select);
    }
      
    public void selectColumn(int valueIndex, boolean select){
        for (int i = 0; i < dataSizeObjects; i++){
            objectData[i].select(valueIndex, select);
        }
        selectHeader(valueIndex,select);
        selectFooter(valueIndex,select);
    }

    public void select(int objectIndex, int valueIndex, boolean select){
        objectData[objectIndex].select(valueIndex,select);
    }

    public void selectFooter(int valueIndex, boolean select){
        footerSelected[valueIndex]=select;
    }
    public void selectHeader(int valueIndex, boolean select){
        headerSelected[valueIndex]=select;
    }
    public boolean isFooterSelected(int valueIndex){
        return footerSelected[valueIndex];
    }
    public boolean isHeaderSelected(int valueIndex){
        return headerSelected[valueIndex];
    }

    public boolean isSelected(int objectIndex, int valueIndex){
        return objectData[objectIndex].isSelected(valueIndex);
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if (!dataSet){
            return;
        }

        if (scroller.computeScrollOffset()){
            scroll(scroller.getCurrX() - windowScrollX,scroller.getCurrY() - windowScrollY);
        }
        drawDataCells(canvas);
        drawStickyColumn(canvas);
        drawStickyHeader(canvas);
        drawStickyFooter(canvas);
        drawCornerCells(canvas);
        canvas.drawLine(stickyColumnWidth,0f,stickyColumnWidth,stickyTableHeight, vertDividingPaint);
        canvas.drawLine(0f,stickyHeaderHeight,stickyTableWidth,stickyHeaderHeight,horzDividingPaint);
        canvas.drawLine(0f,stickyTableHeight-stickyFooterHeight,stickyTableWidth,stickyTableHeight-stickyFooterHeight,
                horzDividingPaint);




    }

    private void drawCornerCells(Canvas canvas){
        //top left
        float leftX = 0f;
        float topY = 0f;

        if (headers!=null){
            for (int i = 0; i <numberStickyColumns; i++){

                if (leftHeadCornerCell!=null && stickyHeaderHeight>0f &&stickyColumnWidths[i]>0f){
                    leftHeadCornerCell.draw(canvas,headers[i],leftX,topY,stickyColumnWidths[i],
                            stickyHeaderHeight, isHeaderSelected(i));
                }
                leftX +=stickyColumnWidths[i];
            }
        }

        //bottom left

        if (footers!=null){
            leftX = 0f;
            topY = 0f + stickyHeaderHeight+activeDataWindowHeight;
            for (int i = 0; i <numberStickyColumns; i++){
                if (leftFootCornerCell!=null && stickyFooterHeight>0f &&stickyColumnWidths[i]>0f){
                    leftFootCornerCell.draw(canvas,footers[i],leftX,topY,stickyColumnWidths[i],
                            stickyFooterHeight, isFooterSelected(i));
                }
                leftX +=stickyColumnWidths[i];
            }
        }


    }





    protected void drawStickyColumn(Canvas canvas){

        if (dataCellHeight == 0f || stickyColumnWidth == 0f || stickyColumnCell == null || objectData == null){
            return;
        }

        int startIndex;

        if (windowScrollY == 0f){
            startIndex = 0;
        } else{
            startIndex = (int) (windowScrollY/dataCellHeight);
        }

        float yOffset = windowScrollY%dataCellHeight;

        int numberCellsDown= (int) (activeDataWindowHeight/dataCellHeight +2);
        if (numberCellsDown+ startIndex > dataSizeObjects){
            numberCellsDown = dataSizeObjects - startIndex;
        }

        float currentLeftX = 0f;
        for (int i = 0; i < numberStickyColumns; i++){
            for (int j = 0; j<numberCellsDown; j++){


                float topY = stickyHeaderHeight+j*dataCellHeight-yOffset;

                if (topY < stickyHeaderHeight+activeDataWindowHeight && objectData[startIndex+j]!=null){
                    stickyColumnCell.draw(canvas,objectData[startIndex+j].getValueAt(i), currentLeftX,
                            topY, stickyColumnWidths[i], dataCellHeight,
                            objectData[startIndex+j].isSelected(i));
                }
            }
            currentLeftX +=stickyColumnWidths[i];
        }
    }

    protected void drawStickyHeader(Canvas canvas){

        if (dataCellWidth == 0f || stickyHeaderHeight ==0f || stickyHeaderCell == null || headers == null){
            return;
        }

        int startIndex;

        if (windowScrollX == 0f){
            startIndex = numberStickyColumns;
        } else{
            startIndex = (int) (numberStickyColumns + windowScrollX/dataCellWidth);
        }

        float xOffset = windowScrollX%dataCellWidth;

        int numberCellsAcross = (int)(activeDataWindowWidth/dataCellWidth+2);

        if (numberCellsAcross+startIndex > dataSizeValues){
            numberCellsAcross = dataSizeValues - startIndex;
        }

        float leftX;
        float topY;

        for (int i = 0; i<numberCellsAcross; i++){

            leftX = stickyColumnWidth+i*dataCellWidth-xOffset;
            topY = 0f;

            if (leftX<stickyColumnWidth+activeDataWindowWidth){
                stickyHeaderCell.draw(canvas,headers[startIndex+i], leftX, topY, dataCellWidth,
                        stickyHeaderHeight, isHeaderSelected(startIndex+i));
            }
        }
    }



    protected void drawStickyFooter(Canvas canvas){

        if (dataCellWidth <= 0f || stickyFooterHeight <= 0f || stickyFooterCell == null || footers == null){
            return;
        }

        int startIndex;

        if (windowScrollX == 0f){
            startIndex = numberStickyColumns;
        } else{
            startIndex = (int) (numberStickyColumns+windowScrollX/dataCellWidth);
        }

        float xOffset = windowScrollX%dataCellWidth;

        int numberCellsAcross = (int)(activeDataWindowWidth/dataCellWidth+2);
        if (numberCellsAcross+ startIndex > dataSizeValues){
            numberCellsAcross = dataSizeValues - startIndex;
        }


        float leftX;
        float topY;

        for (int i = 0; i<numberCellsAcross; i++){

            leftX = stickyColumnWidth+i*dataCellWidth - xOffset;
            topY = stickyHeaderHeight+activeDataWindowHeight;

            if (leftX<stickyColumnWidth+activeDataWindowWidth){
                stickyFooterCell.draw(canvas, ""+footers[startIndex+i], leftX,topY,
                        dataCellWidth,stickyFooterHeight, isFooterSelected(startIndex+i));
            }
        }
    }



    private void drawDataCells(Canvas canvas){

        if (dataCellWidth == 0f || dataCellHeight == 0f || dataCell == null || objectData == null){
            return;
        }

        int startValuesIndex;

        if (windowScrollX == 0f){
            startValuesIndex = numberStickyColumns;
        } else{
            startValuesIndex = (int) (numberStickyColumns+windowScrollX/dataCellWidth);
        }

        float xOffset = windowScrollX%dataCellWidth;


        int startObjectsIndex;


        if (windowScrollY == 0f){
            startObjectsIndex = 0;
        } else{
            startObjectsIndex = (int) (windowScrollY/dataCellHeight);
        }

        float yOffset = windowScrollY%dataCellHeight;

        int numberCellsDown= (int) (activeDataWindowHeight/dataCellHeight +2);
        if (numberCellsDown+ startObjectsIndex > dataSizeObjects){
            numberCellsDown = dataSizeObjects - startObjectsIndex;
        }
        int numberCellsAcross = (int)(activeDataWindowWidth/dataCellWidth+2);
        if (numberCellsAcross+ startValuesIndex > dataSizeValues){
            numberCellsAcross = dataSizeValues - startValuesIndex;
        }

        for (int i =0; i<numberCellsAcross; i++){
            for (int j = 0; j < numberCellsDown; j++){
                float leftX = stickyColumnWidth+i*dataCellWidth - xOffset;
                float topY  = stickyHeaderHeight+j*dataCellHeight - yOffset;
                if (leftX < stickyColumnWidth+activeDataWindowWidth && topY < stickyHeaderHeight+activeDataWindowHeight){
                    if (objectData[startObjectsIndex+j] != null){
                        dataCell.draw(canvas,""+objectData[startObjectsIndex+j].getValueAt(startValuesIndex+i),
                                leftX,topY, dataCellWidth, dataCellHeight,
                                objectData[startObjectsIndex+j].isSelected(startValuesIndex+i));
                    }
                }
            }
        }
    }

    public float getStickyTableWidth(){
        float width = stickyColumnWidth+activeDataWindowWidth;
        return width;
    }

    public float getStickyTableHeight(){
        float height = stickyHeaderHeight+activeDataWindowHeight+stickyFooterHeight;
        return height;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (!scroller.isFinished()){
            scroller.forceFinished(true);
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        processClick(e.getX(), e.getY());
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        scroll(distanceX, distanceY);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


        int velX = (int)(velocityX/SCALE);

        int velY = (int)(velocityY/SCALE);

        scroller.fling((int)windowScrollX, (int)windowScrollY, velX, velY, 0, (int)(maxWindowScrollX), 0 , (int)(maxWindowScrollY));
        postInvalidate();
        return false;
    }

    public int getTotalNumberColumns(){
        return this.dataSizeValues;
    }

    public int getNumberNonStickyColumns(){
        return this.dataSizeValues - this.numberStickyColumns;
    }

    public float getTotalStickyColumnsWidth(){
        return stickyColumnWidth;
    }

    public void setDataAt(int objectIndex, int valueIndex, String data){
        getRowAt(objectIndex).setValue(valueIndex, data);
    }
    public String getDataAt(int objectIndex, int valueIndex){
        return getRowAt(objectIndex).getValueAt(valueIndex);
    }

    public int getNumberObjects(){
        return dataSizeObjects;
    }





    public void setStickyColumnWidth(int columnIndex, float width){
        if (this.stickyColumnWidths==null || columnIndex +1>stickyColumnWidths.length){
            return;
        }
        this.stickyColumnWidths[columnIndex] = width;
        calculateStickyColumnWidth();
        adjustTable();
    }

    public float getStickyColumnWidth(int columnIndex){
        if (columnIndex+1>stickyColumnWidths.length){
            return 0f;
        }
        return this.stickyColumnWidths[columnIndex];
    }



    public void setNumberStickyColumns(int number){
        targetNumberStickyColumns=number;
        if (number>dataSizeValues){
            number = dataSizeValues;
        }
        if (number<0){
            number = 0;
        }
        this.numberStickyColumns = number;

        calculateStickyColumnWidth();
        adjustTable();
    }

    public int getNumberStickyColumns(){
        return this.numberStickyColumns;
    }


    public void incStickyColumnWidth(int columnIndex, float dw){
        this.stickyColumnWidths[columnIndex] +=dw;
        calculateStickyColumnWidth();
        adjustTable();
    }

    private void calculateStickyColumnWidth(){
        float sum = 0;
        for (int i = 0; i < numberStickyColumns; i++){
            sum = sum + getStickyColumnWidth(i);
        }
        this.stickyColumnWidth = sum;
    }

   public void setStickyHeaderTextSize(float size){
        stickyHeaderCell.setTextSize(size);
    }
    public float getStickyHeaderTextSize(){
        return stickyHeaderCell.getTextPaint().getTextSize();
    }

    public void setStickyFooterTextSize(float size){
        stickyFooterCell.setTextSize(size);
    }
    public float getStickyFooterTextSize(){
        return stickyFooterCell.getTextPaint().getTextSize();
    }

    public void setStickyColumnTextSize(float size){
        stickyColumnCell.setTextSize(size);
    }
    public float getStickyColumnTextSize(){
        return stickyColumnCell.getTextPaint().getTextSize();
    }

    public void setDataCellTextSize(float size){
        dataCell.setTextSize(size);
    }
    public float getDataCellTextSize(){
        return dataCell.getTextPaint().getTextSize();
    }

    public void incStickyHeaderHeight(float dh){
        this.stickyHeaderHeight = this.stickyHeaderHeight+dh;
        adjustTable();
    }
    public void setStickyHeaderHeight(float height){
        this.stickyHeaderHeight = height;
        adjustTable();
    }
    public float getStickyHeaderHeight(){
        return stickyHeaderHeight;
    }


    public void hideStickyFooter(){
        setStickyFooterHeight(0);
    }
    public void showStickyFooter(float height){
        setStickyFooterHeight(height);
    }
    public void setStickyFooterHeight(float height){
        this.stickyFooterHeight = height;
        adjustTable();
    }
    public float getStickyFooterHeight(){
        return this.stickyFooterHeight;
    }

    public void incStickyFooterHeight(float dh){
        this.stickyFooterHeight = this.stickyFooterHeight + dh;
        adjustTable();
    }

    public void setDataCellDimensions(float width, float height){
        this.dataCellWidth = width;
        this.dataCellHeight = height;
        adjustTable();
    }

    public float getDataCellWidth(){
        return dataCellWidth;
    }
    public float getDataCellHeight(){
        return dataCellHeight;
    }

    public void setValuesColumn(int headerIndex, String[] column){
        for(int i = 0; i <dataSizeObjects; i++){
            objectData[i].setValue(headerIndex, column[i]);
        }
        updateFooter(headerIndex);
    }

    public void setFooters(String[] footers, float height){
        this.footers = footers;
        setStickyFooterHeight(height);
    }



    public void setRow(int objectIndex, String[] row){
        for (int i =0; i <dataSizeValues; i++){
            objectData[objectIndex].setValue(objectIndex, row[i]);
        }

        for (int i = 0; i < dataSizeValues; i ++){
            updateFooter(i);
        }
    }

    public void setRow(int objectIndex, SpreadsheetRow row){
        objectData[objectIndex] = row;
        for (int i = 0; i < dataSizeValues; i ++){
            updateFooter(i);
        }
    }


    public SpreadsheetRow getRowAt(int objectIndex){
        return objectData[objectIndex];
    }

    public String getFooterAt(int valueIndex){
        return footers[valueIndex];

    }

    public String getHeaderAt(int valueIndex){
        return headers[valueIndex];
    }

    public void setAllCellsTextSize(float textSize){
        stickyColumnCell.setTextSize(textSize);
        stickyHeaderCell.setTextSize(textSize);
        stickyFooterCell.setTextSize(textSize);
        dataCell.setTextSize(textSize);
        leftHeadCornerCell.setTextSize(textSize);
        leftFootCornerCell.setTextSize(textSize);

        stickyColumnCell.setSelectedTextSize(textSize);
        stickyHeaderCell.setSelectedTextSize(textSize);
        stickyFooterCell.setSelectedTextSize(textSize);
        dataCell.setSelectedTextSize(textSize);
        leftHeadCornerCell.setSelectedTextSize(textSize);
        leftFootCornerCell.setSelectedTextSize(textSize);
    }

    public void setAllCellsTextColor(int color){
        stickyColumnCell.setTextColor(color);
        stickyHeaderCell.setTextColor(color);
        stickyFooterCell.setTextColor(color);
        dataCell.setTextColor(color);
        leftHeadCornerCell.setTextColor(color);
        leftFootCornerCell.setTextColor(color);

    }

    public void setAllCellsSelectedTextColor(int color){
        stickyColumnCell.setSelectedTextColor(color);
        stickyHeaderCell.setSelectedTextColor(color);
        stickyFooterCell.setSelectedTextColor(color);
        dataCell.setSelectedTextColor(color);
        leftHeadCornerCell.setSelectedTextColor(color);
        leftFootCornerCell.setSelectedTextColor(color);

    }

    public void setAllCellsTypeface(Typeface typeface){
        stickyColumnCell.setTypeface(typeface);
        stickyHeaderCell.setTypeface(typeface);
        stickyFooterCell.setTypeface(typeface);
        dataCell.setTypeface(typeface);
        leftHeadCornerCell.setTypeface(typeface);
        leftFootCornerCell.setTypeface(typeface);
        stickyColumnCell.setSelectedTypeface(typeface);
        stickyHeaderCell.setSelectedTypeface(typeface);
        stickyFooterCell.setSelectedTypeface(typeface);
        dataCell.setSelectedTypeface(typeface);
        leftHeadCornerCell.setSelectedTypeface(typeface);
        leftFootCornerCell.setSelectedTypeface(typeface);
    }
    public void setLeftFootCornerCell(SpreadsheetCell cell){
        this.leftFootCornerCell = cell;
    }
    public SpreadsheetCell getLeftFootCornerCell(){
        return leftFootCornerCell;
    }
    public void setLeftFootCornerCellTypeface(Typeface typeface){
        leftFootCornerCell.setTypeface(typeface);
    }
    public Typeface getLeftFootCornerCellTypeface(){
        return leftFootCornerCell.getTypeface();
    }
    public void setLeftFootCornerCellTextColor(int color){
        leftFootCornerCell.setTextColor(color);
    }
    public int getLeftFootCornerCellTextColor(){
        return leftFootCornerCell.getTextColor();
    }

    public void setLeftHeadCornerCell(SpreadsheetCell cell){
        this.leftHeadCornerCell = cell;
    }
    public SpreadsheetCell getLeftHeadCornerCell(){
        return leftHeadCornerCell;
    }
    public void setLeftHeadCornerCellTypeface(Typeface typeface){
        leftHeadCornerCell.setTypeface(typeface);
    }
    public Typeface getLeftHeadCornerCellTypeface(){
        return leftHeadCornerCell.getTypeface();
    }
    public void setLeftHeadCornerCellTextColor(int color){
        leftHeadCornerCell.setTextColor(color);
    }
    public int getLeftHeadCornerCellTextColor(){
        return leftHeadCornerCell.getTextColor();
    }

    public void setDataCell(SpreadsheetCell cell){
        this.dataCell = cell;
    }
    public SpreadsheetCell getDataCell(){
        return dataCell;
    }
    public void setDataCellTypeface(Typeface typeface){
        dataCell.setTypeface(typeface);
    }
    public Typeface getDataCellTypeface(){
        return dataCell.getTypeface();
    }
    public void setDataCellTextColor(int color){
        dataCell.setTextColor(color);
    }
    public int getDataCellTextColor(){
        return dataCell.getTextColor();
    }

    public void setStickyColumnCell(SpreadsheetCell cell){
        this.stickyColumnCell = cell;
    }
    public SpreadsheetCell getStickyColumnCell(){
        return stickyColumnCell;
    }
    public void setStickyColumnCellTypeface(Typeface typeface){
        stickyColumnCell.setTypeface(typeface);
    }
    public Typeface getStickyColumnCellTypeface(){
        return stickyColumnCell.getTypeface();
    }
    public void setStickyColumnCellTextColor(int color){
        stickyColumnCell.setTextColor(color);
    }
    public int getStickyColumnCellTextColor(){
        return stickyColumnCell.getTextColor();
    }

    public void setStickyHeaderCell(SpreadsheetCell cell){
        this.stickyHeaderCell = cell;
    }
    public SpreadsheetCell getStickyHeaderCell(){
        return stickyHeaderCell;
    }
    public void setStickyHeaderCellTypeface(Typeface typeface){
        stickyHeaderCell.setTypeface(typeface);
    }
    public Typeface getStickyHeaderCellTypeface(){
        return stickyHeaderCell.getTypeface();
    }
    public void setStickyHeaderCellTextColor(int color){
        stickyHeaderCell.setTextColor(color);
    }
    public int getStickyHeaderCellTextColor(){
        return stickyHeaderCell.getTextColor();
    }


    public void setStickyFooterCell(SpreadsheetCell cell){
        this.stickyFooterCell = cell;
    }
    public SpreadsheetCell getStickyFooterCell(){
        return stickyFooterCell;
    }
    public void setStickyFooterCellTypeface(Typeface typeface){
        stickyFooterCell.setTypeface(typeface);
    }
    public void setStickyFooterCellTextColor(int color){
        stickyFooterCell.setTextColor(color);
    }
    public int getStickyFooterCellTextColor(){
        return stickyFooterCell.getTextColor();
    }

    public void setHorizontalDividingPaint(Paint paint){
        this.horzDividingPaint = paint;
    }

    public Paint getHorizontalDividingPaint(){
        return horzDividingPaint;
    }

    public void setVerticalDividingPaint(Paint paint){
        this.vertDividingPaint = paint;
    }

    public Paint getVerticalDividingPaint(){
        return vertDividingPaint;
    }

    public int getSortedStatus(int valueIndex){
        return sorted[valueIndex];
    }

}