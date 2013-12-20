package oak.widget.StickySpreadSheet;

import android.graphics.*;
import android.graphics.drawable.Drawable;

public class SpreadSheetCell {

   private Paint cellPaint;
   private Paint textPaint;
   private Paint borderPaint;
   private float drawnWidth;
   private float drawnHeight;
   private float insetCellWidth;
   private float insetCellHeight;
   private StickySpreadSheet table;

   private Drawable shape;

   float horizontalBorderWidth;
   float verticalBorderWidth;

    public SpreadSheetCell(StickySpreadSheet table, Paint cellPaint, Paint textPaint, Paint borderPaint,
                           float horizontalBorderWidth, float verticalBorderWidth){
        this.cellPaint = cellPaint;
        this.textPaint = textPaint;
        textPaint.setTextAlign(Paint.Align.CENTER);
        this.borderPaint = borderPaint;
        this.horizontalBorderWidth = horizontalBorderWidth;
        this.verticalBorderWidth = verticalBorderWidth;
        this.table = table;
        this.shape = null;

    }

    public SpreadSheetCell(StickySpreadSheet table, Drawable shape, Paint textPaint, Paint borderPaint,
                           float horizontalBorderWidth, float verticalBorderWidth){
        this.table = table;
        this.shape = shape;
        this.textPaint = textPaint;
        this.horizontalBorderWidth = horizontalBorderWidth;
        this.verticalBorderWidth = verticalBorderWidth;
        this.borderPaint = borderPaint;
    }

    public void setTextSize(float size){
        textPaint.setTextSize(size);
    }

    public Paint getTextPaint(){
        return textPaint;
    }

    public void setTextPaint(Paint paint){
        textPaint = paint;
    }

    public Paint getBorderPaint(){
        return borderPaint;
    }

    public void setBorderPaint(Paint paint){
        this.borderPaint = paint;
    }

    public Paint getCellPaint(){
        return cellPaint;
    }

    public void setCellPaint(Paint paint){
        this.cellPaint = paint;
    }

    public Drawable getDrawable(){
        return this.shape;
    }

    public void setDrawable(Drawable drawable){
        this.shape = drawable;
    }

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


    public void draw(Canvas canvas, String text, float leftX, float topY, float cellWidth, float cellHeight){

        drawnWidth = cellWidth;
        drawnHeight = cellHeight;

        if (leftX+cellWidth > table.getStickyTableWidth()){
            drawnWidth = table.getStickyTableWidth() -leftX;
        }

        if (topY + cellHeight > table.getStickyTableHeight()){
            drawnHeight = table.getStickyTableHeight() - topY;

        }

        //draw the cell border
        canvas.drawRect(leftX, topY, leftX+drawnWidth, topY+drawnHeight,  borderPaint);

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
