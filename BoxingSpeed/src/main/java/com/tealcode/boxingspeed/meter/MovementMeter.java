package com.tealcode.boxingspeed.meter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.FileOutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Boswell Yu on 2017/3/19.
 */

public class MovementMeter implements SensorEventListener{
    private static MovementMeter _instance = null;

    public static void CreateInstance(Context context)
    {
        if(_instance == null) {
            _instance = new MovementMeter(context);
        } else if( _instance.mContext != context) {
            _instance.CleanUp();
            _instance = new MovementMeter(context);
        }
    }

    public static MovementMeter Instance() {
        return _instance;
    }

    private static final String TAG = "MovementMeter";

    private Context mContext;
    private boolean started = false;

    private MOVE_DIRECTION moveDirection;
    private SensorManager mSensorManager = null;
    private long BoxingStartTime = 0;
    private int xIndex, yIndex;
    public int Counter = 0;
    private IMeterReportListener reportListener = null;

    private final float ALPHA = 0.8f;
    private float[] gravity = new float[3];
    private float[] currGravity = new float[3];
    private float[] lastAccelerate = new float[3];
    private float[] currAccelerate = new float[3];
    private final int MAX_RECORD_INDEX = 1800;
    private float[] recordList = new float[3 * MAX_RECORD_INDEX];
    private long [] tsRecordList = new long[MAX_RECORD_INDEX];
    private int recordIndex = 0;
    private boolean recordDone = false;
    private boolean recordDumped = false;

    private MovementMeter(Context context)
    {
        this.mContext = context;
        meterState = ReadyState.Instance();
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void RegisterReportListener(IMeterReportListener listener) {
        reportListener = listener;
    }

    public void Start()
    {
        this.started = true;
    }

    public void Finish()
    {
        this.started = false;
    }

    public void CleanUp()
    {
        mSensorManager.unregisterListener(this);
        this.mContext = null;
        this.started = false;
        reportListener = null;
    }

    private StateBase meterState;
    public void HandleData(long timestamp, float[] values)
    {
        if(!started) {
            return;
        }
        meterState.OnUpdate(timestamp, values);
    }

    public void ChangeState(StateBase st)
    {
        if (meterState == st)
        {
            return;
        }

        meterState.OnEnd();
        meterState = st;
        meterState.OnStart();
    }

    public void StartCounting(long timestamp, float[] values)
    {
        // 记录出拳的开始记录时间
        BoxingStartTime = timestamp;

        // 检查主方向
        CheckMajorDirection(values);

        // 确定当前运动方向
        moveDirection = CheckMovementDirection(values);
    }

    // 找出两个值比较大的方向作为主方向
    private void CheckMajorDirection(float[] values)
    {
        for (int i = 0; i < 3; i++)
        {
            if (values[0] > values[1])
            {
                xIndex = 0;
            }
            else
            {
                xIndex = 1;
            }

            if (values[1 - xIndex] < values[2])
            {
                yIndex = 2;
            }
            else
            {
                yIndex = 1 - xIndex;
            }
        }
    }

    // 检查当前的加速度方向
    public MOVE_DIRECTION CheckMovementDirection(float[] values)
    {
        MOVE_DIRECTION direction = MOVE_DIRECTION.MOVE_NONE;
        if (values[xIndex] == 0 && values[yIndex] == 0)
        {
            direction = MOVE_DIRECTION.MOVE_NONE;
        }

        if (values[xIndex] > 0 && values[yIndex] > 0)
        {
            direction = MOVE_DIRECTION.MOVE_UP_RIGHT;
        }

        if (values[xIndex] > 0 && values[yIndex] < 0)
        {
            direction = MOVE_DIRECTION.MOVE_DOWN_RIGHT;
        }

        if (values[xIndex] < 0 && values[yIndex] < 0)
        {
            direction = MOVE_DIRECTION.MOVE_DOWN_LEFT;
        }

        if (values[xIndex] < 0 && values[yIndex] > 0)
        {
            direction = MOVE_DIRECTION.MOVE_UP_LEFT;
        }
        return direction;
    }

    public void SetMovementDirection(MOVE_DIRECTION direction)
    {
        moveDirection = direction;
    }

    public Boolean IsOppositeDirection(MOVE_DIRECTION direction)
    {
        if (direction == MOVE_DIRECTION.MOVE_NONE)
        {
            return false;
        }
        if ((direction.ordinal() + 2)%4 == moveDirection.ordinal())
        {
            return true;
        }
        return false;
    }

    public void GenerateResultReport(long startTime, long endTime, int counter) {
        Log.d(TAG, "Generate Report, Start Time:" + startTime + ", End Time:" + endTime
                + "Counter: " + counter + "\n");

        if(reportListener != null) {
            reportListener.ReportMeasureResult(endTime - startTime, counter);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Boolean hasValueChange = false;
        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                currGravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * event.values[0];
                currGravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * event.values[1];
                currGravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * event.values[2];

                currAccelerate[0] = event.values[0] - currGravity[0];
                currAccelerate[1] = event.values[1] - currGravity[1];
                currAccelerate[2] = event.values[2] - currGravity[2];

                // 将传感器数据发送给运动统计表
                long currTime = event.timestamp /1000000l;
                HandleData(currTime, currAccelerate);

                // 记录数据
                RecordData(event.timestamp);

                if(recordDone && !recordDumped) {
                    DumpRecordToFile();
                    recordDumped = true;
                }

                break;
            case Sensor.TYPE_GRAVITY:
                gravity[0] = event.values[0];
                gravity[1] = event.values[1];
                gravity[2] = event.values[2];
                break;
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void RecordData(long timestamp) {

        if(recordIndex >= MAX_RECORD_INDEX) {
            recordDone = true;
            return;
        }

        int index = recordIndex * 3;
        recordList[index] = currAccelerate[0];
        recordList[index + 1] = currAccelerate[1];
        recordList[index + 2] = currAccelerate[2];

        tsRecordList[recordIndex] = timestamp;

        recordIndex++;
    }

    private void DumpRecordToFile() {

        if(mContext == null) {
            Log.e(TAG, "Context not initialized, could not create output file");
            return;
        }

        String filename = "record.txt";
        try{
            FileOutputStream fout = mContext.openFileOutput(filename, MODE_PRIVATE);
            String content;
            for(int i = 0; i < MAX_RECORD_INDEX; i++) {
                int index = i * 3;
                content = String.format("%d,%f,%f,%f\n", tsRecordList[i],
                        recordList[index], recordList[index + 1], recordList[index + 2]);
                fout.write(content.getBytes());
            }

            fout.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
