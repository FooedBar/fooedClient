package com.finder.fooedbar.client.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by svarlamov on 21/5/2016.
 */

public class RestaurantSuggestions {
    private ArrayList<Restaurant> restaurants = new ArrayList<>();
    private int sessionId;
    private JsonHttpUtils utils;

    public RestaurantSuggestions(int sessionId) {
        this.sessionId = sessionId;
        utils = new JsonHttpUtils(this.sessionId);
    }

    public void getSuggestions() throws Exception {
        JSONObject resp = utils.makeJsonGetRequest("v0/suggestions/restaurants");
        JSONArray items = resp.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject obj = items.getJSONObject(i);
            this.restaurants.add(new Restaurant(obj.getInt("id"),
                    obj.getString("name"), obj.getString("description"),
                    obj.getString("style"), obj.getString("imageUrl"),
                    obj.getInt("imageHeight"), obj.getInt("imageWidth"), this.sessionId,
                    new double[]{obj.getDouble("lat"), obj.getDouble("long")}));
        }
    }

    public int size() {
        return this.restaurants.size();
    }

    public Restaurant get(int index) {
        return this.restaurants.get(index);
    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }
}
