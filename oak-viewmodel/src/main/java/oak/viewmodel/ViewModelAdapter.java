package oak.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * User: evantatarka Date: 9/25/13 Time: 4:31 PM
 * <p/>
 * A basic adapter that follows the ViewModel pattern. If you only need to show a list of
 * homogeneous items you should use or subclass this. The layout that you pass must be a custom
 * ViewGroup that implements {@code ViewModel<T>}.
 */
public class ViewModelAdapter<T> extends BaseAdapter {
    private Context mContext;
    private int mLayoutId;
    private List<T> mItems;

    /**
     * Constructs a new ViewModelAdapter with the given layout and items. The layout must implement
     * {@code ViewModel<T>} as is expected to hold the logic of updating its children.
     * <p/>
     * Note that this class does not make a copy of the list you pass in. You can modify the
     * original list and call {@code notifyDataSetChanged()} to update the view.
     *
     * @param context  the context
     * @param layoutId the layout to inflate
     * @param items    the list of items to show
     */
    public ViewModelAdapter(Context context, int layoutId, List<T> items) {
        mContext = context;
        mLayoutId = layoutId;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public T getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewModel<T> view = ViewModelUtil.getViewModel(LayoutInflater.from(mContext), mLayoutId, convertView, parent);
        view.populate(getItem(i));
        return (View) view;
    }

    /**
     * Returns the backing list. This is the same one that is passed into the constructor.
     *
     * @return the list of items
     */
    public List<T> getItems() {
        return mItems;
    }

    public void replace(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    protected Context getContext() {
        return mContext;
    }
}

