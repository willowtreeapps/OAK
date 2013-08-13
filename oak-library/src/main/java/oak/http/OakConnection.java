package oak.http;

import oak.http.exception.AuthenticationException;
import oak.http.exception.OakHttpException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

/**
 * User: mlake
 * Date: 8/9/12
 * Time: 9:28 AM
 *
 * Deprecated: Use OkHttp
 */
@Deprecated
public class OakConnection {

    private HttpURLConnection mHttpUrlConnection;

    public OakConnection(HttpURLConnection httpURLConnection) {
        mHttpUrlConnection = httpURLConnection;
    }

    private boolean mPrintToLog;

    public HttpURLConnection getHttpURLConnection() {
        return mHttpUrlConnection;
    }

    public BufferedReader getBufferedResponseReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public InputStream getInputStream() throws IOException {
        try {
            InputStream is = mHttpUrlConnection.getInputStream();
            if("gzip".equals(mHttpUrlConnection.getContentEncoding())){
                is = new GZIPInputStream(is);
            }
            return is;
        } catch (IOException e) {
            e.printStackTrace();
            mHttpUrlConnection.disconnect();
            int responseCode = mHttpUrlConnection.getResponseCode();
            switch (responseCode) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new AuthenticationException();
                default:
                    throw new OakHttpException(responseCode);
            }
        }
    }


    public OakConnection printResponseToLog(boolean printToLog) {
        mPrintToLog = printToLog;
        return this;
    }

    public OakConnection withCache(long cacheTimeoutSeconds) {
        if (cacheTimeoutSeconds <= 0) {
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            mHttpUrlConnection.setRequestProperty("Cache-Control", "max-stale=" + maxStale);
        } else {
            mHttpUrlConnection.setRequestProperty("Cache-Control", "max-age=" + cacheTimeoutSeconds);
        }
        return this;
    }

    public OakConnection followRedirects(boolean followRedirects){
        mHttpUrlConnection.setInstanceFollowRedirects(followRedirects);
        return this;
    }

    public void disconnect() {
        mHttpUrlConnection.disconnect();
    }
}
