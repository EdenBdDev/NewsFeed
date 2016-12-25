package logutil.newsfeed;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.lang.ref.WeakReference;

import logutil.newsfeed.data.NewsItem;
import logutil.newsfeed.fragments.ActionBarFrag;
import logutil.newsfeed.fragments.Board_Info;
import logutil.newsfeed.fragments.ExtenedView;
import logutil.newsfeed.fragments.NewsListFragment;
import logutil.newsfeed.utils.CommunicationUtil;


public class MainActivity extends FragmentActivity implements NewsListFragment.NewsFeedFragListener, ActionBarFrag.ActionBarFragListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommunicationUtil.init(new WeakReference<Context>(this));
    }

    /**
     * Displays pop-up card for more information depends on the movie's id that inserted.
     *
     * @param MovieItem :</br>
     *                  <strong> Board_Info.About </strong>: Display About card view. </br>
     *                  <strong> Board_Info.CONTACT </strong>: Display Contact card view. </br>
     *                  <strong> Board_Info.WELCOME </strong>: Display Welcome card view. </br>
     *                  <strong> Board_Info.NO_MATCH </strong>: Display No Match card view. </br>
     *                  <strong> Default </strong>: Display Movie card view. </br>
     */
    @Override
    public void OnListnListener(NewsItem.MovieItem MovieItem) {
        ExtenedView frag = (ExtenedView) getSupportFragmentManager().findFragmentById(R.id.Exteded_View_Frag);
        switch (MovieItem.id) {
            case Board_Info.ABOUT:
                showAbout("About", getResources().getString(R.string.ABOUT), R.id.About);
                break;
            case Board_Info.CONTACT:
                showContact("Contact", getResources().getString(R.string.CONTACT), getResources().getString(R.string.SITE_URL));
                break;
            case Board_Info.WELCOME:
                showAbout("Hey there", getResources().getString(R.string.WELCOME), R.id.Home);
                break;
            case Board_Info.NO_MATCH:
                toggleSearch();
                break;
            default:
                frag.changeUI(MovieItem);
                break;
        }

    }

    /**
     * Calls to ActionBarFrag's first search function (startSearch)
     */
    private void toggleSearch() {
        ActionBarFrag frag = (ActionBarFrag) getSupportFragmentManager().findFragmentById(R.id.ActionBar_Frag);
        frag.startSearch();
    }

    /**
     * Calls NewsListFragment for a related-multiple item search (search_for,flush&NON-single)
     */
    @Override
    public void searchOtherMovies() {
        NewsListFragment frag = (NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.NewsFeeedFrag);
        frag.search_for(NewsItem.getMovieTitle(), true, false);
    }

    /**
     * Calls to Board_Info's card display function (changeUI)
     *
     * @param about - Card's Title
     * @param text  - Card's Text
     * @param id    -  Card's Id </br>
     *              R.id.About - about card </br>
     *              R.id.Contact - contact card </br>
     *              R.id.Home  - home card</br>
     *              Default: Not displaying anything </br>
     */
    @Override
    public void showAbout(String about, String text, int id) {
        Board_Info frag = (Board_Info) getSupportFragmentManager().findFragmentById(R.id.Board_info_Frag);
        frag.changeUI(about, text, "", id);
    }

    /**
     * Calls to Board_Info's card display function <strong>with URL</strong> - (changeUI)
     * @param contact - Card's Title
     * @param text Card's Text
     * @param url - raw unfiltered string of a URL.
     */
    @Override
    public void showContact(String contact, String text, String url) {
        Board_Info frag = (Board_Info) getSupportFragmentManager().findFragmentById(R.id.Board_info_Frag);
        frag.changeUI(contact, text, url, R.id.Contact);
    }

    /**
     * Calls NewsListFragment to first appearance UI - (reset)
     */
    @Override
    public void restart() {
        NewsListFragment frag = (NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.NewsFeeedFrag);
        frag.reset();
    }

    /**
     * Calls NewsListFragment for a single item search - (search_for,flush&single)
     * @param search_title - raw unfiltered string of a movie/series title.
     */
    @Override
    public void searchForIt(String search_title) {
        NewsListFragment frag = (NewsListFragment) getSupportFragmentManager().findFragmentById(R.id.NewsFeeedFrag);
        frag.search_for(search_title, true, true);

    }
}
