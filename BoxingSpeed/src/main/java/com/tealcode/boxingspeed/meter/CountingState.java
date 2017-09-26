package com.tealcode.boxingspeed.meter;

import android.util.Log;

import com.tealcode.boxingspeed.utility.MathUtil;

/**
 * Created by Boswell Yu on 2017/3/19.
 */

public class CountingState extends StateBase {
    private static CountingState _instance = null;
    public static CountingState Instance()
    {
        if (_instance == null)
        {
            _instance = new CountingState();
        }
        return _instance;
    }

    private static final String CountingStateTag = "CountingState";

    private long startCountingTime = 0;
    private long lastCountingTime = 0;
    private int boxingCounter = 0;
    private boolean isStableState = false;
    private long stableStartTime = 0;

    private CountingState()
    {

    }

    @Override
    public void OnStart()
    {
        startCountingTime = 0;
        boxingCounter = 0;

        isStableState = true;
        stableStartTime = 0;
    }

    @Override
    public void OnUpdate(long ts, float [] values)
    {
        // 计算振幅，幅度太小并且持续超过0.5秒，则切换到就绪状态，重新开始计数
        if (MathUtil.SqrtMagnitude(values, 3) < 36.0f)
        {
            if (StableStateTooLong(ts))
            {
                MovementMeter.Instance().ChangeState(ReadyState.Instance());
                return;
            }
        }
        else
        {
            ClearStableFlag(ts);
            lastCountingTime = ts;
        }

        if(startCountingTime == 0) {
            startCountingTime = ts;
        }

        MOVE_DIRECTION currDirection = MovementMeter.Instance().CheckMovementDirection(values);

        if (MovementMeter.Instance().IsOppositeDirection(currDirection))
        {
            // Movement Direction changed, record one boxing
            boxingCounter++;
            Log.d(CountingStateTag, "Counter: " + boxingCounter);
            MovementMeter.Instance().SetMovementDirection(currDirection);
        }
    }

    private boolean StableStateTooLong(long ts)
    {
        if (isStableState)
        {
            if (ts - stableStartTime >= 500)
            {
                return true;
            }
        }
        else
        {
            isStableState = true;
            stableStartTime = ts;
        }
        return false;
    }

    public void ClearStableFlag(long ts)
    {
        isStableState = false;
        stableStartTime = 0;
    }

    @Override
    public void OnEnd()
    {
        // 为了减小误差，检测到的连续出拳次数少于三次的，不统计；频率太快的忽略掉，没人能在0.1秒内打出3拳
        if(startCountingTime > 0 && lastCountingTime - startCountingTime > 100 && boxingCounter >= 6) {
            MovementMeter.Instance().GenerateResultReport(startCountingTime, lastCountingTime, boxingCounter / 2);
        }
    }
}
