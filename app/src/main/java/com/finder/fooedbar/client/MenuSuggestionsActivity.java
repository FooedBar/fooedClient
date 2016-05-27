package com.finder.fooedbar.client;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.finder.fooedbar.FooedBarApplication;
import com.finder.fooedbar.R;
import com.finder.fooedbar.client.api.MenuItem;
import com.finder.fooedbar.client.api.MenuSuggestions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 26/5/16.
 */

public class MenuSuggestionsActivity extends AppCompatActivity{

    private int restaurantId;
    private MenuSuggestions menSug;
    private String restaurantName;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        restaurantName = getIntent().getStringExtra("restaurant_name");
        restaurantId = getIntent().getIntExtra("restaurant_id", 0);
        setContentView(R.layout.activity_menu_suggestions);
        TextView restaurantTitleView = (TextView) findViewById(R.id.menu_restaurant_name);
        restaurantTitleView.setText(restaurantName);

        if (restaurantId == 0) {
            Log.d("debug", "Unable to get Id");
        }
        Log.d("debug restaurantId", restaurantId+"");

        new FetchMenuTask().execute();
    }

    class FetchMenuTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.d("resID", restaurantId+"");
                menSug = new MenuSuggestions(((FooedBarApplication)getApplication()).getSessionID(),  restaurantId);
                menSug.getMenuSuggestions();
                Log.d("ASYNC", "running inside FetchRestaurantTask");
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                try {
                    Log.d("debug", "loadBegin");
                    final ListView lv = (ListView) findViewById(R.id.curatedMenu);
                    lv.setAdapter(new MyMenuAdapter(getApplicationContext(), R.layout.restaurant_list_item, menSug.getCuratedMenu()));
                    Log.d("adapter layout", lv.getAdapter().getCount()+"");
                    Log.d("debug", "loadSuccess");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private class MyMenuAdapter extends ArrayAdapter<MenuItem> {
            private int layout;
            private List<MenuItem> mObjects;

            private MyMenuAdapter(Context context, int resource, ArrayList<MenuItem> objects) {
                super(context, resource, objects);
                mObjects = objects;
                layout = resource;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                ViewHolder mainViewholder = null;

                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(MenuSuggestionsActivity.this);
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
                    }
                });
                String str = getItem(position).getName();
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
}
