package fretx.version4.amazon;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.File;

import fretx.version4.Constants;
import fretx.version4.Util;

public final class Amazon {

    private static AmazonS3Client                     S3Client;
    private static TransferUtility                    transferUtil;
    private static CognitoCachingCredentialsProvider  CredProvider;

    public static String checkS3Access(Context context) {      // get permitted S3 folder name
        String macAddress = Util.getMacFromUserFile(context);
        return Util.getFolderNameFromAccessFile(context, macAddress);
    }

    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (CredProvider != null) return CredProvider;
        Context appContext = context.getApplicationContext();
        CredProvider = new CognitoCachingCredentialsProvider( appContext, Constants.COGNITO_POOL_ID, Regions.EU_WEST_1 );
        CredProvider.refresh();
        return CredProvider;
    }

    public static AmazonS3Client getS3Client(Context context) {
        if (S3Client != null) return S3Client;
        S3Client = new AmazonS3Client(getCredProvider(context));
        return S3Client;
    }

    public static TransferUtility getTransferUtility(Context context) {
        if (transferUtil != null) return transferUtil;
        Context appContext = context.getApplicationContext();
        transferUtil = new TransferUtility( getS3Client(appContext), appContext );
        return transferUtil;
    }

    public static TransferObserver downloadFile(Context context, String bucketName, String fileName) {
        File file = new File(context.getFilesDir().toString() + "/" + fileName);
        TransferUtility transferUtility = getTransferUtility(context);
        return transferUtility.download(bucketName, fileName, file);
    }

    public static String getYoutubeKey( S3ObjectSummary summary ) {
        return summary.getKey().split("\\.")[1];
    }

    public static String getSongname( S3ObjectSummary summary ) {
        return summary.getKey().split("\\.")[0];
    }

    public static File getSongfile(Context context, String name) {
        return new File(context.getFilesDir().toString() + "/" + name);
    }
}
