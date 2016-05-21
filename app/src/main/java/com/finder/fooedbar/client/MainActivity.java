package com.finder.fooedbar.client;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
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
//    private Session curr;
//    private JsonHttpUtils connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // borrowed code
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setParentActivity(this);

        new FetchSessionIDTask().execute();

        // TODO: open connection with server, get data
//        if (true) {
//            Log.d("debug", "enters getsession");
//            Session curr = ((FooedBarApplication)getApplication()).getCurrentSession();
//            Session curr = null;
//                try {
//                    curr = new Session(22.309638, 114.2245);
//
//                    Log.d("debug", "successfully obtains session");
//                    ri = new RandomItems(curr.getId());
//                    while (ri.hasMore()) {
//                        ri.fetchCurrentPage();
//                    }
//                    ml = ri.getItems();
//
//                    if (ml == null) {
//                        Log.d("checkNull", "menuList received is null");
//                    }
//                    else {
//                        Log.d("checkNull", ml.toString());
//
//                    }

//                myAppAdapter = new MyAppAdapter(ml, MainActivity.this);
//                flingContainer.setAdapter(myAppAdapter);
//                flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
//                    @Override
//                    public void removeFirstObjectInAdapter() {
//
//                    }
//
//                    @Override
//                    public void expandFirstObjectInAdapter() {
//                        // TODO: Animation
//
//                    }
//
//                    @Override
//                    public void onLeftCardExit(Object dataObject) {
//                        Log.d("NOPE", al.get(0).getDescription() + " is swiped left");
//                        ml.remove(0);
////                        al.remove(0);
//                        myAppAdapter.notifyDataSetChanged();
//                        //Do something on the left!
//                        //You also have access to the original object.
//                        //If you want to use it just cast it (String) dataObject
//
//                    }
//
//                    @Override
//                    public void onRightCardExit(Object dataObject) {
//
//                        ml.remove(0);
////                        al.remove(0);
//                        myAppAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onAdapterAboutToEmpty(int itemsInAdapter) {
//
//                    }
//
//                    @Override
//                    public void onScroll(float scrollProgressPercent) {
//
//                        View view = flingContainer.getSelectedView();
//                        view.findViewById(R.id.background).setAlpha(0);
//                        view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                        view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
//                    }
//                });
//
//
//                // Optionally add an OnItemClickListener
//                flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClicked(int itemPosition, Object dataObject) {
//
//                        View view = flingContainer.getSelectedView();
//                        view.findViewById(R.id.background).setAlpha(0);
//
//                        Log.d("ONCLICK", "item position " + itemPosition + " clicked");
//                        //TODO: slideshow expands here into its own activity
//
//
//                        myAppAdapter.notifyDataSetChanged();
//                    }
//                });
//
//                mProgressView = findViewById(R.id.login_progress);
//                nomButton = (Button)findViewById(R.id.nom_button);
//                nomButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (flingContainer.mAdapter != null) {
//                            Log.d("nomButton", "removing all views");
//                            flingContainer.removeAllViewsInLayout();
////                    flingContainer.removeAllViews(); // is this necessary?
//                            ((MyAppAdapter)flingContainer.mAdapter).removeAll();
//                            flingContainer.layoutChildren(0, myAppAdapter.getCount());
//                            flingContainer.setTopView();
//
//                        }
//                        showProgress(true);
//                        mNomTask = new FetchRestaurantTask();
//                        mNomTask.execute();
//                    }
//                });

//            } catch (Exception e) {
//                Log.d("debug","Exception in getting session");
//                e.printStackTrace();
//            }

//            connection = new JsonHttpUtils(curr.getId());

        }
//        else {
//            Log.d("debug", "not really");
//
//        }


