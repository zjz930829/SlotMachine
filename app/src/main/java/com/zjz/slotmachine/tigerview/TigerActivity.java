package com.zjz.slotmachine.tigerview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.zjz.slotmachine.R;

import java.util.ArrayList;
import java.util.Random;

public class TigerActivity extends AppCompatActivity {
    private SlotWheelView slot1;
    private SlotWheelView slot2;
    private SlotWheelView slot3;
    private final int SPIN_TIME = 3000;//抽奖时间
    private final int MESSAGE_CHECK_MATCH = 0x111;
    //    private final int MESSAGE_PLAY_MUSIC = 0x123;
    private int[] results = {SlotItemsImpl.SLOT_ITEM1, SlotItemsImpl.SLOT_ITEM2, SlotItemsImpl.SLOT_ITEM3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiger);
        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);
        initSlotData();
    }

    private void initSlotData() {
        ArrayList<ISlotMachineItem> slotItems1 = new ArrayList<ISlotMachineItem>();
        ArrayList<ISlotMachineItem> slotItems2 = new ArrayList<ISlotMachineItem>();
        ArrayList<ISlotMachineItem> slotItems3 = new ArrayList<ISlotMachineItem>();

        slotItems1.add(new SlotItemsImpl(this, SlotItemsImpl.SLOT_ITEM1, R.mipmap.icon_jiemaogao));
        slotItems1.add(new SlotItemsImpl(this, SlotItemsImpl.SLOT_ITEM2, R.mipmap.icon_bushuiyi));
        slotItems1.add(new SlotItemsImpl(this, SlotItemsImpl.SLOT_ITEM3, R.mipmap.icon_kouhong));
        slot1.setSlotItems(slotItems1);

        slotItems2.add(new SlotItemsImpl(this, SlotItemsImpl.SLOT_ITEM3, R.mipmap.icon_kouhong));
        slotItems2.add(new SlotItemsImpl(this, SlotItemsImpl.SLOT_ITEM1, R.mipmap.icon_jiemaogao));
        slotItems2.add(new SlotItemsImpl(this, SlotItemsImpl.SLOT_ITEM2, R.mipmap.icon_bushuiyi));
        slot2.setSlotItems(slotItems2);

        slotItems3.add(new SlotItemsImpl(this, SlotItemsImpl.SLOT_ITEM2, R.mipmap.icon_bushuiyi));
        slotItems3.add(new SlotItemsImpl(this, SlotItemsImpl.SLOT_ITEM3, R.mipmap.icon_kouhong));
        slotItems3.add(new SlotItemsImpl(this, SlotItemsImpl.SLOT_ITEM1, R.mipmap.icon_jiemaogao));
        slot3.setSlotItems(slotItems3);
    }

    public void btnStart(View view) {
        int result = new Random().nextInt(3);
        slot1.startScroll(results[result], SPIN_TIME);
        slot2.startScroll(results[result], SPIN_TIME);
        slot3.startScroll(results[result], SPIN_TIME);
        Message msg = Message.obtain();
        msg.what = MESSAGE_CHECK_MATCH;
        msg.obj = result;
        detectAnyMatchHandler.sendMessageDelayed(msg, SPIN_TIME + 1000);

    }


    private Handler detectAnyMatchHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_CHECK_MATCH) {
                int result = (int) msg.obj;
                String toast = "";
                switch (result) {
                    case SlotItemsImpl.SLOT_ITEM1:
                        toast = "恭喜抽中睫毛膏";
                        break;
                    case SlotItemsImpl.SLOT_ITEM2:
                        toast = "恭喜抽中补水仪";
                        break;
                    case SlotItemsImpl.SLOT_ITEM3:
                        toast = "恭喜抽中口红";
                        break;
                }
                Toast.makeText(TigerActivity.this,toast,Toast.LENGTH_SHORT).show();
//                LotteryData data = (LotteryData) msg.obj;
//                stopLottery(data);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (detectAnyMatchHandler != null) {
            detectAnyMatchHandler.removeCallbacksAndMessages(null);
            detectAnyMatchHandler = null;
        }
    }
}
