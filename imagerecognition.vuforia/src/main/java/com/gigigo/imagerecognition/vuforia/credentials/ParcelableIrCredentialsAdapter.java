package com.gigigo.imagerecognition.vuforia.credentials;

import com.gigigo.imagerecognition.ImageRecognitionCredentials;

public class ParcelableIrCredentialsAdapter {

  public ParcelableVuforiaCredentials getParcelableFromCredentialsForVuforia(
      ImageRecognitionCredentials irc) {

    return new ParcelableVuforiaCredentials(irc.getLicensekey(), irc.getClientAccessKey(),
        irc.getClientSecretKey());
  }
}
