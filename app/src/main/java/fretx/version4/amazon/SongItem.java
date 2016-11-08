package fretx.version4.amazon;

import android.graphics.drawable.Drawable;

public class SongItem {
    public String songName;
    public String songURl;
    public String songTxt;
    public Drawable image;

    public SongItem( String name, String url, String txt, Drawable img ) {
        songName = name;
        songURl  = url;
        songTxt  = txt;
        image    = img;
    }
}
