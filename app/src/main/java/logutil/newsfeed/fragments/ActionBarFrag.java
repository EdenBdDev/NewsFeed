package logutil.newsfeed.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.android.volley.RequestQueue;

import logutil.newsfeed.MainActivity;
import logutil.newsfeed.R;
import logutil.newsfeed.data.NewsItem;
import logutil.newsfeed.utils.CommunicationUtil;


public class ActionBarFrag extends Fragment {

    /**
     *
     */
    private ActionBarFragListener mListener;
    /**
     * Volley's communication request queue.
     */
    private RequestQueue communicationUtil;

    // Layout components
    private SearchView searchView;
    private LinearLayout ll;

    /**
     * Constructor
     */
    public ActionBarFrag() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_actionbar, container, false);

        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    searchView.clearFocus();
                    toggleSearch(false);
                }

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    communicationUtil = CommunicationUtil.getInstance().getmRequestQueue();
                    communicationUtil.cancelAll(MainActivity.class);
                    NewsItem.flushList();
                    mListener.searchForIt(query);
                    searchView.clearFocus();
                    toggleSearch(false);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ll = (LinearLayout) view.findViewById(R.id.ll_container);

        ImageView img = (ImageView) view.findViewById(R.id.search_image);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearch();
            }
        });
        img = (ImageView) view.findViewById(R.id.list_menu);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                popup.getMenuInflater().inflate(R.menu.clipboard_popup,
                        popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.About:
                                mListener.showAbout("About", getResources().getString(R.string.ABOUT), R.id.About);
                                break;
                            case R.id.Contact:
                                mListener.showContact("Contact", getResources().getString(R.string.CONTACT), getResources().getString(R.string.SITE_URL));
                                break;
                            case R.id.Home:
                                mListener.restart();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        return view;
    }

    public void startSearch() {
        toggleSearch(true);
        searchView.setQuery("", false);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
    }

    public void toggleSearch(boolean flag) {
        if ((ll != null) && (searchView != null)) {
            if (flag) {
                ll.setVisibility(View.INVISIBLE);
                searchView.setVisibility(View.VISIBLE);
            } else {
                ll.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActionBarFragListener) {
            mListener = (ActionBarFragListener) context;
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
     *  Interface for communication with parent activity
     */
    public interface ActionBarFragListener {
        void showAbout(String about, String text, int id);

        void showContact(String contact, String text, String url);

        void restart();

        void searchForIt(String search_title);
    }

}
