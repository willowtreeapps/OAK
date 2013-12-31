package oak.widget.spreadsheetview;

public class SpreadsheetRow {

    private String[] values;
    private boolean[] selected;

    public SpreadsheetRow(int length){
        values = new String[length];
        selected = new boolean[length];
        for (int i = 0; i < selected.length; i++){
            selected[i] = false;
        }
    }

    public SpreadsheetRow(String[] values){
        this.values = values;
        this.selected = new boolean[values.length];
        for (int i = 0; i < selected.length; i++){
            selected[i] = false;
        }
    }


    public String getValueAt(int valueIndex){
        return this.values[valueIndex];
    }

    public void setValue(int valueIndex, String value){
        this.values[valueIndex] = value;
    }

    public boolean isSelected(int valueIndex){
        return this.selected[valueIndex];
    }

    public void select(int valueIndex, boolean select){
        this.selected[valueIndex] = select;
    }

    public void selectRow(boolean select){
        for (int i = 0; i < selected.length; i++){
            selected[i] = select;
        }
    }

    public int getLength(){
        return values.length;
    }
}
