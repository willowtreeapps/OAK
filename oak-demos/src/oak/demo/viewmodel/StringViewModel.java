package oak.demo.viewmodel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import oak.demo.R;
import oak.viewmodel.ViewModel;

/**
 * Created by ericrichardson on 3/7/14.
 */
public class StringViewModel extends RelativeLayout implements ViewModel<String> {
    TextView tv;

    public StringViewModel(Context context) {
        super(context);
    }

    public StringViewModel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StringViewModel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv = (TextView) findViewById(R.id.textView);
    }

    @Override
    public void populate(String item) {
        tv.setText(item);
    }
}
