package oak.demo.utils;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;

import java.util.Random;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.util.FontTypefaceSpan;

/**
 * Created by ericrichardson on 9/25/13.
 */
public class FontTypeFaceSpanActivity extends OakDemoActivity {

    private String[] fonts = new String[]{
            "Apple Chancery.ttf",
            "LiberationMono-Regular.ttf",
            "Once_upon_a_time.ttf",
            "Roboto.ttf",
            "Roboto-Bold.ttf",
            "Roboto-Light.ttf",
            "Roboto-Thin.ttf"};

    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.font_span);
        title = getSupportActionBar().getTitle().toString();
        findViewById(R.id.font_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyFont();
            }
        });
    }

    private void applyFont(){
        SpannableString s = new SpannableString(title);
        s.setSpan(new FontTypefaceSpan(this, fonts[new Random().nextInt(fonts.length-1)]), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
    }
}
