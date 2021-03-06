package com.nelson.glidetest.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.nelson.glidetest.BaseActivity;
import com.nelson.glidetest.R;
import com.nelson.glidetest.databinding.ActivityAnimateBinding;
import com.nelson.glidetest.model.ResourceConfig;

/**
 * 用Animate()自定义动画
 *
 * Created by Nelson on 2018/4/13.
 */

public class AnimateActivity extends BaseActivity {

    private ActivityAnimateBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Animate");

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_animate);

        showTranslate();

        showScale();

        showAnimateOnCustomView();
    }

    /**
     * 从左滑入
     */
    private void showTranslate() {
        Glide.with(this)
                .load(ResourceConfig.IMAGE_REMOTE_URLS[0])
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .animate(R.anim.anim_slide_in_left)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mBinding.ivTranslate);
    }

    /**
     * 缩放动画
     */
    private void showScale() {
        Glide.with(this)
                .load(ResourceConfig.IMAGE_REMOTE_URLS[0])
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .animate(R.anim.anim_zoom_in)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mBinding.ivTranslate);
    }

    private ViewPropertyAnimation.Animator animator = new ViewPropertyAnimation.Animator() {
        @Override
        public void animate(final View view) {
            // 这个视图对象是整个target视图，如果他是一个自定义的视图，你要找到你的视图的子元素，并且做些必要的动画。

            //if it's a custom view class,cast it here then find subviews and do the animations here,
            //we just use the entire view for the fade animation
            // 渐现动画
//            view.setAlpha(0f);
//
//            ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
//            fadeAnim.setDuration(2500);
//            fadeAnim.start();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    view.setScaleX((float) (0.5 + 0.5 * value));
                    view.setScaleY((float) (0.5 + 0.5 * value));
                    view.setAlpha(value);
                }
            });
            valueAnimator.start();
        }
    };

    private void showAnimateOnCustomView() {
        Glide.with(this)
                .load(ResourceConfig.IMAGE_REMOTE_URLS[0])
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .animate(animator)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mBinding.ivCustom);
    }

}
