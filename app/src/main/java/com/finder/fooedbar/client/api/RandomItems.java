package com.finder.fooedbar.client.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by svarlamov on 21/5/2016.
 */

public class RandomItems {
    private ArrayList<MenuItem> items = new ArrayList<>();
    private JsonHttpUtils httpUtils = null;
    private int limit = 30;
    private int offset = 0;
    private boolean existMore = true;
    private int sessionId;

    public RandomItems(int sessionId) throws Exception {
        super();
        Log.d("debug", sessionId+"bye");
        this.sessionId = sessionId;
        this.httpUtils = new JsonHttpUtils(this.sessionId);
        Log.d("debug", "gets right before fetchCurrentPage");
//        this.fetchCurrentPage();
        Log.d("debug", "gets right after fetchCurrentPage");
    }

    public void fetchCurrentPage() throws Exception {
        if (existMore == false) {
            return;
        }
        // TODO: Need to implement server-side endpoint for this too
        Log.d("debug", "gets before this shit");
        JSONObject resp = this.httpUtils.makeJsonGetRequest("v0/menuItems?limit=" + limit + "&offset=" + offset);
        Log.d("debug", "gets through this shit");
        JSONArray items = resp.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject obj = items.getJSONObject(i);
            MenuItem temp = new MenuItem(sessionId);
            temp.restaurantId = obj.getInt("restaurantId");
            temp.imageUrl = obj.getString("imageUrl");
            temp.imageHeight = obj.getInt("imageHeight");
            temp.imageWidth = obj.getInt("imageWidth");
            temp.id = obj.getInt("id");
            temp.name = obj.getString("name");
            temp.description = obj.getString("description");
            boolean exists = false;
            for (int j = 0; j < this.items.size(); j++) {
                if (this.items.get(j).id == temp.id) {
                    exists = true;
                    break;
                }
            }
            if (exists == false) {
                this.items.add(temp);
            }
        }

        if (items.length() < this.limit) {
            this.existMore = false;
        } else {
            this.offset += this.limit;
        }
    }

    public Boolean hasMore() {
        return this.existMore;
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

    public ArrayList<MenuItem> getItems() {
        return this.items;
    }

    public JsonHttpUtils getHttpUtils() {
        return this.httpUtils;
    }
}
