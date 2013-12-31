package oak.widget.spreadsheetview;

import android.graphics.*;
import android.graphics.drawable.Drawable;

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

    float horizontalBorderWidth;
    float verticalBorderWidth;

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

    public SpreadsheetCell(SpreadsheetView table, Drawable shape, Drawable selectedShape,
                           Paint textPaint, Paint borderPaint,
                           float horizontalBorderWidth, float verticalBorderWidth){
        this.table = table;
        this.shape = shape;
        this.selectedShape = selectedShape;
        this.textPaint = textPaint;
        this.selectedTextPaint = new Paint(textPaint);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.selectedTextPaint.setTextAlign(Paint.Align.CENTER);
        this.horizontalBorderWidth = horizontalBorderWidth;
        this.verticalBorderWidth = verticalBorderWidth;
        this.borderPaint = borderPaint;
        this.selectedBorderPaint = new Paint(borderPaint);
    }

    public void setTextSize(float size){
        textPaint.setTextSize(size);
        selectedTextPaint.setTextSize(size);
    }

    public Paint getTextPaint(){
        return textPaint;
    }

    public void setTextPaint(Paint paint){
        textPaint = paint;
    }

    public Paint getSelectedTextPaint(){
        return selectedTextPaint;
    }

    public void setSelectedTextPaint(Paint paint){ this.selectedTextPaint = paint;}

    public Paint getBorderPaint(){
        return borderPaint;
    }

    public void setBorderPaint(Paint paint){
        this.borderPaint = paint;
    }

    public Paint getSelectedBorderPaint(){ return selectedBorderPaint;}

    public void setSelectedBorderPaint(Paint paint) { this.selectedBorderPaint = paint;}

    public Paint getCellPaint(){
        return cellPaint;
    }

    public void setCellPaint(Paint paint){
        this.cellPaint = paint;
    }

    public Paint getSelectedCellPaint(){ return selectedCellPaint;}

    public void setSelectedCellPaint(Paint paint){ this.selectedCellPaint = paint;}

    public Drawable getDrawable(){
        return this.shape;
    }

    public void setDrawable(Drawable drawable){
        this.shape = drawable;
    }

    public Drawable getSelectedDrawable() { return this.selectedShape;}

    public void setSelectedDrawable(Drawable drawable) { this.selectedShape = shape;}

    public void setHorizontalBorderWidth(float width){
        this.horizontalBorderWidth = width;
    }

    public float getHorizontalBorderWidth(){
        return this.horizontalBorderWidth;
    }

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

        if (leftX+cellWidth > table.getStickyTableWidth()){
            drawnWidth = table.getStickyTableWidth() -leftX;
        }

        if (topY + cellHeight > table.getStickyTableHeight()){
            drawnHeight = table.getStickyTableHeight() - topY;

        }

        //draw the cell border
        if (selected){
            canvas.drawRect(leftX, topY, leftX+drawnWidth, topY+drawnHeight,  selectedBorderPaint);
        } else{
            canvas.drawRect(leftX, topY, leftX+drawnWidth, topY+drawnHeight,  borderPaint);
        }

        //draw the cell itself

        insetCellHeight = cellHeight-verticalBorderWidth*2;
        if (insetCellHeight + verticalBorderWidth > drawnHeight){
            insetCellHeight = drawnHeight - verticalBorderWidth;
        }
        if (insetCellHeight<0){
            insetCellHeight =0;
        }


        insetCellWidth = cellWidth - horizontalBorderWidth*2;
        if (insetCellWidth + horizontalBorderWidth>drawnWidth){
            insetCellWidth = drawnWidth - horizontalBorderWidth;
        }
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
