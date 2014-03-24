package oak.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;

import oak.OAK;

/**
 * Activity that takes a url and displays it inside an OakWebViewFragment with Navigation
 * Created by ericrichardson on 1/9/14.
 */
public class WebViewActivity extends FragmentActivity {
    public static final String EXTRA_URL = "oak_url";
    private String urlToLoad;
    public boolean openInBrowserEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        urlToLoad = getIntent().getExtras().getString(EXTRA_URL);
        if (TextUtils.isEmpty(urlToLoad)) {
            throw new IllegalArgumentException("You much include an IntentExtra for OAK.EXTRA_URL for the URL to display");
        }
        try {
            URL url = new URL(urlToLoad);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Please provide a valid url to the WebViewActivity getIntent() or startWebActivity() method");
        }
        if (savedInstanceState == null) {
            if (getIntent().hasExtra(OAK.EXTRA_OPEN_IN_BROWSER)) {
                openInBrowserEnabled = getIntent().getBooleanExtra(OAK.EXTRA_OPEN_IN_BROWSER, false);
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(android.R.id.content, OakWebViewFragment.getInstance(urlToLoad, true, getIntent().getIntExtra(OAK.EXTRA_LAYOUT, 0)));
            fragmentTransaction.commit();
        } else {
            openInBrowserEnabled = savedInstanceState.getBoolean(OAK.EXTRA_OPEN_IN_BROWSER);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(OAK.EXTRA_OPEN_IN_BROWSER, openInBrowserEnabled);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((OakWebViewFragment) getSupportFragmentManager().findFragmentById(android.R.id.content)).setOpenInBrowserEnabled(openInBrowserEnabled);
    }

    public static Intent getIntent(Context context, String url, boolean openInBrowserEnabled, int layoutId) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(OAK.EXTRA_URL, url);
        intent.putExtra(OAK.EXTRA_OPEN_IN_BROWSER, openInBrowserEnabled);
        intent.putExtra(OAK.EXTRA_LAYOUT, layoutId);
        return intent;
    }

    public static void startWebActivity(Context context, String url, boolean openInBrowserEnabled) {
        context.startActivity(getIntent(context, url, openInBrowserEnabled, 0));
    }

    public static void startWebActivity(Context context, String url) {
        context.startActivity(getIntent(context, url, true, 0));
    }

    public static void startWebActivity(Context context, String url, int layoutId) {
        context.startActivity(getIntent(context, url, true, layoutId));
    }
    @Override
    public void onBackPressed() {
        if (((OakWebViewFragment) getSupportFragmentManager().findFragmentById(android.R.id.content)).webView.canGoBack()) {
            ((OakWebViewFragment) getSupportFragmentManager().findFragmentById(android.R.id.content)).webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
