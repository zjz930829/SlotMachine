package com.zjz.slotmachine.tigerview;

import android.view.View;

/**
 * @DESC 老虎机每个item 暴露信息的接口.
 * Created by ZJZ on 2020/02/20.
 */
public interface ISlotMachineItem {
	/**
	 * @return 老虎机每个item 的 view
	 */
	View getView();
	int getResult();
}
