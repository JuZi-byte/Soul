package com.tang.xu.mysoul.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tang.xu.formwork.adapter.BaseAdapter;
import com.tang.xu.formwork.base.BaseUiActivity;
import com.tang.xu.formwork.utils.LoginUtils;
import com.tang.xu.formwork.utils.MediaPlayerUtils;
import com.tang.xu.mysoul.R;

import java.util.ArrayList;

public class AnimotionActivity extends BaseUiActivity implements View.OnClickListener{

    private ImageView musicButton;
    private TextView next;
    private ImageView point1;
    private ImageView point2;
    private ImageView point3;
    private ViewPager viewPager;
    private View star;
    private View night;
    private View smile;
    private ArrayList<View> arrayList = new ArrayList<>();
    private BaseAdapter baseAdapter;
    private ImageView one;
    private ImageView two;
    private ImageView three;
    private MediaPlayerUtils mediaPlayerUtils;
    private ObjectAnimator objectAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animotion);
        initView();
        startMusic();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AnimotionActivity.this,LoginActivity.class));
                finish();
            }
        });
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MediaPlayerUtils.play==MediaPlayerUtils.buff){
                    objectAnimator.pause();
                    musicButton.setImageResource(R.drawable.img_guide_music_off);
                    mediaPlayerUtils.pausePlay();
                }else if (MediaPlayerUtils.pause==MediaPlayerUtils.buff){
                    objectAnimator.start();
                    musicButton.setImageResource(R.drawable.img_guide_music);
                    mediaPlayerUtils.continuPlay();
                }else if (MediaPlayerUtils.stop==MediaPlayerUtils.buff){
                    objectAnimator.start();
                    musicButton.setImageResource(R.drawable.img_guide_music);
                    mediaPlayerUtils.continuPlay();
                }

            }
        });
    }

    private void startMusic() {
        mediaPlayerUtils = new MediaPlayerUtils();
        mediaPlayerUtils.isLopper(true);
        AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.guide);
        mediaPlayerUtils.startPlay(assetFileDescriptor);

        musicButton.setImageResource(R.drawable.img_guide_music);
        //旋转动画
        objectAnimator = ObjectAnimator.ofFloat(musicButton,"rotation",0f,360f);
        objectAnimator.setDuration(2*1000);
        //设置动画一直循环
        //objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    private void initView() {
        musicButton = findViewById(R.id.music_button);
        next = findViewById(R.id.next);
        point1 = findViewById(R.id.point_1);
        point2 = findViewById(R.id.point_2);
        point3 = findViewById(R.id.point_3);
        point1.setOnClickListener(this);
        point2.setOnClickListener(this);
        point3.setOnClickListener(this);
        viewPager = findViewById(R.id.viewPager);
        star =  View.inflate(this,R.layout.layout_image_1,null);
        night =  View.inflate(this,R.layout.layout_image_2,null);
        smile =  View.inflate(this,R.layout.layout_image_3,null);
        arrayList.add(star);
        arrayList.add(night);
        arrayList.add(smile);
        baseAdapter = new BaseAdapter(arrayList);
        viewPager.setOffscreenPageLimit(arrayList.size());
        viewPager.setAdapter(baseAdapter);

        one = star.findViewById(R.id.one);
        two = night.findViewById(R.id.two);
        three = smile.findViewById(R.id.three);

        AnimationDrawable animationDrawable = (AnimationDrawable) one.getBackground();
        animationDrawable.start();
        AnimationDrawable animationDrawable2 = (AnimationDrawable) two.getBackground();
        animationDrawable2.start();
        AnimationDrawable animationDrawable3 = (AnimationDrawable) three.getBackground();
        animationDrawable3.start();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        point1.setImageResource(R.drawable.img_guide_point_p);
                        point2.setImageResource(R.drawable.img_guide_point);
                        point3.setImageResource(R.drawable.img_guide_point);
                        break;
                    case 1:
                        point1.setImageResource(R.drawable.img_guide_point);
                        point2.setImageResource(R.drawable.img_guide_point_p);
                        point3.setImageResource(R.drawable.img_guide_point);
                        break;
                    case 2:
                        point1.setImageResource(R.drawable.img_guide_point);
                        point2.setImageResource(R.drawable.img_guide_point);
                        point3.setImageResource(R.drawable.img_guide_point_p);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.point_1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.point_2:
                viewPager.setCurrentItem(1);
                break;
            case R.id.point_3:
                viewPager.setCurrentItem(2);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayerUtils.stopPlay();

    }
}