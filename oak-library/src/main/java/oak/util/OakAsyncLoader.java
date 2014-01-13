package oak.util;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;


/**
 * AsyncTaskLoader that actually works as you would think.
 * Created by ericrichardson on 1/7/14.
 */
public abstract class OakAsyncLoader<T> extends AsyncTaskLoader<T> {
    T mData;


    public OakAsyncLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(T data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            // This can happen when the Loader is reset while an asynchronous query
            // is working in the background. That is, when the background thread
            // finishes its work and attempts to deliver the results to the client,
            // it will see here that the Loader has been reset and discard any
            // resources associated with the new data as necessary.
            if (data != null) {
                return;
            }
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // The old data may still be in use (i.e. bound to an adapter, etc.), so
        // we must protect it until the new data has been delivered.
        T oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, have the superclass deliver the
            // results to the client.
            super.deliverResult(data);
        }

    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

        if (takeContentChanged()) {
            // When the observer detects a new installed application, it will call
            // onContentChanged() on the Loader, which will cause the next call to
            // takeContentChanged() to return true. If this is ever the case (or if
            // the current data is null), we force a new load.
            forceLoad();
        } else if (mData == null) {
            // If the current data is null... then we should make it non-null! :)
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mData != null) {
            mData = null;
        }
    }
}
