package logutil.newsfeed.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsItem {

    /**
     * An array of sample (MovieItem) items.
     */
    public static final List<MovieItem> ITEMS = new ArrayList<MovieItem>();
    /**
     * A map of sample (MovieItem) items, by ID.
     */
    private static final Map<String, MovieItem> ITEM_MAP = new HashMap<String, MovieItem>();

    /**
     * Unique ID for search mode
     */
    public static String SEARCH_OTHER_MOVIES = "4x213MVItt0";
    /**
     * Rating star maximum capacity
     */
    public static int MAX_RATING = 10;

    /**
     * Page indicator variable
     */
    public static int CURRENT_PAGE = 1;
    /**
     *
     */
    public static String TEMP = "Temp";
    /**
     * A current movie name string variable.
     */
    private static String MOVIE_NAME = "";

    /**
     * Add function, checks for duplicates within the map and only unique items will be added.
     *
     * @param item - MovieItem
     */
    public static void addItem(MovieItem item) {
        if (item != null) {
            if (!ITEM_MAP.containsKey(item.id)) {
                ITEMS.add(item);
                ITEM_MAP.put(item.id, item);
            }
        } else {
            ITEMS.add(null);
        }
    }

    /**
     * Increment current page to next one
     */
    public static void incPage() {
        CURRENT_PAGE++;
    }

    /**
     * Reset current page status to default : <strong>Page 1</strong>.
     */
    public static void pageOne() {
        CURRENT_PAGE = 1;
    }

    /**
     * Removes item from the map (only if the item exist within).
     *
     * @param position - item's position
     */
    public static void delItem(int position) {
        if (ITEMS.get(position) != null) {
            ITEM_MAP.remove(ITEMS.get(position).id);
        }
        ITEMS.remove(position);
    }

    /**
     * Clears all items within the map.
     */
    public static void flushList() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    /**
     * Attach tp current MOVIE_NAME variable new name.
     *
     * @param movie_name - New Movie name.
     */
    public static void changeTitle(String movie_name) {
        MOVIE_NAME = movie_name;
    }

    /**
     * @return Current MOVIE_NAME variable value.
     */
    public static String getMovieTitle() {
        return MOVIE_NAME;
    }


    /**
     * A Movie item.
     */
    public static class MovieItem implements Serializable {
        public final String id;
        public final String summery;
        public final String title;
        public final String urlLink;
        public final String year;
        public final String imdbRating;
        public final String movieRuntime;

        /**
         * Customized constartor.
         *
         * @param id           - Moive's Id
         * @param summery      - Moive's Summery
         * @param title        - Movie's Title
         * @param urlLink      - Movie's Poster Image URL Link </br>
         *                     <strong> blank/incorrect URL will result default image background instead </strong>
         * @param year         - Movie's release date year.
         * @param imdbRating   - ImdB rating for the movie.
         * @param movieRuntime - Movie's length
         */
        public MovieItem(String id, String summery, String title, String urlLink, String year, String imdbRating, String movieRuntime) {
            this.id = id;
            this.summery = summery;
            this.title = title;
            this.urlLink = urlLink;
            this.year = year;
            this.imdbRating = imdbRating;
            this.movieRuntime = movieRuntime;
        }

        /**
         * Default constartor with no parameters.
         */
        public MovieItem() {
            this.id = SEARCH_OTHER_MOVIES;
            this.summery = "";
            this.title = "";
            this.urlLink = "";
            this.year = "";
            this.imdbRating = "";
            this.movieRuntime = "";
        }

        /**
         * @return Fully detailed of all MovieItem's attributes value.
         */
        @Override
        public String toString() {
            return "Movie's id:" + id + "\n" +
                    "Movie's Title:" + title + "\n" +
                    "Movie's Summery:" + summery + "\n" +
                    "Movie's Poster-Url-Link:" + urlLink + "\n" +
                    "Movie's ImdB rating:" + imdbRating + "\n" +
                    "Movie's Run time:" + movieRuntime + "\n" +
                    "Movie's Publish date:" + year + "\n";
        }
    }
}
