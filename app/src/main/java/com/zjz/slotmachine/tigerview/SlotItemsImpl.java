package com.zjz.slotmachine.tigerview;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjz.slotmachine.R;
import com.zjz.slotmachine.ScreenUtils;
import com.zjz.slotmachine.SizeUtils;


/**
 * Description: 老虎机item实例.
 * Created by ZJZ on 2020/2/21.
 */
public class SlotItemsImpl implements ISlotMachineItem {

    public static final int SLOT_ITEM1 = 0;//类型1
    public static final int SLOT_ITEM2= 1;//类型2
    public static final int SLOT_ITEM3 = 2;//类型3
    private int slotResult;
    private int slotDrawable;
    private String slotText;
    private Activity activity;

    public SlotItemsImpl(Activity activity, int slotResult, int drawable) {
        this.activity = activity;
        this.slotResult = slotResult;
        this.slotDrawable = drawable;
    }

    public SlotItemsImpl(Activity activity, int slotResult, int drawable, String slotText) {
        this.activity = activity;
        this.slotResult = slotResult;
        this.slotDrawable = drawable;
        this.slotText = slotText;
    }

    @Override
    public View getView() {
        View view = activity.getLayoutInflater().inflate(R.layout.slot_item_layout, null, false);
        ImageView img_slot = view.findViewById(R.id.img_slot);
        LinearLayout ll_slot_root = view.findViewById(R.id.ll_slot_root);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int wheelWidth = (ScreenUtils.getScreenWidth() * 144 / 375) / 3 - SizeUtils.dp2px(4);
        int wheelHeight = wheelWidth * 56 / 44;
        params.width = wheelWidth;
        params.height = wheelHeight;
        int imageSize = 28 * wheelWidth / 44;
        LinearLayout.LayoutParams imageParam = (LinearLayout.LayoutParams) img_slot.getLayoutParams();
        imageParam.width = imageSize;
        imageParam.height = imageSize;
        ll_slot_root.setLayoutParams(params);
        TextView t_slot = view.findViewById(R.id.t_slot);
        img_slot.setImageResource(slotDrawable);
//        Resources resources = getResources();
//        itemImageView.setImageResource(slotItem1Images[slotItemPos]);
//        itemTextView.setText(resources.getString(slotItem1Texts[slotItemPos]));
//			if (wheelPos == 1) {
//				itemImageView.setImageResource(slotItem1Images[slotItemPos]);
//				itemTextView.setText(resources.getString(slotItem1Texts[slotItemPos]));
//			} else if (wheelPos == 2) {
//				itemImageView.setImageResource(slotItem2Images[slotItemPos]);
//				itemTextView.setText(resources.getString(slotItem2Texts[slotItemPos]));
//			} else if (wheelPos == 3) {
//				itemImageView.setImageResource(slotItem3Images[slotItemPos]);
//				itemTextView.setText(resources.getString(slotItem3Texts[slotItemPos]));
//			}
        return view;
    }

    @Override
    public int getResult() {
        return slotResult;
    }
}
