package com.gigigo.imagerecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ImageRecognizerReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {

    if (intent!=null && intent.getExtras().containsKey(context?.getPackageName())
        && intent.getExtras().containsKey(ImageRecognitionConstants.PATTERN_ID)) {

      vuforiaPatternRecognized(intent?.getStringExtra(ImageRecognitionConstants.PATTERN_ID))
    }
  }

  fun vuforiaPatternRecognized(code: String) {
    ImageRecognizerActivity.showCode(code)
  }
}