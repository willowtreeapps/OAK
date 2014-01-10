package oak.demo.http;

import com.google.gson.Gson;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oak.http.OkHttpTool;

/**
 * Created by ericrichardson on 1/10/14.
 */
//START SNIPPET okhttptool
public class DemoAPI {

    OkHttpTool mOkHttpTool;
    Gson gson;

    public DemoAPI() {
        mOkHttpTool = new OkHttpTool();
        gson = new Gson();
        Map<String, String> defaultHeaders = new HashMap<String, String>();
        defaultHeaders.put("Content-Type", "application/json");
        mOkHttpTool.setDefaultHeaders(defaultHeaders);
    }

    private <T> T parseResponse(HttpURLConnection connection, Type type) throws IOException {
        BufferedReader bufferedReader = mOkHttpTool.getBufferedInputReader(connection);
        T response = gson.fromJson(bufferedReader, type);
        connection.disconnect();
        return response;
    }

    public Object getData(String url) throws IOException {
        return parseResponse(mOkHttpTool.get(url), Object.class);
    }

    public Object postData(String url, String userId, String data) throws IOException {
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("userid", userId));
        params.add(new BasicNameValuePair("data", data));
        return parseResponse(mOkHttpTool.post(url, new UrlEncodedFormEntity(params)), Object.class);
    }
}
//END SNIPPET okhttptool
