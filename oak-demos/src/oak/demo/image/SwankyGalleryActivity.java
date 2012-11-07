package oak.demo.image;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import oak.widget.SwankyGallery;
import oak.widget.SwankyGallery.SwankyAdapter;
import oak.widget.SwankyGallery.OnGalleryPageSelectedListener;
import oak.demo.R;

/**
 * User: Nate Date: 7/9/12 Time: 11:25 AM
 */
public class SwankyGalleryActivity extends Activity {

    private int[] mImageIds = new int[] {R.drawable.swanky1, R.drawable.swanky2, R.drawable.swanky3, R.drawable.swanky4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swanky_gallery_demo);
        SwankyGallery gallery = (SwankyGallery) findViewById(R.id.swanky_gallery);
        gallery.setAdapter(new SwankyAdapter(this, mImageIds));
        gallery.setOnGalleryPageSelectedListener(new OnGalleryPageSelectedListener() {
            @Override
            public void onPageSelected(int i) {
                Toast.makeText(SwankyGalleryActivity.this, i + " selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
