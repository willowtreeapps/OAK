package oak.web;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import oak.R;

/**
 * Created by ericrichardson on 1/9/14.
 */
public class OakWebViewFragment extends Fragment {

    private String url;
    public WebView webView;
    View refresh;
    View progress, back, fwd;
    boolean hidden;

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
        url = getArguments().getString(WebViewActivity.EXTRA_URL);
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
                view.loadUrl(url);
                return true;
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
}
