package com.finder.fooedbar.client.api;

/**
 * Created by svarlamov on 21/5/2016.
 */

public class MenuItem {
    public int id;
    public int restaurantId;
    public String imageUrl;
    public int imageHeight;
    public int imageWidth;
    public String name;
    public String description;
    private int sessionId;

    public MenuItem(int sessionId) {
        super();
        this.setSessionId(sessionId);
    }

    public void setSessionId(int id) {
        sessionId = id;
    }

    public void callInSelection(JsonHttpUtils utils, boolean isLiked) throws Exception {
        if (sessionId == 0) {
            throw new Exception("Null session id");
        }
        Selection selection = new Selection(this.id, isLiked);
        selection.postSelection(utils);
    }

}
