package fretx.version4.fretx_api;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

public class FretxApi {

    static String ApiBase = "http://fretx.herokuapp.com/";
    static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String path) {
        try                { client.get(ApiBase + path, null, new FretxResponseHandler()); }
        catch(Exception e) { Log.d("result", e.toString()); }
    }

}
