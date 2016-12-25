package logutil.newsfeed.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.lang.ref.WeakReference;

/**
 *  Class for interaction with Volley lib.
 */
public class CommunicationUtil {
    /**
     *  CommunicationUtl instance.
     */
    private static CommunicationUtil sInstance = null;
    /**
     *  Volley request queue.
     */
    private RequestQueue mRequestQueue;

    /**
     * Private constructor
     * @param context - Activity
     */
    private CommunicationUtil(Context context){
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     *
     * @return current instance
     */
    public static CommunicationUtil getInstance(){
        return sInstance;
    }

    /**
     * Public initialization for instance's context.
     * @param contextWrapper - Context
     */
    public static void init(WeakReference<Context> contextWrapper){
        if(sInstance == null){
            sInstance = new CommunicationUtil(contextWrapper.get());
        }
    }

    /**
     *
     * @return Volley's request queue.
     */
    public RequestQueue getmRequestQueue(){
        return mRequestQueue;
    }
}
