package fretx.version4;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import fretx.version4.activities.main.MainActivity;

public class LearnFragmentButton extends Fragment {

    MainActivity mActivity;
    View rootView = null;

    Button btExerciseOne;
    Button btExerciseTwo;
    Button btExerciseThree;
    Button btExerciseFour;
    Map userHistory = new HashMap();

    public LearnFragmentButton(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity)getActivity();

        rootView = inflater.inflate(R.layout.learn_fragment_buttons, container, false);
        btExerciseOne = (Button)rootView.findViewById(R.id.btExerciseOne);
        btExerciseOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.learn_container, new LearnFragmentOne());
                fragmentTransaction.commit();

            }
        });
        btExerciseTwo = (Button)rootView.findViewById(R.id.btExerciseTwo);
        btExerciseTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.learn_container, LearnFragmentEx.newInstance(2));
                fragmentTransaction.commit();

            }
        });
        btExerciseThree = (Button)rootView.findViewById(R.id.btExerciseThree);
        btExerciseThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.learn_container, LearnFragmentEx.newInstance(3));
                fragmentTransaction.commit();

            }
        });
        btExerciseFour = (Button)rootView.findViewById(R.id.btExerciseFour);
        btExerciseFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.learn_container, LearnFragmentEx.newInstance(4));
                fragmentTransaction.commit();

            }
        });

        btExerciseTwo.setEnabled(true);
        btExerciseThree.setEnabled(true);
        btExerciseFour.setEnabled(true);

        /*userHistory = Util.checkUserHistory(mActivity);
        int highestExercise = 0;
        if(userHistory != null){
            highestExercise = (Integer)userHistory.get("highestExercise");
        }
        if(highestExercise < 1){
            btExerciseTwo.setEnabled(false);
            btExerciseTwo.setBackgroundColor(Color.GRAY);
        }
        if(highestExercise < 2){
            btExerciseThree.setEnabled(false);
            btExerciseThree.setBackgroundColor(Color.GRAY);
        }
        if(highestExercise < 3){
            btExerciseFour.setEnabled(false);
            btExerciseFour.setBackgroundColor(Color.GRAY);
        }*/
        return rootView;
        //return null;
    }
}