package logutil.newsfeed.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import logutil.newsfeed.MainActivity;
import logutil.newsfeed.R;
import logutil.newsfeed.adapters.NewsAdapter;
import logutil.newsfeed.data.NewsItem;
import logutil.newsfeed.utils.CommunicationUtil;
import logutil.newsfeed.utils.OnLoadMoreListener;

import static android.content.ContentValues.TAG;

public class NewsListFragment extends Fragment {

    private static boolean firstTime = true;
    private final String base_url = "http://www.omdbapi.com/";
    private final String page_url_add = "&page=";
    NewsAdapter mAdapter;
    private NewsFeedFragListener mListener;
    private RecyclerView recyclerView;
    private RequestQueue communicationUtil;

    private String url_json_add = "&r=json";
    private String url_plot_add = "&plot=full";
    private String url_title_search = "?s=";
    private String url_id_search = "?i=";
    private String url_single_search = "?t=";
    private Snackbar snackBar;

    /**
     *  Constructor
     */
    public NewsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsitem_list, container, false);
        mAdapter = new NewsAdapter(NewsItem.ITEMS, mListener);
        Context context = view.getContext();
        final LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(context);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    mAdapter.visibleItemCount = layoutManager.getChildCount();
                    mAdapter.totalItemCount = layoutManager.getItemCount();
                    mAdapter.pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (!mAdapter.isLoading) {
                        if ((mAdapter.visibleItemCount + mAdapter.pastVisiblesItems) >= mAdapter.totalItemCount) {
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            if (mAdapter.mOnLoadMoreListener != null) {
                                mAdapter.mOnLoadMoreListener.onLoadMore();
                            } else {
                                Log.e(TAG, "onScrolled: ERRROR");
                            }
                            mAdapter.isLoading = true;
                        }
                    }
                }
            }
        });
        recyclerView.setItemAnimator(new FadeInUpAnimator(new OvershootInterpolator(1f)));
        recyclerView.getItemAnimator().setAddDuration(1000);
        recyclerView.getItemAnimator().setRemoveDuration(1000);
        recyclerView.getItemAnimator().setMoveDuration(1000);
        recyclerView.getItemAnimator().setChangeDuration(1000);
        if (firstTime) {
            generateHomePage();
        }

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("ttt", "onLoadMore:Start loading ");

                NewsItem.incPage();
                search_for(NewsItem.getMovieTitle(), false, false);
            }
        });

        return view;
    }

    /**
     * Generate Costume news Feeds
     * @param id - Costume news id
     * @param title -  Costume news title
     * @param text - Costume news summery
     */
    private void generateCostumeNews(String id, String title, String text) {
        NewsItem.addItem(new NewsItem.MovieItem(id, text, title, "", NewsItem.TEMP, "10", NewsItem.TEMP));
        mAdapter.notifyItemInserted(NewsItem.ITEMS.size() - 1);
    }

    /**
     *  Populate main page with About, Contact and Home cards.
     */
    private void generateHomePage() {
        generateCostumeNews(Board_Info.WELCOME, "Hey there", getResources().getString(R.string.WELCOME));
        generateCostumeNews(Board_Info.ABOUT, "About us", getResources().getString(R.string.ABOUT));
        generateCostumeNews(Board_Info.CONTACT, "Contact", getResources().getString(R.string.CONTACT));
    }

    /**
     * Get ImdB's infromation from the title request.
     * @param movie_name- Title.
     * @param flush - Clear current list of NewsItems.
     * @param singleItem -
     *                   </br><strong>True:</strong> Get only on match from ImdB.
     *                   </br><strong>False:</strong> Get all related title matches from ImdB.
     */
    public void search_for(final String movie_name, final boolean flush, final boolean singleItem) {
        String movie_name_holder = movie_name;
        //
        communicationUtil = CommunicationUtil.getInstance().getmRequestQueue();

        // Convert the spaces and other symbols to URL-Readable format [UTF-8]
        try {
            movie_name_holder = URLEncoder.encode(movie_name_holder, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (flush) {
            NewsItem.pageOne();
            NewsItem.addItem(null);
            NewsItem.changeTitle(movie_name_holder);
        }
        String url_build = "";
        if (!singleItem) {
            url_build = base_url + url_title_search + movie_name_holder + page_url_add + NewsItem.CURRENT_PAGE;
        } else {
            url_build = base_url + url_single_search + movie_name_holder + url_plot_add + url_json_add;
        }
        communicationUtil.cancelAll(MainActivity.class);

        String url_string = "http://suggestqueries.google.com/complete/search?client=firefox&q=" + movie_name_holder;
        JsonArrayRequest textRequest = new JsonArrayRequest(Request.Method.GET, url_string, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.e(TAG, "onResponse: [GOOGLE SUGGEST] " + response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        communicationUtil.add(textRequest);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url_build,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Debugging logs
                        //Log.d("JSON", response.toString());
                        //Log.d("JSON", response.toString().replaceAll(",", ",\n").replace("\\", ""));

                        try {
                            // Clear the NewsItem list.
                            if (flush) {
                                NewsItem.flushList();
                                mAdapter.notifyItemRemoved(0);
                            }
                            // Get all related title movies
                            if (!singleItem) {
                                JSONArray arrayItems = response.getJSONArray("Search");
                                for (int i = 0; i < arrayItems.length(); i++) {
                                    JSONObject currentItem = arrayItems.getJSONObject(i);
                                    Log.e("JSON", i + ":" + currentItem.toString());
                                    /**
                                     *
                                     */
                                    final String url_build = base_url + url_id_search + currentItem.getString("imdbID") + url_plot_add + url_json_add;
                                    JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url_build, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.e("Movie:", response.toString());
                                            try {
                                                String id = response.getString("imdbID");
                                                String summery = response.getString("Plot");
                                                String title = response.getString("Title");
                                                String urlLink = response.getString("Poster");
                                                urlLink.replace("\\", "");
                                                String year = response.getString("Released");
                                                String imdbRating = response.getString("imdbRating");
                                                String movieRuntime = response.getString("Runtime");

                                                NewsItem.addItem(new NewsItem.MovieItem(id, summery, title, urlLink, year, imdbRating, movieRuntime));
                                                if (NewsItem.CURRENT_PAGE == 1) {
                                                    mAdapter.notifyItemInserted(NewsItem.ITEMS.size() - 1);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }


                                        }

                                    }, new Response.ErrorListener() {

                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            //Toast.makeText(MainActivity.this, "ERROR:" + error, Toast.LENGTH_LONG).show();
                                        }

                                    });
                                    request2.setTag(MainActivity.class);
                                    communicationUtil.add(request2);
                                }
                                Log.e("haint", "Loaded More " + arrayItems.length());
                            } else {
                                Log.e("Single Movie:", response.toString());

                                try {
                                    if (!response.getString("Response").equals("False")) {
                                        String id = response.getString("imdbID");
                                        String summery = response.getString("Plot");
                                        String title = response.getString("Title");
                                        String urlLink = response.getString("Poster");
                                        urlLink.replace("\\", "");
                                        String year = response.getString("Released");
                                        String imdbRating = response.getString("imdbRating");
                                        String movieRuntime = response.getString("Runtime");

                                        NewsItem.addItem(new NewsItem.MovieItem(id, summery, title, urlLink, year, imdbRating, movieRuntime));
                                        mAdapter.notifyItemInserted(NewsItem.ITEMS.size() - 1);
                                        NewsItem.addItem(new NewsItem.MovieItem(NewsItem.SEARCH_OTHER_MOVIES, summery, title, urlLink, year, imdbRating, movieRuntime));
                                    } else {
                                        NewsItem.addItem(new NewsItem.MovieItem(Board_Info.NO_MATCH, getResources().getString(R.string.NO_MATCH), "No match", "", "Temp", "0", "Temp"));
                                        mAdapter.notifyItemInserted(NewsItem.ITEMS.size() - 1);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (NewsItem.CURRENT_PAGE > 1) {
                                callSnackBar("More feeds were loaded!", true);
                            } else {
                                mAdapter.notifyItemInserted(NewsItem.ITEMS.size() - 1);
                            }
                            mAdapter.setLoaded();

                        } catch (JSONException e) {
                            if (NewsItem.CURRENT_PAGE == 1) {
                                //Re-search
                                callSnackBarAnnouncer("Could not find specific feed,\nRe-searching for multuple options.");
                                search_for(movie_name, true, false);
                            } else {
                                callSnackBar("No more feeds.", false);
                            }
                        }

                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }

        });
        communicationUtil.add(request);
    }

    /**
     *
     * @return SnackBar callback with adapter's notify of insertion.
     */
    private Snackbar.Callback newCallbeck() {
        return new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                    // Snackbar closed on its own
                    mAdapter.notifyItemInserted(NewsItem.ITEMS.size() - 1);
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
            }

        };
    }

    /**
     *  Creates a SnackBar with text-title
     * @param title - SnackBar title
     */
    private void callSnackBarAnnouncer(String title) {
        snackBar = Snackbar.make(recyclerView, title, Snackbar.LENGTH_LONG);
        View snackbarView = snackBar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#f48660"));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackBar.show();
    }

    /**
     * Creates a SnackBar with text-title with optional of callback.
     * @param title - SnackBar title.
     * @param callCallbeck -
     *                     </br> <strong> True:</strong> Attach callback.
     *                     </br> <strong> False:</strong> Dont attach callback
     */
    private void callSnackBar(String title, boolean callCallbeck) {
        snackBar = Snackbar.make(recyclerView, title, Snackbar.LENGTH_LONG);
        if (callCallbeck) {
            snackBar.setCallback(newCallbeck());
        }
        View snackbarView = snackBar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#f48660"));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackBar.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewsFeedFragListener) {
            mListener = (NewsFeedFragListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *  Reset view to Home Page's view
     */
    public void reset() {
        NewsItem.flushList();
        mAdapter.notifyItemRemoved(0);
        generateHomePage();
    }


    /**
     *  Interface for communication with parent activity
     */
    public interface NewsFeedFragListener {
        /**
         * Displays pop-up card for more information depends on the movie's id that inserted.
         *
         * @param item :</br>
         *                  <strong> Board_Info.About </strong>: Display About card view. </br>
         *                  <strong> Board_Info.CONTACT </strong>: Display Contact card view. </br>
         *                  <strong> Board_Info.WELCOME </strong>: Display Welcome card view. </br>
         *                  <strong> Board_Info.NO_MATCH </strong>: Display No Match card view. </br>
         *                  <strong> Default </strong>: Display Movie card view. </br>
         */
        void OnListnListener(NewsItem.MovieItem item);

        void searchOtherMovies();
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        //... constructor
        private WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens");
            }
        }
    }


}
