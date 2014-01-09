package oak.viewmodel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ericrichardson on 1/9/14.
 */
public class ViewModelUtil {
    private ViewModelUtil() {
    }

    /**
     * A utility function for getting a ViewModel out of a view in an adapter. If convertView is
     * null, the view is inflated, otherwise convertView is returned. (The view to inflate must
     * implement ViewModel).
     * <p/>
     * A standard implementation would call this, populate the ViewModel, then return it.
     * <pre>
     *     ViewModel{@code <T>} view = getViewModel(inflater, layoutId, convertView, parent);
     *     view.populate(getItem(position));
     *     return (View) view;
     * </pre>
     *
     * @param inflater    the view inflater
     * @param layoutId    the layout to inflate
     * @param convertView the convertView from adapter.getView()
     * @param parent      the parent from adapter.getView()
     * @param <T>         the type of item in the ViewModel
     * @return the ViewModel
     */
    public static <T> ViewModel<T> getViewModel(LayoutInflater inflater, int layoutId, View convertView, ViewGroup parent) {
        if (convertView == null) {
            return (ViewModel<T>) inflater.inflate(layoutId, parent, false);
        } else {
            return (ViewModel<T>) convertView;
        }
    }
}
