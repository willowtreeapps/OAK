package oak.http;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.integralblue.httpresponsecache.HttpResponseCache;
import oak.Base64;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.message.BasicNameValuePair;
import roboguice.util.Ln;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: mlake
 * Date: 8/9/12
 * Time: 9:17 AM
 */

@Singleton
public class OakHttpTool {

    private static final String TAG = OakHttpTool.class.getName();

    Application mApplication;
    Map<String, String> mDefaultHeaders = new HashMap<String, String>();

    Map<String, String> mCredentialsMap = new HashMap<String, String>();

    private boolean mReportVersion = true;

    private static final String ANDROID_VERSION_NAME = "AndroidVersionName";

    private static final String ANDROID_VERSION_CODE = "AndroidVersionCode";

    private String appVersionName;

    private int appVersionCode;

    @Inject
    public OakHttpTool(Application application) {
        mApplication = application;

        installCache();

        try {
            appVersionName = mApplication.getPackageManager()
                    .getPackageInfo(mApplication.getPackageName(), 0).versionName;
            appVersionCode = mApplication.getPackageManager()
                    .getPackageInfo(mApplication.getPackageName(), 0).versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "There was an error setting the version for the app");
        }
    }


    protected void installCache() {
        final long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
        final File httpCacheDir = new File(mApplication.getCacheDir(), "http");
        try {
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
            Log.i(TAG, "cache set up properly");
        } catch (IOException e) {
            Ln.e(e, "Failed to set up com.integralblue.httpresponsecache.HttpResponseCache");
        }
    }

    public void setDefaultHeaders(Map<String, String> defaultHeaders) {
        mDefaultHeaders = defaultHeaders;
    }

    public Map<String, String> getDefaultHeaders() {
        return mDefaultHeaders;
    }

    public void setPreEmptiveBasicAuth(String targetHost, String username, String password) {
        if (targetHost != null && username != null && password != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(username);
            sb.append(":");
            sb.append(password);
            String encodedBasicAuth = new String(Base64.encode(sb.toString().getBytes(), Base64.NO_WRAP));
            mCredentialsMap.put(targetHost, encodedBasicAuth);
        } else {
            throw new RuntimeException("Please supply targethost, username, and password credentials");
        }
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setReportAppVersion(boolean mReportVersion) {
        this.mReportVersion = mReportVersion;
    }

    private void configureDefaults(URLConnection urlConnection) {

        for (String host : mCredentialsMap.keySet()) {
            if (host.equalsIgnoreCase(urlConnection.getURL().getHost())) {
                urlConnection.setRequestProperty("Authorization", "Basic " + mCredentialsMap.get(host));
            }
        }

        for (String key : mDefaultHeaders.keySet()) {
            urlConnection.setRequestProperty(key, mDefaultHeaders.get(key));
        }

        if (mReportVersion) {
            urlConnection.setRequestProperty(ANDROID_VERSION_NAME, appVersionName);
            urlConnection.setRequestProperty(ANDROID_VERSION_CODE, String.valueOf(appVersionCode));
        }
    }

    public OakConnection get(String url) throws IOException {
        URL typedUrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) typedUrl.openConnection();
        configureDefaults(httpURLConnection);
        httpURLConnection.setRequestMethod("GET");
        return new OakConnection(httpURLConnection);
    }

    public OakConnection post(String url, List<BasicNameValuePair> params) throws IOException {
        URL typedUrl = new URL(url);
        HttpsURLConnection httpURLConnection = (HttpsURLConnection)typedUrl.openConnection();
        configureDefaults(httpURLConnection);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        OutputStream out = httpURLConnection.getOutputStream();
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
        entity.writeTo(out);
        out.close();
        return new OakConnection(httpURLConnection);
    }

    public void setCertValidationDisabled(boolean isDisabled) {

        if (isDisabled) {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                Ln.e(e);
            }

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new X509HostnameVerifier() {
                @Override
                public void verify(String s, SSLSocket sslSocket) throws IOException {
                }

                @Override
                public void verify(String s, X509Certificate x509Certificate) throws SSLException {
                }

                @Override
                public void verify(String s, String[] strings, String[] strings1)
                        throws SSLException {
                }

                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        }

    }

    public void setTLSCertValidationDisabled(boolean isDisabled) {

        if (isDisabled) {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                Ln.e(e);
            }

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new X509HostnameVerifier() {
                @Override
                public void verify(String s, SSLSocket sslSocket) throws IOException {
                }

                @Override
                public void verify(String s, X509Certificate x509Certificate) throws SSLException {
                }

                @Override
                public void verify(String s, String[] strings, String[] strings1)
                        throws SSLException {
                }

                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        }

    }

    public void resetCache() {
        try {
            HttpResponseCache installed = HttpResponseCache.getInstalled();
            installed.delete();
            installCache();
        } catch (IOException e) {
            Ln.e(e);
        }
    }
}
