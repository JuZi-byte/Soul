package com.tang.xu.formwork.cloud;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.utils.LoginUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.calllib.IRongCallListener;
import io.rong.calllib.IRongReceivedCallListener;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.OnReceiveMessageListener;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;

/**
 * 融云管理
 */
public class CloudMessage {

    //来电铃声
    public static final String callAudioPath = "http://downsc.chinaz.net/Files/DownLoad/sound1/201501/5363.wav";
    //挂断铃声
    public static final String callAudioHangup = "http://downsc.chinaz.net/Files/DownLoad/sound1/201501/5351.wav";


    public static final String TokenUrl="https://api-cn.ronghub.com/user/getToken.json";
    public static final String App_Key="0vnjpoad0i0gz";
    public static final String Secret="rWbrW3hF39";

    //ObjectName
    public static final String MSG_TEXT_NAME  ="RC:TxtMsg";
    public static final String MSG_IMAGE_NAME  ="RC:ImgMsg";
    public static final String MSG_LOCATION_NAME  ="RC:LBSMsg";

    //MsgType
    //普通消息
    public static final String TYPE_TEXT = "TYPE_TEXT";
    //添加好友消息
    public static final String TYPE_ADD_FRIEND  ="TYPE_ADD_FRIEND";
    //同意添加好友的消息
    public static final String TYPE_ADD_ARGEED  ="TYPE_ADD_ARGEED";

    private static volatile CloudMessage cloudMessage;

    private CloudMessage() {
    }

    public static CloudMessage getInstance(){
        if (cloudMessage==null){
            synchronized (CloudMessage.class){
                if (cloudMessage==null){
                    cloudMessage = new CloudMessage();
                }
            }
        }
        return cloudMessage;
    }

    /**
     * 初始化SDK
     */
    public void init(Context context){
        RongIMClient.init(context);

    }

