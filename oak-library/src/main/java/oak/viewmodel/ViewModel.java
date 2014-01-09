package oak.viewmodel;

/**
 * User: evantatarka Date: 9/25/13 Time: 4:30 PM
 * <p/>
 * An interface to mark a view that implements the ViewModel adapter pattern. All implementations of
 * this must subclass an android ViewGroup class as they are expected to be inflated from xml.
 * <p/>
 * You should override {@code onFinishInflate()} to get references to your children views. Then set
 * up the display of the view in {@code populate()}.
 */
public interface ViewModel<T> {
    /**
     * Sets the data of the view to the given item. This is expected to be called multiple times as
     * the view is recycled.
     *
     * @param item the item to populate the view with
     */
    void populate(T item);
}
