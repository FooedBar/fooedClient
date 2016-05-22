package com.finder.fooedbar.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finder.fooedbar.R;
import com.finder.fooedbar.client.api.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasonlin on 5/21/16.
 * There are two ways of defining this restaurant: one to create a list view
 * Or, if we have time, we create a VR unity scene
 */


public class SuggestionsActivity extends AppCompatActivity{

    private ArrayList<Restaurant> restaurants;

    {
        restaurants = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            restaurants = (ArrayList<Restaurant>) extras.getSerializable("SUGGESTIONS");
            // DEBUG
            for (Restaurant item : restaurants) {
                Log.d("res", item.getName());
            }


            MyListAdaper la = new MyListAdaper(getApplicationContext(), R.layout.restaurant_list_item, restaurants);
            final ListView SuggestionsListView = (ListView) findViewById(R.id.mylist);
            SuggestionsListView.setAdapter(la);
            SuggestionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Sasha & Daniel look here
                    Log.d("debug", "hello world");
                }
            });
        }

    }


    void openRestaurant() {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        startActivity(intent);
    }

    private class MyListAdaper extends ArrayAdapter<Restaurant> {
        private int layout;
        private List<Restaurant> mObjects;

        private MyListAdaper(Context context, int resource, List<Restaurant> objects) {
            super(context, resource, objects);

            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_title);
                viewHolder.button = (ImageButton) convertView.findViewById(R.id.list_item_btn_right);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("debug", "restaurant item is clicked");
//                    Toast.makeText(getContext(), "Button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
                    openRestaurant();
                    //Toast.makeText(getContext(), "Button was clicked for list item " + position, Toast.LENGTH_SHORT).show();
                }
            });
            String str = getItem((position)).getName();
            String[] split = str.split("\\*");
            String eventName = split[0];
            mainViewholder.title.setText(eventName);
            return convertView;
        }

        public class ViewHolder {

            TextView title;
            ImageButton button;
        }


    }
}


