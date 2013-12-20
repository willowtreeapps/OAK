package oak.widget.StickySpreadSheet;

public class SpreadSheetRow {

    private String[] values;

    public SpreadSheetRow(int length){
        values = new String[length];
    }

    public SpreadSheetRow(String[] values){
        this.values = values;
    }

    public String getValueAt(int valueIndex){
        return this.values[valueIndex];
    }

    public void setValue(int valueIndex, String value){
        this.values[valueIndex] = value;
    }

    public int getLength(){
        return values.length;
    }

}
