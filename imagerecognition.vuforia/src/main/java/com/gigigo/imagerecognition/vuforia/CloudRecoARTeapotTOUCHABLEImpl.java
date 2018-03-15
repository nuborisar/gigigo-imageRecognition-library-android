package com.gigigo.imagerecognition.vuforia;

import android.app.Activity;
import android.opengl.GLES20;
import com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition.ICloudRecognitionAR;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.CubeShaders;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.Teapot;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.Texture;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.VuforiaUtils;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;
import java.util.Vector;

/**
 * Created by nubor on 14/03/2018.
 */

public class CloudRecoARTeapotTOUCHABLEImpl implements ICloudRecognitionAR {

  private int shaderProgramID;
  private int vertexHandle;

  private int textureCoordHandle;
  private int mvpMatrixHandle;
  private int texSampler2DHandle;

  private Vector<Texture> mTextures;
  private Teapot mTeapot;
  Activity mActivity; //asv solo se usa pa cargar una imagen

  private static final float OBJECT_SCALE_FLOAT = 0.005f;//0.003f


  private void loadTextures() {
    mTextures.add(Texture.loadTextureFromApk("TextureTeapotBlue.png", mActivity.getAssets()));
  }

  @Override public void initRender(Activity activity) {
    mActivity = activity;
    // Define clear color
    GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f : 1.0f);
    mTextures = new Vector<Texture>();
    loadTextures();
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
    textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID, "vertexTexCoord");
    mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID, "modelViewProjectionMatrix");
    texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID, "texSampler2D");

    mTeapot = new Teapot();
  }

  @Override public void onRenderAR(TrackableResult trackableResult, float[] projectionMatrix) {
  /*  System.out.println("*******************RENDER AR");
    float[] modelViewMatrix = Tool.convertPose2GLMatrix(
        trackableResult.getPose()).getData();

    // The image target specific result:
    ImageTargetResult imageTargetResult = (ImageTargetResult) trackableResult;

    // Set transformations:
    float[] modelViewProjection = new float[16];
    Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);

    // Set the texture used for the teapot model:
    int textureIndex = 0;

    float vbVertices[] = new float[imageTargetResult
        .getNumVirtualButtons() * 24];
    short vbCounter = 0;

    // Iterate through this targets virtual buttons:
    for (int i = 0; i < imageTargetResult.getNumVirtualButtons(); ++i)
    {
      VirtualButtonResult buttonResult = imageTargetResult
          .getVirtualButtonResult(i);
      VirtualButton button = buttonResult.getVirtualButton();

      int buttonIndex = 0;
      // Run through button name array to find button index
      for (int j = 0; j < VirtualButtons.NUM_BUTTONS; ++j)
      {
        if (button.getName().compareTo(
            mActivity.virtualButtonColors[j]) == 0)
        {
          buttonIndex = j;
          break;
        }
      }

      // If the button is pressed, than use this texture:
      if (buttonResult.isPressed())
      {
        textureIndex = buttonIndex + 1;
      }

      // Define the four virtual buttons as Rectangle using the same values as the dataset
      Rectangle vbRectangle[] = new Rectangle[4];
      vbRectangle[0] = new Rectangle(RED_VB_BUTTON[0], RED_VB_BUTTON[1],
          RED_VB_BUTTON[2], RED_VB_BUTTON[3]);
      vbRectangle[1] = new Rectangle(BLUE_VB_BUTTON[0], BLUE_VB_BUTTON[1],
          BLUE_VB_BUTTON[2], BLUE_VB_BUTTON[3]);
      vbRectangle[2] = new Rectangle(YELLOW_VB_BUTTON[0], YELLOW_VB_BUTTON[1],
          YELLOW_VB_BUTTON[2], YELLOW_VB_BUTTON[3]);
      vbRectangle[3] = new Rectangle(GREEN_VB_BUTTON[0], GREEN_VB_BUTTON[1],
          GREEN_VB_BUTTON[2], GREEN_VB_BUTTON[3]);

      // We add the vertices to a common array in order to have one
      // single
      // draw call. This is more efficient than having multiple
      // glDrawArray calls
      vbVertices[vbCounter] = vbRectangle[buttonIndex].getLeftTopX();
      vbVertices[vbCounter + 1] = vbRectangle[buttonIndex]
          .getLeftTopY();
      vbVertices[vbCounter + 2] = 0.0f;
      vbVertices[vbCounter + 3] = vbRectangle[buttonIndex]
          .getRightBottomX();
      vbVertices[vbCounter + 4] = vbRectangle[buttonIndex]
          .getLeftTopY();
      vbVertices[vbCounter + 5] = 0.0f;
      vbVertices[vbCounter + 6] = vbRectangle[buttonIndex]
          .getRightBottomX();
      vbVertices[vbCounter + 7] = vbRectangle[buttonIndex]
          .getLeftTopY();
      vbVertices[vbCounter + 8] = 0.0f;
      vbVertices[vbCounter + 9] = vbRectangle[buttonIndex]
          .getRightBottomX();
      vbVertices[vbCounter + 10] = vbRectangle[buttonIndex]
          .getRightBottomY();
      vbVertices[vbCounter + 11] = 0.0f;
      vbVertices[vbCounter + 12] = vbRectangle[buttonIndex]
          .getRightBottomX();
      vbVertices[vbCounter + 13] = vbRectangle[buttonIndex]
          .getRightBottomY();
      vbVertices[vbCounter + 14] = 0.0f;
      vbVertices[vbCounter + 15] = vbRectangle[buttonIndex]
          .getLeftTopX();
      vbVertices[vbCounter + 16] = vbRectangle[buttonIndex]
          .getRightBottomY();
      vbVertices[vbCounter + 17] = 0.0f;
      vbVertices[vbCounter + 18] = vbRectangle[buttonIndex]
          .getLeftTopX();
      vbVertices[vbCounter + 19] = vbRectangle[buttonIndex]
          .getRightBottomY();
      vbVertices[vbCounter + 20] = 0.0f;
      vbVertices[vbCounter + 21] = vbRectangle[buttonIndex]
          .getLeftTopX();
      vbVertices[vbCounter + 22] = vbRectangle[buttonIndex]
          .getLeftTopY();
      vbVertices[vbCounter + 23] = 0.0f;
      vbCounter += 24;

    }

    // We only render if there is something on the array
    if (vbCounter > 0)
    {
      // Render frame around button
      GLES20.glUseProgram(vbShaderProgramID);

      GLES20.glVertexAttribPointer(vbVertexHandle, 3,
          GLES20.GL_FLOAT, false, 0, fillBuffer(vbVertices));

      GLES20.glEnableVertexAttribArray(vbVertexHandle);

      GLES20.glUniform1f(lineOpacityHandle, 1.0f);
      GLES20.glUniform3f(lineColorHandle, 1.0f, 1.0f, 1.0f);

      GLES20.glUniformMatrix4fv(mvpMatrixButtonsHandle, 1, false,
          modelViewProjection, 0);

      // We multiply by 8 because that's the number of vertices per
      // button
      // The reason is that GL_LINES considers only pairs. So some
      // vertices
      // must be repeated.
      GLES20.glDrawArrays(GLES20.GL_LINES, 0,
          imageTargetResult.getNumVirtualButtons() * 8);

      SampleUtils.checkGLError("VirtualButtons drawButton");

      GLES20.glDisableVertexAttribArray(vbVertexHandle);
    }

    // Assumptions:
    Texture thisTexture = mTextures.get(textureIndex);

    // Scale 3D model
    Matrix.scaleM(modelViewMatrix, 0, kTeapotScale, kTeapotScale,
        kTeapotScale);

    float[] modelViewProjectionScaled = new float[16];
    Matrix.multiplyMM(modelViewProjectionScaled, 0, projectionMatrix, 0, modelViewMatrix, 0);

    // Render 3D model
    GLES20.glUseProgram(shaderProgramID);

    GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
        false, 0, mTeapot.getVertices());
    GLES20.glVertexAttribPointer(textureCoordHandle, 2,
        GLES20.GL_FLOAT, false, 0, mTeapot.getTexCoords());

    GLES20.glEnableVertexAttribArray(vertexHandle);
    GLES20.glEnableVertexAttribArray(textureCoordHandle);

    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
        thisTexture.mTextureID[0]);
    GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
        modelViewProjectionScaled, 0);
    GLES20.glUniform1i(texSampler2DHandle, 0);
    GLES20.glDrawElements(GLES20.GL_TRIANGLES,
        mTeapot.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
        mTeapot.getIndices());

    GLES20.glDisableVertexAttribArray(vertexHandle);
    GLES20.glDisableVertexAttribArray(textureCoordHandle);

    SampleUtils.checkGLError("VirtualButtons renderFrame");

*/  }



}
