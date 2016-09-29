package fretx.version4.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fretx.version4.Config;
import fretx.version4.Constants;
import fretx.version4.CustomGridViewAdapter;
import fretx.version4.R;
import fretx.version4.SongItem;
import fretx.version4.Util;
import fretx.version4.activities.main.MainActivity;
import fretx.version4.amazon.Amazon;
import fretx.version4.youtube.Youtube;


public class PlayFragmentSearchList extends Fragment implements SearchView.OnQueryTextListener {

    private Context appContext;
    private MainActivity activity;

    private View rootView = null;
    private ImageView refresh;
    public SearchView svNews = null;
    public GridView lvListNews = null;

    public ArrayList<SongItem> mainData;
    public ArrayList<SongItem> Data;

    String input;

    public PlayFragmentSearchList() {
        mainData = new ArrayList<SongItem>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();
        appContext = activity.getApplicationContext();
        mainData = new ArrayList<SongItem>();

        rootView = inflater.inflate(R.layout.play_fragment_search_list, container, false);

        svNews = (SearchView) rootView.findViewById(R.id.svSongs);
        svNews.setOnQueryTextListener(this);

        refresh = (ImageView) rootView.findViewById(R.id.fresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });

        if (Constants.refreshed) {
            Data = Constants.savedData;
            lvListNews = (GridView)rootView.findViewById(R.id.lvSongList);
            lvListNews.setAdapter(new CustomGridViewAdapter(activity, R.layout.play_fragment_search_list_row_item, Data));
            //stop_led();
        }
        else {
            mainData = new ArrayList<SongItem>();
            lvListNews = (GridView)rootView.findViewById(R.id.lvSongList);
            lvListNews.setAdapter(new CustomGridViewAdapter(activity, R.layout.play_fragment_search_list_row_item, mainData));
            initData();
            Constants.refreshed = true;
        }
        return rootView;
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
        stop_led();
    }

    public void initData(){
        mainData = new ArrayList<SongItem>();
        String accessFolder = Amazon.checkS3Access(activity);
        input = accessFolder;
        new GetFileListTask().execute(accessFolder);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        lvListNews.setVisibility(View.VISIBLE);
        if (!query.equals(null)){
            ArrayList<SongItem> arrResultTemp = new ArrayList<SongItem>();
            for (int i = 0; i < mainData.size(); i ++){
                if(mainData.get(i).songName.toLowerCase().contains(query.toLowerCase())){
                    arrResultTemp.add(mainData.get(i));
                }
                lvListNews.setAdapter(new CustomGridViewAdapter(activity,R.layout.play_fragment_search_list_row_item, arrResultTemp));
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        lvListNews.setVisibility(View.VISIBLE);
        if (newText.equals(null)){
            lvListNews.setAdapter(new CustomGridViewAdapter(activity,R.layout.play_fragment_search_list_row_item, mainData));
        }else{
            ArrayList<SongItem> arrResultTemp = new ArrayList<SongItem>();
            for (int i = 0; i < mainData.size(); i ++){
                if(mainData.get(i).songName.toLowerCase().contains(newText.toLowerCase())) {
                    arrResultTemp.add(mainData.get(i));
                }
                lvListNews.setAdapter(new CustomGridViewAdapter(activity,R.layout.play_fragment_search_list_row_item, arrResultTemp));
            }
        }
        return false;
    }

    private class GetFileListTask extends AsyncTask<String, Void, Void> {

        private List<S3ObjectSummary> s3ObjList;  // The list of objects we find in the S3 bucket
        private ProgressDialog        dialog;     // A dialog to let the user know we are retrieving the files

        @Override protected void onPreExecute() { dialog = ProgressDialog.show(activity, getString(R.string.refreshing), getString(R.string.please_wait) ); }

        @Override protected Void doInBackground(String... inputs) {
            AmazonS3Client s3 = Amazon.getS3Client(activity);
            s3ObjList = s3.listObjects(inputs[0]).getObjectSummaries();
            mainData.clear();
            for (S3ObjectSummary summary : s3ObjList) {
                String s3key = summary.getKey();
                String SongfilePath = activity.getFilesDir().toString() + "/" + s3key;
                Boolean SongfileExists = new File(SongfilePath).isFile();

                //if( ! SongfileExists ) { Amazon.downloadFile(activity, inputs[0], summary.getKey()); }

                Amazon.downloadFile(activity, inputs[0], s3key);

                String   songname     = Amazon.getSongname   ( summary );
                String   youtubeKey   = Amazon.getYoutubeKey ( summary );
                Drawable thumbnail    = Youtube.getThumbnail ( youtubeKey );

                if(thumbnail == null) { thumbnail = ContextCompat.getDrawable( appContext, R.drawable.defaultthumb ); }

                mainData.add( new SongItem(songname, youtubeKey, s3key, thumbnail ));

            }
            Constants.savedData = mainData;
            return null;
        }

        @Override protected void onPostExecute(Void result) {
            dialog.dismiss();
            lvListNews.setAdapter(new CustomGridViewAdapter(activity, R.layout.play_fragment_search_list_row_item, mainData));
        }
    }

    public void stop_led() { if(Config.bBlueToothActive) { Util.stopViaData(); } }
}