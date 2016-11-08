package fretx.version4.amazon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fretx.version4.Constants;
import fretx.version4.R;
import fretx.version4.youtube.Youtube;

public class GetSongfileListTask extends AsyncTask<String, Void, ArrayList<SongItem>> {

    private Context               context;
    private List<S3ObjectSummary> s3ObjList;  // The list of objects we find in the S3 bucket
    private ArrayList<SongItem>   songList;

    public GetSongfileListTask(Context ctx) { context = ctx; }

    @Override protected ArrayList<SongItem> doInBackground(String... inputs) {

        String s3FolderName = Amazon.checkS3Access(context);
        AmazonS3Client s3   = Amazon.getS3Client(context);
        s3ObjList           = s3.listObjects(s3FolderName).getObjectSummaries();
        songList            = new ArrayList<SongItem>();

        for (S3ObjectSummary summary : s3ObjList) {

            String  s3key          = summary.getKey();
            String  SongfilePath   = context.getFilesDir().toString() + "/" + s3key;
            Boolean SongfileExists = new File(SongfilePath).isFile();

            //if( ! SongfileExists ) { Amazon.downloadFile(context, s3FolderName, summary.getKey()); }

            TransferObserver observer = Amazon.downloadFile(context, s3FolderName, s3key);
            observer.setTransferListener( new SongfileTransferListener() );
            Amazon.waitForDownload(observer);

            String   songname     = Amazon.getSongname   ( summary );
            String   youtubeKey   = Amazon.getYoutubeKey ( summary );
            Drawable thumbnail    = Youtube.getThumbnail ( youtubeKey );

            if(thumbnail == null) { thumbnail = ContextCompat.getDrawable( context, R.drawable.defaultthumb ); }

            songList.add( new SongItem(songname, youtubeKey, s3key, thumbnail ));

        }
        Constants.savedData = songList;
        Constants.refreshed = true;
        return songList;
    }

    @Override protected void onPostExecute(ArrayList<SongItem> result) { }
}