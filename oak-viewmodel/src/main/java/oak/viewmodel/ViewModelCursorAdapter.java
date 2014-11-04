package oak.viewmodel;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ericrichardson on 3/6/14.
 */
public class ViewModelCursorAdapter extends CursorAdapter {
    int mLayoutId;

    public ViewModelCursorAdapter(Context context, Cursor c, boolean autoRequery, int layoutId) {
        super(context, c, autoRequery);
        mLayoutId = layoutId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewModel<Cursor> view = ViewModelUtil.getViewModel(LayoutInflater.from(mContext), mLayoutId, parent);
        return (View) view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((ViewModel<Cursor>) view).populate(cursor);
    }
}
