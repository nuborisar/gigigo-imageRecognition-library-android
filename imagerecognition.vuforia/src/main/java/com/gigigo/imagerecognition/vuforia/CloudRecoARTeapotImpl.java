package com.gigigo.imagerecognition.vuforia;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.Matrix;
import com.gigigo.vuforiacore.sdkimagerecognition.icloudrecognition.ICloudRecognitionAR;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.CubeShaders;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.SampleUtils;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.Teapot;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.Texture;
import com.gigigo.vuforiacore.sdkimagerecognition.vuforiaenvironment.utils.VuforiaUtils;
import com.vuforia.Matrix44F;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;
import java.util.Vector;

/**
 * Created by nubor on 14/03/2018.
 */

public class CloudRecoARTeapotImpl implements ICloudRecognitionAR {

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
    mTextures.add(Texture.loadTextureFromApk("TextureTeapotRed.png", mActivity.getAssets()));
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
    System.out.println("*******************RENDER AR");
    //asv check this
    //https://library.vuforia.com/content/vuforia-library/en/articles/Solution/Working-with-Vuforia-and-OpenGL-ES.html#How-To-Render-Static-3D-Models-using-OpenGL-ES
    ///    https://developer.vuforia.com/forum/ios/how-render-complex-3d-models-animations
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
    //try {
    //  Thread.sleep(50);
    //} catch (InterruptedException e) {
    //  e.printStackTrace();
    //}
    // disable the enabled arrays
    GLES20.glDisableVertexAttribArray(vertexHandle);
    GLES20.glDisableVertexAttribArray(textureCoordHandle);

    SampleUtils.checkGLError("CloudReco renderFrame");
  }
}
