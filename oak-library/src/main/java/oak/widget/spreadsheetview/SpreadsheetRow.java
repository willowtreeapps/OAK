package oak.widget.spreadsheetview;

public class SpreadsheetRow {

    private String[] values;

    public SpreadsheetRow(int length){
        values = new String[length];
    }

    public SpreadsheetRow(String[] values){
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
