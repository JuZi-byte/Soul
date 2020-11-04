package com.tang.xu.formwork;

import android.content.Context;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tang.xu.formwork.bean.FateSet;
import com.tang.xu.formwork.bean.PrivateSet;
import com.tang.xu.formwork.bean.SquareSet;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.cloud.CloudMessage;
import com.tang.xu.formwork.entity.FriendEntity;
import com.tang.xu.formwork.utils.SpUtils;
import com.tang.xu.formwork.utils.VoiceMessage;
import com.tang.xu.formwork.utils.WindowHelper;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.litepal.LitePal;

import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class FormWork {

    private String Key = "a3d6e925e5312ad9f0f9d97574e71b7f";
    private static volatile FormWork formWork;

    private FormWork() {
    }

    public static FormWork getInstance(){
        if (formWork==null){
            synchronized (FormWork.class){
                if (formWork==null){
                    formWork = new FormWork();
                }
            }
        }
        return formWork;
    }

    public void initBob(Context context){
        SpUtils.getInstance().init(context);
        Bmob.initialize(context,Key);
        //初始化融云SDK
        CloudMessage.getInstance().init(context);
        //初始化litepal数据库
        LitePal.initialize(context);
        //初始化Window
        WindowHelper.getInstance().initView(context);
        ZXingLibrary.initDisplayOpinion(context);
    }

    public boolean islogin(){
        return BmobUser.isLogin();
    }

    public void request(String username,QueryListener<Integer> listener){
        BmobSMS.requestSMSCode(username, "",listener);
    }

    public void loginByAccount(String username, String password, final UpdateList updateList) {
        BmobUser.loginByAccount(username, password, new LogInListener<UserBean>() {
            @Override
            public void done(UserBean user, BmobException e) {
                if (e == null) {
                    updateList.onSuccess();
                } else {
                    updateList.onError(e);
                }
            }
        });
    }

    public void login(String username,String code,LogInListener<UserBean> listener){
        BmobUser.signOrLoginByMobilePhone(username, code, listener);
    }

    public UserBean getUser(){
        return BmobUser.getCurrentUser(UserBean.class);
    }

    public void setUpload(final String trim, final File fileUpload, final UpdateList updateList) {
        final UserBean user = getUser();
        final BmobFile bmobFile = new BmobFile(fileUpload);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    user.setNickName(trim);
                    user.setPhoto(bmobFile.getFileUrl());
                    user.setTokenNickName(trim);
                    user.setTokenPhoto(bmobFile.getFileUrl());
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e==null){
                                updateList.onSuccess();
                            }else {
                                updateList.onError(e);
                            }
                        }
                    });
                }else {
                    updateList.onError(e);
                }
            }
        });
    }

    public void addFateSet(SaveListener<String> listener) {
        FateSet fateSet = new FateSet();
        fateSet.setUserId(getUser().getObjectId());
        fateSet.save(listener);
    }

    public interface UpdateList{
        void onSuccess();
        void onError(BmobException e);
    }

    public void queryUser(String phone, FindListener<UserBean> userBeanFindListener){
        BmobQuery<UserBean> beanBmobQuery = new BmobQuery<>();
        beanBmobQuery.addWhereEqualTo("mobilePhoneNumber", phone);
        beanBmobQuery.findObjects(userBeanFindListener);
    }

    public void queryUserAll(FindListener<UserBean> userBeanFindListener){
        BmobQuery<UserBean> beanBmobQuery = new BmobQuery<>();
        beanBmobQuery.findObjects(userBeanFindListener);
    }

    public void queryUserId(String userid,FindListener<UserBean> findListener){
        BmobQuery<UserBean> beanBmobQuery = new BmobQuery<>();
        beanBmobQuery.addWhereEqualTo("objectId",userid);
        beanBmobQuery.findObjects(findListener);
    }

    /**
     * 查询所有的广场的数据
     *
     * @param listener
     */
    public void queryAllSquare(FindListener<SquareSet> listener) {
        BmobQuery<SquareSet> query = new BmobQuery<>();
        query.findObjects(listener);
    }

    /**
     * 查询私有库
     * @param findListener
     */
    public void queryPrivateSet(FindListener<PrivateSet> findListener){
        BmobQuery<PrivateSet> beanBmobQuery = new BmobQuery<>();
        beanBmobQuery.findObjects(findListener);
    }

    public void basequery(String key,String values,FindListener<UserBean> listener){
        BmobQuery<UserBean> query = new BmobQuery<>();
        query.addWhereEqualTo(key,values);
        query.findObjects(listener);
    }

    /**
     * 查询所有好友
     */
    public void queryFriend(FindListener<FriendEntity> findListener){
        BmobQuery<FriendEntity> beanBmobQuery = new BmobQuery<>();
        beanBmobQuery.addWhereEqualTo("me",getUser());
        beanBmobQuery.findObjects(findListener);
    }

    /**
     * 通过Userbean
     * 添加好友
     * @param userBean
     * @param listener
     */
    public void addfriend(UserBean userBean, SaveListener<String> listener) {
        FriendEntity friendEntity = new FriendEntity();
        friendEntity.setMe(getUser());
        friendEntity.setFriend(userBean);
        friendEntity.save(listener);
    }

    /**
     * 添加私有库内容
     * @param listener
     */
    public void addPrivateSet(SaveListener<String> listener) {
        PrivateSet privateSet = new PrivateSet();
        privateSet.setUserId(getUser().getObjectId());
        privateSet.setPhone(getUser().getPhone());
        privateSet.save(listener);
    }

    /**
     * 查询缘分池
     */
    public void queryFateSet(FindListener<FateSet> listener) {
        BmobQuery<FateSet> query = new BmobQuery<>();
        query.findObjects(listener);
    }

    /**
     * 删除缘分池
     * @param id
     */
    public void delFateSet(String id,UpdateListener updateList) {
        FateSet fateSet = new FateSet();
        fateSet.setUserId(id);
        fateSet.delete(updateList);
    }

    /**
     * 删除私有库内容
     * @param listener
     */
    public void delPrivateSet(String id,UpdateListener listener) {
        PrivateSet privateSet = new PrivateSet();
        privateSet.setUserId(id);
        privateSet.delete(listener);
    }

    /**
     * 通过Id
     * 添加好友
     * @param id
     * @param listener
     */
    public void addfriend(String id, final SaveListener<String> listener) {
       queryUserId(id, new FindListener<UserBean>() {
           @Override
           public void done(List<UserBean> list, BmobException e) {
               if (e==null){
                   if (list!=null&&list.size()>0){
                       UserBean userBean = list.get(0);
                       addfriend(userBean,listener);
                   }
               }
           }
       });
    }

    /**
     * 发布广场
     *
     * @param mediaType 媒体类型
     * @param text      文本
     * @param path      路径
     */
    public void pushSquare(int mediaType, String text, String path, SaveListener<String> listener) {
        SquareSet squareSet = new SquareSet();
        squareSet.setUserId(getUser().getObjectId());
        squareSet.setPushTime(System.currentTimeMillis());

        squareSet.setText(text);
        squareSet.setMediaUrl(path);
        squareSet.setPushType(mediaType);
        squareSet.save(listener);
    }
}
