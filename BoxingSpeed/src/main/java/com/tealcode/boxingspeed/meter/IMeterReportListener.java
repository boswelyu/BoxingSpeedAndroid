package com.tealcode.boxingspeed.meter;

/**
 * Created by Boswell Yu on 2017/3/20.
 */

public interface IMeterReportListener {

    public void ReportMeasureResult(long totalTime, int boxingCount);
}
