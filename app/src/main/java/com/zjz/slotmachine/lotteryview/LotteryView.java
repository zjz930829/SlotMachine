package com.zjz.slotmachine.lotteryview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;


import com.zjz.slotmachine.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ZJZ on 2019/4/19.
 */
public class LotteryView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    boolean flag = false;

    /**
     * 绘制中奖的矩形区域
     */
    private Paint mPaint;//i%2!=0 的背景色
    private Paint mPaint2;//i%2==0 的背景色

    /**
     * 绘制背景
     */
    private Paint mBackgroundPaint;

    /**
     * 绘制开始按钮
     */
    private Paint mButtonPaint;

    /**
     * 绘制边框
     */
    private Paint mBorderPaint;

    /**
     * 绘制阴影
     */
    private Paint mShadePaint;//抽奖中和抽中奖的样式

    /**
     * 绘制文字（底部文字）
     */
    private TextPaint mTextPaint; //i%2!=0 的文字
    private TextPaint mTextPaint2;//i%2==0 的文字
    private TextPaint mTextPaintLottery;//正在开奖或者已经开奖的文字
    private TextPaint mSingleTextPaint;//i%2!=0  单行文字
    private TextPaint mSingleTextPaint2;//i%2==0  单行文字
    private TextPaint mSingleTextPaintLottery;//正在开奖或者已经开奖的单行文字

    /**
     * 绘制文字2（顶部特殊样式文字）
     */
    private TextPaint mNumTextPaint; //i%2!=0 的文字2
    private TextPaint mNumTextPaint2;//i%2==0 的文字2
    private TextPaint mNumTextPaintLottery;//正在开奖或已经开奖的数字

    private ArrayList<Rect> mRectList;
    private int awardCount = 0;
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    /**
     * 列数
     */
    private int columnCount;

    /**
     * 行数
     */
    private int rowCount = 3;
    /**
     * 文字的颜色
     */
    private int fontColor;
    /**
     * 文字的颜色
     */
    private int fontColor2;
    /**
     * 文字的字体大小
     */
    private int fontSize;

    /**
     * 文字的字体大小
     */
    private int subFontSize;

    /**
     * 文字的字体大小
     */
    private int singleFontSize;
    /**
     * 背景颜色
     */
    private int backgroundColor;
    /**
     * 背景颜色
     */
    private int prizeBackgroundColor;
    /**
     * 背景颜色
     */
    private int prizeBackgroundColor2;
    /**
     * 每个矩形方框的尺寸
     */
    private int everyWidth;
    /**
     * 每个矩形方框的尺寸
     */
    private int everyHeight;

    private int currentCount = -1;


    private boolean isDrawing;
    private Thread drawThread;


    private List<LotteryData> awardList;
    private ObjectAnimator mRunningAnimator;

    public static final int IS_LOTTERYING = 1;
    public static final int IS_DEFAULT = 0;
    public static final int IS_RESULT = 2;

    public int lotteryState = IS_DEFAULT;
    /**
     * 抽奖按钮的半径
     */
    private int radius = 100;
    /**
     * 抽奖按钮是否可用
     */
    private boolean isEnable = true;

