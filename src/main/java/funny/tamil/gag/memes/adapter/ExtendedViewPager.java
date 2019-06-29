package funny.tamil.gag.memes.adapter;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.ortiz.touchview.TouchImageView;

public class ExtendedViewPager extends ViewPager {

    public ExtendedViewPager(Context context) {
        super(context);
    }

    public ExtendedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof TouchImageView) {
            return ((TouchImageView) v).canScrollHorizontallyFroyo(-dx);
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }

}