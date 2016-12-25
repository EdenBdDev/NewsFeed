package logutil.newsfeed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import logutil.newsfeed.R;
import logutil.newsfeed.data.NewsItem;


public class ExtenedView extends Fragment {
    /**
     * Parent's context
     */
    private View view;
    /**
     * Poster image view
     */
    private ImageView big_image;
    /**
     * Title image view
     */
    private TextView big_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.newsitem_extend, container, false);
        big_image = (ImageView) view.findViewById(R.id.movie_foter_big);
        big_text = (TextView) view.findViewById(R.id.movie_title_big);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.frame_id);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.INVISIBLE);
            }
        });
        view.setVisibility(View.INVISIBLE);
        return view;
    }

    /**
     * Displays movie card view
     *
     * @param movie - MovieItem
     */
    public void changeUI(NewsItem.MovieItem movie) {
        view.setVisibility(View.VISIBLE);
        if (movie.urlLink.length() > 0) {
            Picasso.with(view.getContext()).load(movie.urlLink)
                    .resize(800, 800)
                    .placeholder(R.drawable.popcorn_logo).into(big_image);

        } else {
            switch (movie.id) {
                case Board_Info.ABOUT:
                    big_image.setImageResource(R.drawable.about_us_bg);
                    break;
                case Board_Info.CONTACT:
                    big_image.setImageResource(R.drawable.contact_us);
                    break;
                case Board_Info.WELCOME:
                    big_image.setImageResource(R.drawable.popcorn_1776004_1280);
                    break;

                default:
                    big_image.setImageResource(R.drawable.popcorn_logo);
                    break;
            }
        }
        big_text.setText(movie.title);
    }
}
