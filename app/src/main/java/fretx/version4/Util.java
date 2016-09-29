package fretx.version4;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fretx.version4.bluetooth.BluetoothActivity;

public final class Util {
    private Util() { }

    public static String getBeforeComma(String txt) {
        if(txt == null) return null;
        return txt.substring(0,txt.indexOf(","));
    }

    public static String getAfterComma(String txt) {
        if(txt == null) return null;
        return txt.substring((txt.indexOf(",")+1), txt.length());
    }

    public static String getMacFromUserFile ( Context context ) {
        try {
            String path = context.getFilesDir().toString() + "/" + Constants.USER_INFO_FILE;
            File userInfoFile = new File(path);
            if( ! userInfoFile.isFile() ) return Constants.NO_BT_DEVICE;
            FileReader fr = new FileReader(userInfoFile);
            BufferedReader br = new BufferedReader(fr);
            return getAfterComma(br.readLine());
        }   catch(Exception e) { e.printStackTrace(); return Constants.NO_BT_DEVICE; }
    }

    public static String getFolderNameFromAccessFile ( Context context, String MAC ) {
        try {
            String path = context.getFilesDir().toString() + "/" + Constants.HW_BUCKET_MAPPING_FILE;
            File hwAccessFile = new File(path);
            if( ! hwAccessFile.isFile() ) return Constants.NO_BT_BUCKET;

            String         line;
            FileReader     fr = new FileReader(hwAccessFile);
            BufferedReader br = new BufferedReader(fr);
            while( ( line = br.readLine() ) != null ) {
                if( MAC == getBeforeComma(line) ) return getAfterComma(line);
            }

            return Constants.NO_BT_BUCKET;
        }   catch ( Exception e ) { e.printStackTrace(); return Constants.NO_BT_BUCKET; }
    }


    public static int score(int timer){
        int score;
             if (timer < 40)  score = 5;
        else if (timer < 60)  score = 4;
        else if (timer < 80)  score = 3;
        else if (timer < 100) score = 2;
        else                  score = 1;
        return score;
    }



    /*public static int updateUserHistory(Context context, int currentLesson, int timer) throws IOException {
        try {
            File userHistoryFile = new File(context.getFilesDir().toString()+ "/" + Constants.USER_HISTORY_FILE);
            int highestScore = 0;
            int score = Util.score(timer);
            String line = "";
            StringBuffer sb = new StringBuffer();
            boolean scoreRecorded = false;

            if(userHistoryFile.isFile()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(userHistoryFile));
                    while ((line = br.readLine()) != null) {
                        if (Config.strUserID.equals(line.split(",")[0]) && line.split(",").length > 1) {
                            if (currentLesson != Integer.parseInt(line.split(",")[1])) {
                                sb.append(line);
                                sb.append('\n');
                            }else{
                                if(score > Integer.parseInt(line.split(",")[2])){
                                    sb.append(Config.strUserID);
                                    sb.append(',');
                                    sb.append(Integer.toString(currentLesson));
                                    sb.append(',');
                                    sb.append(Integer.toString(score));
                                    sb.append('\n');
                                    highestScore = score;
                                }else{
                                    sb.append(line);
                                    sb.append('\n');
                                    highestScore = Integer.parseInt(line.split(",")[2]);
                                }
                                scoreRecorded = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(!scoreRecorded){
                sb.append(Config.strUserID);
                sb.append(',');
                sb.append(Integer.toString(currentLesson));
                sb.append(',');
                sb.append(Integer.toString(score));
                sb.append('\n');
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(userHistoryFile));
            bw.write(sb.toString());
            bw.flush();
            return highestScore;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }*/

    /*public static Map checkUserHistory(Context context){
        File userHistoryFile = new File(context.getFilesDir().toString()+ "/" + Constants.USER_HISTORY_FILE);
        Map userHistory = new HashMap();
        int totalScore = 0;
        int highestExercise = 0;
        String line = "";

        if(userHistoryFile.isFile()){
            try {
                BufferedReader br = new BufferedReader(new FileReader(userHistoryFile));
                while((line = br.readLine()) != null){
                    if(Config.strUserID.equals(line.split(",")[0]) && line.split(",").length > 1){
                        highestExercise = (highestExercise > Integer.parseInt(line.split(",")[1]))?highestExercise:Integer.parseInt(line.split(",")[1]);
                        totalScore = totalScore + Integer.parseInt(line.split(",")[2]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userHistory.put("totalScore", totalScore);
        userHistory.put("highestExercise", highestExercise);

        return userHistory;
    }*/