    public void connect(String token){
         RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
             @Override
             public void onSuccess(String s) {
                 Log.e("onSuccess",s);

             }

             @Override
             public void onError(RongIMClient.ConnectionErrorCode connectionErrorCode) {
                 Log.e("error",connectionErrorCode.toString());
             }

             @Override
             public void onDatabaseOpened(RongIMClient.DatabaseOpenStatus databaseOpenStatus) {

             }
         });
    }

    /**
     * 监听连接状态
     *
     * @param listener
     */
    public void setConnectionStatusListener(RongIMClient.ConnectionStatusListener listener) {
        RongIMClient.setConnectionStatusListener(listener);
    }

    /**
     * 断开连接
     */
    public void disconnect(){
        RongIMClient.getInstance().disconnect();
    }

    /**
     * 退出登录
     */
    public void logout(){
        RongIMClient.getInstance().logout();
    }

    /**
     * 接收消息的监听器
     * @param listener
     */
    public void setOnReceiveMessageListener(RongIMClient.OnReceiveMessageListener listener){
        RongIMClient.setOnReceiveMessageListener(listener);
    }

    public IRongCallback.ISendMessageCallback iSendMediaMessageCallback =
            new IRongCallback.ISendMessageCallback() {

                @Override
                public void onAttached(Message message) {
                    // 消息成功存到本地数据库的回调
                }

                @Override
                public void onSuccess(Message message) {
                    // 消息发送成功的回调
                    LoginUtils.LogE("sendMessage onSuccess" + message.getObjectName());
                }

                @Override
                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                    // 消息发送失败的回调
                    LoginUtils.LogE("sendMessage onError:" + errorCode);
                }
    };

    public void sendTextMessage(String msg,String id){
        TextMessage textMessage = TextMessage.obtain(msg);
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.PRIVATE,id,
                textMessage,
                null,
                null,
                iSendMediaMessageCallback);
    }

    //发送文本消息
    public void sendTextMessage(String msg,String type,String id){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msg",msg);
            //如果没有TYPE就是一条普通信息
            jsonObject.put("type",type);
            sendTextMessage(jsonObject.toString(),id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnect(){
        return RongIMClient.getInstance().getCurrentConnectionStatus()
                ==RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED;
    }

    //查询本地会话记录
    public void queryChatHistory(RongIMClient.ResultCallback<List<Conversation>> callback) {
        RongIMClient.getInstance().getConversationList(callback);
    }

    //加载本地历史记录
    public void getHistoryMessage(String targetId, RongIMClient.ResultCallback<List<Message>> callback){
         RongIMClient.getInstance().getHistoryMessages(Conversation.ConversationType.PRIVATE,
                 targetId,-1,10,callback);
    }

    //加载本地历史记录
    public void getRemoteMessage(String targetId, RongIMClient.ResultCallback<List<Message>> callback){
        RongIMClient.getInstance().getRemoteHistoryMessages(Conversation.ConversationType.PRIVATE,
                targetId,0,20,callback);
    }

    //发送图片信息
    public void sentImage(File file,String targetId,RongIMClient.SendImageMessageCallback callback){
        ImageMessage imageMessage = ImageMessage.obtain(Uri.fromFile(file), Uri.fromFile(file),true);
        RongIMClient.getInstance().sendImageMessage(Conversation.ConversationType.PRIVATE, targetId, imageMessage, null,
                null,callback);
    }

    //发送位置信息
    public void sendAdress(String id,double jin,double wei,String adress){
        LocationMessage locationMessage = LocationMessage.obtain(jin,wei,adress,null);
        io.rong.imlib.model.Message message = io.rong.imlib.model.Message.obtain(id, Conversation.ConversationType.PRIVATE, locationMessage);
        RongIMClient.getInstance().sendLocationMessage(message,null,null,iSendMediaMessageCallback);
    }


    //--------------------Call---------------------------

    /**
     * 拨打电话/视频
     * @param targetId
     * @param type
     */
    public void startCall(String targetId, RongCallCommon.CallMediaType type){
        ArrayList<String> userId = new ArrayList<>();
        userId.add(targetId);
        RongCallClient.getInstance().startCall(Conversation.ConversationType.PRIVATE,
                targetId,userId,null,type,null);
    }

    /**
     * 拨打音频
     * @param targetId
     */
    public void startAudioCall(String targetId){
        startCall(targetId,RongCallCommon.CallMediaType.AUDIO);
    }

    /**
     * 拨打视频
     * @param targetId
     */
    public void startVideoCall(String targetId){
        startCall(targetId,RongCallCommon.CallMediaType.VIDEO);
    }

    /**
     * 监听收到来电
     * @param listener
     */
    public void setReceivedCallListener(IRongReceivedCallListener listener){
        RongCallClient.getInstance().setReceivedCallListener(listener);
    }

    /**
     *接听电话
     * @param userId
     */
    public void acceptCall(String userId){
        RongCallClient.getInstance().acceptCall(userId);
    }

    /**
     *挂断电话
     * @param userId
     */
    public void hangUpCall(String userId){
        RongCallClient.getInstance().hangUpCall(userId);
    }


    /**
     *视频转音频
     * @param type
     */
    public void changeCallMediaType(RongCallCommon.CallMediaType type){
        RongCallClient.getInstance().changeCallMediaType(type);
    }


    /**
     *切换前后摄像头
     */
    public void switchCamera(){
        RongCallClient.getInstance().switchCamera();
    }

    /**
     * 摄像头开关
     *
     * @param enabled
     */
    public void setEnableLocalVideo(boolean enabled) {
        RongCallClient.getInstance().setEnableLocalVideo(enabled);
    }

    /**
     * 音频开关
     *
     * @param enabled
     */
    public void setEnableLocalAudio(boolean enabled) {
        RongCallClient.getInstance().setEnableLocalAudio(enabled);
    }

    /**
     * 免提开关
     *
     * @param enabled
     */
    public void setEnableSpeakerphone(boolean enabled) {
        RongCallClient.getInstance().setEnableSpeakerphone(enabled);
    }

    /**
     * 监听通话状态
     *
     * @param listener
     */
    public void setVoIPCallListener(IRongCallListener listener) {
        if (listener == null) {
            return;
        }
        RongCallClient.getInstance().setVoIPCallListener(listener);
    }

    /**
     * 设备是否可用
     *
     * @param context
     */
    public boolean isVoIPEnable(Context context) {
        return RongCallClient.getInstance().isVoIPEnabled(context);
    }
}
