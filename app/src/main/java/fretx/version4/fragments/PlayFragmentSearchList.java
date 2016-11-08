package fretx.version4.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import java.util.ArrayList;

import fretx.version4.Config;
import fretx.version4.Constants;
import fretx.version4.CustomGridViewAdapter;
import fretx.version4.R;
import fretx.version4.amazon.SongItem;
import fretx.version4.Util;
import fretx.version4.activities.main.MainActivity;
import fretx.version4.amazon.Amazon;


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

    public void updateList() {
        lvListNews.setAdapter( new CustomGridViewAdapter(activity, R.layout.play_fragment_search_list_row_item, mainData) );
    }

    public void stop_led() { if(Config.bBlueToothActive) { Util.stopViaData(); } }
}