package com.tang.xu.mysoul.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.bean.TestBean;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.cloud.CloudMessage;
import com.tang.xu.formwork.entity.CallEntity;
import com.tang.xu.formwork.entity.Contants;
import com.tang.xu.formwork.entity.LitePalEntity;
import com.tang.xu.formwork.event.EventMessage;
import com.tang.xu.formwork.event.EventUtils;
import com.tang.xu.formwork.utils.LitePalHelper;
import com.tang.xu.formwork.utils.LoginUtils;
import com.tang.xu.formwork.utils.MediaPlayerUtils;
import com.tang.xu.formwork.utils.SpUtils;
import com.tang.xu.formwork.utils.TimeUtils;
import com.tang.xu.formwork.utils.WindowHelper;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.view.UserInfoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rong.calllib.IRongCallListener;
import io.rong.calllib.IRongReceivedCallListener;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;

public class CloudService extends Service implements View.OnClickListener{

    private Disposable disposable;
    private CircleImageView audioIvPhoto;
    private TextView audioTvStatus;
    private LinearLayout audioLlRecording;
    private ImageView audioIvRecording;
    private LinearLayout audioLlAnswer;
    private ImageView audioIvAnswer;
    private LinearLayout audioLlHangup;
    private ImageView audioIvHangup;
    private LinearLayout audioLlHf;
    private ImageView audioIvHf;
    private ImageView audioIvSmall;
    private View musicview;
    private UserBean userBean;
    private String callId;
    private int time = 0;
    //音乐
    private MediaPlayerUtils mediaPlayerUtils;
    private MediaPlayerUtils mAudioHangupMedia;
    //摄像
    private SurfaceView myLoadView;
    private SurfaceView yourLoadView;
    //视频
    private RelativeLayout videoBigVideo;
    private RelativeLayout videoSmallVideo;
    private LinearLayout videoLlInfo;
    private CircleImageView videoIvPhoto;
    private TextView videoTvName;
    private TextView videoTvStatus;
    private TextView videoTvTime;
    private LinearLayout videoLlAnswer;
    private LinearLayout videoLlHangup;
    private View videoView;
    //最小化的音频View
    private WindowManager.LayoutParams lpSmallView;
    private LinearLayout layout;
    private View view;
    //时间
    private TextView mSmallTime;
    private boolean isShowLoad = false;

    //是否移动
    private boolean isMove = false;
    //是否拖拽
    private boolean isDrag = false;
    private int mLastX;
    private int mLastY;

    //拨打状态
    private int isCallTo = 0;
    //接听状态
    private int isReceiverTo = 0;
    //拨打还是接听
    private boolean isCallOrReceiver = true;


    private Handler handler = new Handler(){
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@androidx.annotation.NonNull android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what==1000){
                time++;
                String timeS = TimeUtils.formData(time*1000);
                audioTvStatus.setText(timeS+"");
                audioTvStatus.setTextColor(Color.WHITE);
                videoTvTime.setText(timeS+"");
                videoTvTime.setTextColor(Color.WHITE);
                mSmallTime.setText(timeS+"");
                mSmallTime.setTextColor(Color.WHITE);
                handler.sendEmptyMessageDelayed(1000,1000);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initService();
        initWindow();
        linkCloudServer();
    }

