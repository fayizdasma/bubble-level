package com.fm.bubblelevel.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fm.bubblelevel.R;
import com.fm.bubblelevel.utils.AppConstants;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView tv_seekbar_val;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_seekbar_val = (TextView) findViewById(R.id.tv_seekbar_val);
        seekBar.setMax(AppConstants.MAX_RANGE);

        //get value from shared preference and pre-set
        preferences = this.getSharedPreferences(AppConstants.APP_SHARED_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        int progress = preferences.getInt(AppConstants.SHARED_PREF_KEY_TOLERENCE_LEVEL, 0);
        seekBar.setProgress(progress);
        tv_seekbar_val.setText("+/- " + progress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_seekbar_val.setText("+/- " + progress);
                //save to shared preference
                editor.putInt(AppConstants.SHARED_PREF_KEY_TOLERENCE_LEVEL, progress);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}