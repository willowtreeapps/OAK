package oak.demo.verticalpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

import oak.demo.R;

public class ColorFragment extends Fragment {

    public static int[] colors = new int[]{0xffff0000, 0xff00ff00, 0xff0000ff};

    public static Fragment newInstance(int color) {
        Bundle bundle = new Bundle();
        bundle.putInt("key", color);
        ColorFragment colorFragment = new ColorFragment();
        colorFragment.setArguments(bundle);
        return colorFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.color).setBackgroundColor(colors[new Random().nextInt(colors.length - 1)]);
    }
}
