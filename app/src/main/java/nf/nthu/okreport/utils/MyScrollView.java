package nf.nthu.okreport.utils;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import nf.nthu.okreport.Report;

/**
 * Created by rex5207 on 16/8/23.
 */
public class MyScrollView extends ScrollView {

    public boolean isTop=true;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        // Grab the last child placed in the ScrollView, we need it to determinate the bottom position.
        View view = (View) getChildAt(getChildCount()-1);
        if(view.getTop()==t ) {
            isTop = true;
            Report.mPullToRefreshView.setEnabled(true);
        }
        else {
            isTop = false;
            Report.mPullToRefreshView.setEnabled(false);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

}
