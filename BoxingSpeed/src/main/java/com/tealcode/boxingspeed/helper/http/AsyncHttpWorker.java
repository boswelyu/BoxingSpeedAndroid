package com.tealcode.boxingspeed.helper.http;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tealcode.boxingspeed.R;
import com.tealcode.boxingspeed.config.AppConfig;
import com.tealcode.boxingspeed.message.BasicHttpReply;
import com.tealcode.boxingspeed.message.UploadReply;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by YuBo on 2017/10/24.
 * Current only support one task at a time, the pending task before last one will be cancelled
 */

public class AsyncHttpWorker {

    private static final String TAG = "AsyncHttpWorker";

    private AsyncTask<Void, Void, String> workingTask = null;

    public void post(final String urlStr, final String postStr, final AsyncHttpReplyHandler handler)
    {
        if(workingTask != null) {
            workingTask.cancel(true);
        }

        workingTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String reply = null;
                try {
                    URL url;
                    url = new URL(urlStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(2000);
                    urlConnection.setReadTimeout(2000);
                    urlConnection.setRequestMethod("POST");

                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    PrintWriter printWriter = new PrintWriter(urlConnection.getOutputStream());
                    printWriter.write(postStr);
                    printWriter.flush();

                    // Read login response
                    BufferedInputStream bis = new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int len;
                    byte[] buffer = new byte[1024];
                    while((len = bis.read(buffer)) != -1)
                    {
                        bos.write(buffer, 0, len);
                        bos.flush();
                    }
                    bos.close();
                    reply = bos.toString("utf-8");

                }catch(MalformedURLException urle) {
                    return "AsyncHttpWorker Failed With HTTP URL Malformed: " + urle.toString();
                }catch(IOException ioe) {
                    return "AsyncHttpWorker Failed With IOException: " + ioe.toString();
                }
                return reply;
            }

            @Override
            protected void onPostExecute(String retStr) {
                BasicHttpReply reply = JSON.parseObject(retStr, BasicHttpReply.class);
                if(reply != null && reply.getStatus() == 0) {
                    handler.onSuccess(retStr);
                }else {
                    if(reply != null) {
                        handler.onFailure(reply.getStatus(), reply.getErrorInfo());
                    }else {
                        handler.onFailure(-1, "no reply");
                    }
                }

            }

        }.execute();
    }

}
