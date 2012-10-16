package ${package};

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import oak.TextViewWithFont;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

public class TabC extends RoboSherlockFragment {

    @InjectView(R.id.display_text)
        private TextViewWithFont displayText;
    @InjectResource(R.color.custom_blue)
        private int customBlue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View newView = inflater.inflate(R.layout.tab_template,container,false);
        return newView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Injection occurs in onViewCreated

        displayText.setText("And then there is tab C");
        displayText.setTextColor(customBlue);
    }
}
