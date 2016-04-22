package com.welling.kinghacker.customView;

import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.welling.kinghacker.activities.R;

/**
 * Created by KingHacker on 3/5/2016.
 *
 */
public class FlipperView extends ViewFlipper  implements OnGestureListener{
    Animation leftInAnimation,leftOutAnimation,rightInAnimation,rightOutAnimation;
    public GestureDetector detector;
    public FlipperView(Context context){
        super(context);
        init(context);

    }
    public FlipperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }



    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
       Log.i("onSCroll",e1.getX() + " e2:" +e2.getX());
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i("FlipperViewLog","gggg");
        if(e1.getX()-e2.getX()>120){
            setInAnimation(rightInAnimation);
            setOutAnimation(leftOutAnimation);
            showNext();//向左滑动
            return true;
        }else if(e1.getX()-e2.getY()<-120){
            setInAnimation(leftInAnimation);
            setOutAnimation(rightOutAnimation);
            showPrevious();//向右滑动
            return true;
        }
        return false;
    }
    private void init(Context context){
        detector = new GestureDetector(this);
        //动画效果
        leftInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
        leftOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
        rightInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
        rightOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_right);
    }
}
