package logutil.newsfeed.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import logutil.newsfeed.R;


public class Board_Info extends Fragment {
    /**
     *  About id mode.
     */
    public static final String ABOUT = "tRtX4523";
    /**
     *  CONTACT id mode.
     */
    public static final String CONTACT = "tRtX3554";
    /**
     *  WELCOME id mode.
     */
    public static final String WELCOME = "tRtX6485";
    /**
     *  NO_MATCH id mode.
     */
    public static final String NO_MATCH = "tRtX4040";
    /**
     *  Parent's context
     */
    private View view;
    /**
     *  Summery's text view
     */
    private TextView big_text_sum;
    /**
     *  URL-Link text view
     */
    private TextView big_text_url;
    /**
     *  Title text view
     */
    private TextView big_text_title;
    /**
     *  Movie poseter image view
     */
    private ImageView news_container;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.info_template, container, false);
        big_text_sum = (TextView) view.findViewById(R.id.movie_summery_big);
        big_text_url = (TextView) view.findViewById(R.id.url_text);
        big_text_title = (TextView) view.findViewById(R.id.movie_title_big);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.frame_id);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.INVISIBLE);
            }
        });
        view.setVisibility(View.INVISIBLE);
        news_container = (ImageView) view.findViewById(R.id.container_bg);

        return view;
    }

    /**
     * Change the templates UI according to the id parameter
     * @param title - Card's title.
     * @param sum - Card's summery.
     * @param url - Card's URL Link (Contact id required)
     * @param id - Card's id mode:
     *           </br> <strong> R.id.Contact :</strong> Contact layout
     *           </br> <strong> R.id.About :</strong> About layout
     *           </br> <strong> R.id.Home :</strong> Home layout
     */
    public void changeUI(String title, String sum,final String url,int id) {
        view.setVisibility(View.VISIBLE);
        float alpha = 0.3f;
        switch (id)
        {
            case R.id.Contact:
                view.setVisibility(View.VISIBLE);
                big_text_title.setText(Html.fromHtml(title));
                big_text_sum.setText(sum);
                big_text_url.setVisibility(View.VISIBLE);
                String htmlString="<b><u>"+url+"</u></b>";
                big_text_url.setText(Html.fromHtml(htmlString));
                big_text_url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uriUrl = Uri.parse(url);
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        startActivity(launchBrowser);
                    }
                });
                news_container.setImageResource(R.drawable.contact_us);
                news_container.setAlpha(alpha);
                break;
            case R.id.About:
                view.setVisibility(View.VISIBLE);

                big_text_title.setText(title);
                big_text_sum.setText(sum);
                big_text_url.setVisibility(View.INVISIBLE);
                news_container.setImageResource(R.drawable.about_us_bg);
                news_container.setAlpha(alpha);
                break;
            case R.id.Home:
                view.setVisibility(View.VISIBLE);
                big_text_title.setText(title);
                big_text_sum.setText(sum);
                big_text_url.setVisibility(View.INVISIBLE);
                news_container.setImageResource(R.drawable.popcorn_1776004_1280);
                news_container.setAlpha(alpha);
                break;
            default:
                break;
        }

    }

}
