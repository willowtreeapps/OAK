package ${package};

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * User: aelorix Date: 6/1/12 Time: 11:40 AM
 */
public class TabC extends RoboSherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View newView = inflater.inflate(R.layout.tab_template,container,false);
        oak.TextViewWithFont text = (oak.TextViewWithFont)newView.findViewById(R.id.display_text);

        text.setText("And then there's tab C");
        text.setTextColor(getResources().getColor(R.color.custom_blue));

        return newView;
    }
}
