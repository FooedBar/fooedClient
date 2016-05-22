package com.finder.fooedbar.client.api;

/**
 * Created by jasonlin on 5/21/16.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class MenuSuggestions {
    private ArrayList<MenuItem> curatedMenu = new ArrayList<MenuItem>();
    private int sessionId;
    private int restaurantID;
    private JsonHttpUtils utils;

    public MenuSuggestions(int sessionId, int restaurantID) {
        this.sessionId = sessionId;
        this.restaurantID = restaurantID;
        utils = new JsonHttpUtils(this.sessionId);
    }

    public void getMenuSuggestions() throws Exception {
        Log.d("resId", "In MenuSuggestions "+restaurantID);
        JSONObject resp = utils.makeJsonGetRequest("v0/suggestions/restaurants/"+restaurantID+"/menu");
        JSONArray items = resp.getJSONArray("items");
        Log.d("length", items.length()+"");
        for (int i = 0; i < items.length(); i++) {
            JSONObject obj = items.getJSONObject(i);
            Log.d("debug obj", obj.toString());
            this.curatedMenu.add(new MenuItem(this.sessionId,
                    obj.getInt("restaurantId"), obj.getString("imageUrl"),
                    obj.getString("name")));
        }
    }

    public int size() {
        return this.curatedMenu.size();
    }

    public MenuItem get(int index) {
        return this.curatedMenu.get(index);
    }

    public ArrayList<MenuItem> getCuratedMenu() {
        return curatedMenu;
    }
}