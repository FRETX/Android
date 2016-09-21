package fretx.version4;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LearnFragmentNotesExResult extends DialogFragment {
	public Button btnYes,btnNo,btnRedo;
	static String DialogBoxTitle;
	MainActivity mActivity;

	private LeftNavAdapter adapter;

	public TextView tvText;


	//---empty constructor required
	public LearnFragmentNotesExResult(){
		
	}

	public static LearnFragmentNotesExResult newInstance(int exId, String title, int nextExId, int score, int highestScore) {
		LearnFragmentNotesExResult myFragment = new LearnFragmentNotesExResult();

		Bundle args = new Bundle();
		args.putInt("exId", exId);
		args.putString("title", title);
		args.putInt("nextExId", nextExId);
		args.putInt("score", score);
		args.putInt("highestScore", highestScore);
		myFragment.setArguments(args);

		return myFragment;
	}

	//---set the title of the dialog window---
	public void setDialogTitle(String title) {
		DialogBoxTitle= title;
	}
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState ) {
		mActivity = (MainActivity)getActivity();

		/*TextView totalScore = (TextView) mActivity.findViewById(R.id.tvPopularity);
		Map userHistory = Util.checkUserHistory(mActivity);
		if(userHistory != null){
			totalScore.setText("Points: " + userHistory.get("totalScore"));
		}*/

		View view= inflater.inflate(R.layout.learn_fragment_dialog, container);
		//---get the Button views---
		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnNo = (Button) view.findViewById(R.id.btnNo);
		btnRedo = (Button) view.findViewById(R.id.btnRedo);
		if(getArguments().getInt("nextExId") < 0) {
			btnNo.setVisibility(View.INVISIBLE);
		}

		tvText = (TextView) view.findViewById(R.id.tvTextView);
		tvText.setText("You just learned how to play " + getArguments().getString("title") + "!"
				+ "\n\nYou scored : " + getArguments().getInt("score")
				+ "\n\nHighest score : " + getArguments().getInt("highestScore"));

		// Button listener
		btnYes.setOnClickListener(btnListener);
		btnNo.setOnClickListener(btnListener);
		btnRedo.setOnClickListener(btnListener);
		
		//---set the title for the dialog
		getDialog().setTitle(DialogBoxTitle);
		
		return view;
	}
	
	//---create an anonymous class to act as a button click listener
	private OnClickListener btnListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if (((Button) v).getText().toString().equals("Go to Learn Menu")){

				FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
				android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.learn_container, new LearnFragmentButton());
				fragmentTransaction.commit();

			}else if(((Button) v).getText().toString().equals("Replay Exercise")) {

				FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
				android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.learn_container, LearnFragmentEx.newInstance(getArguments().getInt("exId")));
				fragmentTransaction.commit();
			}else if(((Button) v).getText().toString().equals("Next Exercise")) {

				FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
				android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.learn_container, LearnFragmentEx.newInstance(getArguments().getInt("nextExId")));
				fragmentTransaction.commit();
			}
			dismiss();
		}
	};
}
