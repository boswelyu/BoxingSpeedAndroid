package com.tealcode.boxingspeed.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.meter.IMeterReportListener;
import com.tealcode.boxingspeed.meter.MovementMeter;

/**
 * Created by Boswell Yu on 2017/9/24.
 */

public class BoxingFragment extends BaseFragment implements IMeterReportListener{

    private static final String TAG = "BoxingFragment";

    private View mCurrView = null;

    private Button mStartBoxingButton = null;

    private TextView mBoxingCountText;
    private TextView mBoxingTimeText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MovementMeter.CreateInstance(getActivity());
        MovementMeter.Instance().RegisterReportListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mCurrView != null) {
            ViewGroup parent = (ViewGroup) mCurrView.getParent();
            if(parent != null) {
                parent.removeView(mCurrView);
            }
            Log.d(TAG, "mCurrView Already Created Before");
            return mCurrView;
        }

        mCurrView = inflater.inflate(R.layout.fragment_boxing, topContentView);
        setTopTitleBold(getString(R.string.main_boxing));

        mStartBoxingButton = (Button) mCurrView.findViewById(R.id.start_boxing);
        mStartBoxingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecordBoxingSpeed();
            }
        });

        mBoxingCountText = (TextView) mCurrView.findViewById(R.id.boxing_count_text);
        mBoxingTimeText = (TextView) mCurrView.findViewById(R.id.boxing_time_text);

        return mCurrView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MovementMeter meter = MovementMeter.Instance();
        if(meter != null) {
            meter.CleanUp();
        }
    }

    private void startRecordBoxingSpeed()
    {
        MovementMeter meter = MovementMeter.Instance();
        if(meter != null) {
            meter.Start();
        }
        else {
            Log.e(TAG, "MovementMeter is NULL");
        }
    }

    @Override
    public void ReportMeasureResult(long totalTime, int boxingCount) {
        mBoxingTimeText.setText(String.format("%s: %d", getString(R.string.boxing_time), totalTime));
        mBoxingCountText.setText(String.format("%s : %d", getString(R.string.boxing_count), boxingCount));
    }
}
