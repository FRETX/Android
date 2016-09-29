package fretx.version4.amazon;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import java.io.File;

public class GetSongfileTask extends AsyncTask<String, Void, File> {

    private Context context;
    private ProgressDialog dialog;  // A dialog to let the user know we are retrieving the files

    public GetSongfileTask(Context ctx) { context = ctx; }

    @Override protected File doInBackground(String... inputs) {

        final String filename = inputs[0];
        String accessFolder = Amazon.checkS3Access(context);
        File songFile = Amazon.getSongfile(context, filename);
        if(songFile.isFile()) return songFile;

        TransferObserver observer = Amazon.downloadFile(context, accessFolder, inputs[0]);
        observer.setTransferListener( new SongfileTransferListener() );

        while (true) {
            TransferState state = observer.getState();
            if (state == TransferState.COMPLETED) break;
            if (state == TransferState.FAILED) break;
        }

        return Amazon.getSongfile(context, filename);
    }

    @Override protected void onPostExecute(File result) { }
}