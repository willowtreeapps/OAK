package oak.web;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by ericrichardson on 1/9/14.
 */
public class OakWebViewFragment extends Fragment {

    private int layoutId;
    private String url;

    public static OakWebViewFragment getInstance(String url) {
        OakWebViewFragment fragment = new OakWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(WebViewActivity.EXTRA_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout root = new RelativeLayout(getActivity());

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
