package com.finder.fooedbar.client.api;

import org.json.JSONObject;

/**
 * Created by svarlamov on 21/5/2016.
 */

public class Restaurant {
    private String id;
    private String name;
    private String description;
    private String style;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStyle() {
        return style;
    }

    public Restaurant(int id, int sessionId) throws Exception {
        JsonHttpUtils utils = new JsonHttpUtils(sessionId);
        this.id = ((Integer)id).toString();
        JSONObject obj = utils.makeJsonGetRequest("v0/restaurants/" + this.id);
        this.name = obj.getString("name");
        this.description = obj.getString("description");
        this.style = obj.getString("style");
    }
}
