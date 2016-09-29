package fretx.version4.amazon;

import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

public class SongfileTransferListener implements TransferListener {

    @Override public void onError(int id, Exception e) {
        Log.e("MainActivity", "onError: " + id, e);
    }

    @Override public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        Log.d("MainActivity", String.format("onProgressChanged: %d, total: %d, current: %d", id, bytesTotal, bytesCurrent));
        if(bytesTotal==bytesCurrent) onComplete();
    }

    @Override public void onStateChanged(int id, TransferState state) {
        Log.d("MainActivity", "onStateChanged: " + id + ", " + state);
    }

    public void onComplete() { }

}
