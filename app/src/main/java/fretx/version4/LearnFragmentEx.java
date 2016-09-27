package fretx.version4;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Map;

public class LearnFragmentEx extends Fragment {

    static ObservableVideoView vvMain1;
    MediaController mc;
    private Handler mCurTimeShowHandler = new Handler();

    RelativeLayout llMain;
    TextView tvInstruction;

    MainActivity mActivity;

    View rootView = null;

    Button   btLearned;
    Button   btPlayReplay;
    TextView tvTitle;
    Exercise ex;

    Uri videoUri;

    int durationTime = 0;               ///Video duration

    int nReplay = 0;

    public LearnFragmentEx(){

    }

    public static LearnFragmentEx newInstance(int exId) {
        LearnFragmentEx myFragment = new LearnFragmentEx();

        Bundle args = new Bundle();
        args.putInt("exId", exId);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity)getActivity();
        rootView = inflater.inflate(R.layout.learn_fragment_play, container, false);
        initUI();       ///Init UI(textView, VideoView....)
        return rootView;
    }

    private void initUI() {
        Map<Integer, Exercise> exMap = Util.loadJSONFromAsset(mActivity);
        ex = exMap.get(getArguments().getInt("exId"));

        btLearned = (Button)rootView.findViewById(R.id.btLearned);
        btLearned.setVisibility(View.INVISIBLE);
        btLearned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.learn_container, LearnFragmentNotesEx.newInstance(ex.exId, ex.title, ex.nextExId, ex.labels));
                fragmentTransaction.commit();
            }
        });

        llMain = (RelativeLayout) rootView.findViewById(R.id.llVideoView);
        tvInstruction = (TextView) rootView.findViewById(R.id.tvInstruction);
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvTitle.setText(ex.title);

        if(ex != null && ex.videoRes != null && !"null".equals(ex.videoRes)) {
            tvInstruction.setVisibility(View.INVISIBLE);
            videoUri = Uri.parse("android.resource://" + mActivity.getPackageName() + "/raw/" + ex.videoRes);

            vvMain1 = (ObservableVideoView) rootView.findViewById(R.id.vvMain);
            vvMain1.setVideoURI(videoUri);
            mc = new MediaController(vvMain1.getContext());
            mc.setMediaPlayer(vvMain1);

            vvMain1.setMediaController(mc);

            vvMain1.start();
            durationTime = vvMain1.getDuration(); //Get video duration.

            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    {
                        if (!vvMain1.isPlaying()) {
                            startButton();
                        }
                        mCurTimeShowHandler.postDelayed(this, 1);
                    }
                }
            };
            mCurTimeShowHandler.post(runnable);
            btPlayReplay = (Button) rootView.findViewById(R.id.btPlayReplay);
            btPlayReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btPlayReplay.setVisibility(View.INVISIBLE);
                    vvMain1.start();
                }
            });
        }else{
            llMain.setVisibility(View.INVISIBLE);
            tvInstruction.setText("Let's learn how to play " + ex.title + "!");
            btLearned.setVisibility(View.VISIBLE);
        }

    }
    public void startButton(){
        btLearned.setVisibility(View.VISIBLE);
        //\if (nReplay != 0) {
            btPlayReplay.setVisibility(View.INVISIBLE);
        //    btPlayReplay.setText("Replay");
        //}
        //nReplay = 1;
    }
    public static VideoView getVideoView(){
        return vvMain1;
    }
}
