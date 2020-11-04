package com.tang.xu.formwork.utils;

import android.content.Context;
import android.view.Gravity;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.tang.xu.formwork.R;
import com.tang.xu.formwork.view.DilogViewRoom;

public class VoiceMessage {

   private static volatile VoiceMessage voiceMessage = null;
   private RecognizerDialog recognizerDialog;
   private VoiceMessage(Context context) {

        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=5f9a26e9");

        recognizerDialog = new RecognizerDialog(context, new InitListener() {
            @Override
            public void onInit(int i) {
                LoginUtils.LogE("recognizerDialog"+i);
            }
        });
        if (recognizerDialog!=null){
            recognizerDialog.setParameter( SpeechConstant.CLOUD_GRAMMAR, null );
            recognizerDialog.setParameter( SpeechConstant.SUBJECT, null );
            recognizerDialog.setParameter(SpeechConstant.RESULT_TYPE, "json");
            recognizerDialog.setParameter( SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            recognizerDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            recognizerDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
            recognizerDialog.setParameter(SpeechConstant.VAD_BOS, "4000");
            recognizerDialog.setParameter(SpeechConstant.VAD_EOS, "1000");
            recognizerDialog.setParameter(SpeechConstant.ASR_PTT,"1");
        }
    }

    public static VoiceMessage getInstance(Context context){
       if (voiceMessage==null){
           synchronized (VoiceMessage.class){
               if (voiceMessage==null){
                   voiceMessage=new VoiceMessage(context);
               }
           }
       }
       return voiceMessage;
   }

   public void startSpeak(RecognizerDialogListener listener){
       recognizerDialog.setListener(listener);
       recognizerDialog.show();
   }

}
