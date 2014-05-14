package oak.widget.spreadsheetview;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 *  This cell is used within the SpreadsheetView
 */

public class SpreadsheetCell {

    private Paint cellPaint;
    private Paint selectedCellPaint;

    private Paint textPaint;
    private Paint selectedTextPaint;

    private Paint borderPaint;
    private Paint selectedBorderPaint;

    private float drawnWidth;
    private float drawnHeight;
    private float insetCellWidth;
    private float insetCellHeight;
    private SpreadsheetView table;

    private Drawable shape;
    private Drawable selectedShape;

    private float horizontalBorderWidth;
    private float verticalBorderWidth;

    public SpreadsheetCell(SpreadsheetView table, Paint cellPaint, Paint textPaint, Paint borderPaint,
                           float horizontalBorderWidth, float verticalBorderWidth){
        this.cellPaint = cellPaint;
        this.selectedCellPaint = new Paint(cellPaint);
        this.textPaint = textPaint;
        this.selectedTextPaint = new Paint(textPaint);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.selectedTextPaint.setTextAlign(Paint.Align.CENTER);
        this.borderPaint = borderPaint;
        this.selectedBorderPaint = new Paint(borderPaint);
        this.horizontalBorderWidth = horizontalBorderWidth;
        this.verticalBorderWidth = verticalBorderWidth;
        this.table = table;
        this.shape = null;

    }

    public SpreadsheetCell(SpreadsheetView table, Drawable shape, Drawable selectedShape, Paint borderPaint,
                           Paint textPaint, float horizontalBorderWidth, float verticalBorderWidth){
        this.table = table;
        this.shape = shape;
        this.selectedShape = selectedShape;
        this.textPaint = textPaint;
        this.borderPaint = borderPaint;
        this.selectedTextPaint = new Paint(textPaint);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.selectedTextPaint.setTextAlign(Paint.Align.CENTER);
        this.horizontalBorderWidth = horizontalBorderWidth;
        this.verticalBorderWidth = verticalBorderWidth;
        this.selectedBorderPaint = new Paint(borderPaint);
    }

    /**
     * Set the text size for the cells
     *
     * @param size
     */
    public void setTextSize(float size){
        textPaint.setTextSize(size);
        selectedTextPaint.setTextSize(size);
    }

    public Paint getTextPaint(){
        return textPaint;
    }

    /**
     * Sets custom paint parameter to the text in each cell
     *
     * @param paint
     */
    public void setTextPaint(Paint paint){
        textPaint = paint;
    }

    /**
     * Seta custom typeface to the text
     *
     * @param typeface
     */
    public void setTypeface(Typeface typeface){
        textPaint.setTypeface(typeface);
    }
    public Typeface getTypeface(){
        return textPaint.getTypeface();
    }

    /**
     * Sets time in seconds for how long after touch buttons fade out
     *
     * @param typeface
     */
    public void setSelectedTypeface(Typeface typeface){
        selectedTextPaint.setTypeface(typeface);
    }

    /**
     * Sets cusotm color to the text of each cell
     *
     * @param color
     */
    public void setTextColor(int color){
        textPaint.setColor(color);
    }
    public int getTextColor(){
        return textPaint.getColor();
    }

    public Paint getSelectedTextPaint(){
        return selectedTextPaint;
    }


    /**
     * Set custom paint to the selected text of each cell
     *
     * @param paint
     */
    public void setSelectedTextPaint(Paint paint){ this.selectedTextPaint = paint;}

    /**
     * Set custom color to the selected text of each cell
     *
     * @param color
     */
    public void setSelectedTextColor(int color){
        selectedTextPaint.setColor(color);
    }
    public int getSelectedTextColor(){
        return selectedTextPaint.getColor();
    }

    /**
     * Set the text size of the selected text of each cell
     *
     * @param size
     */
    public void setSelectedTextSize(float size){
        selectedTextPaint.setTextSize(size);
    }
    public float getSelectedTextSize(){
        return selectedTextPaint.getTextSize();
    }


    public Paint getBorderPaint(){
        return borderPaint;
    }

    /**
     * Set custom paint used as the border of eachc ell
     *
     * @param paint
     */
    public void setBorderPaint(Paint paint){
        this.borderPaint = paint;
    }

    public Paint getSelectedBorderPaint(){ return selectedBorderPaint;}

    /**
     * Sets a custom paint border to a selected cell
     *
     * @param paint
     */
    public void setSelectedBorderPaint(Paint paint) { this.selectedBorderPaint = paint;}

    public Paint getCellPaint(){
        return cellPaint;
    }

