package oak.widget;

import android.widget.ImageView;

public class SceneAnimation {

    private ImageView mImageView;
    private int[] mFrameRess;
    private int[] mDurations;
    private int mDuration;

    private int mLastFrameNo;
    private long mBreakDelay;

    public SceneAnimation(ImageView pImageView, int[] pFrameRess, int[] pDurations) {
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDurations = pDurations;
        mLastFrameNo = pFrameRess.length - 1;

        mImageView.setImageResource(mFrameRess[0]);
        play(1);
    }

    public SceneAnimation(ImageView pImageView, int[] pFrameRess, int pDuration) {
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDuration = pDuration;
        mLastFrameNo = pFrameRess.length - 1;

        mImageView.setImageResource(mFrameRess[0]);
        playConstant(1);
    }

    public SceneAnimation(ImageView pImageView, int[] pFrameRess, int pDuration, long pBreakDelay) {
        mImageView = pImageView;
        mFrameRess = pFrameRess;
        mDuration = pDuration;
        mLastFrameNo = pFrameRess.length - 1;
        mBreakDelay = pBreakDelay;

        mImageView.setImageResource(mFrameRess[0]);
        playConstant(1);
    }

    private void play(final int pFrameNo) {
        mImageView.postDelayed(new Runnable() {
            public void run() {
                mImageView.setImageResource(mFrameRess[pFrameNo]);
                if (pFrameNo == mLastFrameNo) {
                    play(0);
                } else {
                    play(pFrameNo + 1);
                }
            }
        }, mDurations[pFrameNo]);
    }

    private void playConstant(final int pFrameNo) {
        mImageView.postDelayed(new Runnable() {
            public void run() {
                mImageView.setImageResource(mFrameRess[pFrameNo]);

                if (pFrameNo == mLastFrameNo) {
                    playConstant(0);
                } else {
                    playConstant(pFrameNo + 1);
                }
            }
        }, pFrameNo == mLastFrameNo && mBreakDelay > 0 ? mBreakDelay : mDuration);
    }
}