package com.tealcode.boxingspeed.meter;

/**
 * Created by Boswell Yu on 2017/3/19.
 */

public enum MOVE_DIRECTION {
    MOVE_NONE(0),
    MOVE_UP_RIGHT(1),
    MOVE_DOWN_RIGHT(2),
    MOVE_DOWN_LEFT(3),
    MOVE_UP_LEFT(4);

    private int direction;
    MOVE_DIRECTION(int value) {
        direction = value;
    }
}
