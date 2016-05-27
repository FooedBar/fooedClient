package com.finder.fooedbar.client;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.finder.fooedbar.FooedBarApplication;
import com.finder.fooedbar.R;
import com.finder.fooedbar.client.api.MenuItem;
import com.finder.fooedbar.client.api.RandomItems;
import com.finder.fooedbar.client.api.RestaurantSuggestions;
import com.finder.fooedbar.client.api.Session;
import com.finder.fooedbar.client.tindercard.FlingCardListener;
import com.finder.fooedbar.client.tindercard.SwipeFlingAdapterView;
import com.finder.fooedbar.server.Data;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FlingCardListener.ActionDownInterface {

    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    private ArrayList<Data> al;
    private ArrayList<MenuItem> ml;
    private SwipeFlingAdapterView flingContainer;
    private View mProgressView;
    private Button nomButton;
    private FetchRestaurantTask mNomTask;
    private RandomItems ri;
    private Session curr;
    private RestaurantSuggestions resSug;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 21;
//    private Session curr;
//    private JsonHttpUtils connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

        // borrowed code
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setParentActivity(this);

        new FetchSessionIDTask().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        if (!(grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Log.d("debug", "Camera permission accepted");
        } else {
            Log.d("debug", "Camera permission denied");
            finishAndRemoveTask();
        }
        return;
    }


    public void initializeMenuListAdapter() {
        myAppAdapter = new MyAppAdapter(ml, MainActivity.this);
        Log.d("debug", "ready to setAdapter");
        flingContainer.setAdapter(myAppAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void expandFirstObjectInAdapter() {
                // TODO: Animation

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Log.d("NOPE", ml.get(0).getName() + " is swiped left");
                new SelectionTask(ml.get(0)).execute(false);

                ml.remove(0);
                //                        al.remove(0);
                myAppAdapter.notifyDataSetChanged();
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                new SelectionTask(ml.get(0)).execute(true);

                ml.remove(0);
                //                        al.remove(0);
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);

                Log.d("ONCLICK", "item position " + itemPosition + " clicked");
                //TODO: slideshow expands here into its own activity


                myAppAdapter.notifyDataSetChanged();
            }
        });

        mProgressView = findViewById(R.id.login_progress);
        nomButton = (Button) findViewById(R.id.nom_button);
        nomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                if (flingContainer.mAdapter != null) {
                    Log.d("nomButton", "removing all views");
                    flingContainer.removeAllViewsInLayout();
                    //                    flingContainer.removeAllViews(); // is this necessary?
                    ((MyAppAdapter) flingContainer.mAdapter).removeAll();
                    flingContainer.layoutChildren(0, myAppAdapter.getCount());
                    flingContainer.setTopView();

                }
//                    showProgress(true);
                mNomTask = new FetchRestaurantTask();
                mNomTask.execute();
            }
        });
    }

    public static void removeBackground() {
        viewHolder.background.setVisibility(View.GONE);
        myAppAdapter.notifyDataSetChanged();
    }

    /**
     * Shows the progress UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onActionDownPerform() {
        Log.e("action", "bingo");
    }

    public static class ViewHolder {
        public static FrameLayout background;
        public TextView DataText;
        public ImageView cardImage;


    }


    class FetchSessionIDTask extends AsyncTask<Void, Void, Boolean> {

//        private Session curr;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                curr = new Session(22.309638, 114.2245);
                Log.d("debug", "successfully obtains session");

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
//            Log.d("debug", "successfully obtains session");

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d("debug", "returns here");
            try {
                Log.d("debug", curr.getId() + "");
                ((FooedBarApplication) getApplication()).setSessionID(curr.getId());
                new FetchMenuItemTask().execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class FetchMenuItemTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.d("debug", "current id to randomItems " + curr.getId());
                ri = new RandomItems(curr.getId());
                Log.d("debug", "passed try fuck me");

                while (ri.hasMore()) {
                    ri.fetchCurrentPage();
                }
                ml = ri.getItems();


                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
//            Log.d("debug", "successfully obtains session");

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d("debug", "enters onPostExecute of fetchmenuitem" + success);
            if (success) {
                Log.d("debug", "success onPost");
                if (ml == null) {
                    Log.d("checkNull", "menuList received is null");
                } else {
                    Log.d("checkNull", ml.toString());
                    initializeMenuListAdapter();
                    flingContainer.onResultsReceived();

                }
            }

        }
    }

    class FetchRestaurantTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
//                Thread.sleep(5000);
                resSug = new RestaurantSuggestions(curr.getId());
                resSug.getSuggestions();
                Log.d("ASYNC", "running inside FetchRestaurantTask");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // fetch the data
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            Intent i = new Intent(getBaseContext(), RestaurantSuggestionsActivity.class);
            i.putExtra("SUGGESTIONS", resSug.getRestaurants());
            startActivity(i);
        }

    }

    class SelectionTask extends AsyncTask<Boolean, Void, Boolean> {

        private MenuItem toDelete;

        SelectionTask(MenuItem toDel) {
            toDelete = toDel;
        }

        @Override
        protected Boolean doInBackground(Boolean... isLike) {
            try {
                toDelete.callInSelection(ri.getHttpUtils(), isLike[0]);
                Log.d("debug", "passed SelectionTask");

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
//            Log.d("debug", "successfully obtains session");

        }

        @Override
        protected void onPostExecute(final Boolean success) {
//            if (success) {
//                ml.remove(toDelete);
//                myAppAdapter.notifyDataSetChanged();
//            }
        }
    }


    public class MyAppAdapter extends BaseAdapter {


        //        public List<Data> parkingList;
        public List<MenuItem> menuList;
        public Context context;

//        private MyAppAdapter(List<Data> apps, Context context) {
//            this.parkingList = apps;
//            this.context = context;
//        }

        // interfacing MenuItem
        private MyAppAdapter(ArrayList<MenuItem> apps, Context context) {
            this.menuList = apps;
            this.context = context;
        }

        @Override
        public int getCount() {
//            return parkingList.size();
            if (menuList != null) {
                return menuList.size();
            } else {
                return 0;
            }
        }

        public void removeAll() {
//            parkingList = new ArrayList<Data>();
            menuList = new ArrayList<MenuItem>();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            View rowView = convertView;
//
//
//            if (rowView == null) {
//
//                LayoutInflater inflater = getLayoutInflater();
//                rowView = inflater.inflate(R.layout.item, parent, false);
//                // configure view holder
//                viewHolder = new ViewHolder();
//                viewHolder.DataText = (TextView) rowView.findViewById(R.id.bookText);
//                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
//                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
//                rowView.setTag(viewHolder);
//
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            viewHolder.DataText.setText(parkingList.get(position).getDescription() + "");
//
//            Log.d("Image", parkingList.get(position).getImagePath());
//            Glide.with(MainActivity.this).load(parkingList.get(position).getImagePath()).into(viewHolder.cardImage);
//
//            return rowView;
//        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.d("Image", "hi");

            View rowView = convertView;


            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.item, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.DataText = (TextView) rowView.findViewById(R.id.menuItemText);
                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.background);
                viewHolder.cardImage = (ImageView) rowView.findViewById(R.id.cardImage);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.DataText.setText(menuList.get(position).getName() + "");

//            Log.d("Image", menuList.get(position).getImagePath());
            Glide.with(MainActivity.this).load(menuList.get(position).getImagePath()).into(viewHolder.cardImage);

            return rowView;
        }
    }
}