    /**
     * Set custom paint to each cell
     *
     * @param paint
     */
    public void setCellPaint(Paint paint){
        this.cellPaint = paint;
    }

    public Paint getSelectedCellPaint(){ return selectedCellPaint;}

    /**
     * Set custom paint to a cell used when selected
     *
     * @param paint
     */
    public void setSelectedCellPaint(Paint paint){ this.selectedCellPaint = paint;}

    public Drawable getDrawable(){
        return this.shape;
    }

    /**
     * Set custom drawable used for each cell
     *
     * @param drawable
     */
    public void setDrawable(Drawable drawable){
        this.shape = drawable;
    }

    public Drawable getSelectedDrawable() { return this.selectedShape;}

    /**
     * Set a custom drawable used when a cell is selected
     *
     * @param drawable
     */
    public void setSelectedDrawable(Drawable drawable) { this.selectedShape = drawable;}

    /**
     * Set the horizontal border width for each cell
     *
     * @param width
     */
    public void setHorizontalBorderWidth(float width){
        this.horizontalBorderWidth = width;
    }

    public float getHorizontalBorderWidth(){
        return this.horizontalBorderWidth;
    }

    /**
     * Set the vertical border width for each cell
     *
     * @param width
     */
    public void setVerticalBorderWidth(float width){
        this.verticalBorderWidth = width;
    }

    public float getVerticalBorderWidth(){
        return this.verticalBorderWidth;
    }


    public void draw(Canvas canvas, String text, float leftX, float topY, float cellWidth,
                     float cellHeight, boolean selected){

        drawnWidth = cellWidth;
        drawnHeight = cellHeight;

        /*
        if (leftX+cellWidth > table.getStickyTableWidth()){
            drawnWidth = table.getStickyTableWidth() -leftX;
        }

        if (topY + cellHeight > table.getStickyTableHeight()){
            drawnHeight = table.getStickyTableHeight() - topY;

        }

        */

        //draw the cell border
        if (selected && selectedBorderPaint!=null){
            canvas.drawRect(leftX, topY, leftX+drawnWidth, topY+drawnHeight,  selectedBorderPaint);
        } else if (borderPaint!=null){
            canvas.drawRect(leftX, topY, leftX+drawnWidth, topY+drawnHeight,  borderPaint);
        }

        //draw the cell itself

        insetCellHeight = cellHeight-verticalBorderWidth*2;
        /*
        if (insetCellHeight + verticalBorderWidth > drawnHeight){
            insetCellHeight = drawnHeight - verticalBorderWidth;
        }
        */
        if (insetCellHeight<0){
            insetCellHeight =0;
        }


        insetCellWidth = cellWidth - horizontalBorderWidth*2;
        /*
        if (insetCellWidth + horizontalBorderWidth>drawnWidth){
            insetCellWidth = drawnWidth - horizontalBorderWidth;
        }
        */
        if (insetCellWidth<0){
            insetCellWidth = 0;
        }


        if (selected){
            if (selectedShape!=null){

                selectedShape.setBounds((int)(leftX+horizontalBorderWidth),(int)(topY+verticalBorderWidth),
                        (int)(leftX+horizontalBorderWidth+insetCellWidth),
                        (int) (topY+verticalBorderWidth+insetCellHeight));

                selectedShape.draw(canvas);
            } else{
                canvas.drawRect(leftX+horizontalBorderWidth, topY+verticalBorderWidth,
                        leftX+horizontalBorderWidth+insetCellWidth,
                        topY+verticalBorderWidth+insetCellHeight,selectedCellPaint);

            }


            if (text==null){
                text = "null";
            }
            canvas.drawText(text, leftX+cellWidth/2, topY+cellHeight/2, selectedTextPaint); //may draw out of bounds

        } else{

            if (shape!=null){

                shape.setBounds((int)(leftX+horizontalBorderWidth),(int)(topY+verticalBorderWidth),
                        (int)(leftX+horizontalBorderWidth+insetCellWidth),
                        (int) (topY+verticalBorderWidth+insetCellHeight));

                shape.draw(canvas);
            } else{
                canvas.drawRect(leftX+horizontalBorderWidth, topY+verticalBorderWidth,
                        leftX+horizontalBorderWidth+insetCellWidth,
                        topY+verticalBorderWidth+insetCellHeight,cellPaint);

            }


            if (text==null){
                text = "null";
            }
            canvas.drawText(text, leftX+cellWidth/2, topY+cellHeight/2, textPaint); //may draw out of bounds

        }

    }
}
