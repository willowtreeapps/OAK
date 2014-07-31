package oak.demo.widget;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.widget.ResizableHeaderScrollView;

/**
 * Created by sean.kenkeremath on 7/29/14.
 */
public class ResizableHeaderScrollViewActivity extends OakDemoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resizable_header_demo);
        ResizableHeaderScrollView view = (ResizableHeaderScrollView) findViewById(R.id.scrollview);
        view.setHeaderChangeListener(new ResizableHeaderScrollView.HeaderChangeListener(){

            @Override
            public void collapse(View header) {
                header.findViewById(R.id.header_subtitle).setVisibility(View.GONE);
            }

            @Override
            public void expand(View header) {
                header.findViewById(R.id.header_subtitle).setVisibility(View.VISIBLE);
            }
        });
    }

}
