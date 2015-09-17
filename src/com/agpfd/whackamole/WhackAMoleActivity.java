package com.agpfd.whackamole;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class WhackAMoleActivity extends Activity {

    private WhackAMoleView myWhackAMoleView;
    private static final int TOGGLE_SOUND = 1;
    private boolean soundEnabled = true;
    private static final String PREFERENCES_NAME = "MyPreferences";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.whackamole_layout);
        myWhackAMoleView = (WhackAMoleView) findViewById(R.id.mole);
        myWhackAMoleView.setKeepScreenOn(true);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Load settins from SharedPreferences object
//        SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
//        soundEnabled = settings.getBoolean("soundSetting", true);
//        myWhackAMoleView.soundOn = soundEnabled;
        // Load settings from SQLite DB
        DatabaseAdapter db = new DatabaseAdapter(this);
        try {
            db.open();
        } catch (SQLException sqle) {
            throw sqle;
        }
        Cursor c = db.getRecord(1);
        startManagingCursor(c);
        if (c.moveToFirst()) {
            do  {
                soundEnabled = Boolean.parseBoolean(c.getString(1));
            } while (c.moveToNext());
        }
        db.close();
        myWhackAMoleView.soundOn = soundEnabled;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem toggleSound = menu.add(0, TOGGLE_SOUND, 0, R.string.toggle_sound);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case TOGGLE_SOUND:
                String soundEnabledText = "Sound On";
                if (soundEnabled) {
                    soundEnabled = false;
                    myWhackAMoleView.soundOn = false;
                    soundEnabledText = "Sound Off";
                } else {
                    soundEnabled = true;
                    myWhackAMoleView.soundOn = true;
                }
                // Persist configuration with SharedPreferences object
//                SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, 0);
//                SharedPreferences.Editor editor = settings.edit();
//                editor.putBoolean("soundSetting", soundEnabled);
//                editor.commit();
                // Persist configuration with SQLite Database
                DatabaseAdapter db = new DatabaseAdapter(this);
                try {
                    db.open();
                } catch (SQLException sqle) {
                    throw sqle;
                }
                db.insertOrUpdateRecord(Boolean.toString(soundEnabled));
                db.close();
                Toast.makeText(this, soundEnabledText, Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }
}
