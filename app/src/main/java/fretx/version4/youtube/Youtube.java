package fretx.version4.youtube;

import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.net.URL;

public final class Youtube {

    public static Drawable getThumbnail( String key ){
        try{
            String path = "http://img.youtube.com/vi/" + key + "/0.jpg";
            InputStream is = (InputStream) new URL(path).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }catch (Exception e) {
            System.out.println("Exc="+e);
            return null;
        }
    }
}