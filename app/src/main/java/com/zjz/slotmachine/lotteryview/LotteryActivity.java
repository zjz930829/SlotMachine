package com.zjz.slotmachine.lotteryview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zjz.slotmachine.R;
import com.zjz.slotmachine.ScreenUtils;
import com.zjz.slotmachine.SizeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LotteryActivity extends AppCompatActivity implements LotteryView.LotteryClickListener {
    private LotteryView lotteryView;
    private RelativeLayout rl_lottery;
    private int width;//抽奖视图的宽度
    private int height;//抽奖视图的高度
    private List<LotteryData> lotteryData = new ArrayList<>();
    private boolean isLotterying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery);
        rl_lottery = findViewById(R.id.rl_lottery);
        lotteryView = findViewById(R.id.lottery_view);
        lotteryView.setLotteryClickListener(this);
        initLotteryView();
        setLotteryData();
    }

    private void initLotteryView() {
        //抽奖view 布局的宽
        width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(60);
        //抽奖view 布局的高
        height = 70 * (width) / 5 / 63 * 3;
        //整体抽奖布局的高度 230=70*3+20  335=63*5+20
        int heightRoot = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(20)) * 230 / 335 + SizeUtils.dp2px(13);

        LinearLayout.LayoutParams lotteryParam = (LinearLayout.LayoutParams) rl_lottery.getLayoutParams();
        lotteryParam.height = heightRoot;
        lotteryParam.setMargins(SizeUtils.dp2px(10), 0, SizeUtils.dp2px(10), 0);


        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lotteryView.getLayoutParams();
        params.width = width;
        params.height = height;


    }


    private void setLotteryData() {
        lotteryData.add(new LotteryData("1", "奖品1"));
        lotteryData.add(new LotteryData("2", "奖品2"));
        lotteryData.add(new LotteryData("3", "奖品3"));
        lotteryData.add(new LotteryData("4", "奖品4"));
        lotteryData.add(new LotteryData("5", "奖品5"));
        lotteryData.add(new LotteryData("6", "奖品6"));
        lotteryData.add(new LotteryData("7", "奖品7"));
        lotteryData.add(new LotteryData("8", "奖品8"));
        lotteryData.add(new LotteryData("9", "奖品9"));
        lotteryData.add(new LotteryData("10", "奖品10"));
        lotteryData.add(new LotteryData("11", "奖品11"));
        lotteryData.add(new LotteryData("12", "奖品112"));
        lotteryView.setAwardList(lotteryData);
        if (lotteryView.getLotteryState() != LotteryView.IS_DEFAULT) {
            lotteryView.resetLottery();
        }
        isLotterying = false;
    }

    public void btnStart(View view) {
        if (isLotterying) return;
        isLotterying = true;

        lotteryView.startLottery();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int result = new Random().nextInt(12);
                lotteryView.setResult(result);
                lotteryView.stopLottery();
            }
        }, 1000);
    }


    @Override
    public void startLottery() {

    }

    @Override
    public void startResultAnim() {

    }

    @Override
    public void stopLottery(LotteryData lotteryData) {
        isLotterying = false;
    }
}
