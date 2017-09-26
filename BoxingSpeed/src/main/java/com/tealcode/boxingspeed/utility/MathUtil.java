/**
 * Created by Boswell Yu on 2017/9/26.
 */

package com.tealcode.boxingspeed.utility;

public final class MathUtil {
    public static float DotProduct(float[] left, float[] right) {
        return left[0] * right[0] + left[1] * right[1] + left[2] * right[2];
    }

    public static float SqrtMagnitude(float[] values, int len)
    {
        float result = 0;
        for (int i = 0; i < len; i++)
        {
            result += values[i]*values[i];
        }
        return result;
    }
}
