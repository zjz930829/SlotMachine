package com.zjz.slotmachine.tigerview;

import android.content.Context;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Scroller;

/**
 * @DESC 负责根据给定的距离和时间生成滚动值。
 * 使用“Scroller”类生成滚动值。
 * Created by ZJZ on 2020/02/20.
 */
public class SlotReelScroller implements Runnable {

    /**
     * Scrolling listener interface
     */
    public interface ScrollingListener {
        /**
         * Scrolling callback called when scrolling is performed.
         * 滚动的距离
         */
        void onScroll(int distance);

        /**
         * 滚动完成时回调
         */
        void onFinished();        
    }

    private Handler mHandler;
    private Scroller mScroller;
    private ScrollingListener mScrollListener;
    int lastY = 0;
    private int distance;
    private int previousDistance;
    
    public SlotReelScroller(Context context, ScrollingListener listener) {
    	mHandler = new Handler();
    	mScroller = new Scroller(context, new AccelerateDecelerateInterpolator());
    	//mScroller = new Scroller(context, new AccelerateInterpolator());
    	//mScroller = new Scroller(context);
    	mScrollListener = listener;
    }
    
    public void scroll(int distance, int duration) {
    	this.distance = distance;
        mScroller.forceFinished(true);
    	mScroller.startScroll(0, 0, 0, distance, duration);
    	mHandler.post(this);
    }
    
    public void run() {		
		int delta = 0;
		mScroller.computeScrollOffset();
		int currY = mScroller.getCurrY();		
		
		delta = currY - lastY;
		lastY = currY;	
		
		if (Math.abs(delta) != previousDistance && delta != 0) {
			mScrollListener.onScroll(delta);
		}			
		
		if (mScroller.isFinished() == false) {
			//Post this runnable again on UI thread until all scroll values are read.
			mHandler.post(this);
		} else {
			previousDistance = distance;
			mScrollListener.onFinished();
		}
    }
}
