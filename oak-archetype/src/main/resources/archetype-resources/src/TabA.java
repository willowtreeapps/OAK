package ${package};

import com.google.inject.Inject;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * User: aelorix Date: 6/1/12 Time: 11:40 AM
 */
public class TabA extends RoboSherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View newView = inflater.inflate(R.layout.tab_template,container,false);
        oak.TextViewWithFont text = (oak.TextViewWithFont)newView.findViewById(R.id.display_text);

        text.setText("This is tab A");
        text.setTextColor(getResources().getColor(R.color.custom_red));

        return newView;
    }
}
