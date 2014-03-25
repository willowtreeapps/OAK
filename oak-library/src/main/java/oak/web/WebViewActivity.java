package oak.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import oak.OAK;

/**
 * Activity that takes a url and displays it inside an OakWebViewFragment with Navigation
 * Created by ericrichardson on 1/9/14.
 */
public class WebViewActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (!getIntent().hasExtra(OAK.EXTRA_URL)) {
                throw new IllegalArgumentException("You must include a URL extra using OAK.EXTRA_URL in the bundle for this activity");
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            OakWebViewFragment.BundleBuilder builder = new OakWebViewFragment.BundleBuilder(getIntent().getExtras());
            builder.openInBrowserEnabled(true);
            fragmentTransaction.add(android.R.id.content, OakWebViewFragment.getInstance(builder.build()));
            fragmentTransaction.commit();
        }
    }

    public static Intent getIntent(Context context, Bundle bundle) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    public static void startWebActivity(Context context, String url) {
        OakWebViewFragment.BundleBuilder builder = new OakWebViewFragment.BundleBuilder(url);
        context.startActivity(getIntent(context, builder.build()));
    }

    public static void startWebActivity(Context context, Bundle bundle) {
        context.startActivity(getIntent(context, bundle));
    }
    @Override
    public void onBackPressed() {
        if (((OakWebViewFragment) getSupportFragmentManager().findFragmentById(android.R.id.content)).canGoBack()) {
            ((OakWebViewFragment) getSupportFragmentManager().findFragmentById(android.R.id.content)).back();
        } else {
            super.onBackPressed();
        }
    }
}