//        al = new ArrayList<>();
//        al.add(new Data("https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/s750x750/sh0.08/e35/12677601_852746651519262_2112221709_n.jpg", "Chef's Special Pork Chop"));
//        al.add(new Data("http://i.ytimg.com/vi/PnxsTxV8y3g/maxresdefault.jpg", "Thai House"));
//        al.add(new Data("http://i.ytimg.com/vi/PnxsTxV8y3g/maxresdefault.jpg", "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness."));
//        al.add(new Data("http://i.ytimg.com/vi/PnxsTxV8y3g/maxresdefault.jpg", "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness."));
//        al.add(new Data("http://i.ytimg.com/vi/PnxsTxV8y3g/maxresdefault.jpg", "But I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness."));
//        myAppAdapter = new MyAppAdapter(ml, MainActivity.this);
//        flingContainer.setAdapter(myAppAdapter);
//        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
//            @Override
//            public void removeFirstObjectInAdapter() {
//
//            }
//
//            @Override
//            public void expandFirstObjectInAdapter() {
//                // TODO: Animation
//
//            }
//
//            @Override
//            public void onLeftCardExit(Object dataObject) {
//                Log.d("NOPE", al.get(0).getDescription() + " is swiped left");
//                al.remove(0);
//                myAppAdapter.notifyDataSetChanged();
//                //Do something on the left!
//                //You also have access to the original object.
//                //If you want to use it just cast it (String) dataObject
//
//            }
//
//            @Override
//            public void onRightCardExit(Object dataObject) {
//
//                al.remove(0);
//                myAppAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onAdapterAboutToEmpty(int itemsInAdapter) {
//
//            }
//
//            @Override
//            public void onScroll(float scrollProgressPercent) {
//
//                View view = flingContainer.getSelectedView();
//                view.findViewById(R.id.background).setAlpha(0);
//                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
//                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
//            }
//        });
//
//
//        // Optionally add an OnItemClickListener
//        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClicked(int itemPosition, Object dataObject) {
//
//                View view = flingContainer.getSelectedView();
//                view.findViewById(R.id.background).setAlpha(0);
//
//                Log.d("ONCLICK", "item position " + itemPosition + " clicked");
//                //TODO: slideshow expands here into its own activity
//
//
//                myAppAdapter.notifyDataSetChanged();
//            }
//        });
//
//        mProgressView = findViewById(R.id.login_progress);
//        nomButton = (Button)findViewById(R.id.nom_button);
//        nomButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (flingContainer.mAdapter != null) {
//                    Log.d("nomButton", "removing all views");
//                    flingContainer.removeAllViewsInLayout();
////                    flingContainer.removeAllViews(); // is this necessary?
//                    ((MyAppAdapter)flingContainer.mAdapter).removeAll();
//                    flingContainer.layoutChildren(0, myAppAdapter.getCount());
//                    flingContainer.setTopView();
//
//                }
//                showProgress(true);
//                mNomTask = new FetchRestaurantTask();
//                mNomTask.execute();
//            }
//        });


        public void initializeMenuListAdapter() {
            myAppAdapter = new MyAppAdapter(ml, MainActivity.this);
            if (myAppAdapter == null) {
            }
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
            nomButton = (Button)findViewById(R.id.nom_button);
            nomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flingContainer.mAdapter != null) {
                        Log.d("nomButton", "removing all views");
                        flingContainer.removeAllViewsInLayout();
    //                    flingContainer.removeAllViews(); // is this necessary?
                        ((MyAppAdapter)flingContainer.mAdapter).removeAll();
                        flingContainer.layoutChildren(0, myAppAdapter.getCount());
                        flingContainer.setTopView();

                    }
                    showProgress(true);
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
                Log.d("debug", curr.getId()+"");
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
                Log.d("debug", "current id to randomItems "+curr.getId());
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
            Log.d("debug", "enters onPostExecute of fetchmenuitem"+success);
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
            Intent i = new Intent(getBaseContext(), SuggestionsActivity.class);
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
                viewHolder.DataText = (TextView) rowView.findViewById(R.id.bookText);
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
