package ${package};

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import oak.widget.TextViewWithFont;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

public class TabA extends RoboSherlockListFragment {

    private String[] dataArray = new String[]{"This", "Is", "An", "Android", "App!"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListAdapter listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, dataArray);
        setListAdapter(listAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_list_fragment, container, false);
    }

    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {
        Toast.makeText(getSherlockActivity(), getListView().getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
    }
}
