package com.gigigo.imagerecognition

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.TextView
import com.gigigo.ggglib.ContextProvider

class ImageRecognizerActivity : AppCompatActivity() {

  private lateinit var licenseKeyTv: TextView
  private lateinit var accessKeyTv: TextView
  private lateinit var secretKeyTv: TextView
  private lateinit var codeTv: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_imagerecognizer)

    initViews()

    licenseKeyTv.text = "license key: ${licenseKey}"
    accessKeyTv.text = "access key: ${clientAccessKey}"
    secretKeyTv.text = "secret key: ${clientSecretKey}"
  }

  private fun initViews() {
    initToolbar()

    licenseKeyTv = findViewById(R.id.imagerecognizer_license_tv) as TextView
    accessKeyTv = findViewById(R.id.imagerecognizer_accessKey_tv) as TextView
    secretKeyTv = findViewById(R.id.imagerecognizer_secretKey_tv) as TextView
    codeTv = findViewById(R.id.imagerecognizer_code_tv) as TextView

    val startButton = findViewById(R.id.start_vuforia_button) as Button
    startButton.setOnClickListener({ startVuforia() })
  }

  private fun initToolbar() {

    val toolbar = findViewById(R.id.toolbar) as Toolbar

    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)

    toolbar.setNavigationOnClickListener { onBackPressed() }

    title = getString(R.string.imagerecognizer_title)
  }

  public override fun onResume() {
    super.onResume()
    imageRecognizerActivity = this
  }

  public override fun onDestroy() {
    super.onDestroy()
    imageRecognizerActivity = null
  }

  private val licenseKey: String = "Abi5nL//////AAAAAXzdWR6MxEppinKbZ9qJjhU7Op5/+8Pwm8tdYfI4f3zFmRweYqowENwgiOUAtaiIH06OpQFISbhX9Linf/uq5JXUADO/MFrnbzy/UIuA3whurbD+Q18bV3uRrm2FtvF64fWdH7R1GoAbOEL6wbF621Da0JJ4uVYAZEYOga/6C4fBEtf0LpKoetdNIVpIxvWsIWHRNVWX41gbRTmwSqCnoV1axtSqBAalAx5Oq/GjoD4a8isoBRJMhkIEOR+4Q7lbyJrQatD+9TqINi9wAuBY9/atNKA27AzMpnQcuAaSr2rv8Y8r3wtk7yQY7oTm8CrBMLri+TdEZoF6Z/TdZaupRaqrlKZqtptOme0zoodbOTVe"

  private val clientAccessKey: String = "fe4d316136ea6b7ee5faa72c4884e33805128b08"

  private val clientSecretKey: String = "670f15bb4cd34c1621a892ced5321896c0b70df6"

  private fun showResponseCode(code: String) {
    codeTv.text = "CODE= $code"

  }

  private fun startVuforia() {
    var imageRecognition = com.gigigo.imagerecognition.vuforia.ImageRecognitionVuforiaImpl()

    val contextProvider = object : ContextProvider {
      override fun getApplicationContext(): Context = this@ImageRecognizerActivity.application.applicationContext

      override fun getCurrentActivity(): Activity = this@ImageRecognizerActivity

      override fun isApplicationContextAvailable(): Boolean = true

      override fun isActivityContextAvailable(): Boolean = true
    }

    imageRecognition.setContextProvider(contextProvider)

    val vuforiaCredentials = object : ImageRecognitionCredentials{
      override fun getLicensekey(): String = this@ImageRecognizerActivity.licenseKey

      override fun getClientSecretKey(): String = this@ImageRecognizerActivity.clientSecretKey

      override fun getClientAccessKey(): String = this@ImageRecognizerActivity.clientAccessKey
    }

    imageRecognition.startImageRecognition(vuforiaCredentials)
  }

  companion object Navigator {
    private var imageRecognizerActivity: ImageRecognizerActivity? = null

    fun showCode(code: String) {
      imageRecognizerActivity?.showResponseCode(code)
    }
  }
}