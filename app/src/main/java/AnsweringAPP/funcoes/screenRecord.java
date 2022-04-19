//package AnsweringAPP.funcoes;
//
//import static android.app.Activity.RESULT_OK;
//import static androidx.core.app.ActivityCompat.startActivityForResult;
//import android.Manifest;
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.media.MediaScannerConnection;
//import android.media.projection.MediaProjectionManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.DrawableRes;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.AnsweringAPP.R;
//import com.hbisoft.hbrecorder.HBRecorder;
//import com.hbisoft.hbrecorder.HBRecorderListener;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.sql.Date;
//import java.text.SimpleDateFormat;
//import java.util.Locale;
//
//public class screenRecord(Context ctx) {
//
//    private static final int SCREEN_RECORD_REQUEST_CODE = 100;
//    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 101;
//    private static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = 102;
//    HBRecorder hbRecorder;
//    Button btnStart,btnStop;
//    boolean hasPermissions;
//    ContentValues contentValues;
//    ContentResolver resolver;
//    Uri mUri;
//    void test(Button btnStart, Button btnStop, Context ctx){
//        hbRecorder = new HBRecorder(ctx, (HBRecorderListener) ctx);
//        hbRecorder.setVideoEncoder("H264");
//
//        btnStart.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    //first check if permissions was granted
//                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO) && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE)) {
//                        hasPermissions = true;
//                    }
//                    if (hasPermissions) {
//
//                        startRecordingScreen();
//
//                    }
//                } else {
//                    //showLongToast("This library requires API 21>");
//                }
//            }
//        });
//        btnStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hbRecorder.stopScreenRecording();
//            }
//        });
//    }
//
//    @Override
//    public void HBRecorderOnStart(Context ctx) {
//        Toast.makeText(ctx, "Started", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void HBRecorderOnComplete(Context ctx) {
//        Toast.makeText(ctx, "Completed", Toast.LENGTH_SHORT).show();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            //Update gallery depending on SDK Level
//            if (hbRecorder.wasUriSet()) {
//                updateGalleryUri();
//            }else{
//                refreshGalleryFile();
//            }
//        }
//    }
//
//    @Override
//    public void HBRecorderOnError(int errorCode, String reason,Context ctx) {
//        Toast.makeText(ctx, errorCode+": "+reason, Toast.LENGTH_SHORT).show();
//    }
//    private void startRecordingScreen(Context ctx) {
//        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) ctx.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
//        startActivityForResult(permissionIntent, SCREEN_RECORD_REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SCREEN_RECORD_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                //Start screen recording
//                hbRecorder.startScreenRecording(data, resultCode, ctx);
//
//            }
//        }
//    }
//    //For Android 10> we will pass a Uri to HBRecorder
//    //This is not necessary - You can still use getExternalStoragePublicDirectory
//    //But then you will have to add android:requestLegacyExternalStorage="true" in your Manifest
//    //IT IS IMPORTANT TO SET THE FILE NAME THE SAME AS THE NAME YOU USE FOR TITLE AND DISPLAY_NAME
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void setOutputPath() {
//        String filename = generateFileName();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            resolver = ctx.getContentResolver();
//            contentValues = new ContentValues();
//            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "SpeedTest/" + "SpeedTest");
//            contentValues.put(MediaStore.Video.Media.TITLE, filename);
//            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
//            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
//            mUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
//            //FILE NAME SHOULD BE THE SAME
//            hbRecorder.setFileName(filename);
//            hbRecorder.setOutputUri(mUri);
//        }else{
//            createFolder();
//            hbRecorder.setOutputPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) +"/HBRecorder");
//        }
//    }
//    //Check if permissions was granted
//    private boolean checkSelfPermission(String permission, int requestCode) {
//        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
//            return false;
//        }
//        return true;
//    }
//    private void updateGalleryUri(Context ctx){
//        contentValues.clear();
//        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0);
//        ctx.getContentResolver().update(mUri, contentValues, null, null);
//    }
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void refreshGalleryFile() {
//        MediaScannerConnection.scanFile(this,
//                new String[]{hbRecorder.getFilePath()}, null,
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    public void onScanCompleted(String path, Uri uri) {
//                        Log.i("ExternalStorage", "Scanned " + path + ":");
//                        Log.i("ExternalStorage", "-> uri=" + uri);
//                    }
//                });
//    }
//    //Generate a timestamp to be used as a file name
//    private String generateFileName() {
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
//        Date curDate = new Date(System.currentTimeMillis());
//        return formatter.format(curDate).replace(" ", "");
//    }
//    //drawable to byte[]
//    private byte[] drawable2ByteArray(@DrawableRes int drawableId, Context ctx) {
//        Bitmap icon = BitmapFactory.decodeResource(ctx.getResources(), drawableId);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        return stream.toByteArray();
//    }
//    //Create Folder
//    //Only call this on Android 9 and lower (getExternalStoragePublicDirectory is deprecated)
//    //This can still be used on Android 10> but you will have to add android:requestLegacyExternalStorage="true" in your Manifest
//    private void createFolder() {
//        File f1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "SpeedTest");
//        if (!f1.exists()) {
//            if (f1.mkdirs()) {
//                Log.i("Folder ", "created");
//            }
//        }
//    }
//}
