package net.ictcampus.paketdienst.helper;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

/**
 * Helps creating timers for items and delivery
 */
public class GameTimer {
    private long deliveryTime;
    private boolean timerRunning;
    private long endTime, timeLeft;
    private CountDownTimer countDownTimer;

    /**
     * Checks status of timer and starts it
     *
     * @param timersFile      Shared Preference, for the timer values
     * @param textView        location, to display time
     * @param keyTimeLeft     key String of Shared Preferences
     * @param keyTimerRunning key String of Shared Preferences
     * @param keyEndTime      key String of Shared Preferences
     * @return if timer runs True or not False
     */
    public boolean checkState(SharedPreferences timersFile, TextView textView, String keyTimeLeft, String keyTimerRunning, String keyEndTime) {

        //Gets previous timer values
        timeLeft = timersFile.getLong(keyTimeLeft, deliveryTime);
        timerRunning = timersFile.getBoolean(keyTimerRunning, false);

        //Checks timer status and stats it if needed
        if (timerRunning) {
            updateTime(textView);
            endTime = timersFile.getLong(keyEndTime, 0);
            timeLeft = endTime - System.currentTimeMillis();

            //If timer is finished
            if (timeLeft < 0) {
                timeLeft = 0;
                timerRunning = false;
                return false;
            } else {
                updateTime(textView);
                startGameTimer(textView);
            }
        }
        return true;
    }

    /**
     * Checks status of timer and starts it
     *
     * @param timersFile      Shared Preference, for the timer values
     * @param button          location, to display time
     * @param keyTimeLeft     key String of Shared Preferences
     * @param keyTimerRunning key String of Shared Preferences
     * @param keyEndTime      key String of Shared Preferences
     * @return if timer runs True or not False
     */
    public boolean checkState(SharedPreferences timersFile, Button button, String keyTimeLeft, String keyTimerRunning, String keyEndTime) {

        //Gets previous timer values
        timeLeft = timersFile.getLong(keyTimeLeft, deliveryTime);
        timerRunning = timersFile.getBoolean(keyTimerRunning, false);

        //Checks timer status and starts it if needed
        if (timerRunning) {
            updateTime(button);
            endTime = timersFile.getLong(keyEndTime, 0);
            timeLeft = endTime - System.currentTimeMillis();

            //If timer is finished
            if (timeLeft < 0) {
                timeLeft = 0;
                timerRunning = false;
                return false;
            } else {
                updateTime(button);
                startGameTimer(button);
            }
        }
        return true;
    }

    /**
     * Checks status of timer and starts it
     *
     * @param timersFile      Shared Preference, for the timer values
     * @param keyTimeLeft     key String of Shared Preferences
     * @param keyTimerRunning key String of Shared Preferences
     * @param keyEndTime      key String of Shared Preferences
     * @return if timer runs True or not False
     */
    public boolean checkState(SharedPreferences timersFile, String keyTimeLeft, String keyTimerRunning, String keyEndTime) {

        //Gets previous timer values
        timeLeft = timersFile.getLong(keyTimeLeft, deliveryTime);
        timerRunning = timersFile.getBoolean(keyTimerRunning, false);

        //Checks timer status and starts it if needed
        if (timerRunning) {
            endTime = timersFile.getLong(keyEndTime, 0);
            timeLeft = endTime - System.currentTimeMillis();

            //If timer is over
            if (timeLeft < 0) {
                timeLeft = 0;
                timerRunning = false;
                return false;
            } else {
                startGameTimer();
            }
        }
        return true;
    }

    /**
     * Starts the Timer
     *
     * @param textView location, to display time
     */
    public void startGameTimer(TextView textView) {

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

    /**
     * Starts the Timer
     *
     * @param button location, to display time
     */
    public void startGameTimer(Button button) {

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

    /**
     * Starts the Timer
     */
    public void startGameTimer() {

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

    /**
     * Saves all current timer values into Shared Preferences
     *
     * @param editorTimers    Shared Preference Editor, to edit the timer values
     * @param keyTimeLeft     key String of Shared Preferences
     * @param keyTimerRunning key String of Shared Preferences
     * @param keyEndTime      key String of Shared Preferences
     */
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

    /**
     * Updates Time on a Textview
     *
     * @param textView location, to display time
     */
    public void updateTime(TextView textView) {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        //String formatter
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView.setText("Time remaining: " + timeFormat);
    }

    /**
     * Updates Time on a Button
     *
     * @param button location, to display time
     */
    public void updateTime(Button button) {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        //String formatter
        String timeFormat = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        button.setText(timeFormat);
    }

    /**
     * Resets timer to standart values, also saves them
     *
     * @param editorTimers    Shared Preference Editor, to edit the timer values
     * @param keyTimeLeft     key String of Shared Preferences
     * @param keyTimerRunning key String of Shared Preferences
     * @param keyEndTime      key String of Shared Preferences
     */
    public void resetTimer(SharedPreferences.Editor editorTimers, String keyTimeLeft, String keyTimerRunning, String keyEndTime) {
        timerRunning = false;
        timeLeft = deliveryTime;
        endTime = 0;

        //Safes standards
        beforeChange(editorTimers, keyTimeLeft, keyTimerRunning, keyEndTime);
    }

    /**
     * Constructor
     *
     * @param deliveryTimeInMinutes standard value for timer to begin
     */
    public GameTimer(long deliveryTimeInMinutes) {
        this.deliveryTime = 60 * deliveryTimeInMinutes * 1000;
    }
}
