package logutil.newsfeed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import logutil.newsfeed.R;
import logutil.newsfeed.data.NewsItem;
import logutil.newsfeed.fragments.Board_Info;
import logutil.newsfeed.fragments.NewsListFragment.NewsFeedFragListener;
import logutil.newsfeed.utils.AnimationsUtils;
import logutil.newsfeed.utils.OnLoadMoreListener;

import static android.content.ContentValues.TAG;


public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<NewsItem.MovieItem> mValues;
    private final NewsFeedFragListener mListener;
    /**
     * MovieHolder Time attribute mode
     */
    private final int VIEW_TYPE_ITEM = 0;
    /**
     * MovieHolder Time attribute mode
     */
    private final int VIEW_TYPE_LOADING = 1;
    /**
     * MovieHolder Time attribute mode
     */
    private final int VIEW_TYPE_LOADING_OTHER = 2;
    /**
     * Flag for loading.
     */
    public boolean isLoading = false;
    /**
     * Interface Listiner for loading more items.
     */
    public OnLoadMoreListener mOnLoadMoreListener;

    //Onloading attributes
    public int totalItemCount;
    public int pastVisiblesItems, visibleItemCount;
    private int prevPosition = 0;

    /**
     * Parent's context
     */
    private Context context;

    /**
     * Default constarctor
     *
     * @param items    - List of MovieItems.
     * @param listener - Fragment's Listiner Interface for communication
     */
    public NewsAdapter(List<NewsItem.MovieItem> items, NewsFeedFragListener listener) {
        mValues = items;
        mListener = listener;
    }

    /**
     * Creates the layout for each view type
     * @param parent - context
     * @param viewType - view type.
     * @return - Layout based of the view type:</br>
     * <strong>VIEW_TYPE_ITEM :</strong> fragment_newsitem layout. </br>
     * <strong>VIEW_TYPE_LOADING :</strong> layout_loading_item layout. </br>
     * <strong>VIEW_TYPE_LOADING_OTHER :</strong> layout_loading_more_items layout. </br>
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_newsitem, parent, false);
                return new MovieHolder(view);

            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingHolder(view);

            case VIEW_TYPE_LOADING_OTHER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_loading_more_items, parent, false);
                return new LoadingMoreItemsHolder(view);
        }

        return null;
    }

    /**
     * Inserts the data to the view.
     * @param holder - relevent View Holder
     * @param position - current position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MovieHolder) {
            MovieHolder itemHolder = (MovieHolder) holder;
            itemHolder.mItem = mValues.get(position);

            if (isLoading) {
                if (position >= prevPosition) {
                    AnimationsUtils.animateYFloat(holder, true);
                } else {
                    AnimationsUtils.animateYFloat(holder, false);
                }
            }
            Log.e(TAG, "onBindViewHolder: " + position);
            prevPosition = position;
            if (((MovieHolder) holder).mItem != null) {

                switch (itemHolder.mItem.id) {
                    case Board_Info.ABOUT:
                        populateCard(holder);
                        break;
                    case Board_Info.CONTACT:
                        populateCard(holder);
                        break;
                    default:
                        populateCard(holder);
                        break;
                }

            }

        } else if (holder instanceof LoadingHolder) {
            LoadingHolder loadingViewHolder = (LoadingHolder) holder;
            AnimationsUtils.animateRotationFloat(loadingViewHolder.img, true);
            Log.e(TAG, "onBindViewHolder: Started ROTATIONNNNN");
        } else if (holder instanceof LoadingMoreItemsHolder) {
            final LoadingMoreItemsHolder ldMoreHolder = (LoadingMoreItemsHolder) holder;
            ldMoreHolder.mItem = mValues.get(position);
            ldMoreHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mListener.searchOtherMovies();
                }
            });
        }
    }

    /**
     * Inserts the data to the view.
     * @param holder Item view
     */
    private void populateCard(final RecyclerView.ViewHolder holder) {
        //Start The movie_title insertion
        String title_build = ((MovieHolder) holder).mItem.title + ", " + ((MovieHolder) holder).mItem.year + ".";
        title_build = title_build.replace("N/A", "").replaceAll("Temp", "").replaceAll(",.", ".");
        ((MovieHolder) holder).movie_title.setText(title_build);
        //End of The movie_title insertion

        //Start The summery insertion
        ((MovieHolder) holder).summery.setText(((MovieHolder) holder).mItem.summery.replace("N/A", "No summery."));
        //End of The summery insertion

        //Start The Poster Image insertion
        if (((MovieHolder) holder).mItem.urlLink.length() > 0) {
            Picasso.with(context).load(((MovieHolder) holder).mItem.urlLink)
                    .resize(800, 800)
                    .placeholder(R.drawable.popcorn_logo).into(((MovieHolder) holder).movie_foter);

        } else {
            switch (((MovieHolder) holder).mItem.id) {
                case Board_Info.ABOUT:
                    ((MovieHolder) holder).movie_foter.setImageResource(R.drawable.about_us_bg);
                    break;
                case Board_Info.CONTACT:
                    ((MovieHolder) holder).movie_foter.setImageResource(R.drawable.contact_us);
                    break;
                case Board_Info.WELCOME:
                    ((MovieHolder) holder).movie_foter.setImageResource(R.drawable.popcorn_1776004_1280);
                    break;

                default:
                    ((MovieHolder) holder).movie_foter.setImageResource(R.drawable.popcorn_logo);
                    break;
            }
        }
        //End of The Poster Image insertion

        // Attach on click handling
        ((MovieHolder) holder).mView.setOnClickListener(callExpandView(((MovieHolder) holder).mItem));

        //Start The star rating insertion
        double rating;
        int roundRating;
        if (!((MovieHolder) holder).mItem.imdbRating.equals("N/A")) {
            rating = Double.parseDouble(((MovieHolder) holder).mItem.imdbRating);
            roundRating = (int) rating;
        } else {
            rating = -1;
            roundRating = 0;
        }
        if (((MovieHolder) holder).rating.getChildCount() > 0)
            ((MovieHolder) holder).rating.removeAllViews();
        ImageView imageView;
        String rate_for_text;
        if (-1 < rating && rating <= NewsItem.MAX_RATING) {
            // Positive rating handling
            for (int i = 0; i < roundRating; i++) {
                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.fullstar);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                //marginParams.setMargins(left, top, right, bottom);
                marginParams.setMargins(6, 15, 0, 15);
                ((MovieHolder) holder).rating.addView(imageView);
            }
            // Half rating handling
            if (rating - (double) roundRating > 0) {
                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.halfstar);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                marginParams.setMargins(6, 15, 0, 15);
                ((MovieHolder) holder).rating.addView(imageView);
                roundRating++;
                rate_for_text = rating + "/" + NewsItem.MAX_RATING;
            } else {
                rate_for_text = roundRating + "/" + NewsItem.MAX_RATING;
            }
            // Fills the rest with blank stars
            for (; roundRating < NewsItem.MAX_RATING; roundRating++) {
                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.blankstar);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                marginParams.setMargins(6, 15, 0, 15);
                ((MovieHolder) holder).rating.addView(imageView);
            }
            TextView ratingText = new TextView(context);
            ratingText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) ratingText.getLayoutParams();
            marginParams.setMargins(6, 15, 0, 0);
            ratingText.setText(rate_for_text);
            ratingText.setTextSize(12);
            ((MovieHolder) holder).rating.addView(ratingText);
            //End of The star rating insertion
        }

    }

    /**
     * Calls Expanded View throught the lisiner communicator
     *
     * @param mItem - MovieItem.
     * @return - New Click listener method
     */
    private View.OnClickListener callExpandView(final NewsItem.MovieItem mItem) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnListnListener(mItem);
            }
        };
    }

    /**
     * @return total size of the dataset
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * @param position - current position
     * @return MovieHolder for each mode: </br>
     * <strong>VIEW_TYPE_ITEM :</strong> MovieHolder View. </br>
     * <strong>VIEW_TYPE_LOADING :</strong> LoadingHolder View. </br>
     * <strong>VIEW_TYPE_LOADING_OTHER :</strong> LoadingMoreItemsHolder View. </br>
     */
    @Override
    public int getItemViewType(int position) {
        if (mValues.get(position) == null) {
            return VIEW_TYPE_LOADING;
        } else if (mValues.get(position).id.equals(NewsItem.SEARCH_OTHER_MOVIES)) {
            return VIEW_TYPE_LOADING_OTHER;
        }
        return VIEW_TYPE_ITEM;
        //return mValues.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    /**
     * Establishes interface for loading more items.
     *
     * @param mOnLoadMoreListener - Interface listener for loading more items. </br>
     *                            (OnLoadMoreListener)
     */
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    /**
     *  Disable loading state
     */
    public void setLoaded() {
        isLoading = false;
    }

    private static class LoadingHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        /**
         * Default constractor.
         *
         * @param itemView - layout_loading_items layout
         */
        private LoadingHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.progress_image);
        }
    }

    private static class LoadingMoreItemsHolder extends RecyclerView.ViewHolder {
        private View mView;
        private NewsItem.MovieItem mItem;

        /**
         * Default constractor.
         *
         * @param itemView - layout_loading_more_items layout
         */
        private LoadingMoreItemsHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
    }

    private class MovieHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private NewsItem.MovieItem mItem;
        private ImageView movie_foter;
        private TextView movie_title;
        private TextView summery;
        private LinearLayout rating;

        /**
         * Default constractor.
         *
         * @param view - fragment_newsitem layout
         */
        private MovieHolder(View view) {
            super(view);
            mView = view;

            movie_foter = (ImageView) itemView.findViewById(R.id.movie_foter);
            movie_title = (TextView) itemView.findViewById(R.id.movie_title);
            summery = (TextView) itemView.findViewById(R.id.summery);
            rating = (LinearLayout) itemView.findViewById(R.id.StarHolder);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

}
