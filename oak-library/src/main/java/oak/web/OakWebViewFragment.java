package oak.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URL;

import oak.OAK;
import oak.R;

/**
 * Created by ericrichardson on 1/9/14.
 */
public class OakWebViewFragment extends Fragment {

    private String url;
    public WebView webView;
    View refresh;
    View progress, back, fwd;
    boolean hidden, openInBrowserEnabled = false;

    public static OakWebViewFragment getInstance(String url) {
        return getInstance(url, false);
    }

    public static OakWebViewFragment getInstance(String url, boolean openInBrowserEnabled) {
        OakWebViewFragment fragment = new OakWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(OAK.EXTRA_URL, url);
        bundle.putBoolean(OAK.EXTRA_OPEN_IN_BROWSER, openInBrowserEnabled);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static OakWebViewFragment getInstance(URL url) {
        return getInstance(url.toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(OAK.EXTRA_URL);
        openInBrowserEnabled = getArguments().getBoolean(OAK.EXTRA_OPEN_IN_BROWSER, false);
        setHasOptionsMenu(openInBrowserEnabled);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.webview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = view.findViewById(R.id.back);
        fwd = view.findViewById(R.id.forward);
        refresh = view.findViewById(R.id.refresh);
        progress = view.findViewById(R.id.progress);
        webView = (WebView) view.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.INVISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                refresh.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);
                super.onPageFinished(view, url);
                configureButtons(view);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });
        fwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.reload();
                progress.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.INVISIBLE);
            }
        });

        webView.loadUrl(url);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (hidden) {
                    hidden = false;
                    unHide();
                } else {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hide();
                            hidden = true;
                        }
                    }, 1000);
                }
                return false;
            }
        });

    }

    @SuppressLint("NewApi")
    private void unHide() {
        configureButtons(webView);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            refresh.animate().alpha(1.0f);
            progress.animate().alpha(1.0f);
        }
    }

    @SuppressLint("NewApi")
    private void hide() {
        configureButtons(webView);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            refresh.animate().alpha(0.2f);
            progress.animate().alpha(0.2f);
        }
    }

    @SuppressLint("NewApi")
    private void configureButtons(WebView webView) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            if (webView.canGoBack()) {
                back.animate().alpha(hidden ? 0.2f : 1f);
                back.setEnabled(true);
            } else {
                back.animate().alpha(0.2f);
                back.setEnabled(false);
            }

            if (webView.canGoForward()) {
                fwd.animate().alpha(hidden ? 0.2f : 1f);
                fwd.setEnabled(true);
            } else {
                fwd.animate().alpha(0.2f);
                fwd.setEnabled(false);
            }
        }
    }

    public void setOpenInBrowserEnabled(boolean openInBrowserEnabled) {
        this.openInBrowserEnabled = openInBrowserEnabled;
        setHasOptionsMenu(openInBrowserEnabled);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(Menu.NONE, R.id.oak_menu_open_in_broswer, Menu.NONE, R.string.open_in_browser);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.oak_menu_open_in_broswer) {
            Intent toBroswer = new Intent(Intent.ACTION_VIEW);
            toBroswer.setData(Uri.parse(webView.getUrl()));
            startActivity(toBroswer);
        }
        return super.onOptionsItemSelected(item);
    }
}
