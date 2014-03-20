package oak.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Activity that takes a url and displays it inside an OakWebViewFragment with Navigation
 * Created by ericrichardson on 1/9/14.
 */
public class WebViewActivity extends FragmentActivity {
    public static final String EXTRA_URL = "oak_url";
    private String urlToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        urlToLoad = getIntent().getExtras().getString(EXTRA_URL);
        try {
            URL url = new URL(urlToLoad);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Please provide a valid url to the WebViewActivity getIntent() or startWebActivity() method");
        }
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(android.R.id.content, OakWebViewFragment.getInstance(urlToLoad));
            fragmentTransaction.commit();
        }
    }

    public static Intent getIntent(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    public static void startWebActivity(Context context, String url) {
        context.startActivity(getIntent(context, url));
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
