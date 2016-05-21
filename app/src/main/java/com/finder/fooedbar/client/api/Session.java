package com.finder.fooedbar.client.api;

import org.json.JSONObject;

/**
 * Created by svarlamov on 21/5/2016.
 */

public class Session {
    private int id;
    private double lat;
    private double lng;
    private boolean isCreated = false;

    // make the request to create session
    public Session(double la, double lg) throws Exception {
        this.lat = la;
        this.lng = lg;
        JSONObject req = new JSONObject();
        req.put("lat", la);
        req.put("long", lg);
        JsonHttpUtils utils = new JsonHttpUtils();
        JSONObject resp = utils.makeJsonPostRequest("v0/sessions", req);
        int sId = resp.getInt("id");
        this.id = sId;
        this.isCreated = true;
    }

    public Session() {
        super();
    }

    public int getId() {
        return this.id;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public void setId(int id) {
        this.id = id;
        this.isCreated = true;
    }
}
