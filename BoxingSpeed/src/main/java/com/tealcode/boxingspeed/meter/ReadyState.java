package com.tealcode.boxingspeed.meter;

/**
 * Created by Boswell Yu on 2017/3/19.
 */

public class ReadyState extends StateBase {
    private static ReadyState _instance = null;

    public static ReadyState Instance()
    {
        if (_instance == null)
        {
            _instance = new ReadyState();
        }
        return _instance;
    }

    private ReadyState()
    {

    }

    @Override
    public void OnStart()
    {
    }

    @Override
    public void OnUpdate(long ts, float [] values)
    {
        // 如果加速度超过6m/s^2，触发进入Counting State
        if((values[0] * values[0] + values[1] * values[1] + values[2] * values[2]) >= 25f)
        {
            // Check Major Direction
            MovementMeter.Instance().StartCounting(ts, values);

            MovementMeter.Instance().ChangeState(CountingState.Instance());

        }
    }

    @Override
    public void OnEnd()
    {
    }
}
