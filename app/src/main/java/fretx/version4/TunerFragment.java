package fretx.version4;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

import fretx.version4.activities.main.MainActivity;


public class TunerFragment extends Fragment {
	public static final String TAG = "AndroidTuner";
	private PdUiDispatcher dispatcher;
	private MainActivity mActivity;
	View rootView = null;

	private Button eButton;
	private Button aButton;
	private Button dButton;
	private Button gButton;
	private Button bButton;
	private Button eeButton;
	private TextView pitchLabel;
	private PitchView pitchView;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (MainActivity)getActivity();
		initSystemServices();
		mActivity.bindService(new Intent(mActivity, PdService.class), pdConnection, mActivity.BIND_AUTO_CREATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.tuner_fragment_new, container, false);
		initGui();      ///Init UI(textView, VideoView....)
		return rootView;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mActivity.unbindService(pdConnection);
	}

	private void initGui() {
		eButton = (Button) rootView.findViewById(R.id.e_button);
		eButton.setOnClickListener(tunerListener);
		aButton = (Button) rootView.findViewById(R.id.a_button);
		aButton.setOnClickListener(tunerListener);
		dButton = (Button) rootView.findViewById(R.id.d_button);
		dButton.setOnClickListener(tunerListener);
		gButton = (Button) rootView.findViewById(R.id.g_button);
		gButton.setOnClickListener(tunerListener);
		bButton = (Button) rootView.findViewById(R.id.b_button);
		bButton.setOnClickListener(tunerListener);
		eeButton = (Button) rootView.findViewById(R.id.ee_button);
		eeButton.setOnClickListener(tunerListener);
		pitchLabel = (TextView) rootView.findViewById(R.id.pitch_label);
		pitchView = (PitchView) rootView.findViewById(R.id.pitch_view);
		pitchView.setCenterPitch(40);
		pitchLabel.setText("Low E-String");
	}

	private void  initPd() throws IOException {
		// Configure the audio glue
		AudioParameters.init(mActivity);
		int sampleRate = AudioParameters.suggestSampleRate();
		PdAudio.initAudio(sampleRate, 1, 2, 8, true);
		//pdService.initAudio(sampleRate, 1, 2, 10.0f);
		start();

		// Create and install the dispatcher
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, final float x) {
				pitchView.setCurrentPitch(x);
			}
		});
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

	private void triggerNote(int n) {
		PdBase.sendFloat("midinote", n);
		PdBase.sendBang("trigger");
		pitchView.setCenterPitch(n);
	}

	private View.OnClickListener tunerListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.e_button:
					triggerNote(40); // E (low) is MIDI note 40.
					pitchLabel.setText("Low E-String");
					break;
				case R.id.a_button:
					triggerNote(45); // A is MIDI note 45.
					pitchLabel.setText("A-String");
					break;
				case R.id.d_button:
					triggerNote(50); // D is MIDI note 50.
					pitchLabel.setText("D-String");
					break;
				case R.id.g_button:
					triggerNote(55); // G is MIDI note 55.
					pitchLabel.setText("G-String");
					break;
				case R.id.b_button:
					triggerNote(59); // B is MIDI note 59.
					pitchLabel.setText("B-String");
					break;
				case R.id.ee_button:
					triggerNote(64); // E (high) is MIDI note 64.
					pitchLabel.setText("High E-String");
					break;
			}
		}
	};
}