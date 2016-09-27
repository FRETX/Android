package fretx.version4;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.io.PdAudio;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class LearnFragmentNotesEx extends Fragment{
	public static final String TAG = "AndroidTuner";
	private PdUiDispatcher dispatcher;
	private MediaPlayer mediaPlayer;

/*	private final boolean LAUNCHANALYZER = true;

	private int nFlag = 0;*/

	Timer timer;
	MyTimerTask myTimerTask;
	int nCounter = 0;

	private ArrayList<Button> buttons = new ArrayList<Button>();


	private TextView tvTimeLapse;
	//int oldString = 1;

	int completedCount = 0;
	int newPitch = 0;
	//int notes[] = new int[]{41, 47, 51, 57, 60, 66};
	ArrayList<NoteItem> notes = new ArrayList<NoteItem>();
	String labels[];

	private PdService pdService = null;

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				initPd();
				loadPatch();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				mActivity.finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};

	private MainActivity mActivity;
	private View rootView;

	public LearnFragmentNotesEx(){

	}

	public static LearnFragmentNotesEx newInstance(int exId, String title, int nextExId, String[] labels) {
		LearnFragmentNotesEx myFragment = new LearnFragmentNotesEx();

		Bundle args = new Bundle();
		args.putInt("exId", exId);
		args.putString("title", title);
		args.putInt("nextExId", nextExId);
		args.putStringArray("labels", labels);
		myFragment.setArguments(args);

		return myFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		labels = getArguments().getStringArray("labels");
		notes = Util.getNoteItems(mActivity, getArguments().getStringArray("labels"));

		rootView = inflater.inflate(R.layout.learn_fragment_tuner, container, false);
		LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.linearLayout);

		for(int i = 0; i < 6; i++) {
			Button myButton = new Button(mActivity);
			myButton.setText(labels[i]);
			myButton.setTag(i + labels[i]);
			myButton.setWidth(200);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.addView(myButton, lp);
			buttons.add((Button) ll.findViewWithTag(i + labels[i]));

			if(i==0){
				buttons.get(i).setBackgroundColor(Color.GREEN);
			}else{
				buttons.get(i).setBackgroundColor(Color.BLUE);
			}
		}

		tvTimeLapse = (TextView)rootView.findViewById(R.id.tvTimeLapse);

		startTimer();
		return rootView;

    }

	public void startTimer(){
		if (timer != null){
			timer.cancel();
		}
		timer = new Timer();

		myTimerTask = new MyTimerTask();

		timer.schedule(myTimerTask, 1000, 1000);
	}

	private void  initPd() throws IOException {
		// Configure the audio glue
		AudioParameters.init(mActivity);
		int sampleRate = AudioParameters.suggestSampleRate();
		PdAudio.initAudio(sampleRate, 1, 2, 8, true);
		//pdService.initAudio(sampleRate, 1, 2, 5000f);

		ConnectThread connectThread = new ConnectThread(Util.str2array("{" + (6-newPitch) + ",0}"));
		connectThread.run();

		start();

		// Create and install the dispatcher
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, final float x) {
				float dx = (x - notes.get(newPitch).noteMidi) / 2;
				if (-0.2 < dx && dx < 0.2) {
					buttons.get(newPitch % 6).setBackgroundColor(Color.BLUE);
					if(newPitch == notes.size() - 1){
						completedCount++;
					}
					if(newPitch < notes.size() - 1) {
						newPitch++;
					}else{
						newPitch = 0;//rand.nextInt(notes.length);
					}
					buttons.get(newPitch % 6).setBackgroundColor(Color.GREEN);
					if(newPitch % 6 == 0){
						for(int j = newPitch; j < newPitch + 6; j++){
							if(j < labels.length) {
								buttons.get(j % 6).setText(labels[j]);
							}else{
								buttons.get(j % 6).setText("");
							}
						}
					}
					successPlayer();
					ConnectThread connectThread = new ConnectThread(Util.str2array(notes.get(newPitch).ledArray));
					connectThread.run();
				}
				/*if(completedCount == 2){
					timer.cancel();
					try {
						int highestScore = Util.updateUserHistory(mActivity, getArguments().getInt("exId"), nCounter);
						android.support.v4.app.FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
						LearnFragmentNotesExResult yesnoDialog = LearnFragmentNotesExResult.newInstance(getArguments().getInt("exId"),
								getArguments().getString("title"),
								getArguments().getInt("nextExId"),
								Util.score(nCounter), highestScore);
						yesnoDialog.setCancelable(true);
						yesnoDialog.setDialogTitle("Congrats");
						yesnoDialog.show(fragmentManager, "Yes/No Dialog");
						mActivity.unbindService(pdConnection);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}*/
			}
		});
	}

	public void successPlayer(){
		mediaPlayer.start();
	}

	private void start() {
		if (!pdService.isRunning()) {
			Intent intent = new Intent(mActivity,
					MainActivity.class);
			pdService.startAudio(intent, R.drawable.icon,
					"GuitarTuner", "Return to GuitarTuner.");
		}
	}

	private void loadPatch() throws IOException {
		File dir = mActivity.getFilesDir();
		IoUtils.extractZipResource(
				getResources().openRawResource(R.raw.tuner), dir, true);
		File patchFile = new File(dir, "tuner.pd");
		PdBase.openPatch(patchFile.getAbsolutePath());
	}

	private void initSystemServices() {
		TelephonyManager telephonyManager =
				(TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				if (pdService == null) return;
				if (state == TelephonyManager.CALL_STATE_IDLE) {
					start();
				} else {
					pdService.stopAudio();
				}
			}
		}, PhoneStateListener.LISTEN_CALL_STATE);
	}

	class MyTimerTask extends TimerTask {

		@Override
		public void run() {

			nCounter ++;

			mActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					tvTimeLapse.setText("" + nCounter / 60 + " : " + nCounter % 60);
				}
			});
		}

	}

	/////////////////////////////////BlueToothConnection/////////////////////////
	static private class ConnectThread extends Thread {
		byte[] array;
		public ConnectThread(byte[] tmp) {
			array = tmp;
		}

		public void run() {
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				Util.startViaData(array);
			} catch (Exception connectException) {
				Log.i(BluetoothClass.tag, "connect failed");
				// Unable to connect; close the socket and get out
				try {
					BluetoothClass.mmSocket.close();
				} catch (IOException closeException) {
					Log.e(BluetoothClass.tag, "mmSocket.close");
				}
				return;
			}
			// Do work to manage the connection (in a separate thread)
			if (BluetoothClass.mHandler == null)
				Log.v("debug", "mHandler is null @ obtain message");
			else
				Log.v("debug", "mHandler is not null @ obtain message");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity)getActivity();
		ConnectThread connectThread = new ConnectThread(Util.str2array("{0}"));
		connectThread.run();
		initSystemServices();
		mediaPlayer = MediaPlayer.create(mActivity, R.raw.success_sound);
		mActivity.bindService(new Intent(mActivity, PdService.class), pdConnection, mActivity.BIND_AUTO_CREATE);
	}

	@Override
	public void onResume() {
		super.onResume();
		mActivity = (MainActivity)getActivity();
		ConnectThread connectThread = new ConnectThread(Util.str2array("{0}"));
		connectThread.run();
		initSystemServices();
		mActivity.bindService(new Intent(mActivity, PdService.class), pdConnection, mActivity.BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ConnectThread connectThread = new ConnectThread(Util.str2array("{0}"));
		connectThread.run();
		//mActivity.unbindService(pdConnection);
	}
}