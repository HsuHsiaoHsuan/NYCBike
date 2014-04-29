package idv.funnybrain.bike.nyc.data;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by freeman on 2014/4/28.
 */
public class DataDownloader {
    private static final String DataURL = "http://citibikenyc.com/stations/json";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(1);
        client.setMaxRetriesAndTimeout(10, 1300);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(1);
        client.setMaxRetriesAndTimeout(10, 1300);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return DataURL + relativeUrl;
    }
}
