package com.tealcode.boxingspeed.meter;

import android.util.Log;

/**
 * Created by Boswell Yu on 2017/3/19.
 */

public class StateBase {

    private static final String StateBaseTag = "StateBase";

    public void OnStart() {

    }

    public void OnUpdate(long ts, float[] values) {

    }

    public void OnEnd()
    {
        Log.d(StateBase.StateBaseTag, "OnEnd of BaseState");
    }
}
