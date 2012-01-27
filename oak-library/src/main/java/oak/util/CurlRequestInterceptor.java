package oak.util;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.EntityEnclosingRequestWrapper;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: mlake Date: 1/27/12 Time: 11:18 AM
 */
public class CurlRequestInterceptor implements HttpRequestInterceptor{
    
    public static String TAG = CurlRequestInterceptor.class.getSimpleName();
    
    private String authUserName;
    private String additionalCommands;

    public void setAuthUserName(String authUserName) {
        this.authUserName = authUserName;
    }

    public void setAdditionalCommands(String additionalCommands) {
        this.additionalCommands = additionalCommands;
    }

    @Override
    public void process(HttpRequest httpRequest, HttpContext httpContext)
            throws HttpException, IOException {

        StringBuilder sb = new StringBuilder();
        HttpHost targetHost = (HttpHost) httpContext
                .getAttribute(ExecutionContext.HTTP_TARGET_HOST);
        sb.append("curl ");

        if (httpRequest.getRequestLine().getMethod().equals("POST")) {
            try {
                String content = getStringFromIps(
                        ((EntityEnclosingRequestWrapper) httpRequest).getEntity().getContent());
                sb.append("-d \"");
                sb.append(content);
                sb.append("\" ");
            } catch (IOException e) {
                Log.e(TAG, "Unable to log full content of POST");
                e.printStackTrace();
            }

        }

        sb.append("-k ");
        if (authUserName != null){
            sb.append("-u ");
            sb.append(authUserName);
        }        
        for (Header h : httpRequest.getAllHeaders()) {
            sb.append(String.format(" -H \"%s:%s\" ", h.getName(), h.getValue()));
        }
        sb.append("\"");
        sb.append(targetHost.toURI());
        sb.append(httpRequest.getRequestLine().getUri());
        sb.append("\"");
        if (additionalCommands != null) sb.append(additionalCommands);
        Log.d(TAG, sb.toString());
    }


    private static String getStringFromIps(InputStream ips) throws IOException {
        BufferedReader buf = new BufferedReader(new InputStreamReader(ips, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String s;
        while (true) {
            s = buf.readLine();
            if (s == null) {
                break;
            }
            sb.append(s);
        }

        buf.close();
        ips.close();
        return sb.toString();
    }
}
