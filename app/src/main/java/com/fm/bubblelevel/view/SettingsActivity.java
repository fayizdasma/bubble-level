package com.fm.bubblelevel.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.fm.bubblelevel.R;
import com.fm.bubblelevel.utils.AppConstants;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView tv_seekbar_val;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Switch switch_vibrate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_seekbar_val = (TextView) findViewById(R.id.tv_seekbar_val);
        switch_vibrate = (Switch) findViewById(R.id.switch_vibrate);
        seekBar.setMax(AppConstants.MAX_RANGE);

        //get value from shared preference and pre-set
        preferences = this.getSharedPreferences(AppConstants.APP_SHARED_PREF, Context.MODE_PRIVATE);
        editor = preferences.edit();
        int progress = preferences.getInt(AppConstants.SHARED_PREF_KEY_TOLERENCE_LEVEL, 5);
        seekBar.setProgress(progress);
        tv_seekbar_val.setText("+/- " + progress);

        boolean isVibration = preferences.getBoolean(AppConstants.SHARED_PREF_KEY_IS_VIBRATION, true);
        switch_vibrate.setChecked(isVibration);

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

        switch_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(AppConstants.SHARED_PREF_KEY_IS_VIBRATION, isChecked);
                editor.commit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}