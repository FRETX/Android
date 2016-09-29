package fretx.version4.activities.splash;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import fretx.version4.activities.main.MainActivity;
import fretx.version4.R;
import fretx.version4.bluetooth.Bluetooth;
import fretx.version4.callbacks.Callbacks;
import fretx.version4.fretx_api.FretxApi;

public class SplashActivity extends Activity {

    private TextView    mTvAppName;
	private ProgressBar mProgressBar;

	private boolean     isRunning;
    private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) { super.onCreate(null); setupUI(); }

    @Override
    protected void onResume() { super.onResume(); runAnimations(); }

    @Override
    protected void onDestroy() { super.onDestroy(); mHandler.removeCallbacksAndMessages(null); }

    private void setupUI() {
        this.setContentView(R.layout.splash_activity);

        mTvAppName   = (TextView)    findViewById(R.id.tv_app_name);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        mTvAppName.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void runAnimations() {
        if(isRunning) return;
        isRunning = true;
        Bluetooth.Initialize();
        Bluetooth.Enable(this);
        FretxApi.get("songs/list");
        animateLogo();
    }

    private void animateLogo() {
        ImageView logo = (ImageView) findViewById(R.id.img_logo);
        logo.setRotationY(-180);

        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        ViewPropertyAnimator animator = logo.animate().rotationY(0).setDuration(1000).setInterpolator(interpolator);

        animator.setListener( new AnimatorListener() {
            @Override public void onAnimationStart  (Animator animation) {}
            @Override public void onAnimationRepeat (Animator animation) {}
            @Override public void onAnimationEnd    (Animator animation) { logoAnimationComplete(); }
            @Override public void onAnimationCancel (Animator animation) { logoAnimationComplete(); }
        });

        animator.start();
    }

    private void logoAnimationComplete() {
        mProgressBar.setVisibility(View.VISIBLE);
        mTvAppName.setVisibility(View.VISIBLE);
        mHandler.postDelayed( new Runnable() { @Override public void run() { loadingComplete(); } }, 3000);
    }

    private void loadingComplete() {
        Intent mIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mIntent);
        finish();
    }

    protected void onActivityResult(int request, int result, Intent data) {
        Callbacks.onActivityResult(request, result, data);
    }
}
