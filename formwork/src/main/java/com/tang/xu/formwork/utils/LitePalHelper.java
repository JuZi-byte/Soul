package com.tang.xu.formwork.utils;

import com.tang.xu.formwork.entity.CallEntity;
import com.tang.xu.formwork.entity.LitePalEntity;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public
class LitePalHelper {
    public static volatile  LitePalHelper litePalHelper = null;

    private LitePalHelper() {
    }

    public static LitePalHelper getInstance(){
        if (litePalHelper==null){
            synchronized (LitePalHelper.class){
                if (litePalHelper==null){
                    litePalHelper=new LitePalHelper();
                }
            }
        }
        return litePalHelper;
    }

    //保存基类
    private void basesave(LitePalSupport support){
        support.save();
    }

    //保存
    public void save(String msg,String userId){
        LoginUtils.LogE("saveNewFriend");
        LitePalEntity litePalEntity = new LitePalEntity();
        litePalEntity.setUserId(userId);
        litePalEntity.setIsAgree(-1);
        litePalEntity.setName(msg);
        litePalEntity.setSaveTime(System.currentTimeMillis()+"");
        LoginUtils.LogE("saveNewFriend"+litePalEntity.toString());
        basesave(litePalEntity);
    }

    //查询基类
    private List<? extends LitePalSupport> basequery(Class clss){
        return LitePal.findAll(clss);
    }

    //查询
    public List<LitePalEntity> queryFriend(){
        return (List<LitePalEntity>) basequery(LitePalEntity.class);
    }

    //更新状态 -1 0 1
    public void upData(String userId,int agress){
        LitePalEntity albumToUpdate = new LitePalEntity();
        albumToUpdate.setIsAgree(agress); // raise the price
        albumToUpdate.updateAll("userId = ?",userId);
    }

    /**
     * 查询通话记录
     *
     * @return
     */
    public List<CallEntity> queryCallEntity() {
        return (List<CallEntity>) basequery(CallEntity.class);
    }

    /**
     * 保存通话记录
     *
     * @param userId
     * @param mediaType
     * @param callStatus
     */
    public void saveCallRecord(String userId, int mediaType, int callStatus) {
        CallEntity callEntity = new CallEntity();
        callEntity.setUserId(userId);
        callEntity.setMediaType(mediaType);
        callEntity.setCallStatus(callStatus);
        callEntity.setCallTime(System.currentTimeMillis());
        basesave(callEntity);
    }

}
