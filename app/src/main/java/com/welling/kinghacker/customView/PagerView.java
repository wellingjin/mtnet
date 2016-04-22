package com.welling.kinghacker.customView;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.welling.kinghacker.activities.R;
import com.welling.kinghacker.tools.SystemTool;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by KingHacker on 3/6/2016.
 * 左右切换
 */
public class PagerView extends FrameLayout {
    private ViewPager viewPager;
    private  Context context;
    private LinearLayout linearLayout;
    private List<TextView> dots;
    private int dotSize = 20;
    private List<View> pagers;
    private PagerAdapter pagerAdapter;
    private int lastSelectedDot = 0;
    private boolean isCreateDot;

    private OnPagerChangedListener onPagerChangedListener;


    public PagerView(Context context) {
        super(context);
        init(context);
    }
    public PagerView(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
        init(context);
    }
    public PagerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        isCreateDot = true;
        this.context = context;
        viewPager = new ViewPager(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(layoutParams);
        addView(viewPager);
        createDots();
        dots = new ArrayList<>();
        pagers = new ArrayList<>();
//        viewPager.setPageTransformer(true, new PageTransformer());
    }
//    创建导航点的layout
    private void createDots(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.indicate_layout, null);
        linearLayout = (LinearLayout)rootView.findViewById(R.id.indicate);
        addView(linearLayout);

    }
//    增加一个导航点
    private void addDot(){
        if (isCreateDot) {
            TextView dot = new TextView(context);
            dot.setBackground(getResources().getDrawable(R.drawable.shape));
            GradientDrawable myGrad = (GradientDrawable) dot.getBackground();
            if (dots.size() == 0) {
                myGrad.setColor(getResources().getColor(R.color.pagerSelectedColor));
            } else {
                myGrad.setColor(getResources().getColor(R.color.pagerUnselecteColor));
            }

            dot.setWidth(dotSize);
            dot.setHeight(dotSize);
            dots.add(dot);
            linearLayout.addView(dot);
        }
    }

    public void setIsCreateDot(boolean isCreateDot) {
        this.isCreateDot = isCreateDot;
    }

    //    增加一个item
    public void addItem(View view){
        pagers.add(view);
        addDot();
    }
//   设置好了view必须调用这个方法
    public void commit(){

        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return pagers.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(pagers.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(pagers.get(position));
                return pagers.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isCreateDot) {
                    GradientDrawable myGrad = (GradientDrawable) dots.get(position).getBackground();
                    myGrad.setColor(getResources().getColor(R.color.pagerSelectedColor));
                    myGrad = (GradientDrawable) dots.get(lastSelectedDot).getBackground();
                    myGrad.setColor(getResources().getColor(R.color.pagerUnselecteColor));
                    lastSelectedDot = position;
                }
                onPagerChangedListener.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    public void setCurrentPage(int index){
        Log.i("TAG",pagers.size() + "");
        if (pagers.size() > index && index >= 0) {
                viewPager.setCurrentItem(index);
            }
        }

    public void setOnPagerChangedListener(OnPagerChangedListener onPagerChangedListener){
        this.onPagerChangedListener = onPagerChangedListener;
    }
    public interface OnPagerChangedListener{
        void onPageSelected(int position);
    }
    class PageTransformer implements ViewPager.PageTransformer{
        final float MIN_SCALE = 0.85f;
        final float MIN_ALPHA = 0.5f;
        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();
            int pageHeight = page.getHeight();


            if (position < -1)
            { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);

            } else if (position <= 1) //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
            { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0)
                {
                    page.setTranslationX(horzMargin - vertMargin / 2);
                } else
                {
                    page.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
                        / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else
            { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }
        }

    }
}
