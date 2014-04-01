package oak.demo.fragmenthostactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import oak.demo.R;

/**
 * Created by robcook on 3/31/14.
 */
public class ShowTextSupportFragment extends Fragment {

    private static final String PASSED_TEXT_KEY = "ShowTextFragment_PassedTextKey";

    TextView passedTextView;

    String passedText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseArgs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreateView(inflater, parent, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_show_text, parent, false);
        passedTextView = (TextView)view.findViewById(R.id.passed_text);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!TextUtils.isEmpty(passedText)) {
            passedTextView.setText(passedText);
        }
    }

    public static Bundle getArguments(String passedText) {
        Bundle bundle = new Bundle();
        bundle.putString(PASSED_TEXT_KEY, passedText);
        return bundle;
    }

    private void parseArgs() {
        Bundle arguments = getArguments();

        if (arguments.containsKey(PASSED_TEXT_KEY)) {
            passedText = arguments.getString(PASSED_TEXT_KEY);
        } else {
            passedText = "";
        }
    }

}
