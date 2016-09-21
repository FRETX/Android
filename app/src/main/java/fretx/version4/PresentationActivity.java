package fretx.version4;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import java.io.File;

/**
 * Created by Misho on 2/4/2016.
 */

public class PresentationActivity extends Activity
{

    ObservableVideoView vvMain;
    MediaController mc;
    Uri videoUri;
    Button btGoMenu;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        new GetHWAccessFile().execute();

        setContentView(R.layout.presentation_activity);


        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_intro);



        vvMain = (ObservableVideoView)findViewById(R.id.vvMain);
        vvMain.setVideoURI(videoUri);
        mc = new MediaController(vvMain.getContext());
        mc.setMediaPlayer(vvMain);
//        mc.setAnchorView(llMain);

        vvMain.setMediaController(mc);

        btGoMenu = (Button)findViewById(R.id.btGoMenu);
        btGoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent  = new Intent(PresentationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
    }

    private class GetHWAccessFile extends AsyncTask<Void, Void, Void> {
        // A dialog to let the user know we are retrieving the files
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(PresentationActivity.this,
                    getString(R.string.refreshing),
                    getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... inputs) {
            // Queries files in the bucket from S3.
            File hwaccessFile = new File(PresentationActivity.this.getFilesDir().toString()+ "/" + Constants.HW_BUCKET_MAPPING_FILE);
            if(!hwaccessFile.isFile()) {
                TransferObserver observer = Util.downloadFile(PresentationActivity.this, Constants.BUCKET_NAME, Constants.HW_BUCKET_MAPPING_FILE);
                observer.setTransferListener(new DownloadListener());
                while (true) {
                    if (TransferState.COMPLETED.equals(observer.getState())
                            || TransferState.FAILED.equals(observer.getState())) {
                        break;
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            vvMain.start();
        }
    }

    /*
     * A TransferListener class that can listen to a download task and be
     * notified when the status changes.
     */
    private class DownloadListener implements TransferListener {
        // Simply updates the list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e("PresentationActivity", "onError: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d("PresentationActivity", String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState state) {
            Log.d("PresentationActivity", "onStateChanged: " + id + ", " + state);
        }
    }
}
