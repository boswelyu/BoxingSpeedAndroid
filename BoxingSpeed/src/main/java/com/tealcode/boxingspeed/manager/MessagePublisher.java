package com.tealcode.boxingspeed.manager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.google.protobuf.AbstractMessage;
import com.tealcode.boxingspeed.protobuf.Server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * Created by YuBo on 2017/10/11.
 */

public class MessagePublisher {

    private static final String TAG = "MessagePublisher";

    private static MessagePublisher instance = null;
    public static MessagePublisher getInstance() {
        if(instance == null) {
            instance = new MessagePublisher();
        }
        return instance;
    }

    private HashMap<String, LinkedList<Handler>> eventTable;

    private MessagePublisher()
    {
        eventTable = new HashMap<>();
    }

    public void publish(Server.ServerMsg msg)
    {
        if(msg.hasLoginReply()) { raiseEvent(msg.getLoginReply()); }
        if(msg.hasNotification()) { raiseEvent(msg.getNotification());}
    }

    public void register(Class cls, Handler handler) {
        String clsname = cls.getName();
        if(eventTable.containsKey(clsname)) {
            eventTable.get(clsname).add(handler);
        }
        else {
            LinkedList<Handler> handlers = new LinkedList<>();
            handlers.add(handler);
            eventTable.put(clsname, handlers);
        }
    }

    public void unregister(Class cls, Handler handler)
    {
        String clsname = cls.getName();
        if(eventTable.containsKey(clsname)) {
            eventTable.get(clsname).remove(handler);
        }
    }

    public void raiseEvent(AbstractMessage event)
    {
        if(event == null) {
            Log.e(TAG, "Received NULL message");
            return;
        }
        String clsname = event.getClass().getName();

        if(eventTable.containsKey(clsname)){
            LinkedList<Handler> handlers = eventTable.get(clsname);
            for(Iterator iter = handlers.iterator(); iter.hasNext(); )
            {
                Handler handler = (Handler)iter.next();
                if(handler != null) {
                    Message msg = new Message();
                    msg.obj = event;
                    handler.sendMessage(msg);
                }
            }
        }
    }
}
