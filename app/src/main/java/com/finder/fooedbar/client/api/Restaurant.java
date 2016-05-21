package com.finder.fooedbar.client.api;

import org.json.JSONObject;

/**
 * Created by svarlamov on 21/5/2016.
 */

public class Restaurant {
    private int id;
    private String name;
    private String description;
    private String style;
    private String imageUrl;
    private int imageHeight;
    private int imageWidth;
    private int sessionId;

    public Restaurant(int id, int sessionId) throws Exception {
        JsonHttpUtils utils = new JsonHttpUtils(sessionId);
        this.id = id;
        JSONObject obj = utils.makeJsonGetRequest("v0/restaurants/" + ((Integer)this.id).toString());
        this.sessionId = sessionId;
        this.name = obj.getString("name");
        this.description = obj.getString("description");
        this.style = obj.getString("style");
        this.imageUrl = obj.getString("imageUrl");
        this.imageHeight = obj.getInt("imageHeight");
        this.imageWidth = obj.getInt("imageWidth");
    }

    public Restaurant(int id, String name, String description, String style, String imageUrl, int imageHeight, int imageWidth, int sessionId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.style = style;
        this.imageHeight = imageHeight;
        this.imageUrl = imageUrl;
        this.imageWidth = imageWidth;
        this.sessionId = sessionId;
    }
}
