package com.cr5315.screenrecord;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Ben on 11/1/13.
 */
public class Tools {
    Context context;

    public Tools(Context context) {
        this.context = context;
    }

    public boolean runAsRoot(String[] commands, long millisToRun, TextView textView) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            for (String cmd : commands) {
                Log.i("ScreenRecord", "Running command '" + cmd + "'");
                os.writeBytes(cmd + "\n");
                if (cmd.contains("screenrecord")) {
                    new RecordTimer(millisToRun, 1000, textView).start();
                    MainActivity.toggleButton(true);
                }
            }
            os.writeBytes("exit");
            os.flush();
            os.close();
            return true;
        } catch (IOException e) {
            Log.e("ScreenRecord", e.toString());
            return false;
        }
    }

    public String formatTime(String minute, String second) {
        String m = minute; String s = second;
        // minute
        if (m.matches("1")) m = "01";
        else if (m.matches("2")) m = "02";
        else if (m.matches("3")) m = "03";
        else if (m.matches("4")) m = "04";
        else if (m.matches("5")) m = "05";
        else if (m.matches("6")) m = "06";
        else if (m.matches("7")) m = "07";
        else if (m.matches("8")) m = "08";
        else if (m.matches("9")) m = "09";
        else if (m.matches("0")) m = "00";

        // second
        if (s.matches("1")) s = "01";
        else if (s.matches("2")) s = "02";
        else if (s.matches("3")) s = "03";
        else if (s.matches("4")) s = "04";
        else if (s.matches("5")) s = "05";
        else if (s.matches("6")) s = "06";
        else if (s.matches("7")) s = "07";
        else if (s.matches("8")) s = "08";
        else if (s.matches("9")) s = "09";
        else if (s.matches("0")) s = "00";

        return m + ":" + s;
    }

    public String formatSeconds(String second) {
        String s = second;
        // second
        if (s.matches("1")) s = "01";
        else if (s.matches("2")) s = "02";
        else if (s.matches("3")) s = "03";
        else if (s.matches("4")) s = "04";
        else if (s.matches("5")) s = "05";
        else if (s.matches("6")) s = "06";
        else if (s.matches("7")) s = "07";
        else if (s.matches("8")) s = "08";
        else if (s.matches("9")) s = "09";
        else if (s.matches("0")) s = "00";

        return s;
    }

    public class RecordTimer extends CountDownTimer {
        TextView textView;
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public RecordTimer(long millisInFuture, long countDownInterval, TextView textView) {
            super(millisInFuture, countDownInterval);
            this.textView = textView;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (textView.getVisibility() == TextView.INVISIBLE) textView.setVisibility(TextView.VISIBLE);
            String minutes = String.valueOf((int) (millisUntilFinished / 1000) / 60);
            String seconds = String.valueOf((int) (millisUntilFinished / 1000) % 60);
            textView.setText(formatTime(minutes, seconds));
        }

        @Override
        public void onFinish() {
            textView.setText("");
            textView.setVisibility(TextView.INVISIBLE);
            Toast.makeText(context, "Recording finished", Toast.LENGTH_SHORT).show();
            MainActivity.toggleButton(false);
        }
    }

    public long getMillis(int minutes, int seconds) {
        return (minutes * 60000) + (seconds * 1000);
    }

    public long getSeconds(int minutes, int seconds) {
        return (minutes * 60) + seconds;
    }
}
