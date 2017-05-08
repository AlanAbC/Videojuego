package com.claresti.videojuego;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Alan Abundis on 08/05/2017.
 */

public class Preferences extends PreferenceActivity{
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
