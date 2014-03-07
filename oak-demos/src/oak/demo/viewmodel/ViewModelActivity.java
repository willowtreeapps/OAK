package oak.demo.viewmodel;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.viewmodel.ViewModelAdapter;

/**
 * Created by ericrichardson on 3/7/14.
 */
public class ViewModelActivity extends OakDemoActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewmodel);
        ListView list = (ListView) findViewById(R.id.listView);
        List<String> strings = new ArrayList<String>();
        strings.add("Look");
        strings.add("at");
        strings.add("all");
        strings.add("the");
        strings.add("pretty");
        strings.add("strings");
        strings.add("They");
        strings.add("are");
        strings.add("so");
        strings.add("stringy");
        strings.add("mainly");
        strings.add("because");
        strings.add("they");
        strings.add("are");
        strings.add("strings");
        list.setAdapter(new ViewModelAdapter<String>(this, R.layout.list_viewmodelitem, strings));
    }
}