//    private String[] str = {"奖品1", "奖品2", "奖品3", "奖品4", "奖品5", "奖品6", "奖品7", "奖品8"};
    /**
     * 实际绘制区域的宽度
     */
    private int realityWidth;

    /**
     * 实际绘制区域的高度
     */
    private int realityHeight;
    /**
     * 一圈多少毫秒
     */
    private int roundTime = 1000;

    private Region mButtonRegion;
    private ValueAnimator mResultingAnimator;
    private int result;

    public LotteryView(Context context) {
        this(context, null);

    }

    public LotteryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LotteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomAttrs(context, attrs);
        init();
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        //获取自定义属性。
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LotteryViewAttrs);
        //获取默认列数
        columnCount = ta.getInteger(R.styleable.LotteryViewAttrs_columnCount, 5);
        rowCount = ta.getInteger(R.styleable.LotteryViewAttrs_rowCount, 3);
        fontSize = (int) ta.getDimension(R.styleable.LotteryViewAttrs_fontSize, 16);
        subFontSize = (int) ta.getDimension(R.styleable.LotteryViewAttrs_subFontSize, 16);
        singleFontSize = (int) ta.getDimension(R.styleable.LotteryViewAttrs_singleFontSize, 16);
        fontColor = ta.getColor(R.styleable.LotteryViewAttrs_fontColor, Color.BLUE);
        fontColor2 = ta.getColor(R.styleable.LotteryViewAttrs_fontColor2, Color.BLUE);
        backgroundColor = ta.getColor(R.styleable.LotteryViewAttrs_backgroundColor, Color.GREEN);
        prizeBackgroundColor = ta.getColor(R.styleable.LotteryViewAttrs_prizeBackgroundColor, Color.GREEN);
        prizeBackgroundColor2 = ta.getColor(R.styleable.LotteryViewAttrs_prizeBackgroundColor2, Color.GREEN);
        ta.recycle();
    }


    public void setAwardList(List<LotteryData> awardList) {
        if (awardList == null || awardList.size() == 0) {
            Log.d("pdy", "请传入正确的数据");
            return;
        }
        this.awardList = awardList;
        awardCount = awardList.size();
        resetLottery();
    }

    public List<LotteryData> getAwardList() {
        return awardList;
    }

    private int lasCurrentCount;

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
//        if (this.currentCount == mRectList.size()) {
//            this.currentCount = 0;
//        } else {
//            this.currentCount++;
//        }
        if (lasCurrentCount == currentCount) {
            return;
        }

        mRectList.clear();
        try {
            // 这个就相当于帧频了，数值越小画面就越流畅
            if (flag) return;
            mCanvas = mHolder.lockCanvas();
//            mCanvas.save();
//            drawShade(mCanvas);
//            mCanvas.restore();
            draw();
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            LogUtil.d("run_finally--unlockCanvasAndPost：");
            if (!flag) {
                if (mHolder != null && mCanvas != null) {
                    try {
                        mHolder.unlockCanvasAndPost(mCanvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        lasCurrentCount = currentCount;
    }

//    public void setCurrentStopCount(int currentStopCount) {
//        LogUtil.d("setcurrentStopCount--" + currentStopCount);
//        this.currentStopCount = currentStopCount;
//        setCurrentCount(currentCount++);
//    }

    private void init() {
        mHolder = getHolder();
//        setZOrderOnTop(true);
//        setZOrderMediaOverlay(true);
//        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mHolder.addCallback(this);

        mRectList = new ArrayList<>();
        awardList = new ArrayList();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(prizeBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);

        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2.setColor(prizeBackgroundColor2);
        mPaint2.setStyle(Paint.Style.FILL);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(backgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(Color.RED);
        mButtonPaint.setStyle(Paint.Style.FILL);

//        mBorderPaint = new Paint();
//        mBorderPaint.setStrokeWidth(3);
//        mBorderPaint.setColor(Color.RED);
//        mBorderPaint.setStyle(Paint.Style.STROKE);

        mShadePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadePaint.setColor(Color.parseColor("#F03737"));
        mShadePaint.setStrokeWidth(3);
        mShadePaint.setStyle(Paint.Style.FILL);

//        Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/DINCond-Bold.otf");
        mNumTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        mNumTextPaint.setTypeface(typeFace);
        mNumTextPaint.setColor(fontColor);
        mNumTextPaint.setTextSize(fontSize);

        mNumTextPaint2 = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        mNumTextPaint2.setTypeface(typeFace);
        mNumTextPaint2.setColor(fontColor2);
        mNumTextPaint2.setTextSize(fontSize);

        mNumTextPaintLottery = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        mNumTextPaintLottery.setTypeface(typeFace);
        mNumTextPaintLottery.setColor(Color.parseColor("#ffffff"));
        mNumTextPaintLottery.setTextSize(fontSize);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTypeface(typeFace);
        mTextPaint.setColor(fontColor);
        mTextPaint.setTextSize(subFontSize);

        mTextPaint2 = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint2.setTypeface(typeFace);
        mTextPaint2.setColor(fontColor2);
        mTextPaint2.setTextSize(subFontSize);

        mTextPaintLottery = new TextPaint(Paint.ANTI_ALIAS_FLAG);
//        mTextPaintLottery.setTypeface(typeFace);
        mTextPaintLottery.setColor(Color.parseColor("#ffffff"));
        mTextPaintLottery.setTextSize(subFontSize);

        mSingleTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mSingleTextPaint.setColor(fontColor);
        mSingleTextPaint.setTextSize(singleFontSize);

        mSingleTextPaint2 = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mSingleTextPaint2.setColor(fontColor2);
        mSingleTextPaint2.setTextSize(singleFontSize);

        mSingleTextPaintLottery = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mSingleTextPaintLottery.setColor(Color.parseColor("#ffffff"));
        mSingleTextPaintLottery.setTextSize(singleFontSize);

//        awardList.addAll(Arrays.asList(str));
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("pdy", "surfaceCreated--调用surfaceCreated");
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            try {
                holder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        flag = false;
        isDrawing = true;
        drawThread = new Thread(this);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("pdy", "surfaceChanged--调用surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("pdy", "surfaceDestroyed--调用surfaceDestroyed");
        currentCount = -1;
        if (mRunningAnimator != null) {
            mRunningAnimator.cancel();
            mRunningAnimator.removeAllListeners();
        }
        flag = true;
        isDrawing = false;
//        mHolder.removeCallback(this);
//        mHolder = null;
//        mCanvas = null;
        mRectList.clear();
        drawThread = null;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mButtonRegion != null && mButtonRegion.contains(x, y)) {
                    Log.d("pdy", "onTouchEvent-X:" + x + "Y:" + y);

                }
                break;
            case MotionEvent.ACTION_UP:
                if (mButtonRegion != null && mButtonRegion.contains(x, y)) {
                    Log.d("pdy", "onTouchEvent-X:" + x + "Y:" + y);
//                    if (isEnable) {
                    if (lotteryState != IS_LOTTERYING) {
//                            startLottery();
                        if (lotteryClickListener != null) {
                            lotteryClickListener.startLottery();
                        }
                    }
//                    }
                }
                break;
            default:
                break;
        }
        return true;

    }

    @Override
    public void run() {
        while (isDrawing) {
            try {
                // 这个就相当于帧频了，数值越小画面就越流畅
                if (flag) return;
                mCanvas = mHolder.lockCanvas();
                draw();
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                LogUtil.d("run_finally--unlockCanvasAndPost：");
                if (!flag) {
                    try {
                        mHolder.unlockCanvasAndPost(mCanvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    /**
     * 让阴影滚动起来
     *
     * @param
     */
    public void startLottery() {
        Log.d("pdy", "startLottery--开奖了");

        lotteryState = IS_LOTTERYING;
//        drawLotteryButton(mCanvas);
        if (currentCount > mRectList.size()) {
            return;
        }
        if (mRunningAnimator != null) {
            currentCount = -1;
            mRunningAnimator.cancel();
        }
//        int timeResult = testRandom3() * 1000;
        //由于属性动画中，当达到最终值会立刻跳到下一次循环，所以需要补1
        mRunningAnimator = ObjectAnimator.ofInt(this, "currentCount", -1, mRectList.size());
        mRunningAnimator.setRepeatMode(ValueAnimator.RESTART);
        mRunningAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRunningAnimator.setDuration(roundTime);
        mRunningAnimator.setInterpolator(new LinearInterpolator());
        mRunningAnimator.start();

    }

    /**
     * 重置抽奖状态
     */
    public void resetLottery() {
        lotteryState = IS_DEFAULT;
//        drawLotteryButton(mCanvas);
        isDrawing = true;
        isEnable = true;
        currentCount = -1;
        if (mRunningAnimator != null) {
            mRunningAnimator.cancel();
        }
        mRectList.clear();
        try {
            // 这个就相当于帧频了，数值越小画面就越流畅
            if (flag) return;
            mCanvas = mHolder.lockCanvas();
            draw();
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            LogUtil.d("run_finally--unlockCanvasAndPost：");
            if (!flag) {
                try {
                    mHolder.unlockCanvasAndPost(mCanvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void stopLottery() {
        Log.d("pdy", "stopLottery--开奖了");
        isDrawing = true;
        isEnable = false;

        //结果动画
        result = getResult();
        mResultingAnimator = ValueAnimator.ofInt(0, mRectList.size() + result);
        mResultingAnimator.setInterpolator(new DecelerateInterpolator());
        int duration = (int) ((roundTime + 500) + (float) result / mRectList.size() * (roundTime + 500));
        mResultingAnimator.setDuration(duration);
        mResultingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //更新当前的数值
                if (mRectList == null || mRectList.size() == 0) return;
                int currentCount = (int) animation.getAnimatedValue() % mRectList.size();
//                LogUtil.d("stopLottery--ValueAnimator更新。。。" + currentCount);
                setCurrentCount(currentCount);
            }


        });

        mResultingAnimator.addListener(new LotteryAnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                lotteryState = IS_RESULT;
                Log.d("pdy", "stopLottery--结果动画结束。。。");
                if (currentCount != mRectList.size()) {
                    if (lotteryClickListener != null && awardList.size() > currentCount && currentCount >= 0) {
                        lotteryClickListener.stopLottery(awardList.get(currentCount));
                    }
                }
            }

        });
        if (mRectList == null || mRectList.size() == 0) return;
        //不是完整一圈的，要把一圈走完
        final ObjectAnimator tempAnimator = ObjectAnimator.ofInt(this, "currentCount", currentCount, mRectList.size());
//        int duration1 = (int) (roundTime + (float) result / mRectList.size() * roundTime);

        float tempDuration = (float) (mRectList.size() - currentCount) / mRectList.size() * (roundTime + 500);
        if ((long) tempDuration > 0) tempAnimator.setDuration((long) tempDuration);
        tempAnimator.setInterpolator(new LinearInterpolator());
        tempAnimator.addListener(new LotteryAnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                //过渡动画完成，产生真正的结果
                Log.d("pdy", "stopLottery--结果动画开始。。。");
                if (lotteryClickListener != null && lotteryState == IS_LOTTERYING) {
                    lotteryClickListener.startResultAnim();
                }
                mResultingAnimator.start();
            }

        });

        if (mRunningAnimator != null) {
            mRunningAnimator.addListener(new LotteryAnimatorListener() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.d("pdy", "stopLottery--过渡动画开始");
                    tempAnimator.start();

                }

            });
            mRunningAnimator.cancel();
        }

    }

    private int testRandom3() {
        Random random = new Random();
        return random.nextInt(10);
    }


    /**
     * 绘制开始
     */
    private void draw() {
        //计算出抽奖块的位置
        calculate();
        //绘制抽奖的背景
        drawBackground(mCanvas);
        //绘制开始按钮
        drawLotteryButton(mCanvas);
        //绘制遮罩
        drawShade(mCanvas);
    }

    private void drawLotteryButton(Canvas canvas) {
        mButtonRegion = new Region(realityWidth / 2 - everyWidth * 3 / 2, realityHeight / 2 - everyWidth / 2, realityWidth / 2 + everyWidth * 3 / 2, realityHeight / 2 + everyWidth / 2);
//        canvas.drawCircle(realityWidth / 2, realityHeight / 2, radius, mButtonPaint);
//        canvas.drawRect(realityWidth / 2 - everyWidth*3 /2, realityHeight / 2 - everyWidth / 2, realityWidth / 2 + everyWidth*3/2 , realityHeight / 2 + everyWidth / 2,  mButtonPaint);
//        if (lotteryState == IS_LOTTERYING) {
//            Point point = calculateTextLocation(mButtonRegion.getBounds(), "STOP");
//            canvas.drawText("STOP", point.x, point.y, mNumTextPaint);
//        } else {
//            Point point = calculateTextLocation(mButtonRegion.getBounds(), "抽一次");
//            canvas.drawText("抽一次", point.x, point.y, mNumTextPaint);
//        }
    }

    private Point calculateNumTextLocation(Rect rectF1, String text) {
        Point point = new Point();
        //矩形区域的宽度
        int rectWidth = rectF1.right - rectF1.left;
        int rectHeight = rectF1.bottom - rectF1.top;
        Rect rect = new Rect();
        mNumTextPaint.getTextBounds(text, 0, text.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();

        float heih = rectF1.top + rectHeight / 3 + textHeight / 3 * 2;
        point.set(rectF1.left + (rectWidth - textWidth) / 2, (int) heih);

        return point;

    }

    private Point calculateTextLocation(Rect rectF1, String text) {
        Point point = new Point();
        //矩形区域的宽度
        int rectWidth = rectF1.right - rectF1.left;
        int rectHeight = rectF1.bottom - rectF1.top;
        Rect rect = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();

        float heih = rectF1.top + rectHeight / 3 * 2 + textHeight / 2;
        point.set(rectF1.left + (rectWidth - textWidth) / 2, (int) heih);

        return point;

    }

    private Point calculateSingleTextLocation(Rect rectF1, String text) {
        Point point = new Point();
        //矩形区域的宽度
        int rectWidth = rectF1.right - rectF1.left;
        int rectHeight = rectF1.bottom - rectF1.top;
        Rect rect = new Rect();
        mSingleTextPaint.getTextBounds(text, 0, text.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();

        float heih = rectF1.top + rectHeight / 2 + textHeight / 2;
        point.set(rectF1.left + (rectWidth - textWidth) / 2, (int) heih);

        return point;

    }


    private Point calculateSingleTextTopLocation(Rect rectF1, String text) {
        Point point = new Point();
        //矩形区域的宽度
        int rectWidth = rectF1.right - rectF1.left;
        int rectHeight = rectF1.bottom - rectF1.top;
        Rect rect = new Rect();
        mSingleTextPaint.getTextBounds(text, 0, text.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();

        float heih = rectF1.top + rectHeight / 3 + textHeight / 2 + textHeight / 6;
        point.set(rectF1.left + (rectWidth - textWidth) / 2, (int) heih);

        return point;

    }


    private Point calculateSingleTextBottomLocation(Rect rectF1, String text) {
        Point point = new Point();
        //矩形区域的宽度
        int rectWidth = rectF1.right - rectF1.left;
        int rectHeight = rectF1.bottom - rectF1.top;
        Rect rect = new Rect();
        mSingleTextPaint.getTextBounds(text, 0, text.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();

        float heih = rectF1.top + rectHeight / 3 * 2 + textHeight / 2 - textHeight / 6;
        point.set(rectF1.left + (rectWidth - textWidth) / 2, (int) heih);

        return point;

    }

    private void drawShade(Canvas mCanvas) {
//        LogUtil.d("开始绘制阴影图" + currentCount);
//        if (mRectList.size() > currentCount&&currentCount!=-1) {
//            mCanvas.drawRect(mRectList.get(currentCount), mShadePaint);
//        }
        if (mRectList.size() == columnCount * 2 + (rowCount - 2) * 2) {
            isDrawing = false;
        }
    }

    /**
     * 计算需要多少个奖品块，奖品平均分配到4个边上
     */
    private void calculate() {
//        Log.e("zjz", "mCanvas.getWidth()=" + mCanvas.getWidth() + ",mCanvas.getHeight()=" + mCanvas.getHeight());
//        if (mCanvas.getWidth() < mCanvas.getHeight()) {
//            everyWidth = mCanvas.getWidth() / (rowCount + 1);
//        } else {
//            everyWidth = mCanvas.getHeight() / (rowCount + 1);
//        }
        everyWidth = mCanvas.getWidth() / columnCount;
        everyHeight = everyWidth * 70 / 63;
        realityWidth = everyWidth * columnCount;
        realityHeight = everyHeight * rowCount;

        //上
        int left = -everyWidth;
        int top = 0;
        int right = 0;
        int bottom = everyHeight;
        for (int i = 0; i < columnCount - 1; i++) {
            left += everyWidth;
            right += everyWidth;
            Rect rect = new Rect(left, top, right, bottom);
            mRectList.add(rect);
        }

        Log.d("pdy", "calculate1--mRectList长度：" + mRectList.size());
        //右
        left = (columnCount - 1) * everyWidth;
        top = -everyHeight;
        right = columnCount * everyWidth;
        bottom = 0;
        for (int i = 0; i < rowCount - 1; i++) {
            top += everyHeight;
            bottom += everyHeight;
            Rect rect = new Rect(left, top, right, bottom);
            mRectList.add(rect);
//            LogUtil.d("calculate2--top:" + rect.top + "bottom:" + rect.bottom);

        }

        Log.d("pdy", "calculate2--mRectList长度：" + mRectList.size());
        //下
        left = columnCount * everyWidth;
        top = (rowCount - 1) * everyHeight;
        right = (columnCount + 1) * everyWidth;
        bottom = rowCount * everyHeight;
        for (int i = 0; i < columnCount - 1; i++) {
            left -= everyWidth;
            right -= everyWidth;
            Rect rect = new Rect(left, top, right, bottom);
            mRectList.add(rect);
//            LogUtil.d("calculate3--left:" + rect.left + "right:" + rect.right);

        }

        Log.d("pdy", "calculate3--mRectList长度：" + mRectList.size());
        //左
        left = 0;
        top = rowCount * everyHeight;
        right = everyWidth;
        bottom = (rowCount + 1) * everyHeight;
        for (int i = 0; i < rowCount - 1; i++) {
            top -= everyHeight;
            bottom -= everyHeight;
            Rect rect = new Rect(left, top, right, bottom);
            mRectList.add(rect);
//            LogUtil.d("calculate4--top:" + rect.top + "bottom:" + rect.bottom);

        }
        Log.d("pdy", "calculate4--mRectList长度：" + mRectList.size());

    }


    private void drawBackground(Canvas canvas) {
//        LogUtil.d("mRectList长度：" + mRectList.size());
        canvas.drawRect(new Rect(0, 0, mCanvas.getWidth(), canvas.getHeight()), mBackgroundPaint);
        for (int i = 0; i < mRectList.size(); i++) {
//            LogUtil.d("开始绘制第：" + i);
            Rect rectF1 = mRectList.get(i);
            if (lotteryState == IS_LOTTERYING) {
                //正在开奖
                if (mRectList.size() > currentCount && currentCount != -1 && currentCount == i) {
                    canvas.drawRect(rectF1, mShadePaint);
                } else if (i % 2 == 0) {
                    canvas.drawRect(rectF1, mPaint2);
                } else {
                    canvas.drawRect(rectF1, mPaint);
                }
            } else if (lotteryState == IS_RESULT) {
                //已经开奖
                if (mRectList.size() > currentCount && currentCount != -1 && currentCount == i) {
                    canvas.drawRect(rectF1, mShadePaint);
                } else if (i % 2 == 0) {
                    canvas.drawRect(rectF1, mPaint2);
                } else {
                    canvas.drawRect(rectF1, mPaint);
                }
            } else {
                if (i % 2 == 0) {
                    canvas.drawRect(rectF1, mPaint2);
                } else {
                    canvas.drawRect(rectF1, mPaint);
                }
            }
//            canvas.drawRect(rectF1, mBorderPaint);
            //计算文字的位置
            if (i < awardCount) {
                if (lotteryState == IS_LOTTERYING) {
                    //正在开奖
                    if (mRectList.size() > currentCount && currentCount != -1 && currentCount == i) {
                        //选中的样式
                        if (!TextUtils.isEmpty(awardList.get(i).getTitle())) {
                            Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
                            mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaintLottery);
                            Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
                            mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaintLottery);
                        } else {
                            if (awardList.get(i).getSubTitle().contains(",")) {
                                String[] singletext = awardList.get(i).getSubTitle().split(",");
                                if (singletext.length > 1) {
                                    Point singTopPoint = calculateSingleTextTopLocation(rectF1, singletext[0]);
                                    mCanvas.drawText(singletext[0], singTopPoint.x, singTopPoint.y, mSingleTextPaintLottery);
                                    Point singBottomPoint = calculateSingleTextBottomLocation(rectF1, singletext[1]);
                                    mCanvas.drawText(singletext[1], singBottomPoint.x, singBottomPoint.y, mSingleTextPaintLottery);
                                }
                            } else {
                                Point singPoint = calculateSingleTextLocation(rectF1, awardList.get(i).getSubTitle());
                                mCanvas.drawText(awardList.get(i).getSubTitle(), singPoint.x, singPoint.y, mSingleTextPaintLottery);
                            }
                        }

                    } else if (i % 2 == 0) {
                        //未选中的样式
                        if (!TextUtils.isEmpty(awardList.get(i).getTitle())) {
                            Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
                            mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaint2);
                            Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
                            mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint2);
                        } else {
                            if (awardList.get(i).getSubTitle().contains(",")) {
                                String[] singletext = awardList.get(i).getSubTitle().split(",");
                                if (singletext.length > 1) {
                                    Point singTopPoint = calculateSingleTextTopLocation(rectF1, singletext[0]);
                                    mCanvas.drawText(singletext[0], singTopPoint.x, singTopPoint.y, mSingleTextPaint2);
                                    Point singBottomPoint = calculateSingleTextBottomLocation(rectF1, singletext[1]);
                                    mCanvas.drawText(singletext[1], singBottomPoint.x, singBottomPoint.y, mSingleTextPaint2);
                                }
                            } else {
                                Point singPoint = calculateSingleTextLocation(rectF1, awardList.get(i).getSubTitle());
                                mCanvas.drawText(awardList.get(i).getSubTitle(), singPoint.x, singPoint.y, mSingleTextPaint2);
                            }
                        }

                    } else {
                        //未选中的样式
                        if (!TextUtils.isEmpty(awardList.get(i).getTitle())) {
                            Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
                            mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaint);
                            Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
                            mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint);
                        } else {
                            if (awardList.get(i).getSubTitle().contains(",")) {
                                String[] singletext = awardList.get(i).getSubTitle().split(",");
                                if (singletext.length > 1) {
                                    Point singTopPoint = calculateSingleTextTopLocation(rectF1, singletext[0]);
                                    mCanvas.drawText(singletext[0], singTopPoint.x, singTopPoint.y, mSingleTextPaint);
                                    Point singBottomPoint = calculateSingleTextBottomLocation(rectF1, singletext[1]);
                                    mCanvas.drawText(singletext[1], singBottomPoint.x, singBottomPoint.y, mSingleTextPaint);
                                }
                            } else {
                                Point singPoint = calculateSingleTextLocation(rectF1, awardList.get(i).getSubTitle());
                                mCanvas.drawText(awardList.get(i).getSubTitle(), singPoint.x, singPoint.y, mSingleTextPaint);
                            }
                        }

                    }
                } else if (lotteryState == IS_RESULT) {
                    //已经开奖
                    if (mRectList.size() > currentCount && currentCount != -1 && currentCount == i) {
                        //选中的样式
                        if (!TextUtils.isEmpty(awardList.get(i).getTitle())) {
                            Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
                            mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaintLottery);
                            Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
                            mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaintLottery);
                        } else {
                            if (awardList.get(i).getSubTitle().contains(",")) {
                                String[] singletext = awardList.get(i).getSubTitle().split(",");
                                if (singletext.length > 1) {
                                    Point singTopPoint = calculateSingleTextTopLocation(rectF1, singletext[0]);
                                    mCanvas.drawText(singletext[0], singTopPoint.x, singTopPoint.y, mSingleTextPaintLottery);
                                    Point singBottomPoint = calculateSingleTextBottomLocation(rectF1, singletext[1]);
                                    mCanvas.drawText(singletext[1], singBottomPoint.x, singBottomPoint.y, mSingleTextPaintLottery);
                                }
                            } else {
                                Point singPoint = calculateSingleTextLocation(rectF1, awardList.get(i).getSubTitle());
                                mCanvas.drawText(awardList.get(i).getSubTitle(), singPoint.x, singPoint.y, mSingleTextPaintLottery);
                            }
                        }

                    } else if (i % 2 == 0) {
                        //未选中的样式
                        if (!TextUtils.isEmpty(awardList.get(i).getTitle())) {
                            Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
                            mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaint2);
                            Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
                            mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint2);
                        } else {
                            if (awardList.get(i).getSubTitle().contains(",")) {
                                String[] singletext = awardList.get(i).getSubTitle().split(",");
                                if (singletext.length > 1) {
                                    Point singTopPoint = calculateSingleTextTopLocation(rectF1, singletext[0]);
                                    mCanvas.drawText(singletext[0], singTopPoint.x, singTopPoint.y, mSingleTextPaint2);
                                    Point singBottomPoint = calculateSingleTextBottomLocation(rectF1, singletext[1]);
                                    mCanvas.drawText(singletext[1], singBottomPoint.x, singBottomPoint.y, mSingleTextPaint2);
                                }
                            } else {
                                Point singPoint = calculateSingleTextLocation(rectF1, awardList.get(i).getSubTitle());
                                mCanvas.drawText(awardList.get(i).getSubTitle(), singPoint.x, singPoint.y, mSingleTextPaint2);
                            }
                        }

                    } else {
                        //未选中的样式
                        if (!TextUtils.isEmpty(awardList.get(i).getTitle())) {
                            Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
                            mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaint);
                            Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
                            mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint);
                        } else {
                            if (awardList.get(i).getSubTitle().contains(",")) {
                                String[] singletext = awardList.get(i).getSubTitle().split(",");
                                if (singletext.length > 1) {
                                    Point singTopPoint = calculateSingleTextLocation(rectF1, singletext[0]);
                                    mCanvas.drawText(singletext[0], singTopPoint.x, singTopPoint.y, mSingleTextPaint);
                                    Point singBottomPoint = calculateSingleTextLocation(rectF1, singletext[1]);
                                    mCanvas.drawText(singletext[1], singBottomPoint.x, singBottomPoint.y, mSingleTextPaint);
                                }
                            } else {
                                Point singPoint = calculateSingleTextLocation(rectF1, awardList.get(i).getSubTitle());
                                mCanvas.drawText(awardList.get(i).getSubTitle(), singPoint.x, singPoint.y, mSingleTextPaint);
                            }
                        }

                    }
                } else {
                    //默认状态
                    if (i % 2 == 0) {
                        if (!TextUtils.isEmpty(awardList.get(i).getTitle())) {
                            Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
                            mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaint2);
                            Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
                            mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint2);
                        } else {
                            if (awardList.get(i).getSubTitle().contains(",")) {
                                String[] singletext = awardList.get(i).getSubTitle().split(",");
                                if (singletext.length > 1) {
                                    Point singTopPoint = calculateSingleTextTopLocation(rectF1, singletext[0]);
                                    mCanvas.drawText(singletext[0], singTopPoint.x, singTopPoint.y, mSingleTextPaint2);
                                    Point singBottomPoint = calculateSingleTextBottomLocation(rectF1, singletext[1]);
                                    mCanvas.drawText(singletext[1], singBottomPoint.x, singBottomPoint.y, mSingleTextPaint2);
                                }
                            } else {
                                Point singPoint = calculateSingleTextLocation(rectF1, awardList.get(i).getSubTitle());
                                mCanvas.drawText(awardList.get(i).getSubTitle(), singPoint.x, singPoint.y, mSingleTextPaint2);
                            }
                        }

                    } else {
                        if (!TextUtils.isEmpty(awardList.get(i).getTitle())) {
                            Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
                            mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaint);
                            Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
                            mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint);
                        } else {
                            if (awardList.get(i).getSubTitle().contains(",")) {
                                String[] singletext = awardList.get(i).getSubTitle().split(",");
                                if (singletext.length > 1) {
                                    Point singTopPoint = calculateSingleTextLocation(rectF1, singletext[0]);
                                    mCanvas.drawText(singletext[0], singTopPoint.x, singTopPoint.y, mSingleTextPaint);
                                    Point singBottomPoint = calculateSingleTextLocation(rectF1, singletext[1]);
                                    mCanvas.drawText(singletext[1], singBottomPoint.x, singBottomPoint.y, mSingleTextPaint);
                                }
                            } else {
                                Point singPoint = calculateSingleTextLocation(rectF1, awardList.get(i).getSubTitle());
                                mCanvas.drawText(awardList.get(i).getSubTitle(), singPoint.x, singPoint.y, mSingleTextPaint);
                            }
                        }

                    }
                }

            }

