package com.finder.fooedbar.client.api;

import org.json.JSONObject;

/**
 * Created by svarlamov on 21/5/2016.
 */

public class Selection {
    private int menuItemId;
    private boolean like;

    public Selection(int menuItemId, boolean isLiked) {
        this.menuItemId = menuItemId;
        this.like = isLiked;
    }

    public void postSelection(JsonHttpUtils utils) throws Exception {
        JSONObject req = new JSONObject();
        req.put("menuItemId", menuItemId);
        req.put("like", like);
        JSONObject resp = utils.makeJsonPostRequest("v0/selections", req);
    }
}
