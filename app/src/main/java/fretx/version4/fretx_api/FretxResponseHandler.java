package fretx.version4.fretx_api;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FretxResponseHandler extends JsonHttpResponseHandler {

    @Override public void onStart()            { }
    @Override public void onRetry(int retryNo) { }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Log.d("result", response.toString());
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        Log.d("result", response.toString());
    }

    @Override
    public void onFailure(int as, Header[] he, Throwable the, JSONObject response) {
        Log.d("result", "failed");
    }

}

