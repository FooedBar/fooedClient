package com.finder.fooedbar.client.api;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JsonHttpUtils {
    private final String baseApiUrl = "http://192.168.0.100:3000/";
    private String sessionId = "1";
    private final OkHttpClient client = new OkHttpClient();
    private MediaType jsonType = MediaType.parse("application/json");

    public JsonHttpUtils(int sId) {
        this.setSessionId(sId);
    }

    public JsonHttpUtils() {
        super();
    }

    public JSONObject makeJsonPostRequest(String urlSuffix, JSONObject b) throws Exception {
        Log.d("debug", sessionId+"hi");
        RequestBody body = RequestBody.create(jsonType, b.toString());
        Request request = new Request.Builder()
                .url(baseApiUrl + urlSuffix)
                .post(body)
                .addHeader("X-SESSION-ID", sessionId)
                .build();
        Response response = client.newCall(request).execute();
        JSONObject wrapper = new JSONObject(response.body().string());
        return wrapper.getJSONObject("data");
    }

    public JSONObject makeJsonGetRequest(String urlSuffix) throws Exception {
        Log.d("url suffix", urlSuffix);
        Request request = new Request.Builder()
                .url(baseApiUrl + urlSuffix)
                .addHeader("X-SESSION-ID", sessionId)
                .build();
        Log.d("debug request", request.toString());

        Response response = client.newCall(request).execute();
        String temp = response.body().string();
        Log.d("debug", temp);
        JSONObject wrapper = new JSONObject(temp);
        return wrapper.getJSONObject("data");
    }

    public void setSessionId(int sId) {
        this.sessionId = ((Integer)sId).toString();

    }
}
