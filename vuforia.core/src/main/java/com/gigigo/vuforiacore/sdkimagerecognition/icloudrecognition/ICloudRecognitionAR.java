package com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition;

import android.app.Activity;
import com.vuforia.TrackableResult;

/**
 * Created by nubor on 14/03/2018.
 */

public interface ICloudRecognitionAR {

  void initRender(Activity activity);

  void onRenderAR(TrackableResult trackableResult, float[] projectionMatrix);


  //loadtextures()?
}
