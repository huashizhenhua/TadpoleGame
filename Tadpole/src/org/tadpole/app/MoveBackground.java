package org.tadpole.app;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class MoveBackground {

    private View mRunImage;

    private Animation mAniMoveToRight;

    private Animation mAniMoveToLeft;

    public MoveBackground(View runImage) {
        this.mRunImage = runImage;
    }

    public void startMove() {
        mAniMoveToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, -1f, Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f);
        mAniMoveToLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f, Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f);
        mAniMoveToRight.setDuration(25000);
        mAniMoveToLeft.setDuration(25000);
        mAniMoveToRight.setFillAfter(true);
        mAniMoveToLeft.setFillAfter(true);
        mAniMoveToRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                mRunImage.startAnimation(mAniMoveToLeft);
            }
        });
        mAniMoveToLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                mRunImage.startAnimation(mAniMoveToRight);
            }
        });
        mRunImage.startAnimation(mAniMoveToRight);
    }
}
