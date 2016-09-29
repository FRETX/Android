package fretx.version4.activities.main;

import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import fretx.version4.bluetooth.BluetoothActivity;
import fretx.version4.Config;
import fretx.version4.LearnFragmentButton;
import fretx.version4.fragments.PlayFragmentSearchList;
import fretx.version4.R;
import fretx.version4.SlidingTabLayout;
import fretx.version4.Util;
import fretx.version4.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity
{
    private TextView        btState;
    private FragmentManager fragMgr;

    private int mCurrentPosition  = 0;
    private int mPreviousPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragMgr = getSupportFragmentManager();
        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showConnectionState();
    }

    @Override
    public void onBackPressed() {
        FragmentTransaction transaction = fragMgr.beginTransaction();

        if (mCurrentPosition == 0) {
            transaction.replace(R.id.play_container, new PlayFragmentSearchList());
        }
        else if (mCurrentPosition == 1){
            transaction.replace(R.id.learn_container, new LearnFragmentButton());
        }
        transaction.commit();
    }

    private void setupUI() {
        setContentView(R.layout.main_activity_back);
        SlidingTabLayout tabLayout = setupTabLayout();
        ViewPager pager = setupViewPager();
        tabLayout.setViewPager(pager);
        btState = setupBtState();
    }

    private ViewPager setupViewPager() {
        ViewPager pager;
        String titles[] = new String[]{ "Play", "Learn", "Chords", "Tuner"};
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(fragMgr, titles);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(viewPagerAdapter);
        return pager;
    }

    private SlidingTabLayout setupTabLayout() {
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        tabs.setCustomTabColorizer( new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
            mPreviousPosition = mCurrentPosition;
            mCurrentPosition = position;
            if (mPreviousPosition == 1) changeFragments(position);
            return Color.BLUE;
            }
        });
        return tabs;
    }

    private TextView setupBtState() {
        btState = (TextView) findViewById(R.id.tvConnectionState);
        btState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (!Config.bBlueToothActive) {
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(intent);
            }
            else {
                try                 { Util.stopViaData();  }
                catch (Exception e) { e.printStackTrace(); }
                new Handler().postDelayed(new Runnable() {
                        public void run() {
                   Config.bBlueToothActive = false;
                   showConnectionState();
                   BluetoothActivity.mBluetoothGatt.disconnect();
                    }
                    }, 200);
            }
            }
        });
        return btState;
    }

    public void showConnectionState() {
        if (Config.bBlueToothActive){
            btState.setText(R.string.connect);
            btState.setBackgroundColor(Color.GREEN);
        }
        else {
            btState.setText(R.string.disconnect);
            btState.setBackgroundColor(Color.RED);
        }
    }



    public void changeFragments(int position){
        if (position == 2 || position == 0){
            FragmentTransaction transaction = fragMgr.beginTransaction();
            transaction.replace(R.id.learn_container, new LearnFragmentButton());
            transaction.commit();
        }
    }

}
