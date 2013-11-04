package com.cr5315.screenrecord;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    static Button button, locationButton;
    NumberPicker minutes, seconds;
    TextView locationText, timerText;
    Tools tools;

    private static int REQUEST_CODE = 5315;
    private String saveDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tools = new Tools(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("firstrun", true)) {
            String[] swag = { "" };
            tools.runAsRoot(swag, 0, null);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstrun", false);
            editor.commit();
        }

        saveDir = Environment.getExternalStorageDirectory().toString();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDate = simpleDateFormat.format(new Date());
                String filename = '"' + saveDir + "/" + currentDate + ".mp4" + '"';

                int recordingMinutes = minutes.getValue();
                int recordingSeconds = seconds.getValue();
                long recordingTimeMillis = tools.getMillis(recordingMinutes, recordingSeconds);
                long recordingTimeSeconds = tools.getSeconds(recordingMinutes, recordingSeconds);

                String[] commandToRun = { "system/bin/screenrecord " + filename + " --time-limit " + String.valueOf(recordingTimeSeconds) };
                tools.runAsRoot(commandToRun, recordingTimeMillis, timerText);

            }
        });

        timerText = (TextView) findViewById(R.id.countdown);
        minutes = (NumberPicker) findViewById(R.id.minutePicker);
        seconds = (NumberPicker) findViewById(R.id.secondPicker);
        initTimePickers();

        locationText = (TextView) findViewById(R.id.location);
        locationText.setText("Save Location: " + saveDir + "/");

        locationButton = (Button) findViewById(R.id.locationButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DirectoryChooserActivity.class);
                // Optional: Allow users to create a new directory with a fixed name.
                intent.putExtra(DirectoryChooserActivity.EXTRA_NEW_DIR_NAME,
                        "Screen Record");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                // Uri fileDir = data.getData();
                Log.i("ScreenRecord", data
                        .getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
                saveDir = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                locationText.setText("Save Location: " + saveDir + "/");
            }
        }
    }

    private void initTimePickers() {
        // Minutes
        minutes.setMaxValue(3);
        minutes.setMinValue(0);
        minutes.setValue(3);
        minutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (newVal == 3) {
                    seconds.setMaxValue(0);
                    seconds.setMinValue(0);
                } else {
                    seconds.setMaxValue(59);
                    seconds.setMinValue(0);
                }
            }
        });

        //Seconds
        seconds.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return tools.fomatSeconds(String.valueOf(value));
            }
        });
        seconds.setMinValue(0);
        seconds.setMaxValue(0);
        seconds.setValue(0);
    }

    public static void toggleButton(boolean isRecording) {
        button.setEnabled(!isRecording);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.action_settings);
                builder.setMessage(R.string.license);
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}