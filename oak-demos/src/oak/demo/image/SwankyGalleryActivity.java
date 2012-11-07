package oak.demo.image;

import android.os.Bundle;
import android.widget.Toast;

import oak.demo.OakDemoActivity;
import oak.demo.R;
import oak.widget.SwankyGallery;
import oak.widget.SwankyGallery.OnGalleryPageSelectedListener;
import oak.widget.SwankyGallery.SwankyAdapter;
import roboguice.inject.InjectView;

/**
 * User: Nate Date: 7/9/12 Time: 11:25 AM
 */
public class SwankyGalleryActivity extends OakDemoActivity {

    @InjectView(R.id.swanky_gallery) SwankyGallery gallery;

    private int[] mImageIds = new int[]{R.drawable.swanky1, R.drawable.swanky2, R.drawable.swanky3, R.drawable.swanky4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swanky_gallery_demo);
        gallery.setAdapter(new SwankyAdapter(this, mImageIds));
        gallery.setOnGalleryPageSelectedListener(new OnGalleryPageSelectedListener() {
            @Override
            public void onPageSelected(int i) {
                Toast.makeText(SwankyGalleryActivity.this, i + " selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