    private void initService() {
        EventBus.getDefault().register(this);
        //来电铃声
        mediaPlayerUtils = new MediaPlayerUtils();
        //挂断铃声
        mAudioHangupMedia  = new MediaPlayerUtils();

        //无限循环
        mediaPlayerUtils.setOnComplListene(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayerUtils.startPlay(CloudMessage.callAudioPath);
            }
        });
    }

    private void initWindow() {
        musicview = WindowHelper.getInstance().getView(R.layout.layout_chat_audio);
        audioIvPhoto = musicview.findViewById(R.id.audio_iv_photo);
        audioTvStatus = musicview.findViewById(R.id.audio_tv_status);
        audioLlRecording = musicview.findViewById(R.id.audio_ll_recording);
        audioIvRecording = musicview.findViewById(R.id.audio_iv_recording);
        audioLlAnswer = musicview.findViewById(R.id.audio_ll_answer);
        audioIvAnswer = musicview.findViewById(R.id.audio_iv_answer);
        audioLlHangup = musicview.findViewById(R.id.audio_ll_hangup);
        audioIvHangup = musicview.findViewById(R.id.audio_iv_hangup);
        audioLlHf = musicview.findViewById(R.id.audio_ll_hf);
        audioIvHf = musicview.findViewById(R.id.audio_iv_hf);
        audioIvSmall = musicview.findViewById(R.id.audio_iv_small);
        audioLlRecording.setOnClickListener(this);
        audioLlAnswer.setOnClickListener(this);
        audioLlHangup.setOnClickListener(this);
        audioLlHf.setOnClickListener(this);
        audioIvSmall.setOnClickListener(this);

        videoView = WindowHelper.getInstance().getView(R.layout.layout_chat_video);
        videoBigVideo = videoView.findViewById(R.id.video_big_video);
        videoSmallVideo = videoView.findViewById(R.id.video_small_video);
        videoLlInfo = videoView.findViewById(R.id.video_ll_info);
        videoIvPhoto = videoView.findViewById(R.id.video_iv_photo);
        videoTvName = videoView.findViewById(R.id.video_tv_name);
        videoTvStatus = videoView.findViewById(R.id.video_tv_status);
        videoTvTime = videoView.findViewById(R.id.video_tv_time);
        videoLlAnswer = videoView.findViewById(R.id.video_ll_answer);
        videoLlHangup = videoView.findViewById(R.id.video_ll_hangup);
        videoLlAnswer.setOnClickListener(this);
        videoLlHangup.setOnClickListener(this);
        videoSmallVideo.setOnClickListener(this);
        createSmallAudioView();
    }

    private void createSmallAudioView() {
        lpSmallView = WindowHelper.getInstance().createParms(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, Gravity.TOP|Gravity.LEFT);

        view = WindowHelper.getInstance().getView(R.layout.layout_chat_small_audio);
        layout = view.findViewById(R.id.layout);
        mSmallTime = view.findViewById(R.id.mSmallTime);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handler.sendEmptyMessage(1000);
                /**
                 * OnTouch 和 OnClick 点击冲突
                 * 如何判断是点击 还是 移动
                 * 通过点击下的坐标 - 落地的坐标 如果移动则说明是移动 如果 = 0 ，那说明没有移动则是点击
                 */
                int mStartX = (int) event.getRawX();
                int mStartY = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMove = false;
                        isDrag = false;
                        mLastX = (int) event.getRawX();
                        mLastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        //偏移量
                        int dx = mStartX - mLastX;
                        int dy = mStartY - mLastY;

                        if (isMove) {
                            isDrag = true;
                        } else {
                            if (dx == 0 && dy == 0) {
                                isMove = false;
                            } else {
                                isMove = true;
                                isDrag = true;
                            }
                        }

                        //移动
                        lpSmallView.x += dx;
                        lpSmallView.y += dy;

                        //重置坐标
                        mLastX = mStartX;
                        mLastY = mStartY;

                        //WindowManager addView removeView updateView
                        WindowHelper.getInstance().updateView(view, lpSmallView);

                        break;
                }
                return isDrag;
            }
        });
    }

    private void linkCloudServer() {
        String token = SpUtils.getInstance().getString(Contants.token, "");
        //连接服务
        CloudMessage.getInstance().connect(token);
        LoginUtils.LogE("txj"+token);
        //接收消息
        CloudMessage.getInstance().setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            @Override
            public boolean onReceived(Message message, int i) {
                Log.i("message",""+message.getObjectName());
                String objectName = message.getObjectName();
                //文本信息
                if (objectName.equals(CloudMessage.MSG_TEXT_NAME)){
                    TextMessage textMessage = (TextMessage) message.getContent();
                    String content = textMessage.getContent();
                    LoginUtils.LogE("Content");
                    TestBean testBean = new Gson().fromJson(content, TestBean.class);
                    if (testBean.getType().equals(CloudMessage.TYPE_TEXT)){
                        EventMessage eventMessage = new EventMessage(EventMessage.FRIEND_SEND_TEXT);
                        LoginUtils.LogE("post"+testBean.getMsg());
                        eventMessage.setText(testBean.getMsg());
                        eventMessage.setUserId(message.getSenderUserId());
                        EventUtils.getInstance().post(eventMessage);
                    }else if (testBean.getType().equals(CloudMessage.TYPE_ADD_FRIEND)){
                        //存入数据库
                        LoginUtils.LogE("添加好友消息");

                        disposable = Observable.create(new ObservableOnSubscribe<List<LitePalEntity>>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<List<LitePalEntity>> emitter) throws Exception {
                                emitter.onNext(LitePalHelper.getInstance().queryFriend());
                                emitter.onComplete();
                            }
                        }).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<List<LitePalEntity>>() {
                                    @Override
                                    public void accept(List<LitePalEntity> litePalEntities) throws Exception {
                                        if (litePalEntities.size()>0&&litePalEntities!=null){
                                            boolean isHave = false;
                                            for (int j = 0; j < litePalEntities.size(); j++) {
                                                LitePalEntity litePalEntity = litePalEntities.get(j);
                                                if (message.getSenderUserId().equals(litePalEntity.getUserId())){
                                                    isHave=true;
                                                    break;
                                                }
                                            }
                                            if (!isHave){
                                                LitePalHelper.getInstance().save(testBean.getMsg(),message.getSenderUserId());
                                            }
                                        }else {
                                            LitePalHelper.getInstance().save(testBean.getMsg(),message.getSenderUserId());
                                        }
                                    }
                                });
                    }else if (testBean.getType().equals(CloudMessage.TYPE_ADD_ARGEED)){
                            //同意添加好友返回后
                        FormWork.getInstance().addfriend(message.getSenderUserId(), new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e==null){
                                    EventUtils.getInstance().post(EventMessage.FRIEND_ADD);
                                }
                            }
                        });
                    }
                }else if (objectName.equals(CloudMessage.MSG_IMAGE_NAME)){
                    ImageMessage imageMessage = (ImageMessage) message.getContent();
                    String s = imageMessage.getRemoteUri().toString();
                    if (s!=null){
                        EventMessage eventMessage = new EventMessage(EventMessage.FRIEND_SEND_IMAGE);
                        eventMessage.setUserId(s);
                        eventMessage.setUserId(message.getSenderUserId());
                        EventUtils.getInstance().post(eventMessage);
                    }
                }else if (objectName.equals(CloudMessage.MSG_LOCATION_NAME)){
                    LocationMessage locationMessage = (LocationMessage) message.getContent();
                    EventMessage eventMessage = new EventMessage(EventMessage.FRIEND_SEND_LOCATION);
                    eventMessage.setJin((int) locationMessage.getLat());
                    eventMessage.setUserId(message.getSenderUserId());
                    eventMessage.setWei((int) locationMessage.getLng());
                    eventMessage.setAdress(locationMessage.getPoi());
                    EventUtils.post(eventMessage);
                }
                return false;
            }
        });

        //监听电话
        CloudMessage.getInstance().setReceivedCallListener(new IRongReceivedCallListener() {
            @Override
            public void onReceivedCall(RongCallSession rongCallSession) {
                //检查设备是否可以使用
                if (!CloudMessage.getInstance().isVoIPEnable(CloudService.this)){
                    return;
                }
                //呼叫端口ID
                String callerUserId = rongCallSession.getCallerUserId();
                //通话ID
                callId = rongCallSession.getCallId();
                mediaPlayerUtils.startPlay(CloudMessage.callAudioPath);
                updataWindowUserInfo(1,rongCallSession.getMediaType(),callerUserId);

                if (rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
                    WindowHelper.getInstance().show(musicview);
                } else if (rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)) {
                    WindowHelper.getInstance().show(videoView);
                }

                isReceiverTo = 1;
                isCallOrReceiver = false;
            }

            @Override
            public void onCheckPermission(RongCallSession rongCallSession) {

            }
        });
        //监听电话状态
         CloudMessage.getInstance().setVoIPCallListener(new IRongCallListener() {
             @Override
             public void onCallOutgoing(RongCallSession rongCallSession, SurfaceView surfaceView) {

                 isCallOrReceiver=true;
                 //拨打电话
                 isCallTo = 1;

                 //接听端口ID
                 String TargetId = rongCallSession.getTargetId();
                 //通话ID
                 callId = rongCallSession.getCallId();
                 mediaPlayerUtils.startPlay(CloudMessage.callAudioPath);
                 updataWindowUserInfo(0,rongCallSession.getMediaType(),TargetId);

                 if (rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)) {
                     WindowHelper.getInstance().show(musicview);
                 } else if (rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)) {
                     WindowHelper.getInstance().show(videoView);
                     //显示摄像头
                     myLoadView = surfaceView;
                     videoBigVideo.addView(myLoadView);
                 }

             }

             //建立通话
             @Override
             public void onCallConnected(RongCallSession rongCallSession, SurfaceView surfaceView) {
                 isReceiverTo = 2;
                 isCallTo = 2;
                 //关闭铃声
                 mediaPlayerUtils.pausePlay();
                 mAudioHangupMedia.pausePlay();
                 handler.sendEmptyMessage(1000);
                 if (rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){
                     //音频电话
                     goneAudioView(true,false,true,true,true);
                 }else if (rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)){
                     //视屏电话
                     goneVideoView(false,false,true,true,true,true);
                     myLoadView = surfaceView;
                 }
             }

             //挂断电话
             @Override
             public void onCallDisconnected(RongCallSession rongCallSession, RongCallCommon.CallDisconnectedReason callDisconnectedReason) {
                 String callerUserId = rongCallSession.getCallerUserId();
                 //目标ID
                 String targetId = rongCallSession.getTargetId();
                 //关闭铃声
                 handler.removeMessages(1000);
                 mediaPlayerUtils.pausePlay();
                 mAudioHangupMedia.startPlay(CloudMessage.callAudioHangup);
                 time=0;
                 if (rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.AUDIO)){
                     //音频电话
                     WindowHelper.getInstance().hide(musicview);
                     WindowHelper.getInstance().hide(view);
                     WindowHelper.getInstance().hide(videoView);

                     if (isCallOrReceiver){
                         //拨打未接通
                         if (isCallTo==1){
                             LitePalHelper.getInstance().saveCallRecord(targetId, CallEntity.MEDIA_TYPE_AUDIO,CallEntity.CALL_STATUS_DIAL);
                         }else if (isCallTo==2){
                             LitePalHelper.getInstance().saveCallRecord(targetId, CallEntity.MEDIA_TYPE_AUDIO,CallEntity.CALL_STATUS_ANSWER);
                         }
                     }else {
                         //接听方
                         if (isReceiverTo==1){
                             LitePalHelper.getInstance().saveCallRecord(callerUserId, CallEntity.MEDIA_TYPE_AUDIO,CallEntity.CALL_STATUS_UN_ANSWER);
                         }else if (isReceiverTo==2){
                             LitePalHelper.getInstance().saveCallRecord(callerUserId, CallEntity.MEDIA_TYPE_AUDIO,CallEntity.CALL_STATUS_ANSWER);
                         }
                     }

                 }else if (rongCallSession.getMediaType().equals(RongCallCommon.CallMediaType.VIDEO)){
                     //视屏电话
                     WindowHelper.getInstance().hide(videoView);
                     if (isCallOrReceiver){
                         if (isCallTo==1){
                             LitePalHelper.getInstance().saveCallRecord(targetId, CallEntity.MEDIA_TYPE_VIDEO,CallEntity.CALL_STATUS_DIAL);
                         }else if (isCallTo==2){
                             LitePalHelper.getInstance().saveCallRecord(targetId, CallEntity.MEDIA_TYPE_VIDEO,CallEntity.CALL_STATUS_ANSWER);
                         }
                     }else {
                         //接听方
                         if (isReceiverTo==1){
                             LitePalHelper.getInstance().saveCallRecord(callerUserId, CallEntity.MEDIA_TYPE_VIDEO,CallEntity.CALL_STATUS_UN_ANSWER);
                         }else if (isReceiverTo==2){
                             LitePalHelper.getInstance().saveCallRecord(callerUserId, CallEntity.MEDIA_TYPE_VIDEO,CallEntity.CALL_STATUS_ANSWER);
                         }
                     }
                 }
                 isReceiverTo=0;
                 isCallTo=0;
             }

             @Override
             public void onRemoteUserRinging(String s) {

             }

             //被叫端加入通话
             @Override
             public void onRemoteUserJoined(String s, RongCallCommon.CallMediaType callMediaType, int i, SurfaceView surfaceView) {
                 EventMessage eventMessage = new EventMessage(EventMessage.FRIEND_SEND_LOCATION_VIDEO);
                 eventMessage.setSurfaceView(surfaceView);
                 EventUtils.post(eventMessage);
             }

             @Override
             public void onRemoteUserInvited(String s, RongCallCommon.CallMediaType callMediaType) {

             }

             @Override
             public void onRemoteUserLeft(String s, RongCallCommon.CallDisconnectedReason callDisconnectedReason) {

             }

             @Override
             public void onMediaTypeChanged(String s, RongCallCommon.CallMediaType callMediaType, SurfaceView surfaceView) {

             }

             @Override
             public void onError(RongCallCommon.CallErrorCode callErrorCode) {

             }

             @Override
             public void onRemoteCameraDisabled(String s, boolean b) {

             }

             @Override
             public void onRemoteMicrophoneDisabled(String s, boolean b) {

             }

             @Override
             public void onNetworkReceiveLost(String s, int i) {

             }

             @Override
             public void onNetworkSendLost(int i, int i1) {

             }

             @Override
             public void onFirstRemoteVideoFrame(String s, int i, int i1) {

             }

             @Override
             public void onAudioLevelSend(String s) {

             }

             @Override
             public void onAudioLevelReceive(HashMap<String, String> hashMap) {

             }

             @Override
             public void onRemoteUserPublishVideoStream(String s, String s1, String s2, SurfaceView surfaceView) {

             }

             @Override
             public void onRemoteUserUnpublishVideoStream(String s, String s1, String s2) {

             }
         });
    }

    private void updataWindowUserInfo(int index, RongCallCommon.CallMediaType type, String callerUserId) {
        if (type.equals(RongCallCommon.CallMediaType.AUDIO)){
//            //音频电话
//            WindowHelper.getInstance().show(musicview);
            if (index==0){
                goneAudioView(false,false,true,false,false);
            }else if (index==1){
                goneAudioView(false,true,true,false,false);
            }
        }else if(type.equals(RongCallCommon.CallMediaType.VIDEO)){
//            //视屏电话
//            WindowHelper.getInstance().show(videoView);
            if (index==0){
                goneVideoView(true,false,true,false,false,false);
            }else if (index==1){
                goneVideoView(true,true,true,false,false,false);
            }
        }

        FormWork.getInstance().queryUserId(callerUserId, new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                if (e==null){
                    if (list!=null||list.size()>0){
                        userBean = list.get(0);
                        Log.e("userInfo",userBean.toString());
                        if (type.equals(RongCallCommon.CallMediaType.AUDIO)){
                            Glide.with(CloudService.this).load(userBean.getPhoto()).into(audioIvPhoto);
                            if (index==0){
                                audioTvStatus.setText("正在呼叫"+userBean.getNickName()+"....");
                            }else if (index==1){
                                audioTvStatus.setText(userBean.getNickName()+"来电..");
                            }
                        }else if(type.equals(RongCallCommon.CallMediaType.VIDEO)){
                            videoTvName.setText(userBean.getNickName());
                            Glide.with(CloudService.this).load(userBean.getPhoto()).into(videoIvPhoto);
                            if (index==0){
                                videoTvStatus.setText("正在呼叫"+userBean.getNickName()+"....");
                            }else if (index==1){
                                videoTvStatus.setText(userBean.getNickName()+"来电..");
                            }
                        }

                    }
                }
            }
        });
    }

    private void goneAudioView(boolean recoding,boolean answer,boolean hangup,boolean hf,boolean small){
        audioLlRecording.setVisibility(recoding?View.VISIBLE:View.GONE);
        audioLlAnswer.setVisibility(answer?View.VISIBLE:View.GONE);
        audioLlHangup.setVisibility(hangup?View.VISIBLE:View.GONE);
        audioLlHf.setVisibility(hf?View.VISIBLE:View.GONE);
        audioIvSmall.setVisibility(small?View.VISIBLE:View.GONE);
    }

    private void goneVideoView(boolean info,boolean answer,boolean hangup,boolean time,boolean small,boolean big) {
        videoLlInfo.setVisibility(info?View.VISIBLE:View.GONE);
        videoLlAnswer.setVisibility(answer?View.VISIBLE:View.GONE);
        videoLlHangup.setVisibility(hangup?View.VISIBLE:View.GONE);
        videoTvTime.setVisibility(time?View.VISIBLE:View.GONE);
        videoSmallVideo.setVisibility(small?View.VISIBLE:View.GONE);
        videoBigVideo.setVisibility(big?View.VISIBLE:View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable.isDisposed()){
            disposable.dispose();
        }
        EventBus.getDefault().register(this);
    }

    private boolean hf = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.audio_ll_recording:
                break;
            case R.id.audio_ll_answer:
                CloudMessage.getInstance().acceptCall(callId);
                break;
            case R.id.video_ll_answer:
                CloudMessage.getInstance().acceptCall(callId);
                break;
            case R.id.audio_ll_hangup:
                CloudMessage.getInstance().hangUpCall(callId);
                break;
            case R.id.video_ll_hangup:
                CloudMessage.getInstance().hangUpCall(callId);
                break;
            case R.id.audio_ll_hf:
                if (!hf){
                    hf=false;
                    CloudMessage.getInstance().setEnableLocalAudio(true);
                }else {
                    hf=true;
                    CloudMessage.getInstance().setEnableLocalAudio(false);
                }
                break;
            case R.id.audio_iv_small:
                WindowHelper.getInstance().hide(musicview);
                WindowHelper.getInstance().show(view,lpSmallView);
                break;
            case R.id.video_small_video:
                isShowLoad = !isShowLoad;
                updataVideo();
                break;
            case R.id.layout:
                WindowHelper.getInstance().hide(view);
                WindowHelper.getInstance().show(musicview);
                break;
        }
    }



    private void updataVideo(){
        videoBigVideo.removeAllViews();
        videoSmallVideo.removeAllViews();
        if (isShowLoad){
            if (myLoadView!=null){
                videoSmallVideo.addView(myLoadView);
                myLoadView.setZOrderOnTop(true);
            }
            if (yourLoadView!=null){
                videoBigVideo.addView(yourLoadView);
                yourLoadView.setZOrderOnTop(false);
            }
        }else {
            if (myLoadView!=null){
                videoBigVideo.addView(myLoadView);
                myLoadView.setZOrderOnTop(false);
            }
            if (yourLoadView!=null){
                videoSmallVideo.addView(yourLoadView);
                yourLoadView.setZOrderOnTop(true);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        switch (event.getType()){
            case EventMessage.FRIEND_SEND_LOCATION_VIDEO:
                SurfaceView surfaceView = event.getSurfaceView();
                if (surfaceView!=null){
                    yourLoadView = surfaceView;
                }
                updataVideo();
                break;
        }
    }
}
