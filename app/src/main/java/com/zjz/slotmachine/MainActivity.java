package com.zjz.slotmachine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zjz.slotmachine.lotteryview.LotteryActivity;
import com.zjz.slotmachine.tigerview.TigerActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnTiger(View view) {
        Intent intent = new Intent(this, TigerActivity.class);
        startActivity(intent);
    }

    public void btnLottery(View view) {
        Intent intent = new Intent(this, LotteryActivity.class);
        startActivity(intent);
    }
}
