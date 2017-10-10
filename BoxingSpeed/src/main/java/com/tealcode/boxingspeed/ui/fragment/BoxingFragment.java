package com.tealcode.boxingspeed.ui.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.config.AppConfig;
import com.tealcode.boxingspeed.helper.AppConstant;
import com.tealcode.boxingspeed.helper.packer.ProtobufPacker;
import com.tealcode.boxingspeed.manager.NetworkManager;
import com.tealcode.boxingspeed.meter.IMeterReportListener;
import com.tealcode.boxingspeed.meter.MovementMeter;
import com.tealcode.boxingspeed.protobuf.Client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

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
        // TODO: Start Recording
//        MovementMeter meter = MovementMeter.Instance();
//        if(meter != null) {
//            meter.Start();
//        }
//        else {
//            Log.e(TAG, "MovementMeter is NULL");
//        }
        // Test protobuf message functions
        Client.ClientMsg.Builder clientBuilder = Client.ClientMsg.newBuilder();
        Client.Login.Builder loginBuilder = Client.Login.newBuilder();
        loginBuilder.setDeviceId("abcdefgh");
        clientBuilder.setLogin(loginBuilder.build());

        NetworkManager.getInstance().sendProtobufMessage(clientBuilder);

//        person.writeTo(outputStream);
    }

    @Override
    public void ReportMeasureResult(long totalTime, int boxingCount) {
        mBoxingTimeText.setText(String.format("%s: %d", getString(R.string.boxing_time), totalTime));
        mBoxingCountText.setText(String.format("%s : %d", getString(R.string.boxing_count), boxingCount));
    }
}
