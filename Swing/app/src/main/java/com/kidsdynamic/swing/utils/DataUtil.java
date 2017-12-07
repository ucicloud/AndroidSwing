package com.kidsdynamic.swing.utils;

import com.kidsdynamic.swing.model.WatchActivity;

import java.util.List;

/**
 * DataUtil
 * <p>
 * Created by Stefan on 2017/12/7.
 */

public class DataUtil {

    /*********** Data in DashboardEmotionFragment *******/
    private WatchActivity watchActivityInEmotionFragment;

    /***********Data in DashboardChartSingleFragment*******/
    private Integer emotionInSingleChart;
    private Integer doorTypeInSingleChart;
    private Integer chartTypeInSingleChart;
    private List<WatchActivity> watchActivitiesInSingleChart;

    public Integer getEmotionInSingleChart() {
        return emotionInSingleChart;
    }

    public void setEmotionInSingleChart(Integer emotionInSingleChart) {
        this.emotionInSingleChart = emotionInSingleChart;
    }

    private static class SingletonHolder {
        private static final DataUtil INSTANCE = new DataUtil();
    }

    public static DataUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public WatchActivity getWatchActivityInEmotionFragment() {
        return watchActivityInEmotionFragment;
    }

    public void setWatchActivityInEmotionFragment(WatchActivity watchActivityInEmotionFragment) {
        this.watchActivityInEmotionFragment = watchActivityInEmotionFragment;
    }

    public Integer getDoorTypeInSingleChart() {
        return doorTypeInSingleChart;
    }

    public void setDoorTypeInSingleChart(Integer doorTypeInSingleChart) {
        this.doorTypeInSingleChart = doorTypeInSingleChart;
    }

    public Integer getChartTypeInSingleChart() {
        return chartTypeInSingleChart;
    }

    public void setChartTypeInSingleChart(Integer chartTypeInSingleChart) {
        this.chartTypeInSingleChart = chartTypeInSingleChart;
    }

    public List<WatchActivity> getWatchActivitiesInSingleChart() {
        return watchActivitiesInSingleChart;
    }

    public void setWatchActivitiesInSingleChart(List<WatchActivity> watchActivitiesInSingleChart) {
        this.watchActivitiesInSingleChart = watchActivitiesInSingleChart;
    }

}