//            else {
//
//                if (lotteryState == IS_LOTTERYING) {
//                    //正在开奖
//                    if (mRectList.size() > currentCount && currentCount != -1 && currentCount == i) {
//                        Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
//                        mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaintLottery);
//                        Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
//                        mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaintLottery);
//                    } else if (i % 2 == 0) {
//                        Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
//                        mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaint2);
//                        Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
//                        mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint2);
//                    } else {
//                        Point point = calculateNumTextLocation(rectF1, awardList.get(i).getTitle());
//                        mCanvas.drawText(awardList.get(i).getTitle(), point.x, point.y, mNumTextPaint);
//                        Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
//                        mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint);
//                    }
//                } else if (lotteryState == IS_RESULT) {
//                    //已经开奖
//                    if (mRectList.size() > currentCount && currentCount != -1 && currentCount == i) {
//                        Point point = calculateNumTextLocation(rectF1, awardList.get(i - awardCount).getTitle());
//                        mCanvas.drawText(awardList.get(i - awardCount).getTitle(), point.x, point.y, mNumTextPaintLottery);
//                        Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
//                        mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaintLottery);
//                    } else if (i % 2 == 0) {
//                        Point point = calculateNumTextLocation(rectF1, awardList.get(i - awardCount).getTitle());
//                        mCanvas.drawText(awardList.get(i - awardCount).getTitle(), point.x, point.y, mNumTextPaint2);
//                        Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
//                        mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint2);
//                    } else {
//                        Point point = calculateNumTextLocation(rectF1, awardList.get(i - awardCount).getTitle());
//                        mCanvas.drawText(awardList.get(i - awardCount).getTitle(), point.x, point.y, mNumTextPaint);
//                        Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
//                        mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint);
//                    }
//                } else {
//                    if (i % 2 == 0) {
//                        Point point = calculateNumTextLocation(rectF1, awardList.get(i - awardCount).getTitle());
//                        mCanvas.drawText(awardList.get(i - awardCount).getTitle(), point.x, point.y, mNumTextPaint2);
//                        Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
//                        mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint2);
//                    } else {
//                        Point point = calculateNumTextLocation(rectF1, awardList.get(i - awardCount).getTitle());
//                        mCanvas.drawText(awardList.get(i - awardCount).getTitle(), point.x, point.y, mNumTextPaint);
//                        Point subPoint = calculateTextLocation(rectF1, awardList.get(i).getSubTitle());
//                        mCanvas.drawText(awardList.get(i).getSubTitle(), subPoint.x, subPoint.y, mTextPaint);
//                    }
//                }
//
////                if (i % 2 == 0) {
////                    Point point = calculateNumTextLocation(rectF1, awardList.get(i - awardCount));
////                    mCanvas.drawText(awardList.get(i - awardCount), point.x, point.y, mNumTextPaint2);
////                    Point subPoint = calculateTextLocation(rectF1, "法术碎片");
////                    mCanvas.drawText("法术碎片", subPoint.x, subPoint.y, mTextPaint2);
////                } else {
////                    Point point = calculateNumTextLocation(rectF1, awardList.get(i - awardCount));
////                    mCanvas.drawText(awardList.get(i - awardCount), point.x, point.y, mNumTextPaint);
////                    Point subPoint = calculateTextLocation(rectF1, "法术碎片");
////                    mCanvas.drawText("法术碎片", subPoint.x, subPoint.y, mTextPaint);
////                }
//            }
        }

    }

    private LotteryClickListener lotteryClickListener;

    public void setLotteryClickListener(LotteryClickListener lotteryClickListener) {
        this.lotteryClickListener = lotteryClickListener;
    }

    public interface LotteryClickListener {
        void startLottery();

        void startResultAnim();

        void stopLottery(LotteryData lotteryData);
    }


    public void setResult(int result) {
        this.result = result;
    }

    private int getResult() {
        //产生抽奖结果
        return this.result;
    }


    public int getLotteryState() {
        return lotteryState;
    }
}
