package com.finder.fooedbar.client;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.finder.fooedbar.R;
import com.finder.fooedbar.client.api.Restaurant;

import java.util.ArrayList;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by jasonlin on 5/21/16.
 * There are two ways of defining this restaurant: one to create a list view
 * Or, if we have time, we create a VR unity scene
 */

@RuntimePermissions
public class RestaurantSuggestionsActivity extends AppCompatActivity{

    private ArrayList<Restaurant> restaurants;

    {
        restaurants = null;
    }

    void openRestaurant(String url, int resID, String resName) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra("URL", url);
        intent.putExtra("ID", resID);
        intent.putExtra("NAME", resName);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.qr_toggle) {
            getCameraPermission();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qr_menu, menu);
        return true;
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void getCameraPermission() {
        Intent intent = new Intent(this, QrActivity.class);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RestaurantSuggestionsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_suggestions);

        Toolbar qrBar = (Toolbar) findViewById(R.id.qr_bar);
        setSupportActionBar(qrBar);

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
                    openRestaurant(restaurants.get(position).getUrl(), restaurants.get(position).getID(), restaurants.get(position).getName());
                }
            });
        }

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
//                    openRestaurant();
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


