package com.gigigo.imagerecognition.vuforia;

import android.app.Activity;
import android.content.Context;

public interface ContextProvider {

  Activity getCurrentActivity();

  boolean isActivityContextAvailable();

  Context getApplicationContext();

  boolean isApplicationContextAvailable();
}
