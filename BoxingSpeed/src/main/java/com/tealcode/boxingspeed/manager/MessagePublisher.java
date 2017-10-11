package com.tealcode.boxingspeed.manager;

import android.os.Handler;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Created by YuBo on 2017/10/11.
 */

public class MessagePublisher {
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

    public void subscribe(Class cls, Handler handler) {
        String clsname = cls.getName();
        if(eventTable.containsKey(clsname)) {
            eventTable.get(clsname).add(handler);
        }
        else {
            LinkedList<Handler> handlers = new LinkedList<Handler>();
            handlers.add(handler);
            eventTable.put(clsname, handlers);
        }
    }

    public void unsubscribe(Class cls, Handler handler)
    {
        String clsname = cls.getName();
        if(eventTable.containsKey(clsname)) {
            eventTable.get(clsname).remove(handler);
        }
    }
}
