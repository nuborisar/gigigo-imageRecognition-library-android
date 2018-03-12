package com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.VuforiaSession;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.CubeShaders;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.SampleAppRenderer;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.SampleAppRendererControl;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.SampleUtils;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.Teapot;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.Texture;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.VuforiaUtils;
import com.vuforia.Device;
import com.vuforia.Matrix44F;
import com.vuforia.Renderer;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;
import java.util.Vector;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CloudRecognitionRenderer implements GLSurfaceView.Renderer, SampleAppRendererControl {
  private static final float OBJECT_SCALE_FLOAT = 1.0f;//0.003f
  VuforiaSession vuforiaAppSession;

  private SampleAppRenderer mSampleAppRenderer;
  private int shaderProgramID;
  private int vertexHandle;
  private int normalHandle;
  private int textureCoordHandle;
  private int mvpMatrixHandle;
  private int texSampler2DHandle;

  private Vector<Texture> mTextures;
  private CloudRecognition mCloudReco;

  private boolean mIsActive = false;

  public CloudRecognitionRenderer(VuforiaSession session, CloudRecognition cloudRecog) {
    vuforiaAppSession = session;
    mCloudReco = cloudRecog;
    // SampleAppRenderer used to encapsulate the use of RenderingPrimitives setting
    // the device mode AR/VR and stereo mode
    mSampleAppRenderer =
        new SampleAppRenderer(this, cloudRecog.mActivity, Device.MODE.MODE_AR, false, 0.010f, 5f);
  }

  // Called when the surface is created or recreated.
  @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    // Call Vuforia function to (re)initialize rendering after first use
    // or after OpenGL ES context was lost (e.g. after onPause/onResume):
    vuforiaAppSession.onSurfaceCreated();
    mSampleAppRenderer.onSurfaceCreated();
  }

  // Called when the surface changed size.
  @Override public void onSurfaceChanged(GL10 gl, int width, int height) {
    // Call Vuforia function to handle render surface size changes:
    vuforiaAppSession.onSurfaceChanged(width, height);
    mSampleAppRenderer.onConfigurationChanged(mIsActive);

    // Call function to initialize rendering:
    initRendering();
  }

  // Called to draw the current frame.
  @Override public void onDrawFrame(GL10 gl) {
    // Call our function to render content
    mSampleAppRenderer.render();
  }

  public void setActive(boolean active) {
    mIsActive = active;

    if (mIsActive) mSampleAppRenderer.configureVideoBackground();
  }

  // Function for initializing the renderer.
  private void initRendering() {
    // Define clear color
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f : 1.0f);

    if (mTextures != null && mTextures.size() > 0) {
      for (Texture t : mTextures) {
        GLES20.glGenTextures(1, t.mTextureID, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, t.mWidth, t.mHeight, 0,
            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, t.mData);
      }
    }
    shaderProgramID = VuforiaUtils.createProgramFromShaderSrc(CubeShaders.CUBE_MESH_VERTEX_SHADER,
        CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

    vertexHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexPosition");
    normalHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexNormal");
    textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexTexCoord");
    mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID, "modelViewProjectionMatrix");
    texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID, "texSampler2D");

    mTeapot = new Teapot();
  }

  // The render function.
  @Deprecated private void renderFrame() {
    //region render frame old
    // Clear color and depth buffer
  /*  GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

    // Get the state from Vuforia and mark the beginning of a rendering
    // section
    State state = Renderer.getInstance().begin();

    // Explicitly render the Video Background
    // Renderer.getInstance().drawVideoBackground();
    //mSampleAppRenderer.renderVideoBackground();
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    GLES20.glEnable(GLES20.GL_CULL_FACE);
    if (Renderer.getInstance().getVideoBackgroundConfig().getReflection()
        == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON) {
      GLES20.glFrontFace(GLES20.GL_CW);  // Front camera
    } else {
      GLES20.glFrontFace(GLES20.GL_CCW);   // Back camera
    }

    // Set the viewport
    int[] viewport = vuforiaAppSession.getViewport();
    GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);

    // Did we find any trackables this frame?
    if (state.getNumTrackableResults() > 0) {
      // Gets current trackable result
      TrackableResult trackableResult = state.getTrackableResult(0);

      if (trackableResult == null) {
        return;
      }
/* notenough yet  get targetsizepos4anim
            ObjectTarget objectTarget = (ObjectTarget) trackableResult.getTrackable();

            Matrix44F modelViewMatrix_Vuforia = Tool
                    .convertPose2GLMatrix(trackableResult.getPose());
            float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

            // deal with the modelview and projection matrices
            float[] modelViewProjection = new float[16];

            float[] objectSize = objectTarget.getSize().getData();

            for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
                TrackableResult result2 = state.getTrackableResult(tIdx);
                Trackable trackable = result2.getTrackable();
                ObjectTarget objectTarget2 = (ObjectTarget) trackable;

                Matrix44F modelViewMatrix_Vuforia2 = Tool
                        .convertPose2GLMatrix(result2.getPose());
                float[] modelViewMatrix2 = modelViewMatrix_Vuforia2.getData();

                // deal with the modelview and projection matrices
                float[] modelViewProjection2 = new float[16];

                float[] objectSize2 = objectTarget2.getSize().getData();
                System.out.println("************************" + objectSize2.toString());

            }

      mCloudReco.stopFinderIfStarted();
      // Renders the Augmentation View with the 3D Book data Panel
      renderAugmentation(trackableResult);
    } else {
      mCloudReco.startFinderIfStopped();
    }

    GLES20.glDisable(GLES20.GL_DEPTH_TEST);

    Renderer.getInstance().end();*/
    //endregion
    //region new code
   /* // Renders video background replacing Renderer.DrawVideoBackground()
    mSampleAppRenderer.renderVideoBackground();

    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    GLES20.glEnable(GLES20.GL_CULL_FACE);

    // Did we find any trackables this frame?
    if (state.getNumTrackableResults() > 0) {
      // Gets current trackable result
      TrackableResult trackableResult = state.getTrackableResult(0);

      if (trackableResult == null) {
        return;
      }

      mActivity.stopFinderIfStarted();

      // Renders the Augmentation View with the 3D Book data Panel
      renderAugmentation(trackableResult, projectionMatrix);
    } else {
      mActivity.startFinderIfStopped();
    }

    GLES20.glDisable(GLES20.GL_DEPTH_TEST);

    Renderer.getInstance().end();
  */
    //endregion
  }

  @Deprecated private void renderAugmentation(TrackableResult trackableResult) {
    /*Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(trackableResult.getPose());
    float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

    int textureIndex = 0;

    // deal with the modelview and projection matrices
    float[] modelViewProjection = new float[16];
    Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, OBJECT_SCALE_FLOAT);
    Matrix.scaleM(modelViewMatrix, 0, OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT);
    Matrix.multiplyMM(modelViewProjection, 0, vuforiaAppSession.getProjectionMatrix().getData(), 0,
        modelViewMatrix, 0);

    // activate the shader program and bind the vertex/normal/tex coords
    GLES20.glUseProgram(shaderProgramID);
    GLES20.glEnableVertexAttribArray(vertexHandle);
    GLES20.glEnableVertexAttribArray(normalHandle);
    GLES20.glEnableVertexAttribArray(textureCoordHandle);

    // activate texture 0, bind it, and pass to shader
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    if (mTextures != null && mTextures.size() > 0) {
      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(textureIndex).mTextureID[0]);
    }
    GLES20.glUniform1i(texSampler2DHandle, 0);

    // pass the model view matrix to the shader
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

    // disable the enabled arrays
    GLES20.glDisableVertexAttribArray(vertexHandle);
    GLES20.glDisableVertexAttribArray(normalHandle);
    GLES20.glDisableVertexAttribArray(textureCoordHandle);

    VuforiaUtils.checkGLError("CloudReco renderFrame");
    */
  }

  public void setTextures(Vector<Texture> textures) {
    mTextures = textures;
  }

  @Override public void renderFrame(State state, float[] projectionMatrix) {
    // Renders video background replacing Renderer.DrawVideoBackground()
    mSampleAppRenderer.renderVideoBackground();

    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    GLES20.glEnable(GLES20.GL_CULL_FACE);

    // Did we find any trackables this frame?
    if (state.getNumTrackableResults() > 0) {
      // Gets current trackable result
      TrackableResult trackableResult = state.getTrackableResult(0);

      if (trackableResult == null) {
        return;
      }

      mCloudReco.stopFinderIfStarted();

      // Renders the Augmentation View with the 3D Book data Panel
      renderAugmentation(trackableResult, projectionMatrix);
    } else {
      mCloudReco.startFinderIfStopped();
    }

    GLES20.glDisable(GLES20.GL_DEPTH_TEST);

    Renderer.getInstance().end();
  }

  private Teapot mTeapot;

  private void renderAugmentation(TrackableResult trackableResult, float[] projectionMatrix) {
    Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(trackableResult.getPose());
    float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

    int textureIndex = 0;

    // deal with the modelview and projection matrices
    float[] modelViewProjection = new float[16];
    Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, OBJECT_SCALE_FLOAT);
    Matrix.scaleM(modelViewMatrix, 0, OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT);
    Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);

    // activate the shader program and bind the vertex/normal/tex coords
    GLES20.glUseProgram(shaderProgramID);
    GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, mTeapot.getVertices());
    GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
        mTeapot.getTexCoords());

    GLES20.glEnableVertexAttribArray(vertexHandle);
    GLES20.glEnableVertexAttribArray(textureCoordHandle);

    // activate texture 0, bind it, and pass to shader
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(textureIndex).mTextureID[0]);
    GLES20.glUniform1i(texSampler2DHandle, 0);

    // pass the model view matrix to the shader
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

    // finally draw the teapot
    GLES20.glDrawElements(GLES20.GL_TRIANGLES, mTeapot.getNumObjectIndex(),
        GLES20.GL_UNSIGNED_SHORT, mTeapot.getIndices());

    // disable the enabled arrays
    GLES20.glDisableVertexAttribArray(vertexHandle);
    GLES20.glDisableVertexAttribArray(textureCoordHandle);

    SampleUtils.checkGLError("CloudReco renderFrame");
  }
}
