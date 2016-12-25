package logutil.newsfeed.utils;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.widget.ImageView;


public class AnimationsUtils {
    /**
     * Animate padding down/up
     *
     * @param holder    - RecyclerView item holder
     * @param direction -
     *                  </br> <strong> True </strong>: down.
     *                  </br> <strong> False </strong>: up.
     */
    public static void animateYFloat(RecyclerView.ViewHolder holder, boolean direction) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView, "translationY", (direction) ? 100 : -100, 0);
        animator.setDuration(1000);
        animator.start();
    }

    /**
     * Animate rotation clockwise/counter clockwise
     *
     * @param holder    - RecyclerView item holder
     * @param direction -
     *                  </br> <strong> True </strong>: Clockwise.
     *                  </br> <strong> False </strong>: Counter Clockwise.
     */
    public static void animateRotationFloat(ImageView holder, boolean direction) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder, "rotation", (direction) ? -360 : 360, 0);
        animator.setDuration(1000);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
    }
}
