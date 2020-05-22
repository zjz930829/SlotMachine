package com.zjz.slotmachine.lotteryview;

/**
 * Created by ZJZ on 2019/4/19.
 */

public class LotteryData {

    private String title;
    private String subTitle;

    public LotteryData() {
    }

    public LotteryData(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

}