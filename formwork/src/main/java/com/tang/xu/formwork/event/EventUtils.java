package com.tang.xu.formwork.event;

import com.tang.xu.formwork.utils.DilogMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * EventMessage 调度类
 */
public class EventUtils {
    public static volatile EventUtils eventUtils = null;

    private EventUtils() {
    }

    public static EventUtils getInstance(){
        if (eventUtils==null){
            synchronized (EventUtils.class){
                if (eventUtils==null){
                    eventUtils=new EventUtils();
                }
            }
        }
        return eventUtils;
    }

    /**
     * Evemnt注册
     * @param subscriber
     */
    public void register(Object subscriber){
        EventBus.getDefault().register(subscriber);
    }
    /**
     * Evemnt注销
     * @param subscriber
     */
    public void unregister(Object subscriber){
        EventBus.getDefault().unregister(subscriber);
    }

    /**
     * Evemnt发送广播
     */
    public static void post(int type){
        EventBus.getDefault().post(new EventMessage(type));
    }

    /**
     * Evemnt发送广播
     */
    public static void post(EventMessage eventMessage){
        EventBus.getDefault().post(eventMessage);
    }
}
