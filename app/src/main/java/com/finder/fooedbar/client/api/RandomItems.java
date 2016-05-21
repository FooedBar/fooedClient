package com.finder.fooedbar.client.api;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by svarlamov on 21/5/2016.
 */

public class RandomItems {
    private ArrayList<MenuItem> items = new ArrayList<>();
    private JsonHttpUtils httpUtils = null;
    private int limit = 10;
    private int offset = 0;
    private boolean existMore = true;
    private int sessionId;

    public RandomItems(int sessionId) throws Exception {
        super();
        this.sessionId = sessionId;
        this.httpUtils = new JsonHttpUtils(this.sessionId);
        this.fetchCurrentPage();
    }

    public void fetchCurrentPage() throws Exception {
        if (existMore == false) {
            return;
        }
        // TODO: Need to implement server-side endpoint for this too
        JSONObject resp = this.httpUtils.makeJsonGetRequest("v0/menuItems/1");
        MenuItem dummy = new MenuItem(sessionId);
        dummy.restaurantId = resp.getInt("restaurantId");
        dummy.imageUrl = resp.getString("imageUrl");
        dummy.imageHeight = resp.getInt("imageHeight");
        dummy.imageWidth = resp.getInt("imageWidth");
        dummy.id = resp.getInt("id");
        dummy.name = resp.getString("name");
        dummy.description = resp.getString("description");
        // TODO: If len of items received is less than limit, no more items available
        //       this.existMore = false
        this.offset += this.limit;
        return;
    }

    public MenuItem getMenuItem(int index) {
        return this.items.get(index);
    }

    public void postSelectionForMenuItemAt(int index, boolean isLiked) throws Exception {
        MenuItem item =  this.getMenuItem(index);
        item.callInSelection(this.httpUtils, isLiked);
    }

    public int size() {
        return this.items.size();
    }
}
