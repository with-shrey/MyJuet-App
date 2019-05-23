package app.myjuet.com.myjuet.utilities;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

public class BindingAdapters {
    @BindingAdapter({"android:src"})
    public static void setImageViewResource(ImageView imageView, int resource) {
        if (resource != 0)
        Picasso.get().load(resource).fit().into(imageView);
    }
//    @BindingAdapter("onPageChangeListener")
//    public static void setOnPageChangeListener(ViewPager viewPager, ViewPager.OnPageChangeListener listener) {
//        // clear listeners first avoid adding duplicate listener upon calling notify update related code
//        viewPager.clearOnPageChangeListeners();
//        viewPager.addOnPageChangeListener(listener);
//    }
//
//    @BindingAdapter("offScreenPageLimit")
//    public static void setOffScreenPageLimit(ViewPager viewPager, int offScreenLimit) {
//        // clear listeners first avoid adding duplicate listener upon calling notify update related code
//        viewPager.setOffscreenPageLimit(offScreenLimit);
//    }

}
