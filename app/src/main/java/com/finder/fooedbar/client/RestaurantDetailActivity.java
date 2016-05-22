package com.finder.fooedbar.client;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.finder.fooedbar.FooedBarApplication;
import com.finder.fooedbar.R;
import com.finder.fooedbar.client.api.MenuItem;
import com.finder.fooedbar.client.api.MenuSuggestions;
import com.finder.fooedbar.client.api.Restaurant;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity {
    private static final String TAG = RestaurantDetailActivity.class.getSimpleName();

    private String imageUrl;
    private VrPanoramaView panoWidgetView;
    private Uri fileUri;
    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();
    private ImageLoaderTask backgroundImageLoaderTask;
    private boolean loadImageSuccessful;
    private HashMap<String, String> imageStore;
    private MenuSuggestions menSug;
    private int restaurantID;
    private ListView lv;

    {
        imageStore = new HashMap<String, String>();
        imageStore.put("https://s3-ap-southeast-1.amazonaws.com/ah2016/IMG_3550.JPG", "thai.jpg"); // jason look here
        imageStore.put("https://s3-ap-southeast-1.amazonaws.com/ah2016/IMG_3551.JPG", "stadium.jpg");
        imageStore.put("https://s3-ap-southeast-1.amazonaws.com/ah2016/mcdonalds+(2).jpg", "mcdonalds.jpg");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        lv = (ListView) findViewById(R.id.curatedMenu);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageUrl = extras.getString("URL");
            restaurantID = extras.getInt("ID");

            TextView restaurantName = (TextView) findViewById(R.id.restaurant_name);
            restaurantName.setText(extras.getString("NAME"));
        }

        panoWidgetView = (VrPanoramaView) findViewById(R.id.pano_view);
        handleIntent(getIntent());

        new FetchMenuTask().execute();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        // Load the new image.
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            fileUri = intent.getData();
            if (fileUri == null) {
                Log.w(TAG, "No data uri specified. Use \"-d /path/filename\".");
            } else {
                Log.i(TAG, "Using file " + fileUri.toString());
            }

            panoOptions.inputType = intent.getIntExtra("inputType", VrPanoramaView.Options.TYPE_MONO);
            Log.i(TAG, "Options.inputType = " + panoOptions.inputType);
        } else {
            Log.i(TAG, "Intent is not ACTION_VIEW. Using default pano image.");
            fileUri = null;
            panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
        }
        if (backgroundImageLoaderTask != null) {
            // Cancel any task from a previous intent sent to this activity.
            backgroundImageLoaderTask.cancel(true);
        }
        backgroundImageLoaderTask = new ImageLoaderTask(imageUrl);
        backgroundImageLoaderTask.execute(Pair.create(fileUri, panoOptions));
    }

    @Override
    protected void onPause() {
        panoWidgetView.pauseRendering();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        panoWidgetView.resumeRendering();
    }

    @Override
    protected void onDestroy() {
        // Destroy the widget and free memory.
        panoWidgetView.shutdown();

        // The background task has a 5 second timeout so it can potentially stay alive for 5 seconds
        // after the activity is destroyed unless it is explicitly cancelled.
        if (backgroundImageLoaderTask != null) {
            backgroundImageLoaderTask.cancel(true);
        }
        super.onDestroy();
    }
    class ImageLoaderTask extends AsyncTask<Pair<Uri, VrPanoramaView.Options>, Void, Boolean> {

        private String imageUrl;

        public ImageLoaderTask(String url) {
            this.imageUrl = url;
        }

        /**
         * Reads the bitmap from disk in the background and waits until it's loaded by pano widget.
         */
        @Override
        protected Boolean doInBackground(Pair<Uri, VrPanoramaView.Options>... fileInformation) {
            VrPanoramaView.Options panoOptions = null;  // It's safe to use null VrPanoramaView.Options.
            InputStream istr = null;
            if (fileInformation == null || fileInformation.length < 1
                    || fileInformation[0] == null || fileInformation[0].first == null) {
                AssetManager assetManager = getAssets();
                try {
//                    istr = assetManager.open(imageStore.get(imageUrl));
                    Log.d("res", "url: "+imageUrl+ " Restaurant"+restaurantID);
                    istr = new URL(imageUrl).openStream();
                    panoOptions = new VrPanoramaView.Options();
                    panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
                } catch (IOException e) {
                    Log.e(TAG, "Could not decode default bitmap: " + e);
                    return false;
                }
            } else {
                try {
                    istr = new FileInputStream(new File(fileInformation[0].first.getPath()));
                    panoOptions = fileInformation[0].second;
                } catch (IOException e) {
                    Log.e(TAG, "Could not load file: " + e);
                    return false;
                }
            }
            panoWidgetView.loadImageFromBitmap(BitmapFactory.decodeStream(istr), panoOptions);
            try {
                istr.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close input stream: " + e);
            }

            return true;
        }
    }

    class FetchMenuTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Log.d("resID", restaurantID+"");
                menSug = new MenuSuggestions(((FooedBarApplication)getApplication()).getSessionID(),  restaurantID);
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
                    lv.setAdapter(new MyMenuAdapter(getApplicationContext(), R.layout.restaurant_list_item, menSug.getCuratedMenu()));
                    Log.d("debug", "loadSuccess");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        private class MyMenuAdapter extends ArrayAdapter<MenuItem> {
            private int layout;
            private List<MenuItem> mObjects;

            private MyMenuAdapter(Context context, int resource, List<MenuItem> objects) {
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

    /**
     * Listen to the important events from widget.
     */
    private class ActivityEventListener extends VrPanoramaEventListener {
        @Override
        public void onLoadSuccess() {
            loadImageSuccessful = true;
        }

        /**
         * Called by pano widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {
            loadImageSuccessful = false;
            Toast.makeText(
                    RestaurantDetailActivity.this, "Error loading pano: " + errorMessage, Toast.LENGTH_LONG)
                    .show();
            Log.e(TAG, "Error loading pano: " + errorMessage);
        }
    }
}


