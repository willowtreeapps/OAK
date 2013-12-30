package oak.widget.spreadsheetview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

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
    private final float DEFAULT_STICKY_FOOTER_HEIGHT = 100;
    private final float DEFAULT_STICKY_HEADER_HEIGHT = 100;
    private final float DEFAULT_STICKY_COLUMN_WIDTH = 150;
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
    protected int[] sorted;
    private final static int SORTED_UNSORTED = 0;
    protected final static int SORTED_ASCENDING = 1;
    protected final static int SORTED_DESCENDING = 2;
    protected String[] footers;

    private float[] stickyColumnWidths;
    private int numberStickyColumns;

    private Paint vertDividingPaint;
    private Paint horzDividingPaint;

    private SpreadsheetCell dataCell;
    private SpreadsheetCell stickyHeaderCell;
    private SpreadsheetCell stickyFooterCell;
    private SpreadsheetCell stickyColumnCell;

    private SpreadsheetCell leftFootCornerCell;
    private SpreadsheetCell leftHeadCornerCell;

    public SpreadsheetView(Context context) {
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
    }

    public SpreadsheetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        defaultCells();
        defaultDimensions();
    }

    private void defaultCells() {
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

        Paint cellPaint = new Paint();
        cellPaint.setColor(Color.LTGRAY);
        cellPaint.setStyle(Paint.Style.FILL);

        Paint cellTextPaint = new Paint();
        cellTextPaint.setColor(Color.BLUE);
        cellTextPaint.setStyle(Paint.Style.FILL);

        Paint cellBorderPaint = new Paint(stickyColumnBorderPaint);

        SpreadsheetCell stickyColumnCell = new SpreadsheetCell(this, stickyColumnPaint, stickyColumnTextPaint,
                stickyColumnBorderPaint, DEFAULT_HORIZONTAL_BORDER_WIDTH, DEFAULT_HORIZONTAL_BORDER_WIDTH);

        SpreadsheetCell stickyHeaderCell = new SpreadsheetCell(this, stickyHeaderPaint, stickyHeaderTextPaint,
                stickyHeaderBorderPaint, DEFAULT_HORIZONTAL_BORDER_WIDTH, DEFAULT_HORIZONTAL_BORDER_WIDTH);

        SpreadsheetCell stickyFooterCell = new SpreadsheetCell(this, stickyFooterPaint, stickyFooterTextPaint,
                stickyFooterBorderPaint, DEFAULT_HORIZONTAL_BORDER_WIDTH, DEFAULT_HORIZONTAL_BORDER_WIDTH);

        SpreadsheetCell dataCell = new SpreadsheetCell(this, cellPaint, cellTextPaint, cellBorderPaint,
                DEFAULT_HORIZONTAL_BORDER_WIDTH,
                DEFAULT_HORIZONTAL_BORDER_WIDTH);

        SpreadsheetCell leftHeadCornerCell = new SpreadsheetCell(this, leftHeadCornerPaint, leftHeadCornerTextPaint,
                leftHeadCornerBorderPaint, DEFAULT_HORIZONTAL_BORDER_WIDTH, DEFAULT_HORIZONTAL_BORDER_WIDTH);

        SpreadsheetCell leftFootCornerCell = new SpreadsheetCell(this, leftFootCornerPaint, leftFootCornerTextPaint,
                leftFootCornerBorderPaint, DEFAULT_HORIZONTAL_BORDER_WIDTH, DEFAULT_HORIZONTAL_BORDER_WIDTH);

        setStickyHeaderCell(stickyHeaderCell);
        setStickyFooterCell(stickyFooterCell);
        setStickyColumnCell(stickyColumnCell);
        setLeftFootCornerCell(leftFootCornerCell);
        setLeftHeadCornerCell(leftHeadCornerCell);
        setDataCell(dataCell);
    }

    private void defaultDimensions() {
        stickyColumnWidths = new float[1];
        setNumberStickyColumns(1);
        headers = new String[1];
        headers[0] = "EMPTY";
        footers = new String[1];
        footers[0] = "EMPTY";
        dataSizeValues = 1;
        dataSizeObjects = 0;
        sorted = new int[1];
        sorted[0] = SORTED_UNSORTED;
        setStickyColumnWidth(0, DEFAULT_STICKY_COLUMN_WIDTH);
        setDataCellDimensions(DEFAULT_CELL_WIDTH, DEFAULT_CELL_HEIGHT);
        setStickyHeaderHeight(DEFAULT_STICKY_HEADER_HEIGHT);
        setStickyFooterHeight(DEFAULT_STICKY_FOOTER_HEIGHT);
        setStickyFooterTextSize(DEFAULT_TEXT_SIZE);
        setStickyHeaderTextSize(DEFAULT_TEXT_SIZE);
        setStickyColumnTextSize(DEFAULT_TEXT_SIZE);
        setDataCellTextSize(DEFAULT_TEXT_SIZE);
        getLeftHeadCornerCell().getTextPaint().setTextSize(DEFAULT_TEXT_SIZE);
        getLeftFootCornerCell().getTextPaint().setTextSize(DEFAULT_TEXT_SIZE);
    }

    private void init(Context context) {
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

    private void adjustTable() {
        this.activeDataWindowHeight = this.stickyTableHeight - this.stickyHeaderHeight - this.stickyFooterHeight;
        this.activeDataWindowWidth = this.stickyTableWidth - this.stickyColumnWidth;
        this.maxWindowScrollX = (dataSizeValues - numberStickyColumns) * dataCellWidth - activeDataWindowWidth;
        if (this.maxWindowScrollX < 0) {
            this.maxWindowScrollX = 0;
        }

        this.maxWindowScrollY = dataSizeObjects * dataCellHeight - activeDataWindowHeight;
        if (this.maxWindowScrollY < 0) {
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

    private void processClick(float x, float y) {
        if (y < stickyHeaderHeight) {
            if (headerListener != null) {
                headerListener.headerClick(getValueDataCellIndex(x));
                invalidate();
            }
        } else if (y < stickyHeaderHeight + activeDataWindowHeight) {
            if (cellListener != null) {
                cellListener.cellClick(getObjectDataCellIndex(y), getValueDataCellIndex(x));
                invalidate();
            }
        } else if (x < stickyTableWidth && y < stickyTableHeight) {
            if (footerListener != null) {
                footerListener.footerClick(getValueDataCellIndex(x));
                invalidate();
            }
        }
    }

    public interface OnFooterChangedListener {

        public void updateFooter(int valueIndex);
    }

    public void setFooterChangedListener(OnFooterChangedListener listener) {
        this.footerChangedListener = listener;
    }

    public interface OnHeaderClickListener {

        public void headerClick(int valueIndex);
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        this.headerListener = listener;
    }

    public interface OnFooterClickListener {

        public void footerClick(int valueIndex);
    }

    public void setOnFooterClickListener(OnFooterClickListener listener) {
        this.footerListener = listener;
    }

    public interface OnCellClickListener {

        public void cellClick(int objectIndex, int valueIndex);
    }

    public void setOnCellClickListener(OnCellClickListener listener) {
        this.cellListener = listener;
    }


    public void sortDataAscBy(final int headerIndex, Comparator<SpreadsheetRow> comparator) {
        Arrays.sort(objectData, comparator);
    }

    public void sortDataDescBy(final int headerIndex, Comparator<SpreadsheetRow> comparator) {
        Arrays.sort(objectData, comparator);
    }

    public void setData(SpreadsheetRow[] data, String[] headers) {
        this.objectData = data;
        this.headers = headers;
        this.footers = new String[headers.length];
        this.sorted = new int[headers.length];
        if (headers.length > stickyColumnWidths.length) {
            float[] tempArray = new float[headers.length];
            for (int i = 0; i < headers.length; i++) {
                if (i < stickyColumnWidths.length) {
                    tempArray[i] = stickyColumnWidths[i];
                } else {
                    tempArray[i] = DEFAULT_STICKY_COLUMN_WIDTH;
                }
            }
            stickyColumnWidths = tempArray;
            calculateStickyColumnWidth();
            adjustTable();
            dataSet = true;
        }

        dataSizeObjects = data.length;
        dataSizeValues = headers.length;

        for (int i = 0; i < dataSizeValues; i++) {
            footers[i] = "";
            updateFooter(i);
            sorted[i] = SORTED_UNSORTED;
        }

        calculateStickyColumnWidth();
        adjustTable();
    }

    private int getValueDataCellIndex(float x) {
        if ((x) < stickyColumnWidth) {
            float widthSearched = 0f;
            for (int i = 0; i < numberStickyColumns; i++) {
                widthSearched += stickyColumnWidths[i];
                if ((x) < widthSearched) {
                    return i;
                }
            }
        }
        int startValuesIndex;

        if (windowScrollX == 0f) {
            startValuesIndex = numberStickyColumns;
        } else {
            startValuesIndex = (int) (numberStickyColumns + windowScrollX / dataCellWidth);
        }

        float xOffset = windowScrollX % dataCellWidth;

        float xInto = x - stickyColumnWidth;
        int indexOffsetX = (int) ((xInto + xOffset) / dataCellWidth);
        return indexOffsetX + startValuesIndex;
    }

    private int getObjectDataCellIndex(float y) {
        int startObjectsIndex;

        if (windowScrollY == 0f) {
            startObjectsIndex = 0;
        } else {
            startObjectsIndex = (int) (windowScrollY / dataCellHeight);
        }

        float yOffset = windowScrollY % dataCellHeight;

        float yInto = y - stickyHeaderHeight;
        int indexOffsetY = (int) ((yInto + yOffset) / dataCellHeight);

        return indexOffsetY + startObjectsIndex;
    }


    private void updateFooter(int valueIndex) {
        if (footerChangedListener != null) {
            updateFooter(valueIndex);
        }
    }


    public void scroll(float x, float y) {
        windowScrollX = windowScrollX + x;
        if (windowScrollX < 0) {
            windowScrollX = 0;
        }
        if (windowScrollX >= maxWindowScrollX) {
            windowScrollX = maxWindowScrollX;
        }

        windowScrollY = windowScrollY + y;
        if (windowScrollY < 0) {
            windowScrollY = 0;
        }
        if (windowScrollY >= maxWindowScrollY) {
            windowScrollY = maxWindowScrollY;
        }

        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!dataSet) {
            return;
        }

        if (scroller.computeScrollOffset()) {
            scroll(scroller.getCurrX() - windowScrollX, scroller.getCurrY() - windowScrollY);
        }
        drawDataCells(canvas);
        drawStickyColumn(canvas);
        drawStickyHeader(canvas);
        drawStickyFooter(canvas);
        drawCornerCells(canvas);
        canvas.drawLine(stickyColumnWidth, 0f, stickyColumnWidth, stickyTableHeight, vertDividingPaint);
        canvas.drawLine(0f, stickyHeaderHeight, stickyTableWidth, stickyHeaderHeight, horzDividingPaint);
    }

    private void drawCornerCells(Canvas canvas) {
        //top left
        float leftX = 0f;
        float topY = 0f;

        if (headers != null) {
            for (int i = 0; i < numberStickyColumns; i++) {

                if (leftHeadCornerCell != null && stickyHeaderHeight > 0f && stickyColumnWidths[i] > 0f) {
                    leftHeadCornerCell.draw(canvas, headers[i], leftX, topY, stickyColumnWidths[i], stickyHeaderHeight);
                }
                leftX += stickyColumnWidths[i];
            }
        }

        //bottom left
        if (footers != null) {
            leftX = 0f;
            topY = 0f + stickyHeaderHeight + activeDataWindowHeight;
            for (int i = 0; i < numberStickyColumns; i++) {
                if (leftFootCornerCell != null && stickyFooterHeight > 0f && stickyColumnWidths[i] > 0f) {
                    leftFootCornerCell.draw(canvas, footers[i], leftX, topY, stickyColumnWidths[i], stickyFooterHeight);
                }
                leftX += stickyColumnWidths[i];
            }
        }
    }


    protected void drawStickyColumn(Canvas canvas) {
        if (dataCellHeight == 0f || stickyColumnWidth == 0f || stickyColumnCell == null || objectData == null) {
            return;
        }

        int startIndex;

        if (windowScrollY == 0f) {
            startIndex = 0;
        } else {
            startIndex = (int) (windowScrollY / dataCellHeight);
        }

        float yOffset = windowScrollY % dataCellHeight;

        int numberCellsDown = (int) (activeDataWindowHeight / dataCellHeight + 2);
        if (numberCellsDown + startIndex > dataSizeObjects) {
            numberCellsDown = dataSizeObjects - startIndex;
        }

        float currentLeftX = 0f;
        for (int i = 0; i < numberStickyColumns; i++) {
            for (int j = 0; j < numberCellsDown; j++) {

                float topY = stickyHeaderHeight + j * dataCellHeight - yOffset;

                if (topY < stickyHeaderHeight + activeDataWindowHeight && objectData[startIndex + j] != null) {
                    stickyColumnCell.draw(canvas, objectData[startIndex + j].getValueAt(i), currentLeftX, topY,
                            stickyColumnWidths[i], dataCellHeight);
                }
            }
            currentLeftX += stickyColumnWidths[i];
        }
    }

    protected void drawStickyHeader(Canvas canvas) {
        if (dataCellWidth == 0f || stickyHeaderHeight == 0f || stickyHeaderCell == null || headers == null) {
            return;
        }

        int startIndex;

        if (windowScrollX == 0f) {
            startIndex = numberStickyColumns;
        } else {
            startIndex = (int) (numberStickyColumns + windowScrollX / dataCellWidth);
        }

        float xOffset = windowScrollX % dataCellWidth;

        int numberCellsAcross = (int) (activeDataWindowWidth / dataCellWidth + 2);

        if (numberCellsAcross + startIndex > dataSizeValues) {
            numberCellsAcross = dataSizeValues - startIndex;
        }

        float leftX;
        float topY;

        for (int i = 0; i < numberCellsAcross; i++) {

            leftX = stickyColumnWidth + i * dataCellWidth - xOffset;
            topY = 0f;

            if (leftX < stickyColumnWidth + activeDataWindowWidth) {
                stickyHeaderCell.draw(canvas, headers[startIndex + i], leftX, topY, dataCellWidth, stickyHeaderHeight);
            }
        }
    }


    protected void drawStickyFooter(Canvas canvas) {
        if (dataCellWidth <= 0f || stickyFooterHeight <= 0f || stickyFooterCell == null || footers == null) {
            return;
        }

        int startIndex;

        if (windowScrollX == 0f) {
            startIndex = numberStickyColumns;
        } else {
            startIndex = (int) (numberStickyColumns + windowScrollX / dataCellWidth);
        }

        float xOffset = windowScrollX % dataCellWidth;

        int numberCellsAcross = (int) (activeDataWindowWidth / dataCellWidth + 2);
        if (numberCellsAcross + startIndex > dataSizeValues) {
            numberCellsAcross = dataSizeValues - startIndex;
        }

        float leftX;
        float topY;

        for (int i = 0; i < numberCellsAcross; i++) {

            leftX = stickyColumnWidth + i * dataCellWidth - xOffset;
            topY = stickyHeaderHeight + activeDataWindowHeight;

            if (leftX < stickyColumnWidth + activeDataWindowWidth) {
                stickyFooterCell
                        .draw(canvas, "" + footers[startIndex + i], leftX, topY, dataCellWidth, stickyFooterHeight);
            }
        }
    }


    private void drawDataCells(Canvas canvas) {
        if (dataCellWidth == 0f || dataCellHeight == 0f || dataCell == null || objectData == null) {
            return;
        }

        int startValuesIndex;

        if (windowScrollX == 0f) {
            startValuesIndex = numberStickyColumns;
        } else {
            startValuesIndex = (int) (numberStickyColumns + windowScrollX / dataCellWidth);
        }

        float xOffset = windowScrollX % dataCellWidth;

        int startObjectsIndex;

        if (windowScrollY == 0f) {
            startObjectsIndex = 0;
        } else {
            startObjectsIndex = (int) (windowScrollY / dataCellHeight);
        }

        float yOffset = windowScrollY % dataCellHeight;

        int numberCellsDown = (int) (activeDataWindowHeight / dataCellHeight + 2);
        if (numberCellsDown + startObjectsIndex > dataSizeObjects) {
            numberCellsDown = dataSizeObjects - startObjectsIndex;
        }
        int numberCellsAcross = (int) (activeDataWindowWidth / dataCellWidth + 2);
        if (numberCellsAcross + startValuesIndex > dataSizeValues) {
            numberCellsAcross = dataSizeValues - startValuesIndex;
        }

        for (int i = 0; i < numberCellsAcross; i++) {
            for (int j = 0; j < numberCellsDown; j++) {
                float leftX = stickyColumnWidth + i * dataCellWidth - xOffset;
                float topY = stickyHeaderHeight + j * dataCellHeight - yOffset;
                if (leftX < stickyColumnWidth + activeDataWindowWidth
                        && topY < stickyHeaderHeight + activeDataWindowHeight) {
                    if (objectData[startObjectsIndex + j] != null) {
                        dataCell.draw(canvas, "" + objectData[startObjectsIndex + j].getValueAt(startValuesIndex + i),
                                leftX, topY, dataCellWidth, dataCellHeight);
                    }
                }
            }
        }
    }

    public float getStickyTableWidth() {
        float width = stickyColumnWidth + activeDataWindowWidth;
        return width;
    }

    public float getStickyTableHeight() {
        float height = stickyHeaderHeight + activeDataWindowHeight + stickyFooterHeight;
        return height;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // Log.d(ScrollActivity.DEBUG,"Gesture DOWN");
        if (!scroller.isFinished()) {
            scroller.forceFinished(true);
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // Log.d(ScrollActivity.DEBUG,"Gesture SHOWPRESS");

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
        int velX = (int) (velocityX / SCALE);

        int velY = (int) (velocityY / SCALE);

        scroller.fling((int) windowScrollX, (int) windowScrollY, velX, velY, 0, (int) (maxWindowScrollX), 0,
                (int) (maxWindowScrollY));
        postInvalidate();
        return false;
    }


    public int getNumberStickyColumns() {
        return this.numberStickyColumns;
    }

    public void setStickyColumnWidth(int columnIndex, float width) {
        if (this.stickyColumnWidths == null || columnIndex + 1 > stickyColumnWidths.length) {
            return;
        }
        this.stickyColumnWidths[columnIndex] = width;
        calculateStickyColumnWidth();
        adjustTable();
    }

    public float getStickyColumnWidth(int columnIndex) {
        if (columnIndex + 1 > stickyColumnWidths.length) {
            return 0f;
        }
        return this.stickyColumnWidths[columnIndex];
    }

    public void setNumberStickyColumns(int number) {
        if (number > dataSizeValues) {
            number = dataSizeValues;
        }
        if (number < 0) {
            number = 0;
        }
        this.numberStickyColumns = number;

        calculateStickyColumnWidth();
        adjustTable();
    }

    public void incStickyColumnWidth(int columnIndex, float dw) {
        this.stickyColumnWidths[columnIndex] += dw;
        calculateStickyColumnWidth();
        adjustTable();
    }

    private void calculateStickyColumnWidth() {
        float sum = 0;
        for (int i = 0; i < numberStickyColumns; i++) {
            sum = sum + getStickyColumnWidth(i);
        }
        this.stickyColumnWidth = sum;
    }


    public void setStickyHeaderHeight(float height) {
        this.stickyHeaderHeight = height;
        adjustTable();
    }

    public void setStickyHeaderTextSize(float size) {
        stickyHeaderCell.setTextSize(size);
    }

    public void setStickyFooterTextSize(float size) {
        stickyFooterCell.setTextSize(size);
    }

    public void setStickyColumnTextSize(float size) {
        stickyColumnCell.setTextSize(size);
    }

    public float getStickyFooterHeight() {
        return this.stickyFooterHeight;
    }

    public void setDataCellTextSize(float size) {
        dataCell.setTextSize(size);
    }

    public void incStickyHeaderHeight(float dh) {
        this.stickyHeaderHeight = this.stickyHeaderHeight + dh;
        adjustTable();
    }

    public void setStickyFooterHeight(float height) {
        this.stickyFooterHeight = height;
        adjustTable();
    }

    public void incStickyFooterHeight(float dh) {
        this.stickyFooterHeight = this.stickyFooterHeight + dh;
        adjustTable();
    }

    public void setDataCellDimensions(float width, float height) {
        this.dataCellWidth = width;
        this.dataCellHeight = height;
        adjustTable();
    }


    public void setValuesColumn(int headerIndex, String[] column) {
        for (int i = 0; i < dataSizeObjects; i++) {
            objectData[i].setValue(headerIndex, column[i]);
        }
        updateFooter(headerIndex);
    }

    public void setFooters(String[] footers) {
        this.footers = footers;
    }


    public void setRow(int objectIndex, String[] row) {
        for (int i = 0; i < dataSizeValues; i++) {
            objectData[objectIndex].setValue(objectIndex, row[i]);
        }

        for (int i = 0; i < dataSizeValues; i++) {
            updateFooter(i);
        }
    }

    public void setRow(int objectIndex, SpreadsheetRow row) {
        objectData[objectIndex] = row;
        for (int i = 0; i < dataSizeValues; i++) {
            updateFooter(i);
        }
    }

    public SpreadsheetRow getRowAt(int objectIndex) {
        return objectData[objectIndex];
    }

    public String getFooterAt(int valueIndex) {
        return footers[valueIndex];

    }

    public String getHeaderAt(int valueIndex) {
        return headers[valueIndex];
    }

    public void setLeftFootCornerCell(SpreadsheetCell cell) {
        this.leftFootCornerCell = cell;
    }

    public SpreadsheetCell getLeftFootCornerCell() {
        return leftFootCornerCell;
    }


    public void setLeftHeadCornerCell(SpreadsheetCell cell) {
        this.leftHeadCornerCell = cell;
    }

    public SpreadsheetCell getLeftHeadCornerCell() {
        return leftHeadCornerCell;
    }

    public void setDataCell(SpreadsheetCell cell) {
        this.dataCell = cell;
    }

    public SpreadsheetCell getDataCell() {
        return dataCell;
    }

    public void setStickyColumnCell(SpreadsheetCell cell) {
        this.stickyColumnCell = cell;
    }

    public SpreadsheetCell getStickyColumnCell() {
        return stickyColumnCell;
    }

    public void setStickyHeaderCell(SpreadsheetCell cell) {
        this.stickyHeaderCell = cell;
    }

    public SpreadsheetCell getStickyHeaderCell() {
        return stickyHeaderCell;
    }

    public void setStickyFooterCell(SpreadsheetCell cell) {
        this.stickyFooterCell = cell;
    }

    public SpreadsheetCell getStickyFooterCell() {
        return stickyFooterCell;
    }

    public void setHorizontalDividingPaint(Paint paint) {
        this.horzDividingPaint = paint;
    }

    public Paint getHorizontalDividingPaint() {
        return horzDividingPaint;
    }

    public void setVerticalDividingPaint(Paint paint) {
        this.vertDividingPaint = paint;
    }

    public Paint getVerticalDividingPaint() {
        return vertDividingPaint;
    }
}