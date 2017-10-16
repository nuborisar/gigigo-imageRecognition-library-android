package com.gigigo.imagerecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ImageRecognizerReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {

    if (intent!=null && intent.extras.containsKey(context?.packageName)
        && intent.extras.containsKey(Constants.PATTERN_ID)) {

      vuforiaPatternRecognized(intent?.getStringExtra(Constants.PATTERN_ID))
    }
  }

  fun vuforiaPatternRecognized(code: String) {
    ImageRecognizerActivity.showCode(code)
  }
}