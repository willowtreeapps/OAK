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

import oak.OAK;
import oak.R;

/**
 * Created by ericrichardson on 1/9/14.
 */
public class OakWebViewFragment extends Fragment {

    public static final int PROVIDED_LAYOUT = 0;
    private String url;
    public WebView webView;
    private View refresh, progress, back, fwd;
    private boolean hidden, openInBrowserEnabled = false;
    private boolean hideControls = true;
    private int fadeTimeout = 1500;
    private float fadeoutMinimum = 0.2f;
    private float fadeoutMaximum = 1.0f;


    /**
     * Sets whether buttons fade out after touch
     *
     * @param hideControls
     */
    public void setHideControls(boolean hideControls) {
        this.hideControls = hideControls;
    }

    /**
     * Sets time in seconds for how long after touch buttons fade out
     *
     * @param fadeTimeout
     */
    public void setFadeTimeout(int fadeTimeout) {
        this.fadeTimeout = fadeTimeout;
    }

    /**
     * Sets maximum alpha when buttons fade in
     *
     * @param maxAlpha
     */
    public void setMaximumAlpha(float maxAlpha) {
        this.fadeoutMaximum = maxAlpha;
    }

    /**
     * Set's minimum alpha when buttons fade out
     *
     * @param minAlpha
     */
    public void setMinimumAlpha(float minAlpha) {
        this.fadeoutMinimum = minAlpha;
    }

    public static OakWebViewFragment getInstance(String url) {
        return getInstance(url, true);
    }

    public static OakWebViewFragment getInstance(String url, boolean openInBrowserEnabled) {
        return getInstance(url, openInBrowserEnabled, PROVIDED_LAYOUT);
    }

    public static OakWebViewFragment getInstance(String url, boolean openInBrowserEnabled, int layoutId) {
        OakWebViewFragment fragment = new OakWebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(OAK.EXTRA_URL, url);
        bundle.putBoolean(OAK.EXTRA_OPEN_IN_BROWSER, openInBrowserEnabled);
        if (layoutId > PROVIDED_LAYOUT) {
            bundle.putInt(OAK.EXTRA_LAYOUT, layoutId);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(OAK.EXTRA_URL);
        setOpenInBrowserEnabled(getArguments().getBoolean(OAK.EXTRA_OPEN_IN_BROWSER, true));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getArguments().containsKey(OAK.EXTRA_LAYOUT) ? getArguments().getInt(OAK.EXTRA_LAYOUT) : R.layout.webview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        back = view.findViewById(R.id.back);
        fwd = view.findViewById(R.id.forward);
        refresh = view.findViewById(R.id.refresh);
        progress = view.findViewById(R.id.progress);
        webView = (WebView) view.findViewById(R.id.webview);
        if (webView == null) {
            throw new IllegalStateException("Layout used with this webview must contain a WebView with the id R.id.webview");
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
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
                if (refresh != null) {
                    refresh.setVisibility(View.INVISIBLE);
                }
                if (progress != null) {
                    progress.setVisibility(View.VISIBLE);
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (refresh != null) {
                    refresh.setVisibility(View.VISIBLE);
                }
                if (progress != null) {
                    progress.setVisibility(View.INVISIBLE);
                }
                super.onPageFinished(view, url);
                configureButtons(view);
            }
        });
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (webView.canGoBack()) {
                        webView.goBack();
                    }
                }
            });
        }
        if (fwd != null) {
            fwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (webView.canGoForward()) {
                        webView.goForward();
                    }
                }
            });
        }
        if (refresh != null) {
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    webView.reload();
                    progress.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.INVISIBLE);
                }
            });
        }
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
                    }, fadeTimeout);
                }
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView.getSettings().getBuiltInZoomControls()) {
            hideControls = false;
        }
    }

    @SuppressLint("NewApi")
    private void unHide() {
        configureButtons(webView);

        if (hideControls) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                if (refresh != null) {
                    refresh.animate().alpha(fadeoutMaximum);
                }
                if (progress != null) {
                    progress.animate().alpha(fadeoutMaximum);
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private void hide() {
        configureButtons(webView);

        if (hideControls) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                if (refresh != null) {
                    refresh.animate().alpha(fadeoutMinimum);
                }
                if (progress != null) {
                    progress.animate().alpha(fadeoutMinimum);
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private void configureButtons(WebView webView) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            if (back != null) {
                if (webView.canGoBack()) {
                    back.animate().alpha(hidden && hideControls ? fadeoutMinimum : fadeoutMaximum);
                    back.setEnabled(true);
                } else {
                    back.animate().alpha(fadeoutMinimum);
                    back.setEnabled(false);
                }
            }
            if (fwd != null) {
                if (webView.canGoForward()) {
                    fwd.animate().alpha(hidden && hideControls ? fadeoutMinimum : fadeoutMaximum);
                    fwd.setEnabled(true);
                } else {
                    fwd.animate().alpha(fadeoutMinimum);
                    fwd.setEnabled(false);
                }
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
