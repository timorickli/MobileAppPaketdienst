package net.ictcampus.paketdienst;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class DeliveryTimer {

    private CountDownTimer countDownTimer;
    private long endTime, timeLeft;
    private boolean timerRunning;
    private long deliveryTime;

    public boolean checkState(SharedPreferences timersFile, TextView textView, String keyTimeLeft, String keyTimerRunning, String keyEndTime){

        //Gets previous timer values
        timeLeft = timersFile.getLong(keyTimeLeft, deliveryTime);
        timerRunning = timersFile.getBoolean(keyTimerRunning, false);

        //Checks timer state and starts it if needed
        if (timerRunning) {
            updateTime(textView);
            endTime = timersFile.getLong(keyEndTime, 0);
            timeLeft = endTime - System.currentTimeMillis();
            if (timeLeft < 0) {
                timeLeft = 0;
                timerRunning = false;
                return false;
            } else {
                updateTime(textView);
                startDeliveryTimer(textView);
            }
        }
        return true;
    }

    public boolean checkState(SharedPreferences timersFile, Button button, String keyTimeLeft, String keyTimerRunning, String keyEndTime){
        //Gets previous timer values
        timeLeft = timersFile.getLong(keyTimeLeft, deliveryTime);
        timerRunning = timersFile.getBoolean(keyTimerRunning, false);

        //Checks timer state and starts it if needed
        if (timerRunning) {
            updateTime(button);
            endTime = timersFile.getLong(keyEndTime, 0);
            timeLeft = endTime - System.currentTimeMillis();
            if (timeLeft < 0) {
                timeLeft = 0;
                timerRunning = false;
                return false;
            } else {
                updateTime(button);
                startDeliveryTimer(button);
            }
        }
        return true;
    }


    public boolean checkState(SharedPreferences timersFile, String keyTimeLeft, String keyTimerRunning, String keyEndTime){
        //Gets previous timer values
        timeLeft = timersFile.getLong(keyTimeLeft, deliveryTime);
        timerRunning = timersFile.getBoolean(keyTimerRunning, false);

        //Checks timer state and starts it if needed
        if (timerRunning) {
            endTime = timersFile.getLong(keyEndTime, 0);
            timeLeft = endTime - System.currentTimeMillis();
            if (timeLeft < 0) {
                timeLeft = 0;
                timerRunning = false;
                return false;
            } else {
                startDeliveryTimer();
            }
        }
        return true;
    }

    public void startDeliveryTimer(TextView textView) {

        //Evaluates end time, so timer can run in background
        endTime = System.currentTimeMillis() + timeLeft;

        //Starts new timer
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTime(textView);
            }

            @Override
            public void onFinish() {
                timerRunning = false;
            }
        }.start();
        timerRunning = true;
        updateTime(textView);
    }

    public void startDeliveryTimer(Button button) {

        //Evaluates end time, so timer can run in background
        endTime = System.currentTimeMillis() + timeLeft;

        //Starts new timer
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTime(button);
            }

            @Override
            public void onFinish() {
                timerRunning = false;
            }
        }.start();
        timerRunning = true;
        updateTime(button);
    }

    public void startDeliveryTimer() {

        //Evaluates end time, so timer can run in background
        endTime = System.currentTimeMillis() + timeLeft;

        //Starts new timer
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                timerRunning = false;
            }
        }.start();
        timerRunning = true;
    }

    public void beforeChange(SharedPreferences.Editor editorTimers, String keyTimeLeft, String keyTimerRunning, String keyEndTime) {

        //Edits values, that timer basically runs in background
        editorTimers.putLong(keyTimeLeft, timeLeft);
        editorTimers.putBoolean(keyTimerRunning, timerRunning);
        editorTimers.putLong(keyEndTime, endTime);
        editorTimers.apply();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void updateTime(TextView textView) {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        //String formatter
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView.setText("Time remaining: " + timeFormat);
    }

    public void updateTime(Button button) {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        //String formatter
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        button.setText("Time remaining: " + timeFormat);
    }

    public void resetTimer(SharedPreferences.Editor editorTimers, String keyTimeLeft, String keyTimerRunning, String keyEndTime) {
        countDownTimer.cancel();
        timerRunning = false;
        timeLeft = deliveryTime;
        endTime = 0;
        beforeChange(editorTimers, keyTimeLeft, keyTimerRunning, keyEndTime);
    }

    public DeliveryTimer(long deliveryTimeInMinutes) {
        this.deliveryTime = 60 * deliveryTimeInMinutes * 1000;
    }
}
