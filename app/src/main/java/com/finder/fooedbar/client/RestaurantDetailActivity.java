package com.finder.fooedbar.client;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import com.finder.fooedbar.FooedBarApplication;
import com.finder.fooedbar.R;
import com.finder.fooedbar.client.api.MenuSuggestions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;


public class RestaurantDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = RestaurantDetailActivity.class.getSimpleName();

    private String imageUrl;
    private VrPanoramaView panoWidgetView;
    private Uri fileUri;
    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();
    private ImageLoaderTask backgroundImageLoaderTask;
    private boolean loadImageSuccessful;
    private HashMap<String, String> imageStore;
    private MenuSuggestions menSug;
    private LatLng restaurantCoords;
    private int restaurantID;
    private GoogleMap map;
//    private ListView lv;

    {
        imageStore = new HashMap<String, String>();
        imageStore.put("https://s3-ap-southeast-1.amazonaws.com/ah2016/IMG_3550.JPG", "thai.jpg"); // jason look here
        imageStore.put("https://s3-ap-southeast-1.amazonaws.com/ah2016/IMG_3551.JPG", "stadium.jpg");
        imageStore.put("https://s3-ap-southeast-1.amazonaws.com/ah2016/mcdonalds+(2).jpg", "mcdonalds.jpg");
    }
    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageUrl = extras.getString("URL");
            restaurantID = extras.getInt("ID");
            restaurantCoords = new LatLng(extras.getDoubleArray("COORDS")[0], extras.getDoubleArray("COORDS")[1]);

            TextView restaurantName = (TextView) findViewById(R.id.restaurant_name);
            restaurantName.setText(extras.getString("NAME"));
        }

        panoWidgetView = (VrPanoramaView) findViewById(R.id.pano_view);
        panoWidgetView.setEventListener(new ActivityEventListener());
        handleIntent(getIntent());

        //Gmaps
        Log.d("debug map",  getFragmentManager().findFragmentById(R.id.map).toString());
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        // Load the new image.
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantCoords, 10));
        map.addMarker(new MarkerOptions().position(restaurantCoords));
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
            try {
                istr = new URL(imageUrl).openStream();
//                istr = new URL("http://viewer.spherecast.org/photosphere.jpg").openStream();
                panoOptions = new VrPanoramaView.Options();
                panoOptions.inputType = VrPanoramaView.Options.TYPE_MONO;
            } catch (IOException e) {
                Log.e(TAG, "Could not decode default bitmap: " + e);
                return false;
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

        @Override
        public void onClick() {
            v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            Log.d("debug", "clicked");
            onBackPressed();
        }
    }
}