    public static Map<Integer, Exercise> loadJSONFromAsset(Context ctx) {
        Map<Integer, Exercise> exMap = new HashMap<Integer, Exercise>();
        String json = null;
        try {
            InputStream is = ctx.getResources().openRawResource(R.raw.exercises);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            //JSONObject obj = new JSONObject(json);
            //JSONArray m_jArry = obj.getJSONArray("exercises");
            JSONArray m_jArry = new JSONArray(json);

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Exercise ex = new Exercise();
                ex.exId = jo_inside.getInt("id");
                ex.videoRes = jo_inside.getString("video");
                ex.nextExId = jo_inside.getInt("nextId");
                ex.title = jo_inside.getString("title");
                JSONArray labelsArray = jo_inside.getJSONArray("labels");
                ex.labels = new String[labelsArray.length()];
                for(int j=0; j < labelsArray.length(); j++){
                    ex.labels[j] = labelsArray.get(j).toString();
                }
                exMap.put(jo_inside.getInt("id"), ex);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return exMap;
    }

    public static ArrayList<NoteItem> getNoteItems(Context context, String[] noteLabels){
        InputStream ins = context.getResources().openRawResource(R.raw.notemidimapping);
        String line = "";
        ArrayList<NoteItem> noteItems = new ArrayList<NoteItem>();
        Map<String, NoteItem> noteItemMap = new HashMap<String, NoteItem>();

        if(ins != null){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(ins));
                while((line = br.readLine()) != null){
                    String[] temp = line.split(";");
                    NoteItem tempNote = new NoteItem();
                    tempNote.stringNumber = temp[0];
                    tempNote.fretNumber = temp[1];
                    tempNote.note = temp[2];
                    tempNote.noteMidi = Integer.parseInt(temp[3]);
                    tempNote.ledArray = temp[4];
                    noteItemMap.put(tempNote.stringNumber + " " + tempNote.fretNumber, tempNote);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < noteLabels.length; i++){
            if(noteItemMap.get(noteLabels[i]) != null){
                noteItems.add(noteItemMap.get(noteLabels[i]));
            }
        }

        return noteItems;
    }

    /*
     * Fills in the map with information in the observer so that it can be used
     * with a SimpleAdapter to populate the UI
     */
    public static void fillMap(Map<String, Object> map, TransferObserver observer, boolean isChecked) {
        int progress = (int) ((double) observer.getBytesTransferred() * 100 / observer
                .getBytesTotal());
        map.put("id", observer.getId());
        map.put("checked", isChecked);
        map.put("fileName", observer.getAbsoluteFilePath());
        map.put("progress", progress);
        map.put("bytes",
                getBytesString(observer.getBytesTransferred()) + "/"
                        + getBytesString(observer.getBytesTotal()));
        map.put("state", observer.getState());
        map.put("percentage", progress + "%");
    }

    /**
     * Converts number of bytes into proper scale.
     *
     * @param bytes number of bytes to be converted.
     * @return A string that represents the bytes in a proper scale.
     */
    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[] {
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0;; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

    ///Read the text file from resource(Raw) and divide by end line mark('\n")
    public static String readRawTextFile(Context ctx, String txtFile) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(ctx.getFilesDir().toString()+ "/" + txtFile));
            StringBuilder text = new StringBuilder();

            if(inputStream != null) {
                InputStreamReader inputreader = new InputStreamReader(inputStream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;

                try {
                    while ((line = buffreader.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return text.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void setDefaultValues(Boolean[] bArray)
    {
        for (int i = 0; i < bArray.length; i ++){
            bArray[i] = false;
        }
    }
    public static byte[] str2array(String string){
        String strSub = string.replaceAll("[{}]", "");
        String[] parts = strSub.split(",");
        byte[] array = new byte[parts.length];
        for (int i = 0; i < parts.length; i ++)
        {
            array[i] = Byte.valueOf(parts[i]);
        }
        return array;
    }
    public static void startViaData(byte[] array) {
        if(Config.bBlueToothActive ==  true) {
            BluetoothActivity.mHandler.obtainMessage(BluetoothActivity.FRET, array).sendToTarget();
        }
    }

    public static void stopViaData() {
        if(Config.bBlueToothActive) {
            byte[] array = new byte[]{0};
            BluetoothActivity.mHandler.obtainMessage(BluetoothActivity.FRET, array).sendToTarget();
        }
    }

}
