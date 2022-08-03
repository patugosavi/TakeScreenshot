package com.example.takescreenshot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jraska.falcon.Falcon;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btn_take_screenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_take_screenshot=(Button) findViewById(R.id.btn_take_screenshot);

        btn_take_screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File screenshotFile = getScreenshotFile();

                Falcon.takeScreenshot(MainActivity.this, screenshotFile);

                String message = "Screenshot captured to " + screenshotFile.getAbsolutePath();
                Toast.makeText(getApplicationContext(), ""+message, Toast.LENGTH_LONG).show();

                Uri uri = Uri.fromFile(screenshotFile);
                Intent scanFileIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                sendBroadcast(scanFileIntent);
            }
        });
    }

    protected File getScreenshotFile() {
        File screenshotDirectory;
        try {
            screenshotDirectory = getScreenshotsDirectory(getApplicationContext());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.getDefault());

        String screenshotName = dateFormat.format(new Date()) + ".png";
        return new File(screenshotDirectory, screenshotName);
    }


    private static File getScreenshotsDirectory(Context context) throws IllegalAccessException {
        String dirName = "screenshots_" + context.getPackageName();

        File rootDir;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            rootDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            rootDir = context.getDir("screens", MODE_PRIVATE);
        }

        File directory = new File(rootDir, dirName);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IllegalAccessException("Unable to create screenshot directory " + directory.getAbsolutePath());
            }
        }

        return directory;
    }

}