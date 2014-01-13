package oak.http;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.HttpResponseCache;
import com.squareup.okhttp.OkHttpClient;

import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import oak.Base64;
import oak.OAK;

/**
 * This is a utility class for abstracting commonly used OkHttp methods
 * Created by ericrichardson on 1/6/14.
 */
public class OkHttpTool {
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    private OkHttpClient mClient;
    private Map<String, String> mDefaultHeaders = new HashMap<String, String>();
    private Map<String, String> mCredentialsMap = new HashMap<String, String>();

    /**
     * Creates a client that gets around issues with SSL Context when used with other libraries that may use HttpUrlConnection with SSL.
     * @return OkHttpClient
     */
    private static OkHttpClient createClient() {
        OkHttpClient client = new OkHttpClient();

        // Working around the libssl crash: https://github.com/square/okhttp/issues/184
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }
        client.setSslSocketFactory(sslContext.getSocketFactory());
        return client;
    }

    /**
     * Constructor with a Context to install a ResponseCache
     *
     * @param context used to install ResponseCache
     */
    public OkHttpTool(Context context) {
        if (mClient == null) {
            mClient = createClient();
            installCache(context);
        }
    }

    /**
     * Constructor with no Context. No Response Cache is made, since there is no Context to get a cache directory
     */
    public OkHttpTool() {
        if (mClient == null) {
            mClient = createClient();
        }
    }

    /**
     * Just in case you want to set the client up more than it already is.
     *
     * @return OkHttpClient
     */
    public OkHttpClient getClient() {
        return mClient;
    }

    /**
     * Set's OkHttpClients Read Timeout.
     *
     * @param timeout Time in Milliseconds to wait until Timeout
     */
    public void setReadTimeout(long timeout) {
        mClient.setReadTimeout(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Set's OkHttpClients Connect Timeout.
     *
     * @param timeout Time in Milliseconds to wait until Timeout
     */
    public void setConnectTimeout(long timeout) {
        mClient.setConnectTimeout(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Set default headers to set on all Http Requests
     *
     * @param defaultHeaders Map of http headers
     */
    public void setDefaultHeaders(Map<String, String> defaultHeaders) {
        mDefaultHeaders = defaultHeaders;
    }

    /**
     * Returns the default request headers
     *
     * @return mDefaultHeaders
     */
    public Map<String, String> getDefaultHeaders() {
        return mDefaultHeaders;
    }

    /**
     * Installs a Response Cache
     *
     * @param context used to get a cache directory
     */
    private void installCache(Context context) {
        final long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
        final File httpCacheDir = new File(context.getCacheDir(), "http");
        try {
            mClient.setResponseCache(new HttpResponseCache(httpCacheDir, httpCacheSize));
        } catch (IOException e) {
            //Failed to setup Response Cache. Worse things have happened in the world.
        }
    }

    /**
     * Configures Default Request Headers
     *
     * @param connection URLConnection to add Request Poperties to.
     */
    private void configureDefaults(URLConnection connection) {
        for (String host : mCredentialsMap.keySet()) {
            if (host.equalsIgnoreCase(connection.getURL().getHost())) {
                connection.setRequestProperty("Authorization", "Basic " + mCredentialsMap.get(host));
            }
        }
        for (String key : mDefaultHeaders.keySet()) {
            connection.setRequestProperty(key, mDefaultHeaders.get(key));
        }
    }

    /**
     * Basic GET Request
     *
     * @param url URL to send GET request
     * @return HttpUrlConnection
     * @throws IOException
     */
    public HttpURLConnection get(String url) throws IOException {
        return connectAndDoOutput(url, null, GET);
    }

    /**
     * Basic POST Request
     *
     * @param url    URL to send POST request
     * @param entity StringEntity to send as the body of the POST request
     * @return HttpUrlConnection
     * @throws IOException
     */
    public HttpURLConnection post(String url, StringEntity entity) throws IOException {
        return connectAndDoOutput(url, entity, POST);
    }

    /**
     * Basic PUT Request
     *
     * @param url    URL to send PUT request
     * @param entity StringEntity to send as the body of the PUT request
     * @return HttpUrlConnection
     * @throws IOException
     */
    public HttpURLConnection put(String url, StringEntity entity) throws IOException {
        return connectAndDoOutput(url, entity, PUT);
    }

    /**
     * Basic DELETE Request
     *
     * @param url URL to send DELETE request
     * @return HttpUrlConnection
     * @throws IOException
     */
    public HttpURLConnection delete(String url) throws IOException {
        return connectAndDoOutput(url, null, DELETE);
    }

    /**
     * @param url    URL to send request
     * @param entity StringEntity to send as the body
     * @param method Request Method
     * @return HttpUrlConnection after the output phase
     * @throws IOException
     */
    private HttpURLConnection connectAndDoOutput(String url, StringEntity entity, String method) throws IOException {
        URL typedUrl = new URL(url);
        HttpURLConnection connection = mClient.open(typedUrl);
        configureDefaults(connection);
        connection.setRequestMethod(method);
        if (entity != null) {
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            entity.writeTo(out);
            out.close();
        }
        return connection;
    }

    /**
     * This method makes the app vulnerable to a plethora of nasty things such as man-in-the-middle.
     * Only enable this for debugging if you know what you're doing and your backend has SSL issues.
     * After you enable it, slap your backend people and tell them to fix SSL ASAP before you ship the app
     * Please never leave this enabled.
     * <p/>
     * Seriously. Just don't.
     *
     * If you call this, you should feel bad.
     *
     * @param isDisabled
     */
    @Deprecated
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
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                Log.e(OAK.LOGTAG, e.getMessage() + "");
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


    /**
     * Sets Basic Auth for all requests
     *
     * @param targetHost Host to authenticate to
     * @param username   Username of user
     * @param password   password as user
     */
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

    /**
     * @param connection HttpUrlConnection to get a reader from
     * @return BufferedReader of HttpUrlConnections InputStream
     * @throws IOException
     */
    public BufferedReader getBufferedInputReader(HttpURLConnection connection) throws IOException {
        return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }
}
