package com.gigigo.imagerecognition.vuforia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.gigigo.imagerecognition.ImageRecognition;
import com.gigigo.imagerecognition.ImageRecognitionCredentials;
import com.gigigo.imagerecognition.NotFoundContextException;
import com.gigigo.imagerecognition.vuforia.credentials.ParcelableIrCredentialsAdapter;
import com.gigigo.imagerecognition.vuforia.credentials.ParcelableVuforiaCredentials;
import com.gigigo.permissions.PermissionsActivity;
import com.gigigo.permissions.exception.PermissionException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

/**
 * This is a suitable implementation for image recognition module, in fact this this Vuforia
 * ImageRecognition interface specialization. An instance of this class would call Vuforia SDK
 * when startImageRecognition is called.
 * <p/>
 * This class is already managing Camera permissions implementation.
 */
public class ImageRecognitionVuforiaImpl implements ImageRecognition {

  public static final String IMAGE_RECOGNITION_CREDENTIALS = "IMAGE_RECOGNITION_CREDENTIALS";
  public static final String IMAGE_RECOGNITION_CODE_RESULT = "IMAGE_RECOGNITION_CODE_RESULT";
  private static final int PERMISSIONS_REQUEST_CAMERA = 1;
  private static ContextProvider contextProvider;
  private ParcelableVuforiaCredentials credentials;

  public ImageRecognitionVuforiaImpl() {

  }

  /*we need a persistesd context(ImageRecognitionVuforiaImpl.getContextProvider().getApplicationContext()):
the problem, is sometimes the net confirmation of action is more quickly than finished of vuforia activity
the solution is use context exist in life of runnable and complety sure exist when run sendBroadcast
and the next problem is wait for complete vuforia activity finishing for when receive the action the
activity caller vuforia is started again, for show alertDialog
*/
  public static void sendRecognizedPattern(final Intent i) {

    Handler mHandler = new Handler(Looper.getMainLooper());
    mHandler.postDelayed(new Runnable() {
      @Override public void run() {
        try {
          contextProvider.getApplicationContext().sendBroadcast(i);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }, 1500);
  }

  @Override public <T> void setContextProvider(T contextProvider) {
    this.contextProvider = (ContextProvider) contextProvider;
  }

  /**
   * Checks permissions and starts Image recognitio activity using given credentials. If Permissions
   * were not granted User will be notified. If credentials are not valid you'll have an error log
   * message.
   *
   * @param credentials interface implementation with Vuforia keys
   */
  @Override public void startImageRecognition(ImageRecognitionCredentials credentials) {
    checkContext();

    this.credentials = digestCredentials(credentials);

    PermissionsActivity.Navigator.open(this.contextProvider.getApplicationContext(),
        new Function0<Unit>() {
          @Override public Unit invoke() {
            startImageRecognitionActivity();
            return null;
          }
        }, new Function1<PermissionException, Unit>() {
          @Override public Unit invoke(PermissionException e) {
            Toast.makeText(contextProvider.getCurrentActivity(), "Explanation!!!",
                Toast.LENGTH_SHORT).show();

            return null;
          }
        });
  }

  private void checkContext() throws NotFoundContextException {
    if (contextProvider == null) {
      throw new NotFoundContextException();
    }
  }

  private ParcelableVuforiaCredentials digestCredentials(
      ImageRecognitionCredentials externalCredentials) {
    ParcelableIrCredentialsAdapter adapter = new ParcelableIrCredentialsAdapter();
    ParcelableVuforiaCredentials credentials =
        adapter.getParcelableFromCredentialsForVuforia(externalCredentials);
    return credentials;
  }

  private void startImageRecognitionActivity() {
    Intent imageRecognitionIntent =
        new Intent(contextProvider.getApplicationContext(), VuforiaActivity.class);
    imageRecognitionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    Bundle b = new Bundle();
    b.putParcelable(IMAGE_RECOGNITION_CREDENTIALS, credentials);

    imageRecognitionIntent.putExtra(IMAGE_RECOGNITION_CREDENTIALS, b);
    contextProvider.getApplicationContext().startActivity(imageRecognitionIntent);
  }
}